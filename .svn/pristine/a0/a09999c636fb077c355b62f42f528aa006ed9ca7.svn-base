package pl.edu.agh.cs.kraksim.routing.prediction;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public interface ITrafficPredictor {
	
	public void appendWorldState (WorldState state);
	
	public void adjustCurrentWeightsOfLink (double weightsOfLinks[]);
	
	public void setup (ITrafficPredictionSetup setup);
	
	//TODO Delete in the future;
	public Map<Integer,Set<Integer>> getNeighborsArray();
	public Queue<WorldState> getHistory();
	
}
