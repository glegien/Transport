package pl.edu.agh.cs.kraksim.optapo.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import pl.edu.agh.cs.kraksim.optapo.algo.agent.Direction;

/**
 * This class is used to store a particular solution to a
 * constraint problem between multiple agents
 * <p>
 * Created: Tue Aug 20 14:14:41 2002
 * 
 * @author Roger Mailler
 */
public class Solution extends HashMap<String, Direction> implements Cloneable
{

  private static final long serialVersionUID = 3852870293097308715L;

  double                    effect           = 0.0;
  double                    cost             = 0.0;

  public double getCost() {
    return cost;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

  public double getEffect() {
    return effect;
  }

  public void setEffect(double val) {
    effect = val;
  }

  public boolean equals(Object o1) {
    int found = 0;
    Solution other = (Solution) o1;
    if ( this.size() != other.size() ) return false;
    for (Iterator<String> i = this.keySet().iterator(); i.hasNext();) {
      String name = i.next();
      if ( other.get( name ).equals( this.get( name ) ) ) found++;
    }
    if ( found == this.size() )
      return true;
    else return false;
  }

  /**
   * Determines if one solution is a subset of another
   */
  public boolean subList(Solution soln) {
    boolean ret = true;
    for (Iterator<String> i = soln.keySet().iterator(); i.hasNext();) {
      String agent = i.next();
      Direction value = (Direction) soln.get( agent );
      if ( !this.containsKey( agent ) || !this.get( agent ).equals( value ) ) {
        ret = false;
        break;
      }
    }
    return ret;
  }

  public String toString() {
    String ret = "";
    for (Iterator<String> i = this.keySet().iterator(); i.hasNext();) {
      String name = i.next();
      ret = ret + name + "-" + this.get( name );
      if ( i.hasNext() ) ret = ret + ",";
    }
    return ret;
  }

  public Object clone() {
    Solution newOne = new Solution();
    for (Iterator<String> i = this.keySet().iterator(); i.hasNext();) {
      String name = i.next();
      newOne.put( name, this.get( name ) );
    }
    newOne.setEffect( effect );
    return newOne;
  }

  /**
   * Checks to see if an agent is contained in the solution
   * 
   * @param name the name of the agent to check for
   * @return true if the solution contains the agent
   */
  public boolean containsAgent(String name) {
    return (this.containsKey( name ));
  }

  /**
   * Checks to see if an agent is contained in the solution
   * 
   * @param name the name of the agent to check for
   * @return true if the solution contains the agent
   */
  public ArrayList<String> getAgents() {
    return (new ArrayList<String>( this.keySet() ));
  }

} // Solution
