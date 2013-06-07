package pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms;

import weka.classifiers.Classifier;
import weka.classifiers.trees.REPTree;

public class RepTreeCreator implements IClassifierCreator {

	@Override
	public Classifier getNewClassifier() {
		REPTree repTree = new REPTree();
		repTree.setMaxDepth(250);
		return repTree;
	}

}
