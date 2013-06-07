package pl.edu.agh.cs.kraksim.weka.timeSeries;

import java.util.List;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;
import pl.edu.agh.cs.kraksim.weka.data.History;
import pl.edu.agh.cs.kraksim.weka.data.Info;
import pl.edu.agh.cs.kraksim.weka.data.IntersectionInfo;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;
import pl.edu.agh.cs.kraksim.weka.data.Transaction;
import pl.edu.agh.cs.kraksim.weka.data.TransactionTable;
import pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms.IClassifierCreator;
import pl.edu.agh.cs.kraksim.weka.utils.RulesWriter;
import pl.edu.agh.cs.kraksim.weka.utils.VoidDiscretiser;
import weka.attributeSelection.PrincipalComponents;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.instance.Randomize;

public class TimeSeriesTrainer {
	private static final Logger logger = Logger.getLogger(TimeSeriesTrainer.class);
	private static final String DEFAULT_INSTANCE_NAME = "instance";
	private PredictionSetup setup;
	private RulesWriter rulesWriter;
	private IClassifierCreator classifierCreator;
	private final TimeSeriesTransactionCreator transactionCreator;

	public TimeSeriesTrainer(PredictionSetup setup, TimeSeriesTransactionCreator transactionCreator) {
		super();
		this.setup = setup;
		this.transactionCreator = transactionCreator;
		this.rulesWriter = new RulesWriter(setup.getOutputMainFolder());
		this.classifierCreator = setup.getClassifierCreator();
	}

	public ClassifiersInfo generateOnlyClassDataClassifier(History history, LinkInfo linkInfo) {
		ClassifiersInfo classifiers = new ClassifiersInfo();
		String dataType = setup.getRegressionDataType();
		if (dataType.equals("carsDensity")) {
			classifiers.carsDensityInfo = generateClassifier(history, linkInfo, "carsDensity");
		} else if (dataType.equals("carsOut")) {
			classifiers.carsOutInfo = generateClassifier(history, linkInfo, "carsOut");
		} else if (dataType.equals("carsOn")) {
			classifiers.carsOnInfo = generateClassifier(history, linkInfo, "carsOn");
		} else if (dataType.equals("carsIn")) {
			classifiers.carsInInfo = generateClassifier(history, linkInfo, "carsIn");
		} else if (dataType.equals("durationLevel")) {
			classifiers.durationLevelInfo = generateClassifier(history, linkInfo, "durationLevel");
		} else if (dataType.equals("evaluation")) {
			classifiers.evaluationInfo = generateClassifier(history, linkInfo, "evaluation");
		} else if (dataType.equals("greenDuration")) {
			classifiers.greenDurationInfo = generateClassifier(history, linkInfo, "greenDuration");
		}
		return classifiers;
	}

	public ClassifiersInfo generateClassifiers(History history, Info linkInfo) {
		ClassifiersInfo classifiers = new ClassifiersInfo();
		if (linkInfo instanceof LinkInfo) {
			if (setup.getCarsDensity()) {
				classifiers.carsDensityInfo = generateClassifier(history, linkInfo, "carsDensity");
			}
			if (setup.getCarsOut()) {
				classifiers.carsOutInfo = generateClassifier(history, linkInfo, "carsOut");
			}
			if (setup.getCarsIn()) {
				classifiers.carsInInfo = generateClassifier(history, linkInfo, "carsIn");
			}
			if (setup.getCarsOn()) {
				classifiers.carsOnInfo = generateClassifier(history, linkInfo, "carsOn");
			}
			if (setup.getDurationLevel()) {
				classifiers.durationLevelInfo = generateClassifier(history, linkInfo, "durationLevel");
			}
			if (setup.getEvaluation()) {
				classifiers.evaluationInfo = generateClassifier(history, linkInfo, "evaluation");
			}
			if (setup.getGreenDuration()) {
				classifiers.greenDurationInfo = generateClassifier(history, linkInfo, "greenDuration");
			}
			if (setup.getCarsDensityMovingAvg()) {
				classifiers.evaluationInfo = generateClassifier(history, linkInfo, "carsDensityMovingAvg");
			}
			if (setup.getDurationLevelMovingAvg()) {
				classifiers.greenDurationInfo = generateClassifier(history, linkInfo, "durationlevelMovingAvg");
			}
		} else if (linkInfo instanceof IntersectionInfo) {
			if (setup.getPhase()) {
				classifiers.phase = generateClassifier(history, linkInfo, "phase");
			}
			if (setup.getPhaseWillLast()) {
				classifiers.phaseWillLast = generateClassifier(history, linkInfo, "phaseWillLast");
			}
			if (setup.getPhaseLast()) {
				classifiers.phaseLast = generateClassifier(history, linkInfo, "phaseLast");
			}
		}
		return classifiers;
	}

	private ClassifierInfo generateClassifier(History history, Info linkInfo, String classifierType) {
		TransactionTable transactionTable = transactionCreator.generateNewTransactionsForRoad(history, linkInfo,
				classifierType);
		try {
			FastVector attributes = createAttributeInformation(transactionTable);
			Instances trainingSet = createFromTransactionTable(transactionTable, attributes);

			if (setup.getWriteDataSetToFile()) {
				logger.debug("Write trainingSet to file");
				rulesWriter.writeDataSetToFile(trainingSet);
			}

			logger.debug("Create trainingHeaderSet");
			Instances trainingHeaderSet = new Instances(trainingSet, 0);

			if (!(setup.getDiscretiser() instanceof VoidDiscretiser)) {
				trainingSet = preprocessData(trainingSet);
			}

			if (setup.getPCA()) {
				PrincipalComponents pca = new PrincipalComponents();
				pca.buildEvaluator(trainingSet);
				trainingSet = pca.transformedData(trainingSet);
				Instances trainingPCAHeaderSet = new Instances(trainingSet, 0);

				Classifier classifier = generate(trainingSet);
				return new ClassifierInfo(classifier, attributes, trainingHeaderSet, pca, trainingPCAHeaderSet);
			} else {
				logger.debug("Generate Classifier");
				Classifier classifier = generate(trainingSet);
				logger.debug("Return classifier info");
				return new ClassifierInfo(classifier, attributes, trainingHeaderSet);
			}
			// return new ClassifierInfo(null, attributes, instances);//
			// empty set
		} catch (Exception e) {
			logger.debug(e);
			e.printStackTrace();
		}
		return null;// no classifier
	}

	private Instances preprocessData(Instances trainingSet) throws Exception {
		SpreadSubsample spreadSubsample = new SpreadSubsample();
		spreadSubsample.setInputFormat(trainingSet);
		spreadSubsample.setDistributionSpread(1.0d);
		trainingSet = Filter.useFilter(trainingSet, spreadSubsample);

		Randomize randomizeInstances = new Randomize();
		randomizeInstances.setInputFormat(trainingSet);
		trainingSet = Filter.useFilter(trainingSet, randomizeInstances);
		return trainingSet;
	}

	private Classifier generate(Instances instances) {
		Classifier classifier = classifierCreator.getNewClassifier();

		try {
			classifier.buildClassifier(instances);
		} catch (Exception e) {
			logger.debug(e);
			e.printStackTrace();
		}

		return classifier;
	}

	Instances createFromTransactionTable(TransactionTable transactionTable, FastVector attributes) {

		Instances instances = new Instances(DEFAULT_INSTANCE_NAME, attributes, 0);
		instances.setClassIndex(0);
		for (Transaction transaction : transactionTable) {
			List<Double> transactionVals = transaction.getTransacation();
			double[] valsdouble = covertDoubleToPrimitiveArray(transactionVals);
			instances.add(new Instance(1.0, valsdouble));
		}
		return instances;
	}

	private FastVector createAttributeInformation(TransactionTable transactionTable) {
		int attributeNumber = transactionTable.getTransactionSize();
		List<String> names = transactionTable.getAttributeNames();
		FastVector attributes = new FastVector();
		for (int i = 0; i < attributeNumber; i++) {
			String name = names.get(i);
			attributes.addElement(new Attribute(name));
		}
		return attributes;
	}

	double[] covertDoubleToPrimitiveArray(List<Double> valsDouble) {
		double[] valsdouble = new double[valsDouble.size()];
		for (int i = 0; i < valsDouble.size(); i++) {
			valsdouble[i] = valsDouble.get(i).doubleValue();
		}
		return valsdouble;
	}

}
