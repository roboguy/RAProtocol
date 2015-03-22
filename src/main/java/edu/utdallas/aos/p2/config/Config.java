package edu.utdallas.aos.p2.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Config {

	@Expose
	private Integer N;
	@Expose
	private List<Node> nodes = new ArrayList<Node>();
	@Expose
	private Params params;
	@SerializedName("total_Number_Of_Requests")
	@Expose
	private Integer totalNumberOfRequests;
	@SerializedName("mean_Consecutive_Request_Delay")
	@Expose
	private Integer meanConsecutiveRequestDelay;
	@SerializedName("mean_Duration_Of_CS")
	@Expose
	private Integer meanDurationOfCS;

	/**
	 * 
	 * @return The N
	 */
	public Integer getN() {
		return N;
	}

	/**
	 * 
	 * @param N
	 *            The N
	 */
	public void setN(Integer N) {
		this.N = N;
	}

	/**
	 * 
	 * @return The nodes
	 */
	public List<Node> getNodes() {
		return nodes;
	}

	/**
	 * 
	 * @param nodes
	 *            The nodes
	 */
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	/**
	 * 
	 * @return The params
	 */
	public Params getParams() {
		return params;
	}

	/**
	 * 
	 * @param params
	 *            The params
	 */
	public void setParams(Params params) {
		this.params = params;
	}

	/**
	 * 
	 * @return The totalNumberOfRequests
	 */
	public Integer getTotalNumberOfRequests() {
		return totalNumberOfRequests;
	}

	/**
	 * 
	 * @param totalNumberOfRequests
	 *            The total_Number_Of_Requests
	 */
	public void setTotalNumberOfRequests(Integer totalNumberOfRequests) {
		this.totalNumberOfRequests = totalNumberOfRequests;
	}

	/**
	 * 
	 * @return The meanConsecutiveRequestDelay
	 */
	public Integer getMeanConsecutiveRequestDelay() {
		return meanConsecutiveRequestDelay;
	}

	/**
	 * 
	 * @param meanConsecutiveRequestDelay
	 *            The mean_Consecutive_Request_Delay
	 */
	public void setMeanConsecutiveRequestDelay(
			Integer meanConsecutiveRequestDelay) {
		this.meanConsecutiveRequestDelay = meanConsecutiveRequestDelay;
	}

	/**
	 * 
	 * @return The meanDurationOfCS
	 */
	public Integer getMeanDurationOfCS() {
		return meanDurationOfCS;
	}

	/**
	 * 
	 * @param meanDurationOfCS
	 *            The mean_Duration_Of_CS
	 */
	public void setMeanDurationOfCS(Integer meanDurationOfCS) {
		this.meanDurationOfCS = meanDurationOfCS;
	}

}