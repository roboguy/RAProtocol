package edu.utdallas.aos.p2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import edu.utdallas.aos.p2.config.Node;

public class Shared {
	public static volatile Node myInfo;
	public static volatile boolean isInCS;
	public static volatile boolean hasRequestedCS;

	public static volatile boolean isRequestedCS; // if I have requested for CS
	public static volatile HashMap<Integer, String> haveKeys = new HashMap<Integer, String>();
	public static volatile HashMap<Integer, String> haveNotKeys = new HashMap<Integer, String>();
	public static volatile Queue<Request> bufferingQueue = new LinkedList<Request>();
	// Implement Logical CLock Timestamp
	public static volatile int logicalClockTimeStamp = 0;
	// Request timestamp - kaunsa time par request hui thi.
	public static volatile int requestTimeStamp = 0;

}
