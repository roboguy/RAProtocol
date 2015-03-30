package edu.utdallas.aos.p2;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import edu.utdallas.aos.p2.config.Node;

public class Shared {
	public static volatile Node myInfo = new Node();
	public static volatile ConcurrentHashMap<Integer, Node> nodeInfos = new ConcurrentHashMap<>();
	public static volatile boolean wasSignalled = false;
	public static volatile boolean isInCS = false;
	//TODO: change isRequestedCS to false
	public static volatile boolean isRequestedCS = false; // if I have requested
															// for CS
	public static volatile Set<String> haveKeys = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());	
	public static volatile Set<String> haveNotKeys = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	public static volatile Queue<Message> bufferingQueue = new LinkedList<Message>();
	// Implement Logical CLock Timestamp
	public static volatile int logicalClockTimeStamp = 0;
	// Request timestamp - kaunsa time par request hui thi.
	public static volatile int requestTimeStamp = 0;
	public static volatile Object objForLock = new Object();
	
	public static volatile ExponentialDistribution requestDelay;
	public static volatile ExponentialDistribution durationOfCS;
	
}

