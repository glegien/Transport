package pl.edu.agh.cs.kraksim.weka.utils;

import java.util.List;

import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;

public abstract class Discretiser {
	public abstract boolean classBelongsToCongestionClassSet(double value);
	public abstract boolean classBelongsToHighTrafficClassSet(double value);
	public abstract List<Double> getPossibleClassList();

	public Boolean[] classesToCongestions(Double[] classTables) {
		Boolean[] congestions = new Boolean[classTables.length];
		for (int i = 0; i < classTables.length; i++) {
			congestions[i] = classBelongsToCongestionClassSet(classTables[i]);
		}
		return congestions;
	}

	public AssociatedWorldState discretise(AssociatedWorldState worldState) {
		double[] durationLevelTable = worldState.roads.getDurationLevelTable();
		double[] carsLeavingLinkTable = worldState.roads.getCarsOutLinkTable();
		double[] carDensityTable = worldState.roads.getCarsDensityTable();
		for (int i = 0; i < durationLevelTable.length; i++) {
			double durationClass = discretiseDurationLevel(durationLevelTable[i]);
			durationLevelTable[i] = durationClass;
			double carsLeavingLinkClass = discretiseCarsLeavingLink(carsLeavingLinkTable[i]);
			carsLeavingLinkTable[i] = carsLeavingLinkClass;
			double carsDensityClass = discretiseCarsDensity(carDensityTable[i]);
			carDensityTable[i] = carsDensityClass;
		}
		return worldState;
	}
	
	public abstract double discretiseDurationLevel(double durationLevel);
	public abstract double discretiseCarsLeavingLink(double carsLeavingLink);
	public abstract double discretiseCarsDensity(double carsOnLink);
}
