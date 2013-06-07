package pl.edu.agh.cs.kraksim.routing;

import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.iface.sim.Route;

public interface Router
{

  public Route getRoute(Link sourceLink, Node destGateway) throws NoRouteException;
}
