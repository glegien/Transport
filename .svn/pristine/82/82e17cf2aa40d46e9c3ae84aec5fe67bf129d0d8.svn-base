package pl.edu.agh.cs.kraksim.ministat;

import java.util.Iterator;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Link;

public class CityMiniStatExt
{
  private static final Logger logger = Logger.getLogger( CityMiniStatExt.class );
  final private City          city;
  final private MiniStatEView ev;
  final private StatHelper    helper;

  CityMiniStatExt(City city, MiniStatEView ev, StatHelper helper) {
    this.city = city;
    this.ev = ev;
    this.helper = helper;
    
    if ( logger.isTraceEnabled() ) {
      logger.trace( " " );
    }
    
  }

  public void clear() {
    if ( logger.isTraceEnabled() ) {
      logger.trace( " " );
    }
    helper.clear();

    for (Iterator<Gateway> iter = city.gatewayIterator(); iter.hasNext();) {
      Gateway g = iter.next();
      ev.ext( g ).clear();
    }

    for (Iterator<Link> iter = city.linkIterator(); iter.hasNext();) {
      Link l = iter.next();
      ev.ext( l ).clear();
    }
  }

  public int getCarCount() {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "CarCount=" + helper.getCityCarCount() );
    }

    return helper.getCityCarCount();
  }

  public int getTravelCount() {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "TravelCount=" + helper.getCityTravelCount() );
    }

    return helper.getCityTravelCount();
  }

  public float getAvgVelocity() {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "AverageVelocity=" + helper.getCityAvgVelocity() );
    }

    return helper.getCityAvgVelocity();
  }
  
  //TODO:
  public float getTravelLength() {
    return helper.getCityTravelLength();
  }

  public float getTravelDuration() {
    return helper.getCityTravelDuration();
  }
  
  public float getAvgCarSpeed(){
	  return helper.getCityAvgCarSpeed();
  }
  
  public double getAvgCarLoad(){
	  double avgLoad = 0;
	  Iterator<Link> iter = city.linkIterator();
	  while(iter.hasNext()){
		  avgLoad += iter.next().getLoad();
	  }
	  return avgLoad / city.linkCount();
  }
}
