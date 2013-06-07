package pl.edu.agh.cs.kraksim.dsyncdecision;

import java.util.Iterator;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.decision.CityDecisionIface;
import pl.edu.agh.cs.kraksim.ministat.MiniStatEView;
import pl.edu.agh.cs.kraksim.optapo.algo.AgentFarm;
import pl.edu.agh.cs.kraksim.optapo.algo.agent.Agent;
import pl.edu.agh.cs.kraksim.optapo.algo.agent.AgentInfo;
import pl.edu.agh.cs.kraksim.optapo.algo.agent.Direction;

class CityDsyncDecisionExt implements CityDecisionIface
{
  private static final Logger      logger  = Logger
                                               .getLogger( CityDsyncDecisionExt.class );
  private final City               city;
  private final DSyncDecisionEView ev;
  private AgentFarm                farm;
  private MiniStatEView            statView;
  private Clock                    clock;
  private boolean                  dynamic = false;

  CityDsyncDecisionExt(
      City city,
      Clock clock,
      DSyncDecisionEView ev,
      MiniStatEView statView,
      boolean dynamic)
  {
    this.city = city;
    this.clock = clock;
    this.ev = ev;
    this.statView = statView;
    this.farm = new AgentFarm();
    this.dynamic = dynamic;
  }

  public void initialize() {
    for (Iterator<Intersection> iter = city.intersectionIterator(); iter.hasNext();) {
      Intersection intersection = iter.next();
      IntersectionDsyncDecisionExt isectView = ev.ext( intersection );

      Agent agent = createAgentFor( intersection );
      isectView.setAgent( agent );
    }

    for (Iterator<Intersection> iter = city.intersectionIterator(); iter.hasNext();) {
      ev.ext( iter.next() ).initialize();
    }

    farm.init();
  }

  public void turnEnded() {
    // run simulation.. and mediation
    for (Iterator<Intersection> iter = city.intersectionIterator(); iter.hasNext();) {
      Intersection intersection = iter.next();
      //      IntersectionDsyncDecisionExt isectView = ev.ext( intersection );

      updateAgentFor( intersection );
      //      Collection<AgentInfo> neiList = isectView.getAgent().getNeighborList();
    }

    if ( (clock.getTurn() % 120) == 0 ) {
      runSimulation();
    }
    if ( logger.isTraceEnabled() ) {
      logger.trace( "Changing Lights" );
    }

    for (Iterator<Intersection> iter = city.intersectionIterator(); iter.hasNext();) {
      ev.ext( iter.next() ).makeDecision();
    }
  }

  private void updateAgentFor(Intersection intersection) {
    IntersectionDsyncDecisionExt isectView = ev.ext( intersection );
    Agent agent = isectView.getAgent();

    for (Iterator<Link> linkIter = intersection.inboundLinkIterator(); linkIter.hasNext();)
    {
      Link link = linkIter.next();
      int incoming = getIncoming( link );
      String nodeName = link.getBeginning().getId();

      AgentInfo ag = agent.getNeighbor( nodeName );
      ag.setIncoming( incoming );
      //new AgentInfo( nodeName, Direction.valueOf( link.getDirection() ), 0 );
      //agent.addNeighbor( ag );
    }
  }

  private int getIncoming(Link link) {
    int carCount = statView.ext( link ).getCarCount();
    //    for (Iterator<Lane> iter = link.laneIterator(); iter.hasNext();){
    //      Lane lane = iter.next();
    //  
    //       laneStat = statView.ext( lane );
    //      
    //      ret += laneEval.getEvaluation();
    //    }

    return carCount;
  }

  private Agent createAgentFor(Intersection intersection) {
    logger.trace( "new Agent for: " + intersection.getId() );
    Agent agent = new Agent( farm, intersection.getId(), Direction.NS );

    for (Iterator<Link> linkIter = intersection.inboundLinkIterator(); linkIter.hasNext();)
    {
      Link link = linkIter.next();
      String nodeName = link.getBeginning().getId();

      AgentInfo ag = new AgentInfo( nodeName, Direction.valueOf( link.getDirection() ), 0 );
      agent.addNeighbor( ag );
    }

    farm.addAgent( agent );

    return agent;
  }

  private void runSimulation() {
    if ( dynamic ) {
      try {
        farm.cycle();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
