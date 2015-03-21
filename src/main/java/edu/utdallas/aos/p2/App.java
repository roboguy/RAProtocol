package edu.utdallas.aos.p2;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {

	public static void main(String[] args) {
		System.out.println("Hello World");
		Logger logger = LogManager.getLogger(App.class);
		logger.debug("Hello World");
		
	}

}
