package pl.edu.agh.cs.kraksim.weka.timeSeries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;
import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateIntersections;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;
import pl.edu.agh.cs.kraksim.weka.timeSeries.TimeSeriesPredictor;
import pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms.RepTreeCreator;
import pl.edu.agh.cs.kraksim.weka.utils.Neighbours;
import pl.edu.agh.cs.kraksim.weka.utils.VoidDiscretiser;
import pl.edu.agh.cs.kraksim.weka.utils.VoidMovingAverage;
import weka.classifiers.Classifier;
import weka.classifiers.trees.REPTree;
import weka.core.Instance;

public class TimeSeriesPrediction_ResultTest_ForOnePeriod {
	private TimeSeriesPredictor prediction;
	private PredictionSetup setup;

	@BeforeMethod
	public void setUp() throws Exception {
		setup = Mockito.mock(PredictionSetup.class);

		LinkInfo classRoad = new LinkInfo(0, "X4X0", 0);
		LinkInfo road1 = new LinkInfo(1, "X4X1", 1);
		LinkInfo road2 = new LinkInfo(2, "X4X2", 1);
		LinkInfo road3 = new LinkInfo(3, "X4X3", 1);
		SortedSet<LinkInfo> roads = new TreeSet<LinkInfo>(Arrays.asList(road1,
				road2, road3));
		Map<LinkInfo, Neighbours> neighboursArray = new HashMap<LinkInfo, Neighbours>();
		Neighbours n = new Neighbours();
		n.roads = roads;
		n.intersections = new HashSet<String>();
		neighboursArray.put(classRoad, n);
		neighboursArray.put(road1, new Neighbours());
		neighboursArray.put(road2, new Neighbours());
		neighboursArray.put(road3, new Neighbours());

		Mockito.when(setup.getNeighbourArray()).thenReturn(neighboursArray);

		double levelValue = 0.5;
		Mockito.when(setup.getDiscretiser()).thenReturn(
				new VoidDiscretiser(levelValue));

		RepTreeCreator creator = createCreator();
		Mockito.when(setup.getClassifierCreator()).thenReturn(creator);

		Mockito.when(setup.getRegressionDataType()).thenReturn("carsOn");
		Mockito.when(setup.getCarsOn()).thenReturn(true);
		Mockito.when(setup.getMovingAverage()).thenReturn(
				new VoidMovingAverage());
		Mockito.when(setup.getMinNumberOfInfluencedTimesteps()).thenReturn(1);
		Mockito.when(setup.getMaxNumberOfInfluencedTimesteps()).thenReturn(2);
		Mockito.when(setup.getWorldStateUpdatePeriod()).thenReturn((long) 100);
		Mockito.when(setup.getOutputMainFolder()).thenReturn("test");

		prediction = new TimeSeriesPredictor(setup);
	}

	private RepTreeCreator createCreator() throws Exception {
		Classifier classifier = Mockito.mock(REPTree.class);

		Mockito.when(classifier.classifyInstance(Matchers.any(Instance.class)))
				.thenAnswer(new Answer<Double>() {
					public Double answer(InvocationOnMock invocation) {
						Instance instance = (Instance) invocation
								.getArguments()[0];
						double[] values = instance.toDoubleArray();
						if (values.length < 7) {
							return 0.0d;
						} else {
							if (values[1] == 1.0d) {
								return 1.0d;
							} else {
								return 0.0d;
							}
						}

					}
				});

		RepTreeCreator creator = Mockito.mock(RepTreeCreator.class);
		Mockito.when(creator.getNewClassifier()).thenReturn(classifier);
		return creator;
	}
	
	private void standardPeriod(int turn, double[] carDensity1) {
		prediction.addWorldState(turn, createWorldState(carDensity1));
		prediction.predictCongestions(turn);
	}
	private void predictionPeriod(int turn, double[] carDensity1) {
		prediction.addWorldState(turn, createWorldState(carDensity1));
		prediction.createClassifiers();
		prediction.predictCongestions(turn);
		prediction.makeEvaluation(turn);
	}

	private AssociatedWorldState createWorldState(double[] table) {
		AssociatedWorldState aws = new AssociatedWorldState();
		aws.roads = new WorldStateRoads();
		aws.roads.setCarsDensityTable(table);
		aws.roads.setCarsInLinkTable(table);
		aws.roads.setCarsOnLinkTable(table);
		aws.roads.setCarsOutLinkTable(table);
		aws.roads.setDurationLevelTable(table);
		aws.intersections = new WorldStateIntersections();
		aws.intersections.setActualPhaseMap(new HashMap<String, Integer>());
		aws.intersections.setPhaseWillLastMap(new HashMap<String, Long>());
		aws.intersections.setPhaseLastMap(new HashMap<String, Long>());
		return aws;
	}


	@Test
	public void testNoCongestions() {
		Mockito.when(setup.getTimeSeriesUpdatePeriod()).thenReturn((long) 1000);
		Mockito.when(setup.getStatisticsDumpTime()).thenReturn((long) 1500);

		standardPeriod(100, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(200, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(300, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(400, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });		
		standardPeriod(500, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(600, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(700, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(800, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(900, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		predictionPeriod(1000, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		
		standardPeriod(1100, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1200, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1300, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1400, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		predictionPeriod(1500, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		

		Assert.assertEquals(0, prediction.getTotalCongestionsAmount());
		Assert.assertEquals(0, prediction.getTruePositiveCongestions());
		Assert.assertEquals(0, prediction.getFalsePositiveCongestions());
		Assert.assertEquals(0, prediction.getFalseNegativeCongestions());
	}
	
	@Test
	public void testOneNotPredicted() {
		Mockito.when(setup.getTimeSeriesUpdatePeriod()).thenReturn((long) 1000);
		Mockito.when(setup.getStatisticsDumpTime()).thenReturn((long) 1500);

		standardPeriod(100, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(200, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(300, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(400, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });		
		standardPeriod(500, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(600, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(700, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(800, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(900, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		predictionPeriod(1000, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		
		standardPeriod(1100, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1200, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1300, new double[] { 1.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1400, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		predictionPeriod(1500, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });

		Assert.assertEquals(1, prediction.getTotalCongestionsAmount());
		Assert.assertEquals(0, prediction.getTruePositiveCongestions());
		Assert.assertEquals(1, prediction.getFalsePositiveCongestions());
		Assert.assertEquals(1, prediction.getFalseNegativeCongestions());
	}
	
	
	@Test
	public void test2_1_1_1() {
		Mockito.when(setup.getTimeSeriesUpdatePeriod()).thenReturn((long) 1000);
		Mockito.when(setup.getStatisticsDumpTime()).thenReturn((long) 1500);

		standardPeriod(100, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(200, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(300, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(400, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });		
		standardPeriod(500, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(600, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(700, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(800, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(900, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		predictionPeriod(1000, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		
		standardPeriod(1100, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1200, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1300, new double[] { 1.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1400, new double[] { 1.0d, 0.0d, 0.0d, 0.0d });
		predictionPeriod(1500, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });

		Assert.assertEquals(2, prediction.getTotalCongestionsAmount());
		Assert.assertEquals(1, prediction.getTruePositiveCongestions());
		Assert.assertEquals(1, prediction.getFalsePositiveCongestions());
		Assert.assertEquals(1, prediction.getFalseNegativeCongestions());
	}
	
	
	@Test
	public void test1_0_1_1_FirstTurn() {
		Mockito.when(setup.getTimeSeriesUpdatePeriod()).thenReturn((long) 1000);
		Mockito.when(setup.getStatisticsDumpTime()).thenReturn((long) 1500);

		standardPeriod(100, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(200, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(300, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(400, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });		
		standardPeriod(500, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(600, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(700, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(800, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(900, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		predictionPeriod(1000, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		
		standardPeriod(1100, new double[] { 1.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1200, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1300, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1400, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		predictionPeriod(1500, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });

		Assert.assertEquals(1, prediction.getTotalCongestionsAmount());
		Assert.assertEquals(0, prediction.getTruePositiveCongestions());
		Assert.assertEquals(1, prediction.getFalsePositiveCongestions());
		Assert.assertEquals(1, prediction.getFalseNegativeCongestions());
	}
	
	@Test
	public void test1_1_1_0_FirstTurn() {
		Mockito.when(setup.getTimeSeriesUpdatePeriod()).thenReturn((long) 1000);
		Mockito.when(setup.getStatisticsDumpTime()).thenReturn((long) 1500);

		standardPeriod(100, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(200, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(300, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(400, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });		
		standardPeriod(500, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(600, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(700, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(800, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(900, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		predictionPeriod(1000, new double[] { 1.0d, 1.0d, 0.0d, 1.0d });
		
		standardPeriod(1100, new double[] { 1.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1200, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1300, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1400, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		predictionPeriod(1500, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });

		Assert.assertEquals(1, prediction.getTotalCongestionsAmount());
		Assert.assertEquals(1, prediction.getTruePositiveCongestions());
		Assert.assertEquals(1, prediction.getFalsePositiveCongestions());
		Assert.assertEquals(0, prediction.getFalseNegativeCongestions());
	}
	
	@Test
	public void test1_0_0_1_LastTurn() {
		Mockito.when(setup.getTimeSeriesUpdatePeriod()).thenReturn((long) 1000);
		Mockito.when(setup.getStatisticsDumpTime()).thenReturn((long) 1500);

		standardPeriod(100, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(200, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(300, new double[] { 1.0d, 0.0d, 1.0d, 1.0d });
		standardPeriod(400, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });		
		standardPeriod(500, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		standardPeriod(600, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(700, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(800, new double[] { 1.0d, 0.0d, 0.0d, 1.0d });
		standardPeriod(900, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		predictionPeriod(1000, new double[] { 0.0d, 1.0d, 0.0d, 1.0d });
		
		standardPeriod(1100, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1200, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1300, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		standardPeriod(1400, new double[] { 0.0d, 0.0d, 0.0d, 0.0d });
		predictionPeriod(1500, new double[] { 1.0d, 0.0d, 0.0d, 0.0d });

		Assert.assertEquals(1, prediction.getTotalCongestionsAmount());
		Assert.assertEquals(0, prediction.getTruePositiveCongestions());
		Assert.assertEquals(0, prediction.getFalsePositiveCongestions());
		Assert.assertEquals(1, prediction.getFalseNegativeCongestions());
	}
}
