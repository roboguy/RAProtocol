package edu.utdallas.aos.p2;

import com.google.gson.annotations.Expose;

public class Message {

	@Expose
	private String type;
	@Expose
	private String key;
	@Expose
	private Integer timeStamp;
	@Expose
	private Integer nodeId;

	/**
	 * 
	 * @return The type
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 *            The type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return The key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 
	 * @param key
	 *            The key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 
	 * @return The timeStamp
	 */
	public Integer getTimeStamp() {
		return timeStamp;
	}

	/**
	 * 
	 * @param timeStamp
	 *            The timeStamp
	 */
	public void setTimeStamp(Integer timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * 
	 * @return The nodeId
	 */
	public Integer getNodeId() {
		return nodeId;
	}

	/**
	 * 
	 * @param nodeId
	 *            The nodeId
	 */
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

}