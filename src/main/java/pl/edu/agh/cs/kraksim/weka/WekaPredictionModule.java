package pl.edu.agh.cs.kraksim.weka;


import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;
import pl.edu.agh.cs.kraksim.iface.eval.EvalIView;
import pl.edu.agh.cs.kraksim.ministat.MiniStatEView;
import pl.edu.agh.cs.kraksim.simpledecision.SimpleDecisionEView;
import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;

public class WekaPredictionModule {
	private static final Logger logger = Logger.getLogger(WekaPredictionModule.class);
	private Clock clock;

	private long worldStateLastUpdate = -1;
	private long predictionLastUpdate = -1;
	private long classifiersLastUpdate = 0;
	private long evaluationLastUpdate = 0;

	private PredictionSetup setup;
	private WekaPredictor predictor;

	private WorldStateRoads lastPeriodWorldState;
	private double[] lastPeriodAvgDurationTable;
	private DataPicker dataPicker;

	private double timeTableMultiplier;
	private double evaluationMultiplier;


	public WekaPredictionModule(City city, MiniStatEView statView,
			CarInfoIView carInfoView, Clock clock) {
		this.clock = clock;
		this.dataPicker = new DataPicker(city, clock, statView, carInfoView);
		this.setup = new PredictionSetup(city);
		this.predictor = setup.getPredictor();
		this.lastPeriodAvgDurationTable = new double[city.linkCount()];
		this.timeTableMultiplier = setup.getTimeTableMultiplier();
		this.evaluationMultiplier = setup.getEvaluationMultiplier();
	}

	public void turnEnded() {
		int turn = clock.getTurn();
		if (needAddWorldState()) {
			logger.debug("Add world State");
			dataPicker.refreshDurationTable(lastPeriodAvgDurationTable);
			AssociatedWorldState associatedWorldState = dataPicker
					.createWorldState();
			predictor.addWorldState(turn, associatedWorldState);
		}
		if (needClassifiers()) {
			logger.debug("Create classifiers");
			predictor.createClassifiers();
		}
		if (needPrediction()) {
			logger.debug("Predict congestiosn");
			predictor.predictCongestions(turn);
		}
		if (needEvaluation()) {
			logger.debug("Make evaluation");
			predictor.makeEvaluation(turn);
		}
	}

	private boolean needEvaluation() {
		boolean refresh = false;

		int currentTime = clock.getTurn();
		long difference = currentTime - evaluationLastUpdate;

		if (difference >= this.setup.getTimeSeriesUpdatePeriod()) {
			evaluationLastUpdate = currentTime;
			refresh = true;
		}

		return refresh;
	}

	private boolean needPrediction() {
		boolean refresh = false;
		int currentTime = clock.getTurn();

		if (predictionLastUpdate < 0) {
			refresh = true;
			predictionLastUpdate = currentTime;
		} else {
			long difference = currentTime - predictionLastUpdate;

			if (difference >= this.setup.getWorldStateUpdatePeriod()) {
				predictionLastUpdate = currentTime;
				refresh = true;
			}
		}

		return refresh;
	}
	
	boolean needAddWorldState() {
		boolean refresh = false;
		int currentTime = clock.getTurn();

		if (worldStateLastUpdate < 0) {
			refresh = true;
			worldStateLastUpdate = currentTime;
		} else {
			long difference = currentTime - worldStateLastUpdate;

			if (difference >= this.setup.getWorldStateUpdatePeriod()) {
				worldStateLastUpdate = currentTime;
				refresh = true;
			}
		}
		return refresh;
	}
	
	boolean needClassifiers() {
		boolean refresh = false;

		int currentTime = clock.getTurn();
		long difference = currentTime - classifiersLastUpdate;

		if (difference >= this.setup.getTimeSeriesUpdatePeriod()) {
			classifiersLastUpdate = currentTime;
			refresh = true;
		}

		return refresh;
	}

	

	public double predictAvgDuration(Link link, double avgDuration) {
		if (predictor.willAppearTrafficJam(link)) {
			avgDuration *= timeTableMultiplier;
		}
		return avgDuration;
	}
	
	public float getEvaluationMultiplier(Lane lane, float evaluation) {
		if (predictor.willAppearTrafficJam(lane.getOwner())) {
			evaluation *= evaluationMultiplier;
		}
		return evaluation;
	}

	public double getLastPeriodAvgDurationForLink(int linkNumber) {
		return lastPeriodAvgDurationTable[linkNumber];
	}

	public void setEvalView(EvalIView evalView) {
		dataPicker.setEvalView(evalView);
	}

	public void setSimpleDecisionView(SimpleDecisionEView simpleDecisionView) {
		dataPicker.setSimpleDecisionView(simpleDecisionView);
	}

	
}
