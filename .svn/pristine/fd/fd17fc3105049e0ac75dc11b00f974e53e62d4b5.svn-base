package pl.edu.agh.cs.kraksim.weka;

import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.iface.eval.EvalIView;
import pl.edu.agh.cs.kraksim.simpledecision.SimpleDecisionEView;

public class WekaPredictionModuleHandler {
	private WekaPredictionModule predictionModule;

	public void setPredictionModule(WekaPredictionModule predictionModule) {
		this.predictionModule = predictionModule;
	}

	public void turnEnded() {
		if (predictionModule != null) {
			predictionModule.turnEnded();
		}
	}
	
	public void setEvalView(EvalIView evalView) {
		if (predictionModule != null) {
			predictionModule.setEvalView(evalView);
		}
	}
	
	public void setSimpleDecisionView(SimpleDecisionEView simpleDecisionView) {
		if (predictionModule != null) {
			predictionModule.setSimpleDecisionView(simpleDecisionView);
		}
	}


	public float adjustEvalToPrediction(Lane lane, float evaluation) {
		if (predictionModule == null) {
			return evaluation;
		} else {
			return predictionModule.getEvaluationMultiplier(lane, evaluation);
		}
	}
	
}
