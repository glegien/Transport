package pl.edu.agh.cs.kraksim.optapo.algo.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.optapo.algo.AgentFarm;
import pl.edu.agh.cs.kraksim.optapo.algo.Solution;
import pl.edu.agh.cs.kraksim.optapo.algo.Solver;

public class Agent
{
  private final Logger logger = Logger.getLogger( Agent.class );

  private enum Mediate {
    ACTIVE,
    PASSIVE,
    NONE
  }
  private AgentFarm              farm;
  private static Random          rg         = new Random();
  // d_i
  private Direction              value;
  private String                 name;
  private int                    priority;

  private double                 lowerBound = 0.0;
  private double                 cost       = 0.0;

  private State                  state      = State.INITIALIZE;
  private Collection<Agent>      good_list  = new HashSet<Agent>();
  //  private List<Agent>            good_list  = new ArrayList<Agent>();
  // TODO: change it
  private boolean                mediate    = false;
  private Mediate                m          = Mediate.NONE;

  private int                    step       = 0;
  private Map<String, AgentInfo> neighList  = new HashMap<String, AgentInfo>();
  private Solver                 solver     = new Solver();

  public Agent(AgentFarm farm, String name, Direction direction) {
    this.farm = farm;
    this.name = name;
    this.value = direction;
  }

  public boolean pulse() {
    logger.trace( "Agent[" + name + "].pulse()" );
    boolean finished = false;

    switch ( step )
    {
    case 0:
      checkAgentView();
      step++;
      break;

    case 1:
      checkMediate();
      step++;
      break;

    case 2:
      if ( Mediate.ACTIVE.equals( m ) ) {
        logger.trace( "ACTIVE MEDIATION" );
        //        System.out.println( "ACTIVE MEDIATION: " + name );
        mediate();
      }
      step++;
      // send Messages, and then receive responses....

      break;

    case 3:
      logger.trace( "MEDIATION 2 RECEIVING RESPONSES" );
      // send Messages, and then receive responses....
      chooseSolution();
      step = 0;
      break;

    default:
      break;
    }

    return finished;
  }

  // mediate and choose solution
  private void mediate() {
    boolean trialSucceded = tryLocalChange();
    Map<String, Map<Direction, List<Conflict>>> preferences = new HashMap<String, Map<Direction, List<Conflict>>>();
    // TODO: check if local trial was success, and do not
    // mediate!!!!
    //    System.out.println( "TRIAL SUCcess: " + trialSucceded );
    if ( !trialSucceded ) {
      for (Agent agent : good_list) {
        Map<Direction, List<Conflict>> conflictsMap = agent.evaluateAsk( this );

        // conflictsMap.size() == 0 means wait
        if ( conflictsMap.size() > 0 ) {
          preferences.put( agent.getName(), conflictsMap );
        }
      }

      logger.trace( preferences );

      // System.out.println( preferences );
      //      System.out.println( "PREFERENCEF ARE :" );
      //      printConflictingAgents( preferences );

      Solution solution = findSolution( preferences );

//      System.out.println( "Agent: " + name + ", solution: " + solution );
//      System.out.println( "\teffect: " + solution.getEffect() + ", cost:  "
//                          + solution.getCost() );
      for (Map.Entry<String, Direction> sol : solution.entrySet()) {
        String agentName = sol.getKey();
        Direction newValue = sol.getValue();
        if ( newValue != null ) {
//          System.out.println( agentName + "] " + name + "->val=" + newValue );
          farm.getAgent( agentName ).acceptRequest( newValue, this );
        }
      }
    }
    //    farm.drawSituation();
//    System.out.println( "AFTER:" );
    addConflictingAgents( preferences );
  }

  private void acceptRequest(Direction newValue, Agent agent) {
    m = Mediate.NONE;
    mediate = false;
    if ( !newValue.equals( value ) ) {
      //           System.out.println( agent.getName() + "] " + name + "->val=" + newValue );
      value = newValue;
    }
    // TODO? checkAgentView?
  }

  private void addConflictingAgents(Map<String, Map<Direction, List<Conflict>>> preferences)
  {
//    System.out.println( "Agent: " + name + ", good_list: " + good_list );
//    System.out.println( "Conflicts for agent: " + name );

    for (Map.Entry<String, Map<Direction, List<Conflict>>> element : preferences
        .entrySet())
    {
//      System.out.print( element.getKey() );
      Map<Direction, List<Conflict>> agentConflicts = element.getValue();

      for (Map.Entry<Direction, List<Conflict>> dirConflicts : agentConflicts.entrySet())
      {
//        System.out.println( "\t" + dirConflicts.getKey() + " -> "
//                            + dirConflicts.getValue() );
        List<Conflict> conflicts = dirConflicts.getValue();

        for (Conflict conflict : conflicts) {
          double conflictCost = conflict.getCost();
          String conflictingAgent = conflict.getName();
          if ( conflictCost > 0 ) {
            addAgent( conflictingAgent );
          }

        }
      }
    }

  }

  private void addAgent(String conflictingAgent) {
    Agent ag = farm.getAgent( conflictingAgent );
    this.addAgent( ag );
    ag.addAgent( this );

//    System.out.println( "Agent.addAgent(): " + conflictingAgent );
  }

  public void addAgent(Agent a) {
    if ( !a.getName().equals( this.getName() ) ) {
      good_list.add( a );
    }
  }

  private void printConflictingAgents(Map<String, Map<Direction, List<Conflict>>> preferences)
  {
//    System.out.println( "Agent: " + name + ", good_list: " + good_list );
//    System.out.println( "Conflicts for agent: " + name );
    for (Map.Entry<String, Map<Direction, List<Conflict>>> element : preferences
        .entrySet())
    {
      String agentName = element.getKey();
      Map<Direction, List<Conflict>> agentConflicts = element.getValue();
//      System.out.print( agentName );

      for (Map.Entry<Direction, List<Conflict>> dirConflicts : agentConflicts.entrySet())
      {
//        System.out.println( "\t" + dirConflicts.getKey() + " -> "
//                            + dirConflicts.getValue() );
      }
    }
  }

  private Solution findSolution(Map<String, Map<Direction, List<Conflict>>> preferences) {

    List<Agent> list = new ArrayList<Agent>( good_list );
    list.add( this );
    Solution sol = solver.findBestSoln( farm, list, preferences, lowerBound, cost + 1.0 );
    // update lowerBound, check The Effect
    lowerBound = sol.getCost();

    // System.out.println( "Agent.findSolution() COST=" +
    // sol.getCost() );
    // System.out.println( "Agent.findSolution() EFF=" +
    // sol.getEffect() );
    // System.out.println( "Agent[" + name + "].findSolution()" +
    // name + "->" + sol );
    // farm.drawSituation();

    return sol;
    // logger.trace(name + "->" + sol );
  }

  private Map<Direction, List<Conflict>> evaluateAsk(Agent xj) {
    //   TODO:  cache?
    Map<Direction, List<Conflict>> valueConflictMap = new HashMap<Direction, List<Conflict>>();
    // TODO: waitFor...
    // check if there is an agent that wants mediation, with
    // higher priority than xj
    // boolean isAnotherActive = false;
    // if ( (mediate || isAnotherActive) &&
    // (Mediate.ACTIVE.equals( xj.getM() )) ) {
    // // s
    // System.out.println( "WAIT WAIT" );
    // }
    // else {
    //    System.out.println( "Agent[" + name + "].evaluateAsk() " );
    Direction[] array = Direction.values();
    for (int i = 0; i < array.length; i++) {
      List<Conflict> conflicts = evaluateDirectionCost( array[i] );
      valueConflictMap.put( array[i], conflicts );
    }
    // }

    //    for (Map.Entry<Direction, List<Conflict>> dirConflicts : valueConflictMap.entrySet()) {
    //      System.out.println( "\t" + dirConflicts.getKey() + " -> " + dirConflicts.getValue() );
    //    }

    return valueConflictMap;
  }

  // private Mediate getM() {
  // return m;
  // }

  private boolean tryLocalChange() {

    boolean trialSucceded = false;
    if ( value.equals( Direction.NS ) ) {
      setValue( Direction.WE );
      double tempCost = evaluate( this );
      logger.trace( "Agent.tryLocalChange() tmp:" + tempCost + " current:" + cost );
      if ( tempCost > lowerBound ) {
        setValue( Direction.NS );
      }
      else {
        trialSucceded = true;
      }
    }
    else {
      setValue( Direction.NS );
      double tempCost = evaluate( this );
      logger.trace( "Agent.tryLocalChange() tmp:" + tempCost + " current:" + cost );
      if ( tempCost > lowerBound ) {

        setValue( Direction.WE );
      }
      else {
        trialSucceded = true;
      }
    }

    return trialSucceded;
  }

  private void chooseSolution() {
  // TODO Auto-generated method stub

  }

  /**
   * 
   */
  private void checkAgentView() {
    m = Mediate.NONE;
    mediate = false;
    logger.trace( "Agent[" + name + "].pulse()" );
    cost = evaluate( this );
    logger.trace( " System COST " + cost );

    if ( cost > lowerBound ) {
      // TODO:
      mediate = true;
    }
  }

  // -------------------------------------------------------------------------
  // INITIALIZE
  // -------------------------------------------------------------------------
  public void init() {
    logger.trace( "Agent[" + name + "].init()" );
    good_list.addAll( getNeighbours() );
    //    System.out.println( "Agent.init() :: " + good_list );
    // d_i
    value = getInitialValue();
    // F_i*
    lowerBound = 0.0;
    // p_i
    priority = totalIncomingVehicles();
    name = getInitalName();

    // func = getInitialFunc();

    // good_list.add( this );

    mediate = false;
  }

  private void checkMediate() {
    // [[[
    if ( cost > lowerBound ) {
      // TODO: check this function with article
      if ( isTheHighestPriority() ) {
        m = Mediate.ACTIVE;
      }
      else {
        m = Mediate.PASSIVE;
      }
      logger.trace( "Agent[" + name + "].checkAgentView() MEDIATE:" + m );
      //      System.out.println( "Agent[" + name + "].checkAgentView() MEDIATE:" + m );
    }
  }

  public boolean wantsMediation() {
    return mediate;
  }

  private boolean isTheHighestPriority() {
    boolean highest = true;
    // TODO: nullpointer!!!! why
    for (Agent agent : good_list) {
      //      if ( agent == null ) {
      //        System.out.println( "Agent.isTheHighestPriority() :::" + good_list );
      //      }
      //      System.out.println( "Agenbt---" + agent );
      if ( agent.wantsMediation() ) {
        int prior = agent.getPriority();
        if ( prior > priority ) {
          highest = false;
        }
      }
    }

    return highest;
  }

  private int getPriority() {
    return priority;
  }

  private String getInitalName() {
    return name;
  }

  private Direction getInitialValue() {
    Direction initValue = null;
    if ( value == null ) {
      Direction[] dm = Direction.values();
      int i = rg.nextInt( dm.length - 1 );
      initValue = dm[i];
    }
    initValue = value;
    return initValue;
  }

  public Collection<Agent> getGoodList() {
    return good_list;
  }

  public List<Agent> getNeighbours(Direction ns) {
    List<Agent> result = new ArrayList<Agent>();

    for (Entry<String, AgentInfo> agentName : neighList.entrySet()) {
      if ( ns.equals( agentName.getValue().getDir() ) ) {
        logger.trace( "Agent[" + name + "].getNeighbours()" + ns + " " + agentName );
        Agent agent = farm.getAgent( agentName.getKey() );
        if ( agent == null ) {
          logger.trace( "NULL " + agentName.getKey() );
        }
        else {
          result.add( agent );
        }
      }
    }

    return result;
  }

  public List<Agent> getNeighbours() {
    List<Agent> result = new ArrayList<Agent>();

    for (Entry<String, AgentInfo> agentName : neighList.entrySet()) {
      Agent agent = farm.getAgent( agentName.getKey() );
      if ( agent == null ) {
        //        System.out.println( name + " trying to find:" + agentName + "NULL" );
      }
      else {
        result.add( agent );
      }
    }

    return result;
  }

  public Direction getValue() {
    return value;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  // private Direction higherTraffic;

  public double evaluate(Agent agent_i) {
    double globalCost = 0;
    Collection<Agent> goodList = agent_i.getGoodList();

    if ( incomingVehicles( Direction.NS ) >= incomingVehicles( Direction.WE ) ) {
      // higherTraffic = Direction.NS;
      List<Agent> j_list = agent_i.getNeighbours( Direction.NS );
      logger.trace( "NS" + j_list );
      for (Agent agent_j : j_list) {
        if ( goodList.contains( agent_j ) ) {
          globalCost += agent_i.relation( agent_j, Direction.NS );
        }
      }
    }
    else {
      // higherTraffic = Direction.WE;
      List<Agent> j_list = agent_i.getNeighbours( Direction.WE );
      logger.trace( "WE" + j_list );
      for (Agent agent_j : j_list) {
        if ( goodList.contains( agent_j ) ) {
          globalCost += agent_i.relation( agent_j, Direction.WE );
        }
      }
    }

    return globalCost;
  }

  public List<Conflict> evaluateDirectionCost(Direction value) {
    // double globalCost = 0;
    Direction higherTraffic;
    //    System.out.println( "\tAgent[" + name + "].evaluateDirectionCost( " + value + " )" );
    Collection<Agent> goodList = this.getGoodList();
    List<Conflict> conflicts = new ArrayList<Conflict>();

    if ( incomingVehicles( Direction.NS ) >= incomingVehicles( Direction.WE ) ) {
      higherTraffic = Direction.NS;
      findConflictsForDir( value, higherTraffic, goodList, conflicts );
    }
    else {
      higherTraffic = Direction.WE;
      findConflictsForDir( value, higherTraffic, goodList, conflicts );
    }

    //    System.out.println( "\t\tConflicts: " + conflicts );
    return conflicts;
  }

  private void findConflictsForDir(Direction value,
      Direction higherTraffic,
      Collection<Agent> goodList,
      List<Conflict> conflicts)
  {
    List<Agent> j_list = this.getNeighbours( higherTraffic );
    for (Agent agent_j : j_list) {
      if ( goodList.contains( agent_j ) ) {
        double tmpcost = this.relation( value, agent_j, higherTraffic );
        //        System.out.println( "\t\t\tRelation for agent " + agent_j.getName() + " = " + tmpcost );
        conflicts.add( new Conflict( agent_j.getName(), tmpcost ) );
      }
    }
  }

  public double relation(Agent agent_j, Direction hiTrafficDir) {
    double relationCost = getRelationCost( value, agent_j, hiTrafficDir );
    logger.trace( "                COST: " + relationCost );

    return relationCost;
  }

  public double relation(Direction value, Agent agent_j, Direction hiTrafficDir) {
    double relationCost = getRelationCost( value, agent_j, hiTrafficDir );
    logger.trace( "                COST: " + relationCost );

    return relationCost;
  }

  private double getRelationCost(Direction value, Agent agent_j, Direction hiTrafficDir) {
    double relationCost = 0;

    if ( value.equals( hiTrafficDir ) ) {
      if ( value.equals( agent_j.getValue() ) ) {
        relationCost = 0;
      }
      else {
        int totalInc = totalIncomingVehicles();
        relationCost = ((totalInc == 0) ? 0 : (double) incomingVehiclesFrom( agent_j )
                                              / totalInc);
      }
    }
    else {
      int totalInc = totalIncomingVehicles();
      relationCost = ((totalInc == 0) ? 0 : 2.0 * incomingVehiclesFrom( agent_j )
                                            / totalInc);
    }

    return relationCost;
  }

  public double checkRelation(Direction value_i, Direction value_j, Agent agent_j) {
    Direction hiTrafficDir;
    if ( incomingVehicles( Direction.NS ) >= incomingVehicles( Direction.WE ) ) {
      hiTrafficDir = Direction.NS;
    }
    else {
      hiTrafficDir = Direction.WE;
    }

    double relationCost = 0;
    // TODO: refactor
    if ( value_i.equals( hiTrafficDir ) ) {
      if ( value_i.equals( value_j ) ) {
        relationCost = 0;
      }
      else {
        int totalInc = totalIncomingVehicles();
        relationCost = ((totalInc == 0) ? 0 : (double) incomingVehiclesFrom( agent_j )
                                              / totalInc);
      }
    }
    else {
      int totalInc = totalIncomingVehicles();
      relationCost = ((totalInc == 0) ? 0 : 2.0 * incomingVehiclesFrom( agent_j )
                                            / totalInc);
    }
    logger.trace( "                COST: " + relationCost );
    return relationCost;
  }

  private int totalIncomingVehicles() {

    Collection<AgentInfo> neigh = neighList.values();

    int total = 0;
    for (AgentInfo agent : neigh) {
      if ( good_list.contains( farm.getAgent( agent.getName() ) ) ) {
        total += agent.getIncoming();
      }
    }

    return total;
  }

  private int incomingVehicles(Direction direction) {
    Collection<AgentInfo> neigh = neighList.values();
    int total = 0;
    for (AgentInfo agent : neigh) {
      if ( direction.equals( agent.getDir() ) ) {
        if ( good_list.contains( farm.getAgent( agent.getName() ) ) ) {
          total += agent.getIncoming();
        }

      }
    }

    return total;
  }

  private int incomingVehiclesFrom(Agent agent_j) {
    logger.trace( "Agent[" + name + "].incomingVehiclesFrom()" + agent_j );
    int i = neighList.get( agent_j.getName() ).getIncoming();
    return i;
  }

  public String getName() {
    return name;
  }

  public void addNeighbor(AgentInfo ag) {
    logger.trace( "Agent[" + name + "] adding info: " + ag );
    neighList.put( ag.getName(), ag );
  }

  public AgentInfo getNeighbor(String name) {
    return neighList.get( name );
  }

  public Collection<AgentInfo> getNeighborList() {
    return neighList.values();
  }

  @Override
  public String toString() {
    return name;
  }

  public void setValue(Direction value) {
    this.value = value;
  }

  public String getChosenDirection() {
    return value.toString();
  }

}
