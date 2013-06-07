package pl.edu.agh.cs.kraksim.routing.prediction;

import pl.edu.agh.cs.kraksim.core.City;

public class DefaultTrafficPredictionSetup implements ITrafficPredictionSetup {
	private int numberOfInfluencedTimesteps;
	private City city;
	private int numberOfInfluencedLinks;
	private TrafficLevelDiscretiser discretiser;
	private double cutOutProbability;
	private int cutOutMinimumCounter;
	private double ageingRate;
	/**
	 * @return the numberOfInfluencedTimesteps
	 */
	public int getNumberOfInfluencedTimesteps() {
		return numberOfInfluencedTimesteps;
	}
	/**
	 * @param numberOfInfluencedTimesteps the numberOfInfluencedTimesteps to set
	 */
	public void setNumberOfInfluencedTimesteps(int numberOfInfluencedTimesteps) {
		this.numberOfInfluencedTimesteps = numberOfInfluencedTimesteps;
	}
	/**
	 * @return the city
	 */
	public City getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(City city) {
		this.city = city;
	}
	/**
	 * @return the numberOfInfluencedLinks
	 */
	public int getNumberOfInfluencedLinks() {
		return numberOfInfluencedLinks;
	}
	/**
	 * @param numberOfInfluencedLinks the numberOfInfluencedLinks to set
	 */
	public void setNumberOfInfluencedLinks(int numberOfInfluencedLinks) {
		this.numberOfInfluencedLinks = numberOfInfluencedLinks;
	}
	/**
	 * @return the discretiser
	 */
	public TrafficLevelDiscretiser getDiscretiser() {
		return discretiser;
	}
	/**
	 * @param discretiser the discretiser to set
	 */
	public void setDiscretiser(TrafficLevelDiscretiser discretiser) {
		this.discretiser = discretiser;
	}
	/**
	 * @return the cutOutProbability
	 */
	public double getCutOutProbability() {
		return cutOutProbability;
	}
	/**
	 * @param cutOutProbability the cutOutProbability to set
	 */
	public void setCutOutProbability(double cutOutProbability) {
		this.cutOutProbability = cutOutProbability;
	}
	/**
	 * @return the cutOutMinimumCounter
	 */
	public int getCutOutMinimumCounter() {
		return cutOutMinimumCounter;
	}
	/**
	 * @param cutOutMinimumCounter the cutOutMinimumCounter to set
	 */
	public void setCutOutMinimumCounter(int cutOutMinimumCounter) {
		this.cutOutMinimumCounter = cutOutMinimumCounter;
	}
	
	@Override
	public void setAgeingRate(double ageingRate) {
		this.ageingRate = ageingRate;
	}
	
	@Override
	public double getAgeingRate(){
		return this.ageingRate;
	}
	
}
