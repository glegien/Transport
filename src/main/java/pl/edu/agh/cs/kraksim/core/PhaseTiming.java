package pl.edu.agh.cs.kraksim.core;

public class PhaseTiming
{

  private int phaseId;
  private String name;
  private int duration;

  public PhaseTiming(int phaseId, String name, int phaseDuration) {
    this.phaseId = phaseId;
    this.name = name;
    this.duration = phaseDuration;
  }

  public int getPhaseId() {
    return phaseId;
  }

  public void setPhaseId(int phaseId) {
    this.phaseId = phaseId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

}
