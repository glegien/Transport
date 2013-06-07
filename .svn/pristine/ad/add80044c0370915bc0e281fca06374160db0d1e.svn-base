package pl.edu.agh.cs.kraksim.dsyncdecision;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Phase;
import pl.edu.agh.cs.kraksim.core.PhaseTiming;
import pl.edu.agh.cs.kraksim.core.Phase.LightState;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.optapo.algo.agent.Agent;

class IntersectionDsyncDecisionExt
{
  private static final Logger logger = Logger.getLogger( IntersectionDsyncDecisionExt.class );

  private enum State {
    INIT,
    TRANSITION,
    GREEN
  };

  private final Intersection intersection;

  //  private final EvalIView    evalView;
  private final BlockIView   blockView;
  private final Clock        clock;

  private final int          transitionDuration;
  private State              state;
  private int                stateEndMinTurn;
  //  private Lane greenLane;
  private Phase              nextPhase;
  private Agent              agent;

  //  private PhaseTiming        selectedTiming;

  IntersectionDsyncDecisionExt(Intersection intersection,
  //      Agent agent,
      //      EvalIView evalView,
      BlockIView blockView,
      Clock clock,
      int transitionDuration)
  {
    this.intersection = intersection;
    //    this.agent = agent;
    //    this.evalView = evalView;
    this.blockView = blockView;
    this.clock = clock;
    this.transitionDuration = transitionDuration;
  }

  public void setAgent(Agent agent) {
    this.agent = agent;
  }

  void initialize() {
    blockView.ext( intersection ).blockInboundLinks();

    state = State.INIT;
    stateEndMinTurn = 0;
  }

  void makeDecision() {

    if ( logger.isTraceEnabled() ) {
      logger.trace( state + " phase:" + nextPhase + ", countdown:"
                    + (stateEndMinTurn - clock.getTurn()) );
    }

    switch ( state )
    {
    case INIT:
      initFirstPhase();
      break;

    case TRANSITION:
      setNextPhase();
      break;
    case GREEN:
      if ( isPhaseFinished() ) {
        switchPhase();
      }
      break;
    default:
      break;
    }
  }

  private void switchPhase() {
    //  final Lane chosenLane = choseLane();
    //  if ( logger.isTraceEnabled() ) {
    //    logger.trace( chosenLane );
    //  }

    //  if ( chosenLane != null ) {
    //  final Phase chosenPhase = getPhaseForLane( chosenLane );
    //    final Phase chosenPhase = getPhaseForDirection( chooseDirection() );
    final Phase chosenPhase = getNextPhaseForDirection( chooseDirection() );

//    System.err.println( chosenPhase );
    if ( chosenPhase != null && !chosenPhase.equals( nextPhase ) ) {
      blockView.ext( intersection ).blockInboundLinks();
      changeToGreen( chosenPhase );
    }
    else {
      prolongCurrentPhase( 10 );
    }
    //  }
  }

  private void prolongCurrentPhase(final int dur) {
    stateEndMinTurn = clock.getTurn() + dur;
  }

  private boolean isPhaseFinished() {
    return clock.getTurn() >= stateEndMinTurn;
  }

  private void setNextPhase() {
    if ( isPhaseFinished() ) {
      setGreen( nextPhase );
    }
  }

  private void initFirstPhase() {
    //    final Lane chosenLane = choseLane();
    //    if ( logger.isTraceEnabled() ) {
    //      logger.trace( chosenLane );
    //    }

    //    if ( chosenLane != null ) {
    //      final Phase chosenPhase = getPhaseForLane( chosenLane );
    final Phase chosenPhase = getPhaseForDirection( chooseDirection() );
    if ( chosenPhase != null ) {
      nextPhase = chosenPhase;
      changeToGreen( chosenPhase );
    }
    //    }
  }

  private Phase getNextPhaseForDirection(String direction) {
    //    Phase selected  =
    // TODO: remember OFFSET, 
    int id = nextPhase.getId();
    int max = intersection.trafficLightPhases().size();
    int newId = (id) % max;
    //    id = newId;
    //    System.out.println( "was " + (id-1) + ", is " + newId );
    Phase selected = intersection.trafficLightPhases().get( newId );

    List<PhaseTiming> timing = intersection.getTimingPlanFor( direction );

    PhaseTiming selectedTiming = null;
    if ( timing != null ) {
      for (PhaseTiming phaseTiming : timing) {
        // TODO: by name
        if ( phaseTiming.getName().equals( selected.getName() ) ) {
          selectedTiming = phaseTiming;
          break;
        }
      }
    }

    if ( selectedTiming != null ) {
      selected.setDuration( selectedTiming.getDuration() );
    }
    else {
//      System.err.println( ", NULL TIMING; " );
      selected.setDuration( 20 );
    }

    return selected;
  }

  // TODO: needs refactor
  private Phase getPhaseForDirection(String direction) {
    Phase selected = null;
    List<Phase> phases = intersection.trafficLightPhases();
    Iterator<Phase> it = phases.iterator();

    List<PhaseTiming> timing = intersection.getTimingPlanFor( direction );
    //    System.out.println( timing );
    while ( it.hasNext() ) {
      Phase phase = it.next();
      //        String dir = phase.getSyncDirection();

      //        if ( direction.equals( dir ) ) {
      // TODO: check timing for this plan
      selected = phase;
      break;
      //        }

    }

    PhaseTiming selectedTiming = null;
    if ( timing != null ) {
      for (PhaseTiming phaseTiming : timing) {
        if ( phaseTiming.getPhaseId() == selected.getId() ) {
          selectedTiming = phaseTiming;
          break;
        }
      }
    }

    if ( selectedTiming != null ) {
      selected.setDuration( selectedTiming.getDuration() );
    }
    else {
      selected.setDuration( 20 );
    }
    return selected;
  }

  //  private Phase getPhaseForLane(final Lane l) {
  //    Iterator<Phase> it = intersection.trafficLightPhaseIterator();
  //    Phase selected = null;
  //
  //    while ( it.hasNext() ) {
  //      Phase phase = it.next();
  //      String id = l.getOwner().getBeginning().getId();
  //      int laneNum = l.getRelativeNumber();
  //      LightState config = phase.getConfigurationFor( id, laneNum );
  //      if ( config == null ) {
  //        continue;
  //      }
  //      if ( logger.isDebugEnabled() ) {
  //        logger.debug( "getPhase for " + l );
  //      }
  //
  //      if ( config.isGreen() ) {
  //        selected = phase;
  //        break;
  //      }
  //    }
  //
  //    return selected;
  //  }

  private void setGreen(final Phase phase) {
    int duration = 0;
    int counter = 0;

    for (Iterator<Link> iter = intersection.inboundLinkIterator(); iter.hasNext();) {
      Link link = iter.next();
      String arm = link.getBeginning().getId();

      for (Iterator<Lane> laneIter = link.laneIterator(); laneIter.hasNext();) {
        Lane lane = laneIter.next();

        int laneNum = lane.getRelativeNumber();
        LightState light = phase.getConfigurationFor( arm, laneNum );

        if ( light.isGreen() ) {
          blockView.ext( lane ).unblock();
          duration += phase.getGreenDuration();
          //          duration += evalView.ext( lane ).getMinGreenDuration();
          
          
          counter++;
        }
        else {
          blockView.ext( lane ).block();
        }

      }
    }

    state = State.GREEN;
    // TODO: LDZ WIELEPASOW!!!
    // Dzielenie przez ilosc zielonych swiatel nie dziala
    // i czemu po dodaniu wielu pasow czasem jest ich 0, co psuje algorytm...
   // stateEndMinTurn = clock.getTurn() + (duration / counter);
    stateEndMinTurn = clock.getTurn() + (duration / (counter == 0 ? 1 : counter));
    
    //    stateEndMinTurn = clock.getTurn() + ((duration / counter) + maxDuration) / 2;
    //    stateEndMinTurn = maxDuration;
  }

  private void changeToGreen(final Phase phase) {
    nextPhase = phase;
    if ( transitionDuration > 0 ) {
      setTransition();
    }
    else {
      setGreen( phase );
    }

  }

  //  private void changeToGreen(Lane l) {
  //    greenLane = l;
  //
  //    if ( transitionDuration > 0 ) {
  //      setTransition();
  //    }
  //    else {
  //      setGreen( l );
  //    }
  //
  //  }

  private void setTransition() {
    state = State.TRANSITION;
    stateEndMinTurn = clock.getTurn() + transitionDuration;
  }

  //  private void setGreen(Lane l) {
  //    blockView.ext( l ).unblock();
  //    state = State.GREEN;
  //    stateEndMinTurn = clock.getTurn() + evalView.ext( l ).getMinGreenDuration();
  //  }

  private String chooseDirection() {
    String chosenDirection = agent.getChosenDirection();
//    System.err.print( intersection.getId() + ":" + chosenDirection + " , " );
    //evalView(intersectin)
    return chosenDirection;
  }

  public Agent getAgent() {
    return agent;
  }

  //  private Lane choseLane() {
  //    Lane chosenLane = null;
  //    float chosenEvaluation = Float.NEGATIVE_INFINITY;
  //    //    float chosenEvaluation = -1.0f;
  //    for (Iterator<Link> linkIter = intersection.inboundLinkIterator(); linkIter.hasNext();) {
  //      Link link = linkIter.next();
  //      if ( logger.isTraceEnabled() ) {
  //        logger.trace( link );
  //      }
  //
  //      for (Iterator<Lane> laneIter = link.laneIterator(); laneIter.hasNext();) {
  //        Lane lane = laneIter.next();
  //
  //        float evaluation = evalView.ext( lane ).getEvaluation();
  //        if ( logger.isTraceEnabled() ) {
  //          logger.trace( lane + " " + evaluation + ", chosen= " + chosenEvaluation );
  //        }
  //        if ( evaluation > chosenEvaluation ) {
  //          chosenLane = lane;
  //          chosenEvaluation = evaluation;
  //        }
  //      }
  //    }
  //
  //    if ( logger.isTraceEnabled() ) {
  //      logger.trace( chosenLane );
  //    }
  //
  //    // TODO: this is tricky, it should only work wll for SOTL
  //    //if ( chosenEvaluation == 0.0f ) {
  //    //  return null;
  //    //}
  //    // --- 
  //    //    if ( chosenLane == null ) {
  //    //      if ( ok ) {
  //    //        ok = false;
  //    //        logger.error( "PROBLEM on" + intersection );
  //    //        logger.setLevel( Level.TRACE );
  //    //        makeDecision();
  //    //      }
  //    //      else {
  //    //
  //    //        System.exit( 0 );
  //    //      }
  //    //    }
  //    // ---
  //    return chosenLane;
  //  }
}
