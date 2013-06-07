package pl.edu.agh.cs.kraksim.optapo.algo.agent;

import org.apache.log4j.Logger;

public class AgentInfo
{
  private final Logger logger = Logger.getLogger( AgentInfo.class );
  private Direction    dir;
  private int          incoming;
  private String       name;

  public AgentInfo(String name, Direction d, int i) {
    logger.trace( "AgentiNFO " + name + " " + d + " " + incoming );
    this.name = name;
    dir = d;
    incoming = i;
  }

  public Direction getDir() {
    return dir;
  }

  public void setDir(Direction dir) {
    this.dir = dir;
  }

  public int getIncoming() {
    return incoming;
  }

  public void setIncoming(int incoming) {
    this.incoming = incoming;
  }

  public String getName() {
    return name;
  }
  
  @Override
  public String toString() {
   StringBuilder sb = new StringBuilder();
   sb.append( "AgentInfo=(" );
   sb.append( name ).append( ", " );
   sb.append( dir ).append( ", " );
   sb.append( incoming ).append( ")" );
   
    return sb.toString();
  }
}
