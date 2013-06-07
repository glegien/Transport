package pl.edu.agh.cs.kraksim.routing.prediction;

import pl.edu.agh.cs.kraksim.core.City;

public interface ITrafficPredictionSetup {
	public int getNumberOfInfluencedTimesteps();
	public void setNumberOfInfluencedTimesteps(int numberOfInfluencedTimesteps);
	public City getCity();
	public void setCity(City city);
	public int getNumberOfInfluencedLinks();
	public void setNumberOfInfluencedLinks(int numberOfInfluencedLinks);
	public TrafficLevelDiscretiser getDiscretiser();
	public void setDiscretiser(TrafficLevelDiscretiser discretiser);
	public double getCutOutProbability();
	public void setCutOutProbability(double cutOutProbability);
	public int getCutOutMinimumCounter();
	public void setCutOutMinimumCounter(int cutOutMinimumCounter);
	public void setAgeingRate(double ageingRate);
	public double getAgeingRate();
}
