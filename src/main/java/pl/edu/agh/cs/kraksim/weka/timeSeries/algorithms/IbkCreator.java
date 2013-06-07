package pl.edu.agh.cs.kraksim.weka.timeSeries.algorithms;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;

public class IbkCreator implements IClassifierCreator {
	private final Integer ibkNeighbours;

	public IbkCreator(Integer ibkNeighbours) {
		this.ibkNeighbours = ibkNeighbours;
	}

	@Override
	public Classifier getNewClassifier() {
		IBk iBk = new IBk();
		iBk.setKNN(ibkNeighbours);
		return iBk;
	}

}
