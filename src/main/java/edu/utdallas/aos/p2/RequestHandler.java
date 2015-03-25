package edu.utdallas.aos.p2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import edu.utdallas.aos.p2.config.Node;

public class RequestHandler extends Thread {

	private Logger logger = LogManager.getLogger(RequestHandler.class);
	String message = "";

	public RequestHandler() {

	}

	public RequestHandler(String message) {
		this.message = message;

	}

	@Override
	public void run() {

		Message();

	}

	public void Message() {
		Gson gson = new Gson();
		
		/*
		 * Fixed error in request handler.
		 *  Request request = gson.fromJson(message, Request.class);
		 *  gson.fromJson will return the initialized Request object from the message.
		 *  
		 *  Earlier it was 
		 *  	gson.fromJson(message, Request.class)
		 *  Request request = new Request();
		 *  This will not work as the request object will be empty. 
		 */
		
		Request request = gson.fromJson(message, Request.class);
		logger.debug("Received a request from " + request.getNodeId());
		// Update my latest timestamp.
		Shared.logicalClockTimeStamp = Math.max(request.getTimeStamp(),
				Shared.logicalClockTimeStamp) + 1;
		if (request.getType().equals("REQUEST")) {
			// Latch to BLOCK CS ENTER
			
			/* check whether the node is in Critical Section if it is in 
			 * CS Buffer the request for later processing.
			 */
			if (Shared.isInCS) {
				synchronized (Shared.objForLock) {
					// Set the value in Queue.
					// 1.a If Yes -> Buffer the request in queue (Object of
					// Request Class)
					Shared.bufferingQueue.add(request);
				}

			}
			
			// 2. Else -> Check the Timestamp -> request.getTimeStamp()
			/*
			 * Else we are not inCS
			 */
			else {
				/*
				 * Check if we have a pending CS request
				 * If we have a pending CS request then check received request's timestamp
				 */
				if (Shared.isRequestedCS) {
					
					// If(request.getTimestamp > Shared.myRequestTS)
					// Add it to the buffer queue, this means we have higher priority to execute CS.
					if (request.getTimeStamp() >= Shared.logicalClockTimeStamp) {
						synchronized (Shared.objForLock) {
							Shared.bufferingQueue.add(request);
						}
						/*
						 * Else we do not have a higher priority to execute CS
						 * means we have to give up the key, but we have a pending request.
						 * so give up the key but put RequestedCS as Timestamp.
						 * Update our timestamp for this send event.
						 */
						// RUCHIR - Again send the request with old timestamp to enter
					} else {
						//Some bug here.
						Request sendResponse=new Request();
						sendResponse.setType("RESPONSE");
						sendResponse.setKey(request.getKey());
						Shared.logicalClockTimeStamp++;
						
						//TODO:change to requestedCS Timestamp.
						sendResponse.setTimeStamp(Shared.logicalClockTimeStamp);
						sendResponse.setNodeId(Shared.myInfo.getId());
						
						giveUpKey(sendResponse);

					}

				} //IF REQUESTED FOR CS ENDS
				
				/* else NOT REQUSTED FOR CS & not in CS
				 * means we have to give up the key and update have and have not sets.
				 * we update our timestamp to mark the send event.
				 */
				else {
					Request sendResponse=new Request();
					sendResponse.setType("RESPONSE");
					sendResponse.setKey(request.getKey());
					Shared.logicalClockTimeStamp++;
					
					//TODO: change this to our logical timestamp
					sendResponse.setTimeStamp(Shared.requestTimeStamp);
					sendResponse.setNodeId(Shared.myInfo.getId());
					
					giveUpKey(request);

				}
			}

		}
		// Update have & have not set
		// Check Have not set is empty
		// If it is empty then release Latch
		else if (request.getType().equals("RESPONSE")) {
			addKey(request);
		}
	}

	/*
	 * Update have & have not set 
	 * Check Have not set is empty
	 */
	private void addKey(Request request) {
		synchronized (Shared.objForLock) {
			String concatKey = "";
			if (Shared.myInfo.getId() > request.getNodeId()) {
				concatKey = request.getNodeId().toString() + ","
						+ Shared.myInfo.getId().toString();
			} else {
				concatKey = Shared.myInfo.getId().toString() + ","
						+ request.getNodeId().toString();
			}
			if (Shared.haveNotKeys.contains(concatKey)) {
				Shared.haveNotKeys.remove(concatKey);
				Shared.haveKeys.add(concatKey);
			}
		}
	}

	/*
	 * Giving up the key and updating HashSet
	 */
	public void giveUpKey(Request request) {
		
		synchronized (Shared.objForLock) {
			// comparing and adding smaller with bigger
			String concatKey = "";
			if (Shared.myInfo.getId() > request.getNodeId()) {
				concatKey = request.getNodeId().toString() + ","
						+ Shared.myInfo.getId().toString();
			} else {
				concatKey = Shared.myInfo.getId().toString() + ","
						+ request.getNodeId().toString();
			}
			logger.debug("Giving up key: " + concatKey);
			if (Shared.haveKeys.contains(concatKey)) {
				Shared.haveKeys.remove(concatKey);
				Shared.haveNotKeys.add(concatKey);
			}
			// TODO: Key has not been sent to Other Nodes(TCP)
			Integer receiverID = request.getNodeId();
			Node receiver = Shared.nodeInfos.get(receiverID);
			String hostName = receiver.getHost();
			Integer port = Integer.parseInt(receiver.getPort()); 	
			try
			{
				logger.debug("sending request to host: " + hostName);
				Socket clientSocket = new Socket(hostName,port);
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				writer.println(message);
				writer.close();
				clientSocket.close();
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}

		}//Syncronized block ends
		
	}
}
