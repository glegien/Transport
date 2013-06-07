package pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.KStar;

public class KStarCreator implements IClassifierCreator {

	@Override
	public Classifier getNewClassifier() {
		return new KStar();
	}

}
