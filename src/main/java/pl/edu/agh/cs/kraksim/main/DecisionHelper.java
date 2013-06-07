/**
 * 
 */
package pl.edu.agh.cs.kraksim.main;

import java.util.Random;

import org.apache.log4j.Logger;

public class DecisionHelper
{
  private static final Logger logger = Logger.getLogger( DecisionHelper.class );
  private Random              rg;
  private int                 th;

  // TODO:
  public DecisionHelper(Random randomGen, int threashold) {
    this.rg = randomGen;
    this.th = threashold;
  }

  public boolean decide() {
    boolean decision = false;
    if ( rg.nextInt( 100 ) < th ) {
      decision = true;
    }

    logger.trace( decision );
    
    return decision;
  }
}
