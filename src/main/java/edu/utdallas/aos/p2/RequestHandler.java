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
		 * Fixed error in request handler. Request request =
		 * gson.fromJson(message, Request.class); gson.fromJson will return the
		 * initialized Request object from the message.
		 * 
		 * Earlier it was gson.fromJson(message, Request.class) Request request
		 * = new Request(); This will not work as the request object will be
		 * empty.
		 */

		Message request = gson.fromJson(message, Message.class);
		//logger.debug("Received a request from " + request.getNodeId());

		logger.debug("53-Updated Timestamp to: " + Shared.logicalClockTimeStamp);

		synchronized (Shared.objForLock) {
			
			if (request.getType().equals("REQUEST")) {
				logger.debug("Received a request from " + request.getNodeId());
				// Latch to BLOCK CS ENTER
				// synchronized (Shared.objForLock) {
				/*
				 * check whether the node is in Critical Section if it is in CS
				 * Buffer the request for later processing.
				 */
				if (Shared.isInCS) {

					// Set the value in Queue.
					// 1.a If Yes -> Buffer the request in queue (Object of
					// Request Class)
					// logger.debug("Buffering Request with timestamp" +
					// request.getTimeStamp() + " from node" +
					// request.getNodeId());
					Shared.logicalClockTimeStamp = Math.max(
							request.getTimeStamp(),
							Shared.logicalClockTimeStamp) + 1;
					Shared.bufferingQueue.add(request);
				}

				// 2. Else -> Check the Timestamp -> request.getTimeStamp()
				/*
				 * Else we are not inCS
				 */
				else {
					/*
					 * Check if we have a pending CS request If we have a
					 * pending CS request then check received request's
					 * timestamp
					 */
					if (Shared.isRequestedCS) {

						// If(request.getTimestamp > Shared.myRequestTS)
						// Add it to the buffer queue, this means we have higher
						// priority to execute CS.
						logger.debug("Requested Timestamp: "
								+ request.getTimeStamp() + " Logical TS: "
								+ Shared.logicalClockTimeStamp);
						// Chaged to Shared.requestedTimeStamp
						if (request.getTimeStamp() > Shared.requestTimeStamp) {

							// Do max of timestamps + 1
							Shared.logicalClockTimeStamp = Math.max(
									request.getTimeStamp(),
									Shared.logicalClockTimeStamp) + 1;
							logger.debug("Buffering Request with timestamp: "
									+ request.getTimeStamp() + " from node: "
									+ request.getNodeId());
							Shared.bufferingQueue.add(request);
							
							// Release lock for cs enter to proceed.
//							if(Shared.haveNotKeys.isEmpty()){
//								Shared.wasSignalled = true;
//								logger.debug("Notifying any waiting threads that all keys received.");
//								Shared.objForLock.notify();
//							}
							
							/*
							 * Else we do not have a higher priority to execute
							 * CS means we have to give up the key, but we have
							 * a pending request. so give up the key but put
							 * RequestedCS as Timestamp. Update our timestamp
							 * for this send event.
							 */

							// RUCHIR - Again send the request with old
							// timestamp to enter

						} else if(request.getTimeStamp() < Shared.requestTimeStamp){
							logger.debug("Requst timestamp is less than my timestamp.");
							// Some bug here.
							Message response = new Message();
							response.setType("RESPONSE");
							response.setKey(request.getKey());
							logger.debug("Before update: "
									+ Shared.logicalClockTimeStamp);

							// Timestamp++ not max because our timestamp is
							// already bigger
							Shared.logicalClockTimeStamp++;
							logger.debug("105 - Updated Timestamp to: "
									+ Shared.logicalClockTimeStamp
									+ " reflect send event.");

							// change to requestedCS Timestamp [DONE]
							response.setTimeStamp(Shared.logicalClockTimeStamp);
							response.setNodeId(Shared.myInfo.getId());
							Gson gson1 = new Gson();
							this.message = gson1.toJson(response);

							/*
							 * we are sending a response with our node id and
							 * key to the node we received a request for key
							 * from.
							 */
							giveUpKey(response, request.getNodeId());

							Message requestCSAgain = new Message();
							requestCSAgain.setKey(request.getKey());
							requestCSAgain.setNodeId(Shared.myInfo.getId());
							requestCSAgain
									.setTimeStamp(Shared.requestTimeStamp);
							requestCSAgain.setType("REQUEST");
							String requestString = gson1.toJson(requestCSAgain);
							Node sendNode = getNodeInfo(request.getNodeId()
									.toString());
							sendKeyRequest(requestString, sendNode);

						}
						//Start 
						else if(request.getTimeStamp() == Shared.requestTimeStamp){
							if(request.getNodeId() > Shared.myInfo.getId()){
								// Do max of timestamps + 1
								Shared.logicalClockTimeStamp = Math.max(
										request.getTimeStamp(),
										Shared.logicalClockTimeStamp) + 1;
								logger.debug("Buffering Request with timestamp: "
										+ request.getTimeStamp() + " from node: "
										+ request.getNodeId());
								Shared.bufferingQueue.add(request);
								
								// Release lock for cs enter to proceed.
//								if(Shared.haveNotKeys.isEmpty()){
//									Shared.wasSignalled = true;
//									logger.debug("Notifying any waiting threads that all keys received.");
//									Shared.objForLock.notify();
//								}
							}
							else{
								Message response = new Message();
								response.setType("RESPONSE");
								response.setKey(request.getKey());
								logger.debug("Before update: "
										+ Shared.logicalClockTimeStamp);

								// Timestamp++ not max because our timestamp is
								// already bigger
								Shared.logicalClockTimeStamp++;
								logger.debug("105 - Updated Timestamp to: "
										+ Shared.logicalClockTimeStamp
										+ " reflect send event.");

								// change to requestedCS Timestamp [DONE]
								response.setTimeStamp(Shared.logicalClockTimeStamp);
								response.setNodeId(Shared.myInfo.getId());
								Gson gson1 = new Gson();
								this.message = gson1.toJson(response);

								/*
								 * we are sending a response with our node id and
								 * key to the node we received a request for key
								 * from.
								 */
								giveUpKey(response, request.getNodeId());

								Message requestCSAgain = new Message();
								requestCSAgain.setKey(request.getKey());
								requestCSAgain.setNodeId(Shared.myInfo.getId());
								requestCSAgain
										.setTimeStamp(Shared.requestTimeStamp);
								requestCSAgain.setType("REQUEST");
								String requestString = gson1.toJson(requestCSAgain);
								Node sendNode = getNodeInfo(request.getNodeId()
										.toString());
								sendKeyRequest(requestString, sendNode);
							}
						}
						//END 

					} // IF REQUESTED FOR CS ENDS

					/*
					 * else NOT REQUSTED FOR CS & not in CS means we have to
					 * give up the key and update the have and have not sets. we
					 * update our timestamp to mark the send event.
					 */
					else {
						Shared.logicalClockTimeStamp = Math.max(
								request.getTimeStamp(),
								Shared.logicalClockTimeStamp) + 1;
						Message response = new Message();
						response.setType("RESPONSE");
						response.setKey(request.getKey());
						Shared.logicalClockTimeStamp++;
						logger.debug("Updated TimeStamp to: "
								+ Shared.logicalClockTimeStamp);
						// change this to our logical timestamp [DONE]
						response.setTimeStamp(Shared.logicalClockTimeStamp);
						response.setNodeId(Shared.myInfo.getId());

						Gson gson2 = new Gson();
						this.message = gson2.toJson(response);
						giveUpKey(response, request.getNodeId());
						// Make another request
					}
				}
			}

			// Update have & have not set
			// Check Have not set is empty
			// If it is empty then release Latch
			else if (request.getType().equals("RESPONSE")) {
				logger.debug("Response from " + request.getNodeId());
				Shared.logicalClockTimeStamp = Math.max(request.getTimeStamp(),
						Shared.logicalClockTimeStamp) + 1;
				addKey(request);
			}

		}//Synchronized block ENDS

	} // Message method ends

	/*
	 * Update have & have not set Check Have not set is empty
	 */
	private void addKey(Message response) {
		synchronized (Shared.objForLock) {
			// String concatKey = "";
			String concatKey = concatResponseKey(response,
					Shared.myInfo.getId());
			if (Shared.haveNotKeys.contains(concatKey)) {
				Shared.haveNotKeys.remove(concatKey);
				Shared.haveKeys.add(concatKey);
			}
			
			for(String haveKey:Shared.haveKeys){
				logger.debug("Key Have: " + haveKey);
			}
			
			for(String notHaveKey:Shared.haveNotKeys){
				logger.debug("Key Not With me: " + notHaveKey);
			}
			logger.debug("Before Notify. Timestamp is: " + Shared.logicalClockTimeStamp);
			// If all keys are with us then notify waiting threads
//			if (Shared.haveNotKeys.isEmpty()) {
//				Shared.wasSignalled = true;
//				logger.debug("Added Key, now notifying waiting theads that all keys received.");
//				Shared.objForLock.notify();
//			}
			

			
		}// Synchronized block ends
	}

	/*
	 * Giving up the key and updating HashSets toSend - Message to send. Since
	 * we are sending the message it will have our nodeID toNode - is node id of
	 * the node we need to message
	 */

	public void giveUpKey(Message toSend, Integer toNodeID) {

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

	// TODO: Refactor redundant methods concatResponseKey & concatRequestKey
	private String concatResponseKey(Message response, Integer fromNodeID) {
		String concatKey;
		if (Shared.myInfo.getId() > response.getNodeId()) {
			concatKey = response.getNodeId().toString() + ","
					+ Shared.myInfo.getId().toString();
		} else {
			concatKey = Shared.myInfo.getId().toString() + ","
					+ response.getNodeId().toString();
		}
		return concatKey;
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

	private Node getNodeInfo(String sendNodeId) {
		Integer id = Integer.parseInt(sendNodeId);
		Node n = Shared.nodeInfos.get(id);
		logger.debug("Got node with ID: " + n.getId() + "host:" + n.getHost()
				+ "port:" + n.getPort());
		return n;
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
}