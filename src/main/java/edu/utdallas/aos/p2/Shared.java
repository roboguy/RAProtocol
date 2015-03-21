package edu.utdallas.aos.p2;

import edu.utdallas.aos.p2.config.Node;

public class Shared {

	public static volatile Node myInfo;
	public static volatile boolean isInCS;
	public static volatile boolean hasRequestedCS;

}
