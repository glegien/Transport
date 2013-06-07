package pl.edu.agh.cs.kraksim.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.iface.sim.Route;

public class StaticRouter implements Router
{

  private static final Logger logger = Logger.getLogger( StaticRouter.class );

  private final City          city;

  private static class D
  {
    double d;

    D() {
      d = Double.MAX_VALUE;
    }

    public String toString() {
      return Double.toString( d );
    }
  }

  // source -> (target -> route)
  private final Map<Link, Map<Node, DijkstraRoute>> routes = new HashMap<Link, Map<Node, DijkstraRoute>>();

  public StaticRouter(City city) {
    this.city = city;
  }

  public Route getRoute(Link sourceLink, Node targetNode) throws NoRouteException {
    Node sourceNode = sourceLink.getBeginning();
    //    RoutesMap rm = new SparseRoutesMap();
    //
    //    Dijkstra dj = new Dijkstra( rm );
    //    dj.execute( sourceGateway, targetGateway );
    //
    //    System.err.println( targetGateway );
    //    Node n = dj.getPredecessor( targetGateway );
    //    while ( n != null ) {
    //      System.err.println( n );
    //
    //      n = dj.getPredecessor( n );
    //    }

    if ( sourceNode == null ) throw new IllegalArgumentException( "null source" );
    if ( targetNode == null ) throw new IllegalArgumentException( "null target" );

    assert sourceLink != null;
    assert sourceLink.getBeginning() == sourceNode;

    Map<Node, DijkstraRoute> sourceRoutes = routes.get( sourceLink );
    if ( sourceRoutes == null ) {
      // obliczanie tras gdy pierwszy raz jest potrzebna trasa z danego wezla

      Map<Link, List<Link>> routeMap = dijkstra( sourceLink );
      sourceRoutes = new HashMap<Node, DijkstraRoute>( routeMap.size() );

      for (Entry<Link, List<Link>> entry : routeMap.entrySet()) {
        Link targetLink = entry.getKey();
        assert targetLink != null;
        sourceRoutes.put( targetLink.getEnd(), new DijkstraRoute( entry.getValue() ) );
      }
      //TODO
      routes.put( sourceLink, sourceRoutes );
      Iterator<Entry<Link, Map<Node, DijkstraRoute>>> iter = routes.entrySet().iterator();
      while ( iter.hasNext() ) {
        Entry<Link, Map<Node, DijkstraRoute>> element = iter.next();
        if ( element != null ) {
          if ( logger.isDebugEnabled() ) {
            logger.debug( "\nSOURCE LINK = " + element.getKey().getId() );
            //        logger.debug("\nSOURCE ROUTES\n"+ element.getValue() );
          }
          Iterator<Entry<Node, DijkstraRoute>> srcRoutesIter = element
              .getValue().entrySet().iterator();
          while ( srcRoutesIter.hasNext() ) {
            Entry<Node, DijkstraRoute> route = srcRoutesIter.next();
            if ( logger.isDebugEnabled() ) {
              logger.debug( "-> " + route.getKey().getId() + " = " + route.getValue() );
            }
          }

        }
        else {
          logger.debug( "LINK and ROUTE NULL" );
        }

      }
    }

    DijkstraRoute route = sourceRoutes.get( targetNode );
    if ( route == null ) throw new NoRouteException( "from " + sourceNode.getId() + " to "
                                                     + targetNode.getId() );

    assert route.getSource() == sourceNode;
    assert route.getTarget() == targetNode;

    return route;
  }

  private final static class DijkstraRoute implements Route
  {

    private final List<Link> route;

    DijkstraRoute(List<Link> route) {
      this.route = route;
    }

    public Gateway getSource() {
      return (Gateway) route.get( 0 ).getBeginning();
    }

    public Gateway getTarget() {
      return (Gateway) route.get( route.size() - 1 ).getEnd();
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append( getSource().getId() );
      for (Link lnk : route) {
        sb.append( "-" ).append( lnk.getEnd().getId() );
      }
      return sb.toString();
      //      return getSource().getId() + " -> " + route.get( route.size() - 1 ).getEnd().getId();//getTarget().getId();
    }

    public ListIterator<Link> linkIterator() {
      return route.listIterator();
    }
  }

  private Map<Link, List<Link>> dijkstra(Link s) {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "START\n" + s );
    }

    final int SET_SIZE = city.linkCount();

    Map<Link, D> dMap = new HashMap<Link, D>( SET_SIZE );
    Map<Link, Link> prevMap = new HashMap<Link, Link>( SET_SIZE );

    Set<Link> setQ = new HashSet<Link>( SET_SIZE );
    Set<Link> setS = new HashSet<Link>( SET_SIZE );

    for (Iterator<Link> iter = city.linkIterator(); iter.hasNext();) {
      Link v = iter.next();
      if ( v != null ) {
        dMap.put( v, new D() );
        setQ.add( v );
      }
    }
    dMap.get( s ).d = 0.0;

    while ( !setQ.isEmpty() ) {
      double min = Double.MAX_VALUE;
      Link u = null;

      // szukanie najmniejszego w setQ

      for (Link x : setQ) {
        assert x != null;
        if ( dMap.get( x ).d <= min ) {
          min = dMap.get( x ).d;
          u = x;
        }
      }
      assert u != null;
      // u jest najmniejszy

      setQ.remove( u );

      setS.add( u );

      // aktualizacja wezłów
      Iterator<Link> iter = u.reachableLinkIterator();
      while ( iter.hasNext() ) {
        Link v = iter.next();
        if ( dMap.get( v ).d > dMap.get( u ).d + v.getLength() ) {
          dMap.get( v ).d = dMap.get( u ).d + v.getLength();
          prevMap.put( v, u );
        }
      }
    }

    return generateRoutes( dMap, prevMap );
  }

  private static Map<Link, List<Link>> generateRoutes(Map<Link, D> dMap,
      Map<Link, Link> prevMap)
  {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "START\n" + dMap + "\n" + prevMap );
    }
    Map<Link, List<Link>> routeMap = new HashMap<Link, List<Link>>( dMap.size() );

    for (Entry<Link, D> entry : dMap.entrySet()) {
      if ( entry.getValue().d < Double.MAX_VALUE ) {
        // jest droga

        Link target = entry.getKey();

        List<Link> route = new LinkedList<Link>();

        route.add( 0, target );
        for (Link prev = prevMap.get( target ); prev != null; prev = prevMap.get( prev )) {
          // na poczatek
          route.add( 0, prev );
        }

        routeMap.put( target, route );
      }
    }

    if ( logger.isTraceEnabled() ) {
      logger.trace( "END\n " + routeMap );
    }
    return routeMap;
  }

}
