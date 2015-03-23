package edu.utdallas.aos.p2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

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
			// check whether the node is in Critical Section
			if (Shared.isInCS) {
				synchronized (Shared.objForLock) {
					// Set the value in Queue.
					// 1.a If Yes -> Buffer the request in queue (Object of
					// Request Class)
					Shared.bufferingQueue.add(request);
				}

			}
			// 2. Else -> Check the Timestamp -> request.getTimeStamp()
			else {
				// check my requested timestamp with the request.getTImestamp()
				if (Shared.isRequestedCS) {
					// If(request.getTimestamp > Shared.myRequestTS)
					// Add it to the buffer queue
					// Latch release and free to enter CS
					if (request.getTimeStamp() >= Shared.logicalClockTimeStamp) {
						synchronized (Shared.objForLock) {
							Shared.bufferingQueue.add(request);
						}
					} else {
						giveUpKey(request);

					}

				}
				// else
				// Give up the key and update have and have not sets.
				// RUCHIR - Again send the request with old timestamp to enter
				// CS
				else {
					giveUpKey(request);

				}
			}

		}
		// Update have & have not set
		// Check Have not set is empty
		// If it is empty then release Latch
		else if (request.getType().equals("RESPONSE")) {
			addKeys(request);
		}
	}

	/*
	 * //Update have & have not set // Check Have not set is empty //If it is
	 * empty then release Latch
	 */
	private void addKeys(Request request) {
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
			if (Shared.haveKeys.contains(concatKey)) {
				Shared.haveKeys.remove(concatKey);
				Shared.haveNotKeys.add(concatKey);
			}
			// TODO: Key has not been sent to Other Nodes(TCP)

		}
	}
}
