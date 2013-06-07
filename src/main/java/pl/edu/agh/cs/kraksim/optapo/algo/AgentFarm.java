package pl.edu.agh.cs.kraksim.optapo.algo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import pl.edu.agh.cs.kraksim.optapo.algo.agent.Agent;
import pl.edu.agh.cs.kraksim.optapo.algo.agent.AgentInfo;
import pl.edu.agh.cs.kraksim.optapo.algo.agent.Direction;

public class AgentFarm
{

  Map<String, Agent> agents = new HashMap<String, Agent>();

  public void init() {
    // INIT
    for (Agent agent : agents.values()) {
      agent.init();
    }
  }

  public void cycle() throws InterruptedException {
    int i = 0;
    int size = agents.size();
    // CHECKAgentView
    Queue<Agent> queue = new LinkedList<Agent>( agents.values() );
    while ( queue.size() > 0 ) {
      // System.out.println( queue );
      Agent agent = queue.poll();
      boolean finished = agent.pulse();
      if ( !finished ) {
        queue.offer( agent );
      }
//      Thread.sleep( 10 );
      ++i;
//      System.out.print( (++i % 80 == 0) ? (i + ".\n") : "." );
      if ( i > (10 * size) ) {
        break;
      }

    }
  }

  public static void main(String[] args) throws InterruptedException {
    AgentFarm farm = new AgentFarm();

    Agent a1 = new Agent( farm, "A1", Direction.NS );
    Agent a2 = new Agent( farm, "A2", Direction.NS );
    Agent a3 = new Agent( farm, "A3", Direction.NS );
    Agent a4 = new Agent( farm, "A4", Direction.WE );
    Agent a5 = new Agent( farm, "A5", Direction.NS );

    Agent b1 = new Agent( farm, "B1", Direction.NS );
    Agent b2 = new Agent( farm, "B2", Direction.NS );
    Agent b3 = new Agent( farm, "B3", Direction.WE );
    Agent b4 = new Agent( farm, "B4", Direction.NS );
    Agent b5 = new Agent( farm, "B5", Direction.NS );

    Agent c1 = new Agent( farm, "C1", Direction.NS );
    Agent c2 = new Agent( farm, "C2", Direction.NS );
    Agent c3 = new Agent( farm, "C3", Direction.NS );
    Agent c4 = new Agent( farm, "C4", Direction.NS );
    Agent c5 = new Agent( farm, "C5", Direction.NS );

    // A

    a1.addNeighbor( new AgentInfo( a2.getName(), Direction.WE, 30 ) );
    a1.addNeighbor( new AgentInfo( b1.getName(), Direction.NS, 10 ) );

    a2.addNeighbor( new AgentInfo( a1.getName(), Direction.WE, 30 ) );
    a2.addNeighbor( new AgentInfo( b2.getName(), Direction.NS, 10 ) );
    a2.addNeighbor( new AgentInfo( a3.getName(), Direction.WE, 30 ) );

    a3.addNeighbor( new AgentInfo( a2.getName(), Direction.WE, 30 ) );
    a3.addNeighbor( new AgentInfo( b3.getName(), Direction.NS, 10 ) );
    a3.addNeighbor( new AgentInfo( a4.getName(), Direction.WE, 30 ) );

    a4.addNeighbor( new AgentInfo( a3.getName(), Direction.WE, 30 ) );
    a4.addNeighbor( new AgentInfo( b4.getName(), Direction.NS, 10 ) );
    a4.addNeighbor( new AgentInfo( a5.getName(), Direction.WE, 30 ) );

    a5.addNeighbor( new AgentInfo( a4.getName(), Direction.WE, 30 ) );
    a5.addNeighbor( new AgentInfo( b5.getName(), Direction.NS, 10 ) );

    // B
    b1.addNeighbor( new AgentInfo( a1.getName(), Direction.NS, 10 ) );
    b1.addNeighbor( new AgentInfo( b2.getName(), Direction.WE, 30 ) );
    b1.addNeighbor( new AgentInfo( c1.getName(), Direction.NS, 10 ) );

    b2.addNeighbor( new AgentInfo( b1.getName(), Direction.WE, 30 ) );
    b2.addNeighbor( new AgentInfo( a2.getName(), Direction.NS, 20 ) );
    b2.addNeighbor( new AgentInfo( b3.getName(), Direction.WE, 30 ) );
    b2.addNeighbor( new AgentInfo( c2.getName(), Direction.NS, 20 ) );

    b3.addNeighbor( new AgentInfo( b2.getName(), Direction.WE, 30 ) );
    b3.addNeighbor( new AgentInfo( a3.getName(), Direction.NS, 10 ) );
    b3.addNeighbor( new AgentInfo( b4.getName(), Direction.WE, 30 ) );
    b3.addNeighbor( new AgentInfo( c3.getName(), Direction.NS, 10 ) );

    b4.addNeighbor( new AgentInfo( a4.getName(), Direction.NS, 20 ) );
    b4.addNeighbor( new AgentInfo( b3.getName(), Direction.WE, 30 ) );
    b4.addNeighbor( new AgentInfo( b5.getName(), Direction.WE, 30 ) );
    b4.addNeighbor( new AgentInfo( c4.getName(), Direction.NS, 20 ) );

    b5.addNeighbor( new AgentInfo( a5.getName(), Direction.NS, 10 ) );
    b5.addNeighbor( new AgentInfo( b4.getName(), Direction.WE, 30 ) );
    b5.addNeighbor( new AgentInfo( c5.getName(), Direction.NS, 10 ) );

    // C
    c1.addNeighbor( new AgentInfo( b1.getName(), Direction.NS, 10 ) );
    c1.addNeighbor( new AgentInfo( c2.getName(), Direction.WE, 30 ) );
    // c1.addNeighbor( new AgentInfo( c1.getName(), Direction.NS,
    // 10 ) );

    c2.addNeighbor( new AgentInfo( c1.getName(), Direction.WE, 30 ) );
    c2.addNeighbor( new AgentInfo( b2.getName(), Direction.NS, 20 ) );
    c2.addNeighbor( new AgentInfo( c3.getName(), Direction.WE, 30 ) );
    // c2.addNeighbor( new AgentInfo( d2.getName(), Direction.NS,
    // 20 ) );

    c3.addNeighbor( new AgentInfo( c2.getName(), Direction.WE, 30 ) );
    c3.addNeighbor( new AgentInfo( b3.getName(), Direction.NS, 10 ) );
    c3.addNeighbor( new AgentInfo( c4.getName(), Direction.WE, 30 ) );

    c4.addNeighbor( new AgentInfo( c3.getName(), Direction.WE, 30 ) );
    c4.addNeighbor( new AgentInfo( b4.getName(), Direction.NS, 10 ) );
    c4.addNeighbor( new AgentInfo( c5.getName(), Direction.WE, 30 ) );

    c5.addNeighbor( new AgentInfo( c4.getName(), Direction.WE, 30 ) );
    c5.addNeighbor( new AgentInfo( b5.getName(), Direction.NS, 10 ) );

    farm.addAgent( a1 );
    farm.addAgent( a2 );
    farm.addAgent( a3 );
    farm.addAgent( a4 );
    farm.addAgent( a5 );

    farm.addAgent( b1 );
    farm.addAgent( b2 );
    farm.addAgent( b3 );
    farm.addAgent( b4 );
    farm.addAgent( b5 );

    farm.addAgent( c1 );
    farm.addAgent( c2 );
    farm.addAgent( c3 );
    farm.addAgent( c4 );
    farm.addAgent( c5 );

    farm.drawSituation();
    farm.init();
    farm.cycle();
  }

  public void addAgent(Agent agent) {
    agents.put( agent.getName(), agent );
  }

  public Agent getAgent(String agentName) {
    // System.out.println( "AgentFarm.getAgent()" + agentName );
    return agents.get( agentName );
  }

  public void drawSituation() {

//    for (char c = 'A'; c < 'D'; c++) {
//      for (int i = 1; i < 6; i++) {
//        // System.out.println( "AgentFarm.drawSituation()" +
//        // Character.toString( c ) + i );
//        Agent agent = agents.get( Character.toString( c ) + i );
////        System.out.print( agent.getName() + " " + agent.getValue() + " " );
//      }
////      System.out.println( "" );
//    }

    // for (Entry<String, Agent> entry : set) {
    // System.out.println( entry.getKey() + " " +
    // entry.getValue().getValue() );
    // }

  }
}
