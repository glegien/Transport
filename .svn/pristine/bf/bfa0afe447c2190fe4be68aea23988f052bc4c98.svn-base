package pl.edu.agh.cs.kraksim.weka.utils;

import java.util.ArrayList;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;
import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;

public class ClassValue {
	private PredictionSetup setup;

	public ClassValue(PredictionSetup setup) {
		this.setup = setup;
	}

	public ArrayList<Double> createAttributeValuesWithClassValue(LinkInfo classRoad, AssociatedWorldState headState) {
		ArrayList<Double> attributeValues = new ArrayList<Double>();
		WorldStateRoads roads = headState.roads;
		String classDataType = setup.getRegressionDataType();
		if (classDataType.equals("carsDensity")) {
			double classValue = roads.getCarsDensity(classRoad.linkNumber);
			classValue = setup.getDiscretiser().discretiseCarsDensity(classValue);
			attributeValues.add(classValue);
		} else if (classDataType.equals("carsOut")) {
			double classValue = roads.getCarsOutLink(classRoad.linkNumber);
			classValue = setup.getDiscretiser().discretiseCarsLeavingLink(classValue);
			attributeValues.add(classValue);
		}  else if (classDataType.equals("carsIn")) {
			double classValue = roads.getCarsOutLink(classRoad.linkNumber);
			classValue = setup.getDiscretiser().discretiseCarsLeavingLink(classValue);
			attributeValues.add(classValue);
		}  else if (classDataType.equals("carsOn")) {
			double classValue = roads.getCarsOutLink(classRoad.linkNumber);
			classValue = setup.getDiscretiser().discretiseCarsLeavingLink(classValue);
			attributeValues.add(classValue);
		} else if (classDataType.equals("durationLevel")) {
			double classValue = roads.getDurationLevel(classRoad.linkNumber);
			classValue = setup.getDiscretiser().discretiseDurationLevel(classValue);
			attributeValues.add(classValue);
		}
		return attributeValues;
	}

	public Double[] getClassValues(AssociatedWorldState worldState) {
		WorldStateRoads roads = worldState.roads;
		Discretiser discretiser = setup.getDiscretiser();
		String classDataType = setup.getRegressionDataType();
		Double[] classes = null;
		if (classDataType.equals("carsDensity")) {
			double[] carsDensityTable = roads.getCarsDensityTable();
			classes = new Double[carsDensityTable.length];
			for (int i = 0; i < carsDensityTable.length; i++) {
				classes[i] = discretiser.discretiseCarsDensity(carsDensityTable[i]);
			}			
		} else if (classDataType.equals("carsOut")) {
			double[] carsLeavingTable = roads.getCarsOutLinkTable();
			classes = new Double[carsLeavingTable.length];
			for (int i = 0; i < carsLeavingTable.length; i++) {
				classes[i] = discretiser.discretiseCarsLeavingLink(carsLeavingTable[i]);
			}			
		} else if (classDataType.equals("carsIn")) {
			double[] carsLeavingTable = roads.getCarsOutLinkTable();
			classes = new Double[carsLeavingTable.length];
			for (int i = 0; i < carsLeavingTable.length; i++) {
				classes[i] = discretiser.discretiseCarsLeavingLink(carsLeavingTable[i]);
			}			
		} else if (classDataType.equals("carsOn")) {
			double[] carsLeavingTable = roads.getCarsOutLinkTable();
			classes = new Double[carsLeavingTable.length];
			for (int i = 0; i < carsLeavingTable.length; i++) {
				classes[i] = discretiser.discretiseCarsLeavingLink(carsLeavingTable[i]);
			}			
		} else if (classDataType.equals("durationLevel")) {
			double[] durationLevelTable = roads.getDurationLevelTable();
			classes = new Double[durationLevelTable.length];
			for (int i = 0; i < durationLevelTable.length; i++) {
				classes[i] = discretiser.discretiseDurationLevel(durationLevelTable[i]);
			}			
		}
		
		return classes;
	}
}
