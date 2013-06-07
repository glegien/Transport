package pl.edu.agh.cs.kraksim.routing.prediction;

public class WorldState {
	private double[] state;	// TODO: change to TrafficLevel
	
	public WorldState (double[] state){
		int length = state.length;
		this.state = new double[length];
		for (int i = 0; i < length; i++){
			this.state[i] = state[i];
		}
	}
	
	public double getStateAt (int position){
		if ( (position < 0) || (position >= this.state.length)){
			throw new IndexOutOfBoundsException("Unable to set state ar row no. " + position);
		}
		return this.state[position];
	}
}
