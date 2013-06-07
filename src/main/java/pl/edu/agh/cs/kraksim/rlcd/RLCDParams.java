package pl.edu.agh.cs.kraksim.rlcd;

import org.apache.log4j.Logger;


public class RLCDParams{
	
	private static final Logger logger       = Logger.getLogger( RLCDParams.class );

	  /** discount factor, gamma in equations defining RL algorithm */
	  final float                 discount;
	  /** how often to halve all RL algorithm counters; <= 0 - never halve */
	  final int                   halvePeriod;
	  /** number of turns between starts of two cars standing in the queue */
	  final int                   carStartDelay;
	  /** maximum velocity of a car (in cells per turn) */
	  final int                   carMaxVelocity;

	  public int                  minimumGreen = 5;

	  public RLCDParams(float discount, int halvePeriod, int carStartDelay, int carMaxVelocity) {
	    this.discount = discount;
	    this.halvePeriod = halvePeriod;
	    this.carStartDelay = carStartDelay;
	    this.carMaxVelocity = carMaxVelocity;

	    if ( logger.isTraceEnabled() ) {
	      logger
	          .trace( ": discount=" + discount + ", halvePeriod=" + halvePeriod
	                  + ", carStartDelay=" + carStartDelay + ", carMaxVelocity" + carMaxVelocity );
	    }
	  }
}
