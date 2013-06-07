package pl.edu.agh.cs.kraksim.weka.timeSeries;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;
import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.History;
import pl.edu.agh.cs.kraksim.weka.data.Info;
import pl.edu.agh.cs.kraksim.weka.data.IntersectionInfo;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;
import pl.edu.agh.cs.kraksim.weka.data.Transaction;
import pl.edu.agh.cs.kraksim.weka.data.TransactionTable;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateIntersections;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;
import pl.edu.agh.cs.kraksim.weka.statistics.ClassificationTransactionCreator;
import pl.edu.agh.cs.kraksim.weka.utils.Neighbours;
import pl.edu.agh.cs.kraksim.weka.utils.TransactionCreator;
import weka.core.Instance;

public class TimeSeriesTransactionCreator extends TransactionCreator {
	private static final Logger logger = Logger.getLogger(TransactionCreator.class);
	private TimeSeriesClassValue classValue;

	public TimeSeriesTransactionCreator(PredictionSetup setup) {
		super(setup);
		this.classValue = new TimeSeriesClassValue(setup);
	}

	public TransactionTable generateNewTransactionsForRoad(History worldStateHistory, Info classRoad,
			String classifierType) {
		TransactionTable transactionTable = new TransactionTable();

		History history = new History(worldStateHistory);
		List<String> attributeNames = createAttributeNames(history, classRoad, classifierType);
		transactionTable.setAttributeNames(attributeNames);

		while (history.depth() > this.setup.getMaxNumberOfInfluencedTimesteps()) {
			Transaction t = createTraningTransaction(history, classRoad, classifierType);
			transactionTable.addTransaction(t);
		}

		return transactionTable;
	}

	private Transaction createTraningTransaction(History history, Info classRoad, String classifierType) {
		AssociatedWorldState headState = history.poll();

		ArrayList<Double> attributeValues = classValue.createAttributeValuesWithClassValue(classRoad, headState,
				classifierType);

		int minHistoryDepth = this.setup.getMinNumberOfInfluencedTimesteps() - 1;
		int maxHistoryDepth = this.setup.getMaxNumberOfInfluencedTimesteps();
		addNoClassAttributeValues(history, classRoad, attributeValues, minHistoryDepth, maxHistoryDepth);
		return new Transaction(attributeValues);
	}
	
	public Transaction createTestTransaction(History historyArchive, Info classRoad) {
		ArrayList<Double> attributeValues = new ArrayList<Double>();
		double valueForClassAttribue = Instance.missingValue(); 
		attributeValues.add(valueForClassAttribue);
		int historyDepth = setup.getMaxNumberOfInfluencedTimesteps() - setup.getMinNumberOfInfluencedTimesteps() + 1;
		addNoClassAttributeValues(historyArchive, classRoad, attributeValues, 0, historyDepth);
		
		logger.debug("Test transaction: " + attributeValues);
		return new Transaction(attributeValues);
	}

	protected void addNoClassAttributeValues(History history, Info classRoad, ArrayList<Double> attributeValues,
			int min, int max) {
		for (int depth = min; depth < max; depth++) {
			AssociatedWorldState worldState = history.getByDepth(depth);
			Neighbours neighbours = null;
			if (classRoad instanceof LinkInfo) {
				neighbours = neighboursArray.get(classRoad);
				LinkInfo linkInfo = (LinkInfo) classRoad;
				addRoadDataToAttributeValues(worldState, linkInfo, attributeValues);
			} else if (classRoad instanceof IntersectionInfo) {
				neighbours = intersectionsNeighbours.get(classRoad);
				IntersectionInfo intersectionInfo = (IntersectionInfo) classRoad;
				addIntersectionDataToAttributeValues(worldState, intersectionInfo.intersectionId, attributeValues);
			}

			for (LinkInfo neighbour : neighbours.roads) {
				addRoadDataToAttributeValues(worldState, neighbour, attributeValues);
			}
			for (String intersection : neighbours.intersections) {
				addIntersectionDataToAttributeValues(worldState, intersection, attributeValues);
			}
		}
	}

	private void addIntersectionDataToAttributeValues(AssociatedWorldState worldState, String intersection,
			ArrayList<Double> attributeValues) {
		WorldStateIntersections intersections = worldState.intersections;
		if (setup.getPhase()) {
			Double phase = new Double(intersections.getActualPhase(intersection));
			attributeValues.add(phase);
		}
		if (setup.getPhaseWillLast()) {
			Double phaseWillLast = new Double(intersections.getPhaseWillLast(intersection));
			attributeValues.add(phaseWillLast);
		}
		if (setup.getPhaseLast()) {
			Double phaseLast = new Double(intersections.getPhaseLast(intersection));
			attributeValues.add(phaseLast);
		}
	}

	private void addRoadDataToAttributeValues(AssociatedWorldState worldState, LinkInfo road,
			ArrayList<Double> attributeValues) {

		WorldStateRoads roads = worldState.roads;
		if (setup.getCarsDensity()) {
			attributeValues.add(roads.getCarsDensity(road.linkNumber));
		}
		if (setup.getCarsOut()) {
			attributeValues.add(roads.getCarsOutLink(road.linkNumber));
		}
		if (setup.getCarsIn()) {
			attributeValues.add(roads.getCarsInLink(road.linkNumber));
		}
		if (setup.getCarsOn()) {
			attributeValues.add(roads.getCarsOnLink(road.linkNumber));
		}
		if (setup.getDurationLevel()) {
			attributeValues.add(roads.getDurationLevel(road.linkNumber));
		}
		if (setup.getEvaluation()) {
			attributeValues.add(roads.getEvaluation(road.linkNumber));
		}
		if (setup.getGreenDuration()) {
			attributeValues.add(roads.getGreenDuration(road.linkNumber));
		}
		if (setup.getCarsDensityMovingAvg()) {
			attributeValues.add(roads.getCarsDensityMovingAvg(road.linkNumber));
		}
		if (setup.getDurationLevelMovingAvg()) {
			attributeValues.add(roads.getDurationLevelMovingAvg(road.linkNumber));
		}
	}

	private List<String> createAttributeNames(History worldStateHistory, Info classRoad, String classifierType) {
		ArrayList<String> attributeNames = new ArrayList<String>();
		attributeNames.add(classRoad.getId() + "_" + classifierType);

		for (int depth = this.setup.getMinNumberOfInfluencedTimesteps() - 1; depth < this.setup
				.getMaxNumberOfInfluencedTimesteps(); depth++) {
			String attributeName = classRoad.getId();
			attributeName += "[" + 0 + "]";
			attributeName += "[" + (depth + 1) + "]";

			Neighbours neighbours = null;
			if (classRoad instanceof LinkInfo) {
				neighbours = neighboursArray.get(classRoad);
				if (setup.getCarsDensity()) {
					attributeNames.add(attributeName + "_carsDensity");
				}
				if (setup.getCarsOut()) {
					attributeNames.add(attributeName + "_carsOut");
				}
				if (setup.getCarsIn()) {
					attributeNames.add(attributeName + "_carsIn");
				}
				if (setup.getCarsOn()) {
					attributeNames.add(attributeName + "_carsOn");
				}
				if (setup.getDurationLevel()) {
					attributeNames.add(attributeName + "_durationLevel");
				}
				if (setup.getEvaluation()) {
					attributeNames.add(attributeName + "_evaluation");
				}
				if (setup.getGreenDuration()) {
					attributeNames.add(attributeName + "_greenDuration");
				}
				if (setup.getCarsDensityMovingAvg()) {
					attributeNames.add(attributeName + "_carsDensityMovingAvg");
				}
				if (setup.getDurationLevelMovingAvg()) {
					attributeNames.add(attributeName + "_durationLevelMovingAvg");
				}
			} else if (classRoad instanceof IntersectionInfo) {
				neighbours = intersectionsNeighbours.get(classRoad);
				if (setup.getPhase()) {
					attributeNames.add(attributeName + "_phase");
				}
				if (setup.getPhaseWillLast()) {
					attributeNames.add(attributeName + "_phaseWillLast");
				}
				if (setup.getPhaseLast()) {
					attributeNames.add(attributeName + "_phaseLast");
				}
			}
			for (LinkInfo neighbour : neighbours.roads) {
				attributeName = neighbour.linkId;
				attributeName += "[" + neighbour.linkNumber + "]";
				attributeName += "[" + neighbour.numberOfHops + "]";
				attributeName += "[" + (depth + 1) + "]";

				if (setup.getCarsDensity()) {
					attributeNames.add(attributeName + "_carsDensity");
				}
				if (setup.getCarsOut()) {
					attributeNames.add(attributeName + "_carsOut");
				}
				if (setup.getCarsIn()) {
					attributeNames.add(attributeName + "_carsIn");
				}
				if (setup.getCarsOn()) {
					attributeNames.add(attributeName + "_carsOn");
				}
				if (setup.getDurationLevel()) {
					attributeNames.add(attributeName + "_durationLevel");
				}
				if (setup.getEvaluation()) {
					attributeNames.add(attributeName + "_evaluation");
				}
				if (setup.getGreenDuration()) {
					attributeNames.add(attributeName + "_greenDuration");
				}
				if (setup.getCarsDensityMovingAvg()) {
					attributeNames.add(attributeName + "_carsDensityMovingAvg");
				}
				if (setup.getDurationLevelMovingAvg()) {
					attributeNames.add(attributeName + "_durationLevelMovingAvg");
				}
			}
			for (String intersection : neighbours.intersections) {
				attributeName = intersection;
				attributeName += "[" + (depth + 1) + "]";
				if (setup.getPhase()) {
					attributeNames.add(attributeName + "_phase");
				}
				if (setup.getPhaseWillLast()) {
					attributeNames.add(attributeName + "_phaseWillLast");
				}
				if (setup.getPhaseLast()) {
					attributeNames.add(attributeName + "_phaseLast");
				}
			}
		}
		return attributeNames;
	}
}
