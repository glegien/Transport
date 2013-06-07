package pl.edu.agh.cs.kraksim.iface.sim;

import pl.edu.agh.cs.kraksim.main.Driver;

public interface CitySimIface
{
  public void setCommonTravelEndHandler(TravelEndHandler handler);

  public void insertTravel(Driver driver, Route route, boolean rerouting);

  public void simulateTurn();
}
