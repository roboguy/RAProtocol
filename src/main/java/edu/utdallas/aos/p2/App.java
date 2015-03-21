package edu.utdallas.aos.p2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import edu.utdallas.aos.p2.config.Config;


/*
 * Entry point into the project. 
 * This class reads the configuration file and initializes the node with given node ID.
 * Starts a TCP server to listen for incoming requests. 
 */
public class App {
	
	
	public static void main(String[] args) {
		//System.out.println("Hello World");
		
		if (args.length != 1) {
			System.err.println("Usage: Project1 <node_ID>");
			//logger.error("Invalid Input Paramters or not enoguh input paramters.");
			System.exit(2);
		}
		
		Integer nodeID = Integer.parseInt(args[0]);

		System.setProperty("logFilename", "app"+ nodeID +".log");
		
		org.apache.logging.log4j.core.LoggerContext ctx =
			    (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		ctx.reconfigure();
		
		Logger logger = LogManager.getLogger(App.class);
		logger.debug("Reading Configs");
		Config conf = readConfig();
		if(conf !=null){
			logger.debug("Configuration Read Successfully");
		}
		
		
		Server server = Server.getInstance();
		server.start();
	}

	private static Config readConfig() {
		//Logger logger = LogManager.getLogger(App.class);
		Gson gson = new Gson();
		StringBuilder sb = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("AOS_P2_CONF.json"));
			while(scanner.hasNext()){
				sb.append(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(scanner != null){
				scanner.close();
			}
		}
		
		Config conf = gson.fromJson(sb.toString(), Config.class);
		return conf;
	}

}
