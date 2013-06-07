package pl.edu.agh.cs.kraksim.iface.mon;

public interface LinkMonIface
{

  /*
   * Throws IndexOutOfBoundsException if line is negative or greater then link
   * length
   */
  public void installInductionLoops(int line, CarDriveHandler handler)
      throws IndexOutOfBoundsException;
}
