package pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms;

import weka.classifiers.Classifier;

public interface IClassifierCreator {
	public Classifier getNewClassifier();
}
