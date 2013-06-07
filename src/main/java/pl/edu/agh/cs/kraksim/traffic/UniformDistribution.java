package pl.edu.agh.cs.kraksim.traffic;

import java.util.Random;

public class UniformDistribution implements Distribution
{
  private float a;
  private float b;

  public UniformDistribution(float a, float b) {
    this.a = a;
    this.b = b;
  }

  public float draw(Random rg) {
    return a + (b - a) * rg.nextFloat();
  }
}
