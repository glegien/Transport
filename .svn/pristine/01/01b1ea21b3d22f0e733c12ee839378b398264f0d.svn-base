package pl.edu.agh.cs.kraksim.weka.timeSeries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.weka.PredictionSetup;
import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.Info;
import pl.edu.agh.cs.kraksim.weka.data.IntersectionInfo;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;
import pl.edu.agh.cs.kraksim.weka.data.Transaction;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateIntersections;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;
import pl.edu.agh.cs.kraksim.weka.statistics.Statistics;
import pl.edu.agh.cs.kraksim.weka.utils.Discretiser;
import pl.edu.agh.cs.kraksim.weka.utils.Neighbours;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

public class TimeSeriesStatistics extends Statistics {
	private static final Logger logger = Logger.getLogger(Statistics.class);
	private TimeSeriesPredictor classificationPrediction;
	private TimeSeriesTransactionCreator transactionCreator;
	private TimeSeriesClassValue classValue;

	public TimeSeriesStatistics(PredictionSetup setup, TimeSeriesPredictor classificationPrediction,
			TimeSeriesTransactionCreator transactionCreator) {
		super(setup);
		this.classValue = new TimeSeriesClassValue(setup);
		this.classificationPrediction = classificationPrediction;
		this.transactionCreator = transactionCreator;
	}

	@Override
	public void predict(int turn) {
		Map<LinkInfo, ClassifiersInfo> classifiersMap = classificationPrediction.getClassifierMap();
		Map<IntersectionInfo, ClassifiersInfo> intersectionClassifiers = classificationPrediction
				.getIntersectionClassifiers();
		predictHistory(classifiersMap, intersectionClassifiers);
		
		int congestionTimePrediction = setup.getMinNumberOfInfluencedTimesteps();
		congestionTimePrediction += setup.getPredictionSize();
		int predictedTurn = turn + congestionTimePrediction * (int)setup.getWorldStateUpdatePeriod(); 
		
		Double[] predictionTable = new Double[classifiersMap.keySet().size()];
		for (LinkInfo linkInfo : classifiersMap.keySet()) {
			double prediction = getClassification(linkInfo, classifiersMap);
			predictionTable[linkInfo.linkNumber] = prediction;
			if (discretiser.classBelongsToCongestionClassSet(prediction)) {
				currentPredictionContainer.addPrediction(linkInfo, congestionTimePrediction);
			}
		}
		
		classDataPredictionArchive.storeStatistics(predictedTurn, predictionTable);
		removePredictedHistory();
	}

	private void removePredictedHistory() {
		int predictionSize = setup.getPredictionSize();
		while (predictionSize > 0) {
			historyArchive.remove();
			predictionSize--;
		}

	}

	private void predictHistory(Map<LinkInfo, ClassifiersInfo> classifiersMap,
			Map<IntersectionInfo, ClassifiersInfo> intersectionClassifiers) {
		int predictionSize = setup.getPredictionSize();
		while (predictionSize > 0) {
			AssociatedWorldState predictedWorldState = predictWorldState(classifiersMap, intersectionClassifiers);
			historyArchive.add(-100, predictedWorldState);
			predictionSize--;
		}
	}

	private double getClassification(LinkInfo linkInfo, Map<LinkInfo, ClassifiersInfo> linkClassifiers) {
		ClassifiersInfo classifiersInfo = linkClassifiers.get(linkInfo);
		ClassifierInfo classifierInfo = chooseClassifierInfo(classifiersInfo);
		FastVector attributes = classifierInfo.attributes;
		Instance testInstance = createTestInstance(linkInfo, attributes);
		testInstance.setDataset(classifierInfo.trainingHeaderSet);
		testInstance.setClassMissing();
		try {
			double prediction = classifierInfo.classifier.classifyInstance(testInstance);
			return prediction;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}
	
	private AssociatedWorldState predictWorldState(Map<LinkInfo, ClassifiersInfo> classifiersMap,
			Map<IntersectionInfo, ClassifiersInfo> intersectionClassifiers) {

		AssociatedWorldState worldState = new AssociatedWorldState();
		worldState.roads = predictRoads(classifiersMap);
		worldState.intersections = predictsIntersections(intersectionClassifiers);
		return worldState;
	}

	private WorldStateIntersections predictsIntersections(Map<IntersectionInfo, ClassifiersInfo> intersectionClassifiers) {
		Map<IntersectionInfo, Neighbours> neighbours = setup.getIntersectionNeighbours();
		Set<IntersectionInfo> infoSet = neighbours.keySet();
		Map<String, Integer> actualPhaseMap = new HashMap<String, Integer>();
		Map<String, Long> phaseWillLastMap = new HashMap<String, Long>();
		Map<String, Long> phaseLastMap = new HashMap<String, Long>();		 
		for (IntersectionInfo intersectionInfo : infoSet) {
			ClassifiersInfo classifiersInfo = intersectionClassifiers.get(intersectionInfo);
			ClassifierInfo classifierInfo;
			
			String intersectionId = intersectionInfo.intersectionId;
			if (setup.getPhase()) {
				classifierInfo = classifiersInfo.phase;
				Integer result = (int)predictValue(intersectionInfo, classifierInfo);
				actualPhaseMap.put(intersectionId, result);
			}
			if (setup.getPhaseWillLast()) {
				classifierInfo = classifiersInfo.phaseWillLast;
				Long result = (long)predictValue(intersectionInfo, classifierInfo);
				phaseWillLastMap.put(intersectionId, result);
			}
			if (setup.getPhaseLast()) {
				classifierInfo = classifiersInfo.phaseLast;
				Long result = (long)predictValue(intersectionInfo, classifierInfo);
				phaseLastMap.put(intersectionId, result);
			}
		}
		WorldStateIntersections intersections = new WorldStateIntersections();
		intersections.setActualPhaseMap(actualPhaseMap);
		intersections.setPhaseWillLastMap(phaseWillLastMap);
		intersections.setPhaseLastMap(phaseLastMap);
		return intersections;
	}

	private WorldStateRoads predictRoads(Map<LinkInfo, ClassifiersInfo> classifiersMap) {
		Map<LinkInfo, Neighbours> neighbours = setup.getNeighbourArray();
		Set<LinkInfo> links = neighbours.keySet();
		double[] carsDensityTable = new double[links.size()];
		double[] durationLevelTable = new double[links.size()];
		double[] carsOutLinkTable = new double[links.size()];
		double[] carsInLinkTable = new double[links.size()];
		double[] carsOnLinkTable = new double[links.size()];
		double[] evaluationTable = new double[links.size()];
		double[] greenDurationTable = new double[links.size()];
		for (LinkInfo linkInfo : links) {
			int linkNumber = linkInfo.linkNumber;
			ClassifiersInfo classifiersInfo = classifiersMap.get(linkInfo);
			ClassifierInfo classifierInfo;
			if (setup.getCarsDensity()) {
				classifierInfo = classifiersInfo.carsDensityInfo;
				carsDensityTable[linkNumber] = predictValue(linkInfo, classifierInfo);
			}
			if (setup.getDurationLevel()) {
				classifierInfo = classifiersInfo.durationLevelInfo;
				durationLevelTable[linkNumber] = predictValue(linkInfo, classifierInfo);
			}
			if (setup.getCarsOut()) {
				classifierInfo = classifiersInfo.carsOutInfo;
				carsOutLinkTable[linkNumber] = predictValue(linkInfo, classifierInfo);
			}
			if (setup.getCarsIn()) {
				classifierInfo = classifiersInfo.carsInInfo;
				carsInLinkTable[linkNumber] = predictValue(linkInfo, classifierInfo);
			}
			if (setup.getCarsOn()) {
				classifierInfo = classifiersInfo.carsOnInfo;
				carsOnLinkTable[linkNumber] = predictValue(linkInfo, classifierInfo);
			}
			if (setup.getEvaluation()) {
				classifierInfo = classifiersInfo.evaluationInfo;
				evaluationTable[linkNumber] = predictValue(linkInfo, classifierInfo);
			}
			if (setup.getGreenDuration()) {
				classifierInfo = classifiersInfo.greenDurationInfo;
				greenDurationTable[linkNumber] = predictValue(linkInfo, classifierInfo);
			}
		}
		WorldStateRoads roads = new WorldStateRoads();
		roads.setDurationLevelTable(durationLevelTable);
		roads.setCarsOutLinkTable(carsOutLinkTable);
		roads.setCarsInLinkTable(carsInLinkTable);
		roads.setCarsOnLinkTable(carsOnLinkTable);
		roads.setCarsDensityTable(carsDensityTable);
		roads.setEvaluationTable(evaluationTable);
		roads.setGreenDurationTable(greenDurationTable);
		return roads;
	}

	private double predictValue(Info linkInfo, ClassifierInfo classifierInfo) {
		FastVector attributes = classifierInfo.attributes;
		Instance testInstance = createTestInstance(linkInfo, attributes);
		testInstance.setDataset(classifierInfo.trainingHeaderSet);
		testInstance.setClassMissing();
		try {
			double predictedClass = classifierInfo.classifier.classifyInstance(testInstance);
			return predictedClass;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	private ClassifierInfo chooseClassifierInfo(ClassifiersInfo classifiersInfo) {
		String dataType = setup.getRegressionDataType();
		if (dataType.equals("carsDensity")) {
			return classifiersInfo.carsDensityInfo;
		} else if (dataType.equals("carsOut")) {
			return classifiersInfo.carsOutInfo;
		} else if (dataType.equals("carsOn")) {
			return classifiersInfo.carsOnInfo;
		} else if (dataType.equals("carsIn")) {
			return classifiersInfo.carsInInfo;
		} else if (dataType.equals("durationLevel")) {
			return classifiersInfo.durationLevelInfo;
		} else if (dataType.equals("evaluation")) {
			return classifiersInfo.evaluationInfo;
		} else if (dataType.equals("greenDuration")) {
			return classifiersInfo.greenDurationInfo;
		}
		return null;
	}

	private Instance createTestInstance(Info linkInfo, FastVector attributes) {
		Transaction transaction = transactionCreator.createTestTransaction(historyArchive, linkInfo);
		Instance testInstance = new Instance(attributes.size());
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = (Attribute) attributes.elementAt(i);
			Double value = transaction.getTransacation().get(i);
			testInstance.setValue(attribute, value);
		}
		return testInstance;
	}

	public void add(int turn, AssociatedWorldState worldState) {
		historyArchive.add(turn, worldState);

		Double classes[] = classValue.getClassValues(worldState.roads);
		classDataArchive.storeStatistics(turn, classes);
		
		Boolean congestions[] = discretiser.classesToCongestions(classes);
		congestionsArchive.storeStatistics(turn, congestions);
	}

	public boolean willAppearTrafficJam(Link link) {
		return currentPredictionContainer.willAppearTrafficJam(link);
	}

}
