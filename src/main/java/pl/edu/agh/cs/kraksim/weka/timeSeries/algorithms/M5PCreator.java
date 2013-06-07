package pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms;

import weka.classifiers.Classifier;
import weka.classifiers.trees.M5P;

public class M5PCreator implements IClassifierCreator {

	@Override
	public Classifier getNewClassifier() {
		M5P m5p = new M5P();
		m5p.setBuildRegressionTree(true);
		return m5p;
	}

}
