package pl.edu.agh.cs.kraksim.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Bartosz Rybacki
 *
 */
public class Phase
{

  public static class LightState
  {
    String  arm;
    int     lane;
    boolean green = false;

    public LightState(String arm, int lane, boolean green) {
      this.arm = arm;
      this.lane = lane;
      this.green = green;
    }

    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append( "(arm=" ).append( arm );
      buf.append( ", lane=" ).append( lane );
      buf.append( ", color=" ).append( green ? "green" : "red" );

      buf.append( " )" );
      return buf.toString();
    }

    public boolean isGreen() {
      return green;
    }
  }

  private int             id     = 0;
  //  Collection<LightState> lights = new ArrayList<LightState>();
  Map<String, LightState> lights = new HashMap<String, LightState>();
  private String          syncDirection;
  private int             duration;
  private String          name;

  public Phase(String phaseName, int phaseId, String direction) {
//    System.out.println( "PHASE::" + phaseId + " " + direction );
    name = phaseName;
    id = phaseId;
    syncDirection = direction;
  }

  public void addConfiguration(String arm, int lane, boolean green) {
    lights.put( "A"+arm + "L"+ lane, new LightState( arm, lane, green ) );
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public LightState getConfigurationFor(final String arm, final int lane) {
    return lights.get( "A"+arm +"L"+ lane );
  }

  public Iterator<LightState> iterator() {
    return lights.values().iterator();
  }

  @Override
  public String toString() {
    return lights.toString();
  }

  public String getSyncDirection() {
    return syncDirection;
  }

  public int getGreenDuration() {
    return duration;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
