package pl.edu.agh.cs.kraksim.weka.statistics;

import java.util.List;

import org.apache.log4j.Logger;


import pl.edu.agh.cs.kraksim.weka.PredictionSetup;

public class ErrorResultCreator {
	private static final Logger logger = Logger.getLogger(ErrorResultCreator.class);
	private final Archive<Double> classData;
	private final Archive<Double> classDataPrediction;
	private long count = 0;
	private double mapeSum = 0;
	private final PredictionSetup setup;

	public ErrorResultCreator(PredictionSetup setup, Archive<Double> classData, Archive<Double> classDataPrediction) {
		this.setup = setup;
		this.classData = classData;
		this.classDataPrediction = classDataPrediction;
	}

	public void computePartialResults(int actualTurn) {
		logger.debug("Compute error partial results");
		for (Integer turn : classDataPrediction) {
			if (turn <= actualTurn) {
				List<Double> congestionList = classData.getCongestionListByTurn(turn);
				List<Double> predictionList = classDataPrediction.getCongestionListByTurn(turn);

				for (int i = 0; i < congestionList.size(); i++) {
					Double realValue = congestionList.get(i);
					Double predictedValue = predictionList.get(i);

					if (realValue != 0.0) {
						count++;
						Double value = Math.abs((realValue - predictedValue) / realValue);
						mapeSum += value;
					}
				}
			}
		}
//		logger.debug("Write to excel");
//		PredictionsToExcel pte = new PredictionsToExcel(setup);
//		pte.writeToExcel(actualTurn, classData, classDataPrediction);
		logger.debug("Clear data history");
		classDataPrediction.clear();
		classData.clear();
	}

	public String getResultText(int actualTurn) {
		double mape = mapeSum / count * 100;
		return "MAPE: " + mape + "\n";
	}
}
