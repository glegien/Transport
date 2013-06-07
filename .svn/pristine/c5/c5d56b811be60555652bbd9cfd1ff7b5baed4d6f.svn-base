package pl.edu.agh.cs.kraksim.routing.prediction;

import org.apache.log4j.Logger;

public class TrafficLevel {
	private static final Logger logger = Logger.getLogger(TrafficLevel.class);
	
	private double lowerLevel;
	private double upperLevel;
	private String description;
	private double maxInfluence;
	
	// just in case (ok, i know Java has its own List... But i don't want to bind TrafficLevel with Discretiser)
	private TrafficLevel predecessor;	
	private TrafficLevel proceeder;
	
	/**
	 * 
	 */
	public TrafficLevel() {
		super();
	}

	/**
	 * @param lowerLevel the lowerLevel to set
	 * @param upperLevel the upperLevel to set
	 */
	public TrafficLevel(double lowerLevel, double upperLevel) {
		super();
		this.lowerLevel = lowerLevel;
		this.upperLevel = upperLevel;
	}

	/**
	 * @return the lowerLevel
	 */
	public double getLowerLevel() {
		return lowerLevel;
	}
	
	/**
	 * @param lowerLevel the lowerLevel to set
	 */
	public void setLowerLevel(double lowerLevel) {
		this.lowerLevel = lowerLevel;
	}
	
	/**
	 * @return the upperLevel
	 */
	public double getUpperLevel() {
		return upperLevel;
	}
	
	/**
	 * @param upperLevel the upperLevel to set
	 */
	public void setUpperLevel(double upperLevel) {
		this.upperLevel = upperLevel;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
		if (this.description.contains("->")){
			logger.error("Potentially invalid description of level: \"" + this.description + "\" was set");
		}
	}

	/**
	 * @return the predecessor
	 */
	public TrafficLevel getPredecessor() {
		return predecessor;
	}
	
	/**
	 * @param predecessor the predecessor to set
	 */
	public void setPredecessor(TrafficLevel predecessor) {
		this.predecessor = predecessor;
	}
	
	/**
	 * @return the proceeder
	 */
	public TrafficLevel getProceeder() {
		return proceeder;
	}
	
	/**
	 * @param proceeder the proceeder to set
	 */
	public void setProceeder(TrafficLevel proceeder) {
		this.proceeder = proceeder;
	}
	
	@Override
	public String toString (){
		return description;
	}

	/**
	 * @return the maxInfluence
	 */
	public double getMaxInfluence() {
		return maxInfluence;
	}

	/**
	 * @param maxInfluence the maxInfluence to set
	 */
	public void setMaxInfluence(double maxInfluence) {
		this.maxInfluence = maxInfluence;
	}
	
}
