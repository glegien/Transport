package pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOreg;

public class SMOregCreator implements IClassifierCreator{

	@Override
	public Classifier getNewClassifier() {
		return new SMOreg();
	}

}
