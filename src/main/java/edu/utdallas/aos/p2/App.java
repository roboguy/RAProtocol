package edu.utdallas.aos.p2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import edu.utdallas.aos.p2.config.Config;
import edu.utdallas.aos.p2.config.Node;


/*
 * Entry point into the project. 
 * This class reads the configuration file and initializes the node with given node ID.
 * Starts a TCP server to listen for incoming requests. 
 */
public class App {
	
	
	public static void main(String[] args) {
		//System.out.println("Hello World");
		
		if (args.length != 1) {
			System.err.println("Usage: Project2 <node_ID>");
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

		Shared.myInfo = conf.getNodes().get(nodeID);
		//String myHost = Shared.myInfo.getHost();
		String port = Shared.myInfo.getPort();
		
		/*
		 * Initialize nodeInfos hasmap
		 */
		for(Node node : conf.getNodes()){
			Integer id = node.getId();
			Shared.nodeInfos.put(id, node);
		}
		
		Integer portNum = Integer.parseInt(port);
		Server server = Server.getInstance();
		server.setPort(portNum);
		
		/*
		 * Initialize keys hashSet
		 */
		try {
			initKeys(nodeID, conf);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		server.start();
		
		//Waiting 3 seconds for server to initialize.
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		logger.debug("Starting Application for making CS Requests.");
		Application appln = Application.getInstance();
		Application.setNumberOfRequests(conf.getTotalNumberOfRequests());
		appln.start();
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
		String confJson = sb.toString();
		Config conf = gson.fromJson(confJson, Config.class);
		return conf;
	}
	
	private static void initKeys(Integer nodeID, Config conf) throws FileNotFoundException{
		Scanner scanner = new Scanner(new File("KEYS"));
		for(int rowCount = 0; rowCount < nodeID; rowCount++){
			scanner.nextLine();
		}
		String allocatedKeys = scanner.nextLine().trim();
		scanner.close();
		String[] keys = allocatedKeys.split("\\s+");
		if(keys.length <= 1){
			Integer smaller = Math.min(0, nodeID);
			Integer bigger 	= Math.max(0, nodeID);
			String ownedKey = smaller + "," + bigger;
			Shared.haveKeys.add(ownedKey);
		} else {
			for(int count = 0; count < keys.length; count++){
				if(count == nodeID){
					continue;
				}else{
					String key = keys[count];
					if(key.equals("1")){
						Integer smaller = Math.min(count, nodeID);
						Integer bigger 	= Math.max(count, nodeID);
						String ownedKey = smaller + "," + bigger;
						Shared.haveKeys.add(ownedKey);
					}else if(key.equals("0")){
						Integer smaller = Math.min(count, nodeID);
						Integer bigger 	= Math.max(count, nodeID);
						String notHasKey = smaller + "," + bigger;
						Shared.haveNotKeys.add(notHasKey);
					}
				}
			}//For each key loop ends
		}
		
		
	}

}
