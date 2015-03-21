package edu.utdallas.aos.p2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server extends Thread {

	private static Server server = null;

	private Server() {

	}

	public static Server getInstance() {
		if (server == null) {
			server = new Server();
			server.setName("Server");
		}
		return server;
	}

	@Override
	public void run() {
		Logger logger = LogManager.getLogger(Server.class);
		logger.debug("Starting TCP Server to listen for CS Requests");
		
		/*
		 * TCP Server code goes here.
		 */
		
	}

}
