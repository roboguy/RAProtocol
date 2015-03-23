package edu.utdallas.aos.p2;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import edu.utdallas.aos.p2.config.Node;

public class Shared {
	public static volatile Node myInfo;
	public static volatile boolean isInCS=false;

	public static volatile boolean isRequestedCS=false;	// if I have requested for CS
	public static volatile HashSet<String> haveKeys=new HashSet<String>();
	public static volatile HashSet<String> haveNotKeys=new HashSet<String>();
	public static volatile Queue<Request> bufferingQueue=new LinkedList<Request>();
	//Implement Logical CLock Timestamp
	public static volatile int logicalClockTimeStamp=0;
	//Request timestamp - kaunsa time par request hui thi.
	public static volatile int requestTimeStamp=0;
	public static volatile Object objForLock=new Object();
	//synchronized(objForLock)
	
}