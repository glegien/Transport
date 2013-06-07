package pl.edu.agh.cs.kraksim.optapo.algo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.edu.agh.cs.kraksim.optapo.algo.agent.Agent;
import pl.edu.agh.cs.kraksim.optapo.algo.agent.Conflict;
import pl.edu.agh.cs.kraksim.optapo.algo.agent.Direction;

public class Solver
{

  double                                  currInnerBound = Double.MAX_VALUE;
  double                                  currOuterBound = Double.MAX_VALUE;
  double                                  initialBound   = Double.MAX_VALUE;

  Direction                               currSoln[];
  Direction                               bestSoln[];

  List<Agent>                             goodlist;
  // ArrayList<Agent> agent_view;
  // HashMap<String, Agent> agent_view;
  HashMap<String, Map<Direction, Double>> costTable;
  private AgentFarm                       farm;

  /**
   * Given the preferences and the known interaction structure,
   * find the best possible solution
   */
  public Solution findBestSoln(AgentFarm farm,
      List<Agent> goodlist,
      Map<String, Map<Direction, List<Conflict>>> preferences,
      double lowerBound,
      double upperBound)
  {
    this.farm = farm;
    Solution soln = new Solution();

    currInnerBound = upperBound;
    currOuterBound = Integer.MAX_VALUE;
    initialBound = lowerBound;

//    System.out.println( "Solver.findBestSoln()" + currInnerBound + " " + currOuterBound + " "
//                        + initialBound + " " + goodlist );
    currSoln = new Direction[goodlist.size()];
    bestSoln = new Direction[goodlist.size()];

    this.goodlist = goodlist;

    createCostTable( preferences );

    findSoln( 0, 0, 0 );

    for (int i = 0; i < bestSoln.length; i++) {
      soln.put( goodlist.get( i ).getName(), bestSoln[i] );
    }
    soln.setEffect( currInnerBound + currOuterBound );
    soln.setCost( currInnerBound );

    return soln;
  }

  /**
   * Does a two criteria branch and bound search for the best
   * solution
   */
  private void findSoln(int agent, double innerCount, double outerCount) {
//    System.out.println( currInnerBound + " - " + innerCount + " - " + outerCount );
    // is this the bottom of the recursion?
    if ( agent == goodlist.size() ) {
      // all variables assigned, found a better assignment
      for (int i = 0; i < bestSoln.length; i++) {
        bestSoln[i] = currSoln[i];
      }

      currInnerBound = innerCount;
      currOuterBound = outerCount;

      return;
    }

    // String thisAName = goodlist.get( agent );
    Agent thisA = goodlist.get( agent );
    String thisAName = thisA.getName();
    // ArrayList<String> domain = thisA.getDomain();

    Direction[] domain = Direction.values();

    for (int i = 0; i < domain.length; i++) {
      Direction domVal = domain[i];

      double thisCost = 0;

      for (Iterator<Agent> neighAgent = thisA.getNeighbours().iterator(); neighAgent.hasNext();)
      {
        Agent neigh = neighAgent.next();

        int neighIndex = goodlist.indexOf( neigh );

        if ( neighIndex > -1 && neighIndex < agent ) {
          // Constraint constraint = thisA.getConstraint(
          // neighName );
          thisCost += thisA.checkRelation( domVal, currSoln[neighIndex], neigh );
        }
      }

      if ( innerCount + thisCost < currInnerBound ) {
        double thisOutCost = 0;

        // check to see if better than the outerbound
        if ( costTable.containsKey( thisAName ) ) {

          Map<Direction, Double> prefs = costTable.get( thisAName );
          Double violates = prefs.get( domVal );
          thisOutCost += violates;
        }

        // if (innerCount + thisCost < currInnerBound
        // || (innerCount + thisCost == currInnerBound &&
        // outerCount
        // + thisOutCost < currOuterBound)) {

        currSoln[agent] = domVal;

        findSoln( agent + 1, innerCount + thisCost, outerCount + thisOutCost );

        // if (currInnerBound == initialBound && currOuterBound
        // == 0)

        if ( currInnerBound == initialBound ) return;

        // }
      }
    }
  }

  /**
   * Get the total outer cost of choosing the value which leads
   * to this list of violations
   * 
   * @parm violates A list of agent that will be violated with
   *       their associated value
   */
  public double getCostViolates(List<Conflict> violates, List<Agent> goodlist) {
    double totalCost = 0;
    for (Iterator<Conflict> j = violates.iterator(); j.hasNext();) {
      Conflict conf = j.next();
      String violName = conf.getName();
      double cost = conf.getCost();
      if ( !goodlist.contains( farm.getAgent( violName ) ) ) {
        totalCost += cost;
      }
    }

    return totalCost;
  }

  /**
   * This function creates a cost table that is used to determine
   * the outside costs for forcing an agent to change their
   * domain value
   * 
   * @param preferences
   */
  // private void createCostTable(HashMap<String, HashMap<String,
  // HashMap<String, Integer>>> preferences)
  private void createCostTable(Map<String, Map<Direction, List<Conflict>>> preferences) {
    costTable = new HashMap<String, Map<Direction, Double>>();

    for (Iterator<String> agentNames = preferences.keySet().iterator(); agentNames.hasNext();)
    {
      String agentName = agentNames.next();
      Map<Direction, List<Conflict>> prefs = preferences.get( agentName );

      HashMap<Direction, Double> costs = new HashMap<Direction, Double>();
      for (Iterator<Direction> domVals = prefs.keySet().iterator(); domVals.hasNext();) {
        Direction domVal = domVals.next();
        costs.put( domVal, getCostViolates( prefs.get( domVal ), this.goodlist ) );
      }

      costTable.put( agentName, costs );
    }
  }

}
