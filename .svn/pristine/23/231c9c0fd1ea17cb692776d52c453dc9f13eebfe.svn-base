package pl.edu.agh.cs.kraksim.weka.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;
import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.History;
import pl.edu.agh.cs.kraksim.weka.data.IntersectionInfo;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;
import pl.edu.agh.cs.kraksim.weka.data.Transaction;
import pl.edu.agh.cs.kraksim.weka.data.TransactionTable;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;

public class TransactionCreator {
	private static final Logger logger = Logger
			.getLogger(TransactionCreator.class);
	protected PredictionSetup setup;
	private ClassValue classValue;
	protected Map<LinkInfo, Neighbours> neighboursArray;
	protected Map<IntersectionInfo, Neighbours> intersectionsNeighbours;

	public TransactionCreator(PredictionSetup setup) {
		this.setup = setup;
		this.classValue = new ClassValue(setup);
		this.neighboursArray = setup.getNeighbourArray();
		this.intersectionsNeighbours = setup.getIntersectionNeighbours();
	}

	public TransactionTable generateNewTransactionsForRoad(
			History worldStateHistory, LinkInfo roadInfo) {
		Neighbours neighbours = neighboursArray.get(roadInfo);
		TransactionTable transactionTable = new TransactionTable();
		if (roadBelongToRoadSet(roadInfo)) {
			if (roadHasEnoughNeighbours(neighbours.roads)) {
				transactionTable = createTransactionTable(worldStateHistory,
						roadInfo);
				if (transactionTableHasNOTEnoughInterestingValues(transactionTable)) {
					transactionTable.clear();
				}
			}
		}
		return transactionTable;
	}

	private boolean roadBelongToRoadSet(LinkInfo roadInfo) {
//		Set<String> roads = new HashSet<String>(Arrays.asList("A17I36","C74C28","I39I36","I59C28","A98A25","A25A98",
//				"A18A60","I81I79","A14A21","A21A14","C95C97"));
//		if(roads.contains(roadInfo.linkId)) {
//			return true;
//		}
//		return false;
		return true;
	}

	private TransactionTable createTransactionTable(History worldStateHistory,
			LinkInfo classRoad) {
		TransactionTable transactionTable = new TransactionTable();
		transactionTable.clear();

		History history = new History(worldStateHistory);
		List<String> attributeNames = createAttributeNames(history, classRoad);
		transactionTable.setAttributeNames(attributeNames);

		while (history.depth() > this.setup.getMaxNumberOfInfluencedTimesteps()) {
			Transaction t = createTraningTransaction(history, classRoad);
			transactionTable.addTransaction(t);
		}

		//logger.debug("Transaction table: " + transactionTable);
		return transactionTable;
	}

	private Transaction createTraningTransaction(History history,
			LinkInfo classRoad) {
		AssociatedWorldState headState = history.poll();

		ArrayList<Double> attributeValues = classValue.createAttributeValuesWithClassValue(classRoad, headState);

		int minHistoryDepth = this.setup.getMinNumberOfInfluencedTimesteps() - 1;
		int maxHistoryDepth = this.setup.getMaxNumberOfInfluencedTimesteps();
		addNoClassAttributeValues(history, classRoad, attributeValues,
				minHistoryDepth, maxHistoryDepth);
		return new Transaction(attributeValues);
	}

	protected void addNoClassAttributeValues(History history,
			LinkInfo classRoad, ArrayList<Double> attributeValues, int min,
			int max) {
		for (int depth = min; depth < max; depth++) {
			AssociatedWorldState worldState = history.getByDepth(depth);
			addRoadDataToAttributeValues(worldState, classRoad, attributeValues);
			for (LinkInfo neighbour : neighboursArray.get(classRoad).roads) {
				addRoadDataToAttributeValues(worldState, neighbour,
						attributeValues);
			}
		}
	}

	private void addRoadDataToAttributeValues(AssociatedWorldState worldState,
			LinkInfo road, ArrayList<Double> attributeValues) {
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

	private List<String> createAttributeNames(History worldStateHistory,
			LinkInfo classRoad) {
		ArrayList<String> attributeNames = new ArrayList<String>();
		attributeNames.add(classRoad.linkId);

		for (int depth = this.setup.getMinNumberOfInfluencedTimesteps() - 1; depth < this.setup
				.getMaxNumberOfInfluencedTimesteps(); depth++) {
			String attributeName = classRoad.linkId;
			attributeName += "[" + classRoad.linkNumber + "]";
			attributeName += "[" + 0 + "]";
			attributeName += "[" + (depth + 1) + "]";
			
			if (setup.getCarsDensity()) {
				attributeNames.add(attributeName+ "_carsDensity");
			} 
			if (setup.getCarsOut()) {
				attributeNames.add(attributeName+ "_carsLeaving");
			} 
			if (setup.getDurationLevel()) {
				attributeNames.add(attributeName+ "_durationLevel");
			}
			if (setup.getEvaluation()) {
				attributeNames.add(attributeName+ "_evaluation");
			}
			if (setup.getGreenDuration()) {
				attributeNames.add(attributeName+ "_greenDuration");
			}
			if (setup.getCarsDensityMovingAvg()) {
				attributeNames.add(attributeName+ "_carsDensityMovingAvg");
			}
			if (setup.getDurationLevelMovingAvg()) {
				attributeNames.add(attributeName+ "_durationLevelMovingAvg");
			}
			for (LinkInfo neighbour : neighboursArray.get(classRoad).roads) {
				attributeName = neighbour.linkId;
				attributeName += "[" + neighbour.linkNumber + "]";
				attributeName += "[" + neighbour.numberOfHops + "]";
				attributeName += "[" + (depth + 1) + "]";
				
				if (setup.getCarsDensity()) {
					attributeNames.add(attributeName+ "_carsDensity");
				} 
				if (setup.getCarsOut()) {
					attributeNames.add(attributeName+ "_carsLeaving");
				} 
				if (setup.getDurationLevel()) {
					attributeNames.add(attributeName+ "_durationLevel");
				}
				if (setup.getEvaluation()) {
					attributeNames.add(attributeName+ "_evaluation");
				}
				if (setup.getGreenDuration()) {
					attributeNames.add(attributeName+ "_greenDuration");
				}
				if (setup.getCarsDensityMovingAvg()) {
					attributeNames.add(attributeName+ "_carsDensityMovingAvg");
				}
				if (setup.getDurationLevelMovingAvg()) {
					attributeNames.add(attributeName+ "_durationLevelMovingAvg");
				}
			}
		}
		return attributeNames;
	}

	private boolean roadHasEnoughNeighbours(Set<LinkInfo> neighbours) {
		if (neighbours == null)
			return false;
		return neighbours.size() > 0 ? true : false;
	}

	private boolean transactionTableHasNOTEnoughInterestingValues(
			TransactionTable transactionTable) {
		int congestionPeriods = 0;
		int allPeriods = 0;
		for (Transaction transaction : transactionTable) {
			double classAttributeValue = transaction.getTransacation().get(0);
			Discretiser discretiser = setup.getDiscretiser();
			if (discretiser
					.classBelongsToCongestionClassSet(classAttributeValue)) {
				congestionPeriods++;
			}
			allPeriods++;
		}
		if (allPeriods / 10 > congestionPeriods || allPeriods / 10 > allPeriods - congestionPeriods) {
			return true;
		}
		return false;
	}

}
