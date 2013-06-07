package pl.edu.agh.cs.kraksim.optapo.algo.agent;

public class Conflict
{
  private String name;
  private double cost;

  public Conflict(String name, double tmpcost) {
    this.name = name;
    this.cost = tmpcost;
  }

  @Override
  public String toString()
  {
    return String.format( "%s: %3.2f ", name, cost);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getCost() {
    return cost;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

}
