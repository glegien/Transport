package pl.edu.agh.cs.kraksim.weka.utils;


import static org.testng.Assert.assertEquals;

import java.util.HashMap;

import org.testng.annotations.Test;

import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateIntersections;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;
import pl.edu.agh.cs.kraksim.weka.utils.SimpleMovingAverage;

public class SimpleMovingAverageTest {
	private SimpleMovingAverage movingAverage;
	

	
	@Test
	public void testOnePeriodAverage_AfterOnePeriod() {
		movingAverage = new SimpleMovingAverage(1);
		
		double[] table = new double[]{2,4};
		AssociatedWorldState worldState = createWorldState(table);
		AssociatedWorldState result = movingAverage.computeAverage(worldState);
		
		assertEquals(result.roads.getCarsDensityTable(), new double[]{2,4});
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
	public void testOnePeriodAverage_AfterTwoPeriods() {
		movingAverage = new SimpleMovingAverage(1);
		
		double[] table1 = new double[]{2,4};
		AssociatedWorldState worldState = createWorldState(table1);
		movingAverage.computeAverage(worldState);
		double[] table2 = new double[]{1,5};
		worldState = createWorldState(table2);
		
		AssociatedWorldState result = movingAverage.computeAverage(worldState);
		
		assertEquals(result.roads.getCarsDensityTable(), new double[]{1, 5});
	}
	
	@Test
	public void testTwoPeriodsAverage_AfterTwoPeriods() {
		movingAverage = new SimpleMovingAverage(2);
		
		double[] table1 = new double[]{2,4};
		AssociatedWorldState worldState = createWorldState(table1);
		movingAverage.computeAverage(worldState);
		
		double[] table2 = new double[]{1,5};
		worldState = createWorldState(table2);
		AssociatedWorldState result = movingAverage.computeAverage(worldState);
		
		assertEquals(result.roads.getCarsDensityTable(), new double[]{1.5d, 4.5d});
	}
	
	@Test
	public void testTwoPeriodsAverage_AfterThreePeriods() {
		movingAverage = new SimpleMovingAverage(2);
		
		double[] table1 = new double[]{2,4};
		AssociatedWorldState worldState = createWorldState(table1);
		movingAverage.computeAverage(worldState);
		
		double[] table2 = new double[]{1,5};
		worldState = createWorldState(table2);
		movingAverage.computeAverage(worldState);
		
		double[] table3 = new double[]{6,8};
		worldState = createWorldState(table3);
		AssociatedWorldState result = movingAverage.computeAverage(worldState);
		
		assertEquals(result.roads.getCarsDensityTable(), new double[]{3.5d, 6.5d});
		assertEquals(result.roads.getDurationLevelTable(), new double[]{3.5d, 6.5d});
		assertEquals(result.roads.getCarsInLinkTable(), new double[]{3.5d, 6.5d});
		assertEquals(result.roads.getCarsOnLinkTable(), new double[]{3.5d, 6.5d});
		assertEquals(result.roads.getCarsOutLinkTable(), new double[]{3.5d, 6.5d});
			
	}
}
