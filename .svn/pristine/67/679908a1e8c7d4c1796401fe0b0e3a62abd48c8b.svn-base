package pl.edu.agh.cs.kraksim.real;

import java.util.Random;

public final class RealSimulationParams
{
  final public static float CELL_LENGTH_IN_METERS           = 7.5f;
  final public static float TURN_DURATION_IN_SECONDS        = 1.0f;
  final public static int   DEFAULT_MAX_VELOCITY            = 2;
  final public static float DEFAULT_DECEL_PROB              = 0.2f;
  final public static int   DEFAULT_PRIOR_LANE_TIME_HEADWAY = 4;
  /** For deadlock */
  final public static float DEFAULT_VICTIM_PROB             = 0.8f;

  final Random              rg;
  /** maximum car velocity */
  final int                 maxVelocity;
  /** probability of deceleration in a turn */
  final float               decelProb;
  /** probability of becoming victim in deadlock situation */
  final float               victimProb;
  /** minimum time distance to an intersection for a car on a lane prior to
   *  some action, that an action can be performed
   */
  final int                 priorLaneTimeHeadway;

  public RealSimulationParams(
      Random rg,
      int maxVelocity,
      float decelProb,
      int priorLaneTimeHeadway)
  {
    this.rg = rg;

    this.maxVelocity = maxVelocity;
    this.decelProb = decelProb;
    this.priorLaneTimeHeadway = priorLaneTimeHeadway;
    this.victimProb = DEFAULT_VICTIM_PROB;
  }

  /* use default values */
  public RealSimulationParams(Random rg) {
    this.rg = rg;

    maxVelocity = DEFAULT_MAX_VELOCITY;
    decelProb = DEFAULT_DECEL_PROB;
    priorLaneTimeHeadway = DEFAULT_PRIOR_LANE_TIME_HEADWAY;
    victimProb = DEFAULT_VICTIM_PROB;
  }

  public static float convertToSeconds(int turns) {
    return turns * TURN_DURATION_IN_SECONDS;
  }

  public static float convertToMeters(int cells) {
    return cells * CELL_LENGTH_IN_METERS;
  }

  public static float convertToMeterPS(float speed) {
    return speed * CELL_LENGTH_IN_METERS / TURN_DURATION_IN_SECONDS;
  }

  public static float convertToKPH(float speed) {
    return 3.6f * convertToMeterPS( speed );
  }

}
