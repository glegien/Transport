package pl.edu.agh.cs.kraksim.weka;

import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;

public interface WekaPredictor {
	public boolean willAppearTrafficJam(Link link);
	
	public void addWorldState(int turn, AssociatedWorldState associatedWorldState);
	public void createClassifiers();
	public void predictCongestions(int turn);
	public void makeEvaluation(int turn);
}
