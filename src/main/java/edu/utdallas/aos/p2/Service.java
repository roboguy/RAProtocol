package edu.utdallas.aos.p2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import edu.utdallas.aos.p2.config.Node;

public class Service {

	private Logger logger = LogManager.getLogger(Service.class);

	public void csEnter() {

		synchronized (Shared.objForLock) {

			// UPDATE MY REQUEST.TIMESTAMP
			Shared.logicalClockTimeStamp++;

			logger.debug("Updating Timestamp to:"
					+ Shared.logicalClockTimeStamp + " & isRequestedCS = true.");
			Shared.isRequestedCS = true;

			// Check for Have and Have not keys
			// if (Has all keys)
			// isinCS=true
			// execute CriticalSection
			// call csLeave();

			if (Shared.haveNotKeys.size() <= 1 ) {
				logger.debug("All keys found. isInCS = true");
				Shared.isInCS = true;
				logger.debug("Entering Critical Section");
				criticalSection();
				logger.debug("Finished Critical Section");
				return;
				// csLeave();
			}
			// Else (Has not is not empty)
			else {

				// get corresponding keys from various Servers
				// wait till response (while its not null)
				logger.debug("Keys not found, updating requestedTimestamp to "
						+ Shared.logicalClockTimeStamp);
				Shared.requestTimeStamp = Shared.logicalClockTimeStamp;

				Iterator<String> iSet = Shared.haveNotKeys.iterator();

				logger.debug("Sending request for missing keys");

				while (iSet.hasNext()) {
					String key = iSet.next();
					String split[] = key.split(",");
					String sendNodeId = "";
					if (!(split[0].equals(Shared.myInfo.getId().toString()))) {
						sendNodeId = split[0];

					} else {
						sendNodeId = split[1];

					}
					logger.debug("Sending key request to node: " + sendNodeId);
					// Calling the information from Shared List of all Node Info
					Node sendNode = getNodeInfo(sendNodeId);

					if (!sendNodeId.equals("")) {
						Message rNode = new Message();
						rNode.setKey(key);
						rNode.setNodeId(Shared.myInfo.getId());
						rNode.setTimeStamp(Shared.requestTimeStamp); // Check if
																		// it is
																		// correct
						rNode.setType("REQUEST");
						Gson gson = new Gson();
						// TODO: check if deserialization is correctly done or
						// not.
						String message = gson.toJson(rNode);
						sendKeyRequest(message, sendNode);
					}
				}// WHILE all request is sent

				// TODO: Check if we need to update TS here ?
				Shared.logicalClockTimeStamp++;
				logger.debug("Updated Time Stamp: "
						+ Shared.logicalClockTimeStamp);
			} // Else block ends

			logger.debug("Blocked on pending key requests ....");

		}// Synchronized block ENDS
		
		while (true) {
			// Shared.objForLock.wait();
			synchronized (Shared.objForLock) {
				if(Shared.haveNotKeys.size() <= 1){
					logger.debug("Done waiting for keys... executing CS");
					criticalSection();
					return;
				}
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}// While for spurious wait

	}

	public void criticalSection() {
		// read the file & increment it by 1 . This will be used for testing the
		// mutual exclusion.
		File csfile = new File("csFile.txt");
		try {
			Scanner scanner = new Scanner(csfile);
			String valueStr = scanner.nextLine();
			scanner.close();

			Integer value = Integer.parseInt(valueStr);
			logger.debug("Read value: " + value);

			value++;
			String valuePlus = value.toString();
			BufferedWriter br = new BufferedWriter(new FileWriter(new File(
					"csFile.txt")));
			br.write(valuePlus);
			br.write("\n");
			br.close();
			logger.debug("Successfully wrote value: " + value);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		Double csDuration = Shared.durationOfCS.sample();
		
		logger.info("Simulating a delay of " + csDuration + " for CS Duration");
		
		try {
			Thread.sleep(csDuration.longValue());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public void csLeave() {

		int requestCounter = 0;
		logger.debug("In CS Leave. Setting isInCS = false. isRequestedCS= false");

		// Lock the Queue so that no entry is added further.
		synchronized (Shared.objForLock) {

			Shared.isInCS = false;
			Shared.isRequestedCS = false;
			logger.debug("Processing Queue of size "
					+ Shared.bufferingQueue.size() + " in CS Leave");
			logger.debug("Current TS is: " + Shared.logicalClockTimeStamp);
			/*
			 * Process the entire buffered Queue and fulfill the requests by
			 * sending keys
			 */
			while (!Shared.bufferingQueue.isEmpty()) {
				requestCounter++;
				Message request = Shared.bufferingQueue.poll();
				fulfillReq(request);
			}
			logger.debug("Sent " + requestCounter + " requests.");

		}// Synchronized block ENDS

	}

	// TODO: Refactor these methods into another class as they are duplicated
	private Node getNodeInfo(String sendNodeId) {
		Integer id = Integer.parseInt(sendNodeId);
		Node n = Shared.nodeInfos.get(id);
		logger.debug("Got node with ID: " + n.getId() + "host:" + n.getHost()
				+ "port:" + n.getPort());
		return n;
	}

	/*
	 * This will fulfill the buffered request
	 */
	private void fulfillReq(Message request) {
		
		Integer toNode = request.getNodeId();
		logger.debug("Fulfilling Requests");
		Shared.logicalClockTimeStamp++;
		logger.debug("Service 176 - Updating Timestamp to:"
				+ Shared.logicalClockTimeStamp);
		Message response = new Message();
		response.setType("RESPONSE");
		response.setTimeStamp(Shared.logicalClockTimeStamp);
		response.setNodeId(Shared.myInfo.getId());
		response.setKey(request.getKey());
		Gson gson = new Gson();
		String message1 = gson.toJson(response);
		giveUpKey(response, toNode, message1);
	}

	public void sendKeyRequest(String message, Node sendNode) {
		String hostName = sendNode.getHost();
		Integer port = Integer.parseInt(sendNode.getPort());
		try {
			logger.debug("sending request to host: " + hostName);
			Socket clientSocket = new Socket(hostName, port);
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			writer.println(message);
			writer.close();
			clientSocket.close();
		} catch (IOException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}

	}
	
	public void giveUpKey(Message toSend, Integer toNodeID, String message) {

		synchronized (Shared.objForLock) {
			// comparing and adding smaller with bigger
			// String concatKey = "";

			/*
			 * TODO: Dont send the message toSend here. Instead only send
			 * toSend.getNodeID() as Integer. It does not matter what order it
			 * is in since it will always return the same "smaller,bigger" key.
			 */
			String concatKey = concatRequestKey(toSend, toNodeID);

			logger.debug("Giving up key: " + concatKey);

			if (Shared.haveKeys.contains(concatKey)) {
				Shared.haveKeys.remove(concatKey);
				Shared.haveNotKeys.add(concatKey);
			}

			// TCP Connection
			// Integer receiverID = toNode
			Node receiver = Shared.nodeInfos.get(toNodeID);
			String hostName = receiver.getHost();
			Integer port = Integer.parseInt(receiver.getPort());
			try {
				logger.debug("sending request to host: " + hostName);
				Socket clientSocket = new Socket(hostName, port);
				PrintWriter writer = new PrintWriter(
						clientSocket.getOutputStream());
				logger.debug(message);
				writer.println(message);
				writer.close();
				clientSocket.close();
			} catch (IOException ex) {
				logger.error(ex.getMessage());
				ex.printStackTrace();
			}

		}// Syncronized block ends

	}
	
	private String concatRequestKey(Message toSend, Integer toNodeID) {
		String concatKey;
		if (toSend.getNodeId() > toNodeID) {
			concatKey = toNodeID.toString() + ","
					+ toSend.getNodeId().toString();
		} else {
			concatKey = toSend.getNodeId().toString() + ","
					+ toNodeID.toString();
		}
		return concatKey;
	}

}
