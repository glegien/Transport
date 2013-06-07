package pl.edu.agh.cs.kraksim.ministat;

public class RouteStat
{

  private int   travelCount;
  private float totalTravelLength;
  private float totalTravelDuration;
  private float s;

  void noteTravel(int length, int duration) {
    travelCount++;
    totalTravelLength += length;
    totalTravelDuration += duration;
    s += duration * duration;
  }

  public int getTravelCount() {
    return travelCount;
  }

  public float getAvgVelocity() {
    return totalTravelDuration > 0.0f ? totalTravelLength / totalTravelDuration : 0.0f;
  }

  public float getAvgDuration() {
    return travelCount > 0 ? totalTravelDuration / travelCount : 0.0f;
  }

  public float getStdDevDuration() {
    if ( travelCount > 1 )
      return (float) Math.sqrt( (s - totalTravelDuration / travelCount * totalTravelDuration)
                                / (travelCount - 1) );
    else return 0.0f;
  }
}
