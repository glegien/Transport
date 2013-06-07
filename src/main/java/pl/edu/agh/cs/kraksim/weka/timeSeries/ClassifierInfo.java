package pl.edu.agh.cs.kraksim.weka.timeSeries;

import weka.attributeSelection.PrincipalComponents;
import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instances;

public class ClassifierInfo {
	public Classifier classifier;
	public FastVector attributes;
	public Instances trainingHeaderSet;
	public PrincipalComponents pca;
	public Instances trainingPCAHeaderSet;
	
	public ClassifierInfo(Classifier classifier, FastVector attributes, Instances trainingSet) {
		super();
		this.classifier = classifier;
		this.attributes = attributes;
		this.trainingHeaderSet = trainingSet;
	}

	public ClassifierInfo(Classifier classifier, FastVector attributes, Instances trainingHeaderSet, PrincipalComponents pca,
			Instances trainingPCAHeaderSet) {
		this.classifier = classifier;
		this.attributes = attributes;
		this.trainingHeaderSet = trainingHeaderSet;
		this.pca = pca;
		this.trainingPCAHeaderSet = trainingPCAHeaderSet;
	}

}
