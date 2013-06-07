package pl.edu.agh.cs.kraksim.routing.prediction;

import java.util.ArrayList;
import java.util.List;

public class TrafficLevelDiscretiser {
	private List<TrafficLevel> levels;
	private Double[] defaultsForColumns;
	
	private static final double DEFAULT_LOW_U_LIMIT = 1.20;
	private static final double DEFAULT_MED_U_LIMIT = 1.60;
	
	/**
	 * 
	 */
	public TrafficLevelDiscretiser() {
		super();
		this.levels = new ArrayList<TrafficLevel>();
	}
	
	/**
	 * @param value value to be discretised
	 * @return discrete value - in percents
	 * @throws TrafficPredictionException if the value does not lay in any of discretisation levels
	 */
	private TrafficLevel getLevelForValue(double value) throws TrafficPredictionException{
		for (TrafficLevel level: this.levels){
			if ((value >= level.getLowerLevel()) && (value < level.getUpperLevel())){
				return level;
			}
		}
		throw new TrafficPredictionException("Discretisation definition is incomplete: no value for " + value);
	}
	
	public void addTrafficLevel (TrafficLevel newLevel)throws TrafficPredictionException {
		if (newLevel == null){
			throw new TrafficPredictionException("Unable to set null level");
		}
		// testing if there is a level by the given name
		TrafficLevel temp = null;
		try{
			temp = this.getLevelByName(newLevel.getDescription());
		}catch (TrafficPredictionException e){
			// if an exception is caught then there is no such named level 
		}
		if (temp != null){
			// if there is one - exception
			throw new TrafficPredictionException("Level by the name " + newLevel.getDescription() + " already exists");
		}
		// checking if no ranges are covering
		double loA = newLevel.getLowerLevel();
		double hiA = newLevel.getUpperLevel();
		for (TrafficLevel myLevel: this.levels){
			double loB = myLevel.getLowerLevel();
			double hiB = myLevel.getUpperLevel();
			if ( (loA >= loB) && (loA < hiB) ){
				throw new TrafficPredictionException("Levels " + newLevel + " is in conflict with " + myLevel);
			}
			if ( (loB >= loA) && (loB < hiA) ){
				throw new TrafficPredictionException("Levels " + newLevel + " is in conflict with " + myLevel);
			}
		}
		this.levels.add(newLevel);
	}
	
	/**
	 * This method fills the list of levels with defaults
	 */
	public void populateTrafficLevels() {
		TrafficLevel lowLevel = new TrafficLevel (Double.MIN_VALUE, DEFAULT_LOW_U_LIMIT);
		TrafficLevel mediumLevel = new TrafficLevel (DEFAULT_LOW_U_LIMIT, DEFAULT_MED_U_LIMIT);
		TrafficLevel highLevel = new TrafficLevel (DEFAULT_MED_U_LIMIT, Double.MAX_VALUE);
		
		lowLevel.setPredecessor(null);
		lowLevel.setProceeder(mediumLevel);
		
		mediumLevel.setPredecessor(lowLevel);
		mediumLevel.setProceeder(highLevel);
		
		highLevel.setPredecessor(mediumLevel);
		highLevel.setProceeder(null);
		
		lowLevel.setDescription("Empty");
		mediumLevel.setDescription("Occupied");
		highLevel.setDescription("Stuck");
		
		lowLevel.setMaxInfluence(-0.5);
		mediumLevel.setMaxInfluence(+0.1);
		highLevel.setMaxInfluence(+1.5);
		
		try {
			this.addTrafficLevel(lowLevel);
			this.addTrafficLevel(mediumLevel);
			this.addTrafficLevel(highLevel);
		} catch (TrafficPredictionException e) {
			e.printStackTrace();
		}
			/*
			levels.add(lowLevel);
			levels.add(mediumLevel);
			levels.add(highLevel);
			*/
	}

	public int getNumberOfLevels() {
		return this.levels.size();
	}
	
	public TrafficLevel getLevel (int levelNumber){
		return this.levels.get(levelNumber);
	}
	
	/**
	 * Discretises the value for the given column (relates it to the
	 * default value for this column
	 * @param column number of column the value lies in (link number)
	 * @param value the value to bi discretised
	 * @return traffic level representing that value
	 * @throws TrafficPredictionException if value does not lie within any valid range
	 */
	public TrafficLevel getLevelForValueInColumn(int column, double value) throws TrafficPredictionException{
		double currentValue = value;
		currentValue /= this.defaultsForColumns[column];
		TrafficLevel result = this.getLevelForValue(currentValue);
		return result;
	}

	/**
	 * @param defaultsForColumns the defaultsForColumns to set
	 */
	public void setDefaultsForColumns(Double[] defaultsForColumns) {
		this.defaultsForColumns = defaultsForColumns;
	}
	
	public TrafficLevel getLevelByName (String name) throws TrafficPredictionException{
		for (TrafficLevel temp: this.levels){
			if (temp.getDescription().equals(name)){
				return temp;
			}
		}
		throw new TrafficPredictionException("Discretisation definition is incomplete: no level of name " + name);
	}
}
