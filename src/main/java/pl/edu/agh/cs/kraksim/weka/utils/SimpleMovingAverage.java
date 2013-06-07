package pl.edu.agh.cs.kraksim.weka.utils;

import java.util.Map;

import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateIntersections;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;

public class SimpleMovingAverage extends AbstractMovingAverage {

	public SimpleMovingAverage(int queueSize) {
		this.queueSize = queueSize;
	}

	protected AssociatedWorldState computeAverage() {
		int stateSize = stateQueue.getFirst().roads.getCarsDensityTable().length;
		double[] durationLevelTable = new double[stateSize];
		double[] carsOutLinkTable = new double[stateSize];
		double[] carsInLinkTable = new double[stateSize];
		double[] carsOnLinkTable = new double[stateSize];
		double[] carsDensityTable = new double[stateSize];

		for (AssociatedWorldState state : stateQueue) {
			WorldStateRoads roads = state.roads;
			for (int i = 0; i < stateSize; i++) {
				durationLevelTable[i] += roads.getDurationLevelTable()[i];
				carsOutLinkTable[i] += roads.getCarsOutLinkTable()[i];
				carsInLinkTable[i] += roads.getCarsInLinkTable()[i];
				carsOnLinkTable[i] += roads.getCarsOnLinkTable()[i];
				carsDensityTable[i] += roads.getCarsDensityTable()[i];
			}
		}
		int queueSize = stateQueue.size();
		for (int i = 0; i < stateSize; i++) {
			durationLevelTable[i] /= queueSize;
			carsOutLinkTable[i] /= queueSize;
			carsInLinkTable[i] /= queueSize;
			carsOnLinkTable[i] /= queueSize;
			carsDensityTable[i] /= queueSize;
		}

		//TODO add information to associatedWorldState;
		AssociatedWorldState associatedWorldState = new AssociatedWorldState();
		WorldStateRoads roads = new WorldStateRoads();
		roads.setCarsDensityTable(carsDensityTable);
		roads.setCarsInLinkTable(carsInLinkTable);
		roads.setCarsOnLinkTable(carsOnLinkTable);
		roads.setCarsOutLinkTable(carsOutLinkTable);
		roads.setDurationLevelTable(durationLevelTable);

		
		double[] maxLinkEvaluationTable = stateQueue.getFirst().roads.getEvaluationTable();
		double[] greenDurationTable = stateQueue.getFirst().roads.getGreenDurationTable();
		double[] carsDensityMovingAvgTable = stateQueue.getFirst().roads.getCarsMovingAvgTable();
		double[] durationLevelMovingAvgTable = stateQueue.getFirst().roads.getDurationLevelMovingAvgTable();
		roads.setEvaluationTable(maxLinkEvaluationTable);
		roads.setGreenDurationTable(greenDurationTable);
		roads.setCarsDensityMovingAvgTable(carsDensityMovingAvgTable);
		roads.setDurationLevelMovingAvgTable(durationLevelMovingAvgTable);
		
		associatedWorldState.roads = roads;
		
		copyIntersectionsInformation(associatedWorldState);
		return associatedWorldState;
	}

	
	private void copyIntersectionsInformation(
			AssociatedWorldState associatedWorldState) {
		Map<String, Integer> actualPhaseMap = stateQueue.getFirst().intersections.getActualPhaseMap();
		Map<String, Long> phaseWillLastMap = stateQueue.getFirst().intersections.getPhaseWillLastMap();
		Map<String, Long> phaseLastMap = stateQueue.getFirst().intersections.getPhaseLastMap();
		WorldStateIntersections intersections = new WorldStateIntersections();
		intersections.setActualPhaseMap(actualPhaseMap);
		intersections.setPhaseWillLastMap(phaseWillLastMap);
		intersections.setPhaseLastMap(phaseLastMap);
		associatedWorldState.intersections = intersections;
	}

}
