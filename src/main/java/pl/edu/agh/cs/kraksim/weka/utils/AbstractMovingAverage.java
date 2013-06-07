package pl.edu.agh.cs.kraksim.weka.utils;

import java.util.LinkedList;

import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;

public abstract class AbstractMovingAverage {
	protected LinkedList<AssociatedWorldState> stateQueue = new LinkedList<AssociatedWorldState>();
	protected int queueSize;
	
	public AssociatedWorldState computeAverage(AssociatedWorldState worldState) {
		stateQueue.addFirst(worldState);
		if (stateQueue.size() > queueSize) {
			stateQueue.removeLast();
		}
		return computeAverage();
	}
	
	protected abstract AssociatedWorldState computeAverage();

}