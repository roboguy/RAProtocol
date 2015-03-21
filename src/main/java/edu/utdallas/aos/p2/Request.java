package edu.utdallas.aos.p2;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Request {

	@Expose
	private String type;
	@Expose
	private String key;
	@Expose
	private String timeStamp;
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
	public String getTimeStamp() {
		return timeStamp;
	}

	/**
	 * 
	 * @param timeStamp
	 *            The timeStamp
	 */
	public void setTimeStamp(String timeStamp) {
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