package pl.edu.agh.cs.kraksim.routing.prediction;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class TrafficStatisticsForResult {
	private static final Logger logger = Logger.getLogger(TrafficStatisticsForResult.class);
	
	private Map<String, Double> levelOccurances;
	
	public TrafficStatisticsForResult(){
		this.levelOccurances = new HashMap<String, Double>();
	}
	
	public void incrementCounterForLevel(TrafficLevel level){
		if (this.levelOccurances.containsKey(level.toString())){
			Double counter = this.levelOccurances.get(level.toString());
			this.levelOccurances.remove(level.toString());
			this.levelOccurances.put(level.toString(), counter + 1);
		}else{
			this.levelOccurances.put(level.toString(), new Double (1));
		}
	}
	
	public double getCounterForLevel (TrafficLevel level){
		Double counter = this.levelOccurances.get(level.toString());
		if (counter == null){
			return 0;
		}else return counter.doubleValue();
	}
	
	public double getProbabilityForLevel (TrafficLevel level){
		double levelCount = this.getCounterForLevel(level);
		int sum = 0;
		for (Double value:this.levelOccurances.values()){
			sum += value;
		}
		return levelCount/sum;
	}
	
	public String getNameOfMostFrequentLevel (){
		String result = null;
		double maxOccurences = -1;
		for (String name : this.levelOccurances.keySet()){
			double temp = this.levelOccurances.get(name);
			if (temp > maxOccurences){
				result = name;
				maxOccurences = temp;
			}
		}
		return result;
	}
	
	/**
	 * Performs the ageing process on traffic levels
	 * @param ageingRate rate with ageing shall happen of range (0, 1]
	 */
	public void ageResults (double ageingRate){
		if(ageingRate == 1.0) return;
		for (String name : this.levelOccurances.keySet()){
			Double temp = this.levelOccurances.get(name);
			temp *= ageingRate;
			this.levelOccurances.put(name, temp);
		}
	}
	
}
