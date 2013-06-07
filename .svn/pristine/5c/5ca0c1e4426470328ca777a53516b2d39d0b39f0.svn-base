package pl.edu.agh.cs.kraksim.weka.utils;

import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;

public class VoidMovingAverage extends AbstractMovingAverage {
	
	public VoidMovingAverage() {
		this.queueSize = 1;
	}
	@Override
	protected AssociatedWorldState computeAverage() {
		return stateQueue.removeFirst();
	}
}
