package pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms;

import weka.classifiers.Classifier;
import weka.classifiers.rules.M5Rules;


public class M5RulesCreator implements IClassifierCreator {

	@Override
	public Classifier getNewClassifier() {
		M5Rules m5Rules = new M5Rules();
		m5Rules.setBuildRegressionTree(true);
		return m5Rules;
	}

}
