package pl.edu.agh.cs.kraksim.weka.utils;

import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;

public class RunningMovingAverage extends AbstractMovingAverage {
	private int weight;

	public RunningMovingAverage(int weight) {
		this.queueSize = 2;
		this.weight = weight;
	}

	@Override
	protected AssociatedWorldState computeAverage() {
//		int stateSize = stateQueue.getFirst().getCarsDensityTable().length;
//		double[] durationLevelTable;
//		double[] carsLeavingLinkTable;
//		double[] carsDensityTable;
//		if (stateQueue.size() > 1) {
//			durationLevelTable = computeDurationLevelRMA(stateSize);
//			carsLeavingLinkTable = computeCarsLeavingRMA(stateSize);
//			carsDensityTable = computeCarsDensityRMA(stateSize);
//		} else {
//			WorldStateRoads soleState = stateQueue.getFirst();
//			durationLevelTable = soleState.getDurationLevelTable();
//			carsLeavingLinkTable = soleState.getCarsOutLinkTable();
//			carsDensityTable = soleState.getCarsDensityTable();
//		}
//		double[] maxLinkEvaluationTable = stateQueue.getFirst().getEvaluationTable();
//		double[] greenDurationTable = stateQueue.getFirst().getGreenDurationTable();
//		double[] carsDensityMovingAvgTable = stateQueue.getFirst().getCarsMovingAvgTable();
//		double[] durationLevelMovingAvgTable = stateQueue.getFirst().getDurationLevelMovingAvgTable();
		AssociatedWorldState associatedWorldState = new AssociatedWorldState();
		//TODO add averages to state object
		return associatedWorldState;
		
	}

	private double[] computeCarsDensityRMA(int stateSize) {
		double[] carsDensityTable = new double[stateSize];
//		for (int i = 0; i < stateSize; i++) {
//			double rmaYesterday = stateQueue.get(1).getCarsDensityTable()[i];
//			double currentValue = stateQueue.get(0).getCarsDensityTable()[i];
//			carsDensityTable[i] = (weight - 1) * rmaYesterday + currentValue;
//			carsDensityTable[i] /= weight;
//		}
		return carsDensityTable;
	}

	private double[] computeCarsLeavingRMA(int stateSize) {
		double[] carsLeavingLinkTable = new double[stateSize];
//		for (int i = 0; i < stateSize; i++) {
//			double rmaYesterday = stateQueue.get(1).getCarsOutLinkTable()[i];
//			double currentValue = stateQueue.get(0).getCarsOutLinkTable()[i];
//			carsLeavingLinkTable[i] = (weight - 1) * rmaYesterday + currentValue;
//			carsLeavingLinkTable[i] /= weight;
//		}
		return carsLeavingLinkTable;
	}

	private double[] computeDurationLevelRMA(int stateSize) {
		double[] durationLevelTable = new double[stateSize];
//		for (int i = 0; i < stateSize; i++) {
//			double rmaYesterday = stateQueue.get(1).getDurationLevelTable()[i];
//			double currentValue = stateQueue.get(0).getDurationLevelTable()[i];
//			durationLevelTable[i] = (weight - 1) * rmaYesterday + currentValue;
//			durationLevelTable[i] /= weight;
//		}
		return durationLevelTable;
	}

}
