package edu.utdallas.aos.p2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server extends Thread {

	private static Server server = null;
	public static volatile Boolean isRunning = true;
	public static ServerSocket serverSock = null;
	private static Integer port = 100;
	private static Logger logger = null;
	
	public void setPort(Integer por) {
		port = por;
	}

	private Server() {

	}

	public static Server getInstance() {
		logger = LogManager.getLogger(Server.class);
		
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
		go();
		
	}
	
	
	public static void go()
	{
		try
		{
			//Create a server socket at port 5000
			serverSock = new ServerSocket(Server.port);
			logger.info("Server listening on port:" + Server.port);
			//Server goes into a permanent loop accepting connections from clients			
			while(isRunning)
			{
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				Socket sock = serverSock.accept();
				BufferedReader inFromClient =
			               new BufferedReader(new InputStreamReader(sock.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = inFromClient.readLine();
				while(line != null){
					sb.append(line);
					line = inFromClient.readLine();
				}
				RequestHandler handler = new RequestHandler(sb.toString());
				handler.start();
			}

		}
		catch(IOException ex)
		{
			if(isRunning == true){
				ex.printStackTrace();
				logger.error(ex.getMessage());
			}
			else {
				logger.info("Server Shut Down");
			}

		}
	}

}
