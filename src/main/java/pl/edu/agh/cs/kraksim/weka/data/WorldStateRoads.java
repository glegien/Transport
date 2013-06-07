package pl.edu.agh.cs.kraksim.weka.data;

import java.io.Serializable;
import java.util.Arrays;

public class WorldStateRoads implements Serializable {
	private static final long serialVersionUID = -6558326605653687373L;
	private double[] durationLevelTable;
	private double[] carsOutLinkTable;
	private double[] carsDensityTable;
	private double[] evaluationTable;
	private double[] greenDurationTable;
	private double[] carsDensityMovingAvgTable;
	private double[] durationLevelMovingAvgTable;
	private double[] carsInLinkTable;
	private double[] carsOnLinkTable;

	
	
	public WorldStateRoads(WorldStateRoads oldState) {
		this.durationLevelTable = Arrays.copyOf(oldState.durationLevelTable, oldState.durationLevelTable.length);
		this.carsOutLinkTable = Arrays.copyOf(oldState.carsOutLinkTable, oldState.carsOutLinkTable.length);
		this.carsInLinkTable = Arrays.copyOf(oldState.carsInLinkTable, oldState.carsInLinkTable.length);
		this.carsOnLinkTable = Arrays.copyOf(oldState.carsOnLinkTable, oldState.carsOnLinkTable.length);
		this.carsDensityTable = Arrays.copyOf(oldState.carsDensityTable, oldState.carsDensityTable.length);
		this.evaluationTable = Arrays.copyOf(oldState.evaluationTable, oldState.evaluationTable.length);
		this.greenDurationTable = Arrays.copyOf(oldState.greenDurationTable, oldState.greenDurationTable.length);
		this.carsDensityMovingAvgTable = Arrays.copyOf(oldState.carsDensityMovingAvgTable, oldState.carsDensityMovingAvgTable.length);
		this.durationLevelMovingAvgTable = Arrays.copyOf(oldState.durationLevelMovingAvgTable, oldState.durationLevelMovingAvgTable.length);		
	}

	public WorldStateRoads() {
	}

	public double getCarsDensity(int linkNumber) {
		return this.carsDensityTable[linkNumber];
	}
	
	public double getCarsOutLink(int linkNumber) {
		return this.carsOutLinkTable[linkNumber];
	}
	public double getCarsInLink(int linkNumber) {
		return this.carsInLinkTable[linkNumber];
	}
	public double getCarsOnLink(int linkNumber) {
		return this.carsOnLinkTable[linkNumber];
	}

	public double getDurationLevel(int linkNumber){
		return this.durationLevelTable[linkNumber];
	}
	
	public double getEvaluation(int linkNumber) {
		return this.evaluationTable[linkNumber];
	}
	
	public double getGreenDuration(int linkNumber) {
		return this.greenDurationTable[linkNumber];
	}
	
	public double getCarsDensityMovingAvg(int linkNumber) {
		return this.carsDensityMovingAvgTable[linkNumber];
	}
	
	public double getDurationLevelMovingAvg(int linkNumber) {
		return this.durationLevelMovingAvgTable[linkNumber];
	}

	public double[] getDurationLevelTable() {
		return durationLevelTable;
	}

	public double[] getCarsOutLinkTable() {
		return carsOutLinkTable;
	}
	public double[] getCarsInLinkTable() {
		return carsInLinkTable;
	}
	public double[] getCarsOnLinkTable() {
		return carsOnLinkTable;
	}

	public double[] getCarsDensityTable() {
		return carsDensityTable;
	}

	public double[] getEvaluationTable() {
		return evaluationTable;
	}
	
	public double[] getGreenDurationTable() {
		return greenDurationTable;
	}
	public double[] getCarsMovingAvgTable() {
		return carsDensityMovingAvgTable;
	}
	
	public double[] getDurationLevelMovingAvgTable() {
		return durationLevelMovingAvgTable;
	}

	public void setDurationLevelMovingAvgTable(double[] durationLevelMovingAvgTable) {
		this.durationLevelMovingAvgTable = durationLevelMovingAvgTable;
	}

	public void setCarsInLinkTable(double[] carsInLinkTable) {
		this.carsInLinkTable = carsInLinkTable;		
	}

	public void setCarsOnLinkTable(double[] carsOnLinkTable) {
		this.carsOnLinkTable = carsOnLinkTable;		
	}

	public void setDurationLevelTable(double[] durationLevelTable) {
		this.durationLevelTable = durationLevelTable;
	}

	public void setCarsOutLinkTable(double[] carsOutLinkTable) {
		this.carsOutLinkTable = carsOutLinkTable;
	}

	public void setCarsDensityTable(double[] carsDensityTable) {
		this.carsDensityTable = carsDensityTable;
	}

	public void setEvaluationTable(double[] evaluationTable) {
		this.evaluationTable = evaluationTable;
	}

	public void setGreenDurationTable(double[] greenDurationTable) {
		this.greenDurationTable = greenDurationTable;
	}

	public void setCarsDensityMovingAvgTable(double[] carsDensityMovingAvgTable) {
		this.carsDensityMovingAvgTable = carsDensityMovingAvgTable;
	}
	
}
