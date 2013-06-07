package pl.edu.agh.cs.kraksim.ministat;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.mon.CarEntranceHandler;
import pl.edu.agh.cs.kraksim.iface.mon.CarExitHandler;
import pl.edu.agh.cs.kraksim.iface.mon.GatewayMonIface;
import pl.edu.agh.cs.kraksim.iface.mon.MonIView;

public class GatewayMiniStatExt
{
  private static final Logger           logger = Logger.getLogger( GatewayMiniStatExt.class );
  final private Map<Gateway, RouteStat> routeStatMap;

  GatewayMiniStatExt(
      final Gateway gateway,
      MonIView monView,
      final Clock clock,
      final StatHelper helper)
  {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "for: " + gateway );
    }
    routeStatMap = new HashMap<Gateway, RouteStat>();

    GatewayMonIface g = monView.ext( gateway );
    g.installEntranceSensor( new CarEntranceHandler() {

      public void handleCarEntrance(Object driver) {
        helper.beginTravel( driver, GatewayMiniStatExt.this, clock.getTurn() );
      }
    } );

    g.installExitSensor( new CarExitHandler() {

      public void handleCarExit(Object driver) {
        helper.endTravel( driver, gateway, clock.getTurn() );
      }
    } );
  }

  void clear() {
    routeStatMap.clear();
  }

  void noteTravel(Gateway dest, int length, int duration) {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "Trip: to=" + dest + ", len=" + length + ", dur=" + duration );
    }

    RouteStat rs = getRouteStatForGateway( dest );
    rs.noteTravel( length, duration );
    updateRouteStat( dest, rs );
  }

  private void updateRouteStat(Gateway dest, RouteStat rs) {
    routeStatMap.put( dest, rs );
  }

  private RouteStat getRouteStatForGateway(Gateway dest) {
    RouteStat rs = routeStatMap.get( dest );
    if ( rs == null ) {
      rs = new RouteStat();
    }
    return rs;
  }

  public RouteStat getRouteStat(Gateway dest) {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "to=" + dest );
    }

    return routeStatMap.get( dest );
  }

}
