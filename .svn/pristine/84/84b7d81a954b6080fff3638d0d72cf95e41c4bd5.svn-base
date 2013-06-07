package pl.edu.agh.cs.kraksim.weka.statistics;

import java.util.Set;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;
import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.History;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;
import pl.edu.agh.cs.kraksim.weka.utils.Discretiser;

public abstract class Statistics {
	private static final Logger logger = Logger.getLogger(Statistics.class);
	protected PredictionSetup setup;
	protected Discretiser discretiser;
	private PredictionArchive predictionsArchive;
	protected Archive<Boolean> congestionsArchive;
	protected Archive<Double> classDataArchive;
	protected Archive<Double> classDataPredictionArchive;
	protected CurrentPredictionContainer currentPredictionContainer;
	protected History historyArchive;
	private ResultCreator resultCreator;
	private ResultWriter resultWriter;
	private ErrorResultCreator errorResultCreator;


	protected Statistics(PredictionSetup setup) {
		super();
		this.setup = setup;
		this.discretiser = setup.getDiscretiser();
		this.predictionsArchive = new PredictionArchive();
		this.congestionsArchive = new Archive<Boolean>();
		this.classDataArchive = new Archive<Double>();
		this.classDataPredictionArchive = new Archive<Double>();
		this.historyArchive = new History(setup.getNeighbourArray().keySet(), setup.getIntersectionNeighbours().keySet());
		this.currentPredictionContainer = new CurrentPredictionContainer();
		this.resultCreator = new ResultCreator(setup, congestionsArchive, predictionsArchive);
		this.resultWriter = new ResultWriter(setup, congestionsArchive, predictionsArchive);
		this.errorResultCreator = new ErrorResultCreator(setup, classDataArchive, classDataPredictionArchive);
	}
	
	public void predictCongestions(int turn) {
		if (turn >= this.setup.getTimeSeriesUpdatePeriod()) {
			predict(turn);
		}	
		if (turn == this.setup.getTimeSeriesUpdatePeriod()) {
			currentPredictionContainer.nextPeriod();
		}
		if (turn > this.setup.getTimeSeriesUpdatePeriod()) {			
			storePredictionInArchive(turn);
		}
	}
	
	public void addStatistics(int turn, AssociatedWorldState worldState) {
		if (turn > this.setup.getTimeSeriesUpdatePeriod() - 5000) {
			add(turn, worldState);
		}
	}
	
	public void computePartialResult(Set<LinkInfo> predictableLinks, int turn) {
		if (turn > this.setup.getTimeSeriesUpdatePeriod()) {
			errorResultCreator.computePartialResults(turn);
			resultCreator.computePartialResults(predictableLinks);
		}
		if (turn >= this.setup.getStatisticsDumpTime() && turn < this.setup.getStatisticsDumpTime()  + this.setup.getWorldStateUpdatePeriod()) {
			logger.error("Turn: " + turn);
			String result = resultCreator.getResultText();
			result += errorResultCreator.getResultText(turn);
			logger.debug("Write result to file");
			resultWriter.writeResult(result);
		}
	}
	
	abstract public void predict(int turn);
	abstract public void add(int turn, AssociatedWorldState worldState) ;
	

	

	void storePredictionInArchive(int turn) {
		Set<LinkInfo> predictedLinks = currentPredictionContainer.getPreditionForCurrentPeriod();
		currentPredictionContainer.nextPeriod();
		predictionsArchive.storePrediction(turn, predictedLinks);
	}

	
	public long getFalseNegativeCongestions() {
		return resultCreator.getFalseNegativeCongestions();
	}
	
	public long getTotalItemsAmount() {
		return resultCreator.getTotalItemsAmount();
	}
	
	public long getFalsePositiveCongestions() {
		return resultCreator.getFalsePositiveCongestions();
	}
	
	public long getTotalCongestionsAmount() {
		return resultCreator.getTotalCongestionsAmount();
	}
	
	public long getTruePositiveCongestions() {
		return resultCreator.getTruePositiveCongestions();
	}
}
