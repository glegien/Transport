package pl.edu.agh.cs.kraksim.weka;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.weka.data.IntersectionInfo;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;
import pl.edu.agh.cs.kraksim.weka.timeSeries.TimeSeriesPredictor;
import pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms.IClassifierCreator;
import pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms.IbkCreator;
import pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms.KStarCreator;
import pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms.M5PCreator;
import pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms.M5RulesCreator;
import pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms.RepTreeCreator;
import pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms.SMOregCreator;
import pl.edu.agh.cs.kraksim.weka.utils.AbstractMovingAverage;
import pl.edu.agh.cs.kraksim.weka.utils.Discretiser;
import pl.edu.agh.cs.kraksim.weka.utils.NeighbourArrayCreator;
import pl.edu.agh.cs.kraksim.weka.utils.Neighbours;
import pl.edu.agh.cs.kraksim.weka.utils.RunningMovingAverage;
import pl.edu.agh.cs.kraksim.weka.utils.SimpleMovingAverage;
import pl.edu.agh.cs.kraksim.weka.utils.VoidDiscretiser;
import pl.edu.agh.cs.kraksim.weka.utils.VoidMovingAverage;

public class PredictionSetup {
	Logger logger = Logger.getLogger(PredictionSetup.class);
	private Map<LinkInfo, Neighbours> neighboursArray;
	private Map<IntersectionInfo, Neighbours> intersectionNeighbours;

	private WekaPredictor prediction;

	private long worldStateUpdatePeriod;
	private long timeSeriesUpdatePeriod;
	private long statisticsDumpTime;
	private String outputMainFolder;

	private int maxNumberOfInfluencedTimesteps;
	private int minNumberOfInfluencedTimesteps;
	private int maxNumberOfInfluencedLinks;

	private Discretiser discretiser;
	private IClassifierCreator classifierCreator;

	private String trafficFileName;
	private AbstractMovingAverage movingAverage;
	private String timeSeriesFolder;
	private boolean pca;

	private int predictionSize;
	private String regressionDataType;
	private Boolean carsDensity;
	private boolean carsOut;
	private boolean carsOn;
	private boolean carsIn;
	private boolean durationLevel;
	private boolean evaluation;
	private boolean greenDuration;
	private boolean carsDensityMovingAvg;
	private boolean durationLevelMovingAvg;
	private boolean phase;
	private Boolean phaseWillLast;
	private Boolean phaseLast;
	private double timeTableMultiplier;
	private double evaluationMultiplier;
	private boolean writeDataSetToFile;

	public PredictionSetup(City city) {
		super();
		readSimpleProperties();
		this.neighboursArray = NeighbourArrayCreator.createNeighbourArray(city, maxNumberOfInfluencedLinks);
		NeighbourArrayCreator.addAdjacentIntersectionRoads(neighboursArray, city);
		this.intersectionNeighbours = NeighbourArrayCreator.createIntersectionsArray(city);
		createMainClasses();
	}

	public PredictionSetup(Map<LinkInfo, Neighbours> neighboursArray,
			Map<IntersectionInfo, Neighbours> intersectionNeighbours) {
		super();
		readSimpleProperties();
		this.neighboursArray = neighboursArray;
		this.intersectionNeighbours = intersectionNeighbours;
		createMainClasses();
	}

	private void readSimpleProperties() {
		Properties properties = new Properties();

		try {
			String path = getConfigPath();
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
			properties.load(bis);
			bis.close();

			worldStateUpdatePeriod = Long.valueOf(properties.getProperty("worldStateUpdatePeriod"));
			timeSeriesUpdatePeriod = Long.valueOf(properties.getProperty("timeSeriesUpdatePeriod"));
			statisticsDumpTime = Long.valueOf(properties.getProperty("statisticsDumpTime"));
			maxNumberOfInfluencedLinks = Integer.valueOf(properties.getProperty("maxNumberOfInfluencedLinks"));
			maxNumberOfInfluencedTimesteps = Integer.valueOf(properties.getProperty("maxNumberOfInfluencedTimesteps"));
			minNumberOfInfluencedTimesteps = Integer.valueOf(properties.getProperty("minNumberOfInfluencedTimesteps"));

			outputMainFolder = properties.getProperty("outputMainFolder");
			writeDataSetToFile = Boolean.valueOf(properties.getProperty("writeDataSetToFile"));

			regressionDataType = properties.getProperty("regressionDataType");
			carsDensity = Boolean.valueOf(properties.getProperty("carsDensity"));
			carsOut = Boolean.valueOf(properties.getProperty("carsOut"));
			carsIn = Boolean.valueOf(properties.getProperty("carsIn"));
			carsOn = Boolean.valueOf(properties.getProperty("carsOn"));
			durationLevel = Boolean.valueOf(properties.getProperty("durationLevel"));
			evaluation = Boolean.valueOf(properties.getProperty("evaluation"));
			greenDuration = Boolean.valueOf(properties.getProperty("greenDuration"));
			carsDensityMovingAvg = Boolean.valueOf(properties.getProperty("carsDensityMovingAvg"));
			durationLevelMovingAvg = Boolean.valueOf(properties.getProperty("durationLevelMovingAvg"));

			phase = Boolean.valueOf(properties.getProperty("phase"));
			phaseWillLast = Boolean.valueOf(properties.getProperty("phaseWillLast"));
			phaseLast = Boolean.valueOf(properties.getProperty("phaseLast"));

			timeSeriesFolder = properties.getProperty("timeSeriesFolder");
			pca = Boolean.valueOf(properties.getProperty("pca"));
			predictionSize = Integer.valueOf(properties.getProperty("predictionSize"));

			timeTableMultiplier = Double.valueOf(properties.getProperty("timeTableMultiplier"));
			evaluationMultiplier = Double.valueOf(properties.getProperty("evaluationMultiplier"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createMainClasses() {
		Properties properties = new Properties();

		try {
			String path = getConfigPath();
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
			properties.load(bis);
			bis.close();

			createMovingAverage(properties);
			createDiscretiser(properties);
			createPrediction(properties);
			createClassifier(properties);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getConfigPath() {
		Properties properties = new Properties();
		File f = new File("mainConfig.properties");
		try {
			InputStream inStream = new FileInputStream(f);
			properties.load(inStream);
			inStream.close();
		} catch (FileNotFoundException e) {
			logger.error("No file found: " + f.getAbsolutePath());
			System.exit(-1);
		} catch (IOException e) {
			logger.error("Invalid file format: File " + f.getAbsolutePath());
			System.exit(-1);
		}
		return properties.getProperty("predictionConfiguration");
	}

	private void createMovingAverage(Properties properties) {
		String averageType = properties.getProperty("average");
		if (averageType.equals("simple")) {
			int averageSize = Integer.valueOf(properties.getProperty("averageSize"));
			this.movingAverage = new SimpleMovingAverage(averageSize);
		} else if (averageType.equals("running")) {
			int averageWeight = Integer.valueOf(properties.getProperty("averageWeight"));
			this.movingAverage = new RunningMovingAverage(averageWeight);
		} else {
			this.movingAverage = new VoidMovingAverage();
		}
	}

	private void createPrediction(Properties properties) {
		String predictionSubmodule = properties.getProperty("submodule");
		if (predictionSubmodule.equals("timeSeries")) {
			this.prediction = new TimeSeriesPredictor(this);
		}
	}

	private void createClassifier(Properties properties) {
		String reasonerType = properties.getProperty("regressionAlgorithm");
		if (reasonerType.equals("kstar")) {
			this.classifierCreator = new KStarCreator();
		} else if (reasonerType.equals("m5rules")) {
			this.classifierCreator = new M5RulesCreator();
		} else if (reasonerType.equals("smoreg")) {
			this.classifierCreator = new SMOregCreator();
		} else if (reasonerType.equals("repTree")) {
			this.classifierCreator = new RepTreeCreator();
		} else if (reasonerType.equals("ibk")) {
			Integer ibkNeighbours = Integer.valueOf(properties.getProperty("ibkNeighbours"));
			this.classifierCreator = new IbkCreator(ibkNeighbours);
		} else if (reasonerType.equals("m5p")) {
			this.classifierCreator = new M5PCreator();
		}
	}

	private void createDiscretiser(Properties properties) {
		String voidLevelValue = properties.getProperty("congestionValue");
		this.discretiser = new VoidDiscretiser(Double.valueOf(voidLevelValue));
	}

	public long getWorldStateUpdatePeriod() {
		return worldStateUpdatePeriod;
	}

	public long getTimeSeriesUpdatePeriod() {
		return timeSeriesUpdatePeriod;
	}

	public long getStatisticsDumpTime() {
		return statisticsDumpTime;
	}

	public int getMaxNumberOfInfluencedTimesteps() {
		return maxNumberOfInfluencedTimesteps;
	}

	public int getMinNumberOfInfluencedTimesteps() {
		return minNumberOfInfluencedTimesteps;
	}

	public int getMaxNumberOfInfluencedLinks() {
		return maxNumberOfInfluencedLinks;
	}

	public Discretiser getDiscretiser() {
		return discretiser;
	}

	public String getOutputMainFolder() {
		return outputMainFolder;
	}


	public WekaPredictor getPredictor() {
		return prediction;
	}

	public String getTrafficFileName() {
		return trafficFileName;
	}

	public Map<LinkInfo, Neighbours> getNeighbourArray() {
		return neighboursArray;
	}

	public Map<IntersectionInfo, Neighbours> getIntersectionNeighbours() {
		return intersectionNeighbours;
	}

	public AbstractMovingAverage getMovingAverage() {
		return movingAverage;
	}

	public IClassifierCreator getClassifierCreator() {
		return classifierCreator;
	}

	public String getTimeSeriesFolder() {
		return timeSeriesFolder;
	}

	public boolean getPCA() {
		return pca;
	}

	public String getRegressionDataType() {
		return regressionDataType;
	}

	public Boolean getCarsDensity() {
		return carsDensity;
	}

	public boolean getCarsOut() {
		return carsOut;
	}

	public boolean getDurationLevel() {
		return durationLevel;
	}

	public boolean getEvaluation() {
		return evaluation;
	}

	public boolean getGreenDuration() {
		return greenDuration;
	}

	public boolean getCarsDensityMovingAvg() {
		return carsDensityMovingAvg;
	}

	public boolean getDurationLevelMovingAvg() {
		return durationLevelMovingAvg;
	}

	public int getPredictionSize() {
		return predictionSize;
	}

	public boolean getCarsIn() {
		return carsIn;
	}

	public boolean getCarsOn() {
		return carsOn;
	}

	public boolean getPhase() {
		return phase;
	}

	public Boolean getPhaseWillLast() {
		return phaseWillLast;
	}

	public Boolean getPhaseLast() {
		return phaseLast;
	}

	public double getTimeTableMultiplier() {
		return timeTableMultiplier;
	}

	public double getEvaluationMultiplier() {
		return evaluationMultiplier;
	}

	public boolean getWriteDataSetToFile() {
		return writeDataSetToFile;
	}
}
