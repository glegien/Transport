package pl.edu.agh.cs.kraksim.weka.timeSeries;


public class ClassifiersInfo {
	//roads
	public ClassifierInfo carsDensityInfo;
	public ClassifierInfo durationLevelInfo;
	public ClassifierInfo carsOutInfo;
	public ClassifierInfo carsInInfo;
	public ClassifierInfo carsOnInfo;
	public ClassifierInfo evaluationInfo;
	public ClassifierInfo greenDurationInfo;
	
	public ClassifierInfo carsDensityMovingAvgInfo;
	public ClassifierInfo durationLevelMovingAvgInfo;
	//intersections
	public ClassifierInfo phase;
	public ClassifierInfo phaseWillLast;
	public ClassifierInfo phaseLast;
}
