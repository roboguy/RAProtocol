package edu.utdallas.aos.p2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application extends Thread {
	
	private static Application application = null;
	private static Integer numberOfRequests = 1;
	
	public static void setNumberOfRequests(Integer numberOfRequests) {
		Application.numberOfRequests = numberOfRequests;
	}

	private Application(){
		
	}
	
	public static Application getInstance(){
		if (application == null) {
			application = new Application();
			application.setName("Application");
		}
		return application;
	}
	
	@Override
	public void run() {
		Logger logger = LogManager.getLogger(Application.class);
		Service service = new Service();
		
		for(int reqCount = 1; reqCount <= numberOfRequests; reqCount++){
			logger.debug("Making CS Enter Request. Request Count: " + reqCount);
			service.csEnter();
			logger.debug("Leaving CS for Request Count: " + reqCount);
			service.csLeave();
		}
	}

}
