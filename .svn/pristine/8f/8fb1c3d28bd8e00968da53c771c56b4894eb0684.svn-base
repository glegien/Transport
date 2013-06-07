package pl.edu.agh.cs.kraksim.weka.utils;

import java.util.Arrays;
import java.util.List;

public class VoidDiscretiser extends Discretiser {
	private double levelValue = 1;
	
	public VoidDiscretiser(double levelValue) {
		super();
		this.levelValue = levelValue;
	}
	
	@Override
	public double discretiseDurationLevel(double durationLevel) {
		return durationLevel;
	}

	@Override
	public double discretiseCarsLeavingLink(double carsLeavingLink) {
		return carsLeavingLink;
	}
	
	@Override
	public double discretiseCarsDensity(double carsOnLink) {
		return carsOnLink;
	}
	
	@Override
	public List<Double> getPossibleClassList() {
		return null;
	}


	@Override
	public boolean classBelongsToCongestionClassSet(double value) {
		return value > levelValue;
	}

	@Override
	public boolean classBelongsToHighTrafficClassSet(double value) {
		return 1.0d == value;
	}


}
