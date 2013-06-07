package pl.edu.agh.cs.kraksim.ministat;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Gateway;

final class StatHelper
{
  private static final Logger        logger = Logger.getLogger( StatHelper.class );
  private Map<Object, TravelDetails> tdMap;

  private int                        cityCarCount;
  private int                        cityTravelCount;
  private float                      cityTravelLength;
  private float                      cityTravelDuration;

  private float cityAvgCarSpeed;
  
  final static class TravelDetails
  {
    private GatewayMiniStatExt entranceGateway;
    private int                entranceTurn;
    private int                length;

    TravelDetails(GatewayMiniStatExt entranceGateway, int entranceTurn) {
      this.entranceGateway = entranceGateway;
      this.entranceTurn = entranceTurn;
    }
  }

  StatHelper() {
    tdMap = new HashMap<Object, TravelDetails>();
    if ( logger.isTraceEnabled() ) {
      logger.trace( "StatHelper init<> " );
    }
  }

  void clear() {
    if ( logger.isTraceEnabled() ) {
      logger.trace( " " );
    }

    tdMap.clear();
    cityCarCount = 0;
    cityTravelCount = 0;
    cityTravelLength = 0.0f;
    cityTravelDuration = 0.0f;
    cityAvgCarSpeed = 0.0f;
  }
  
  void beginTravel(Object driver, GatewayMiniStatExt entranceGateway, int turn) {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "Trip: " + driver + ", start time=" + turn );
    }

    cityCarCount++;
    tdMap.put( driver, new TravelDetails( entranceGateway, turn ) );
  }

  void incTravelLength(Object driver, int delta) {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "difference=" + delta );
    }

    tdMap.get( driver ).length += delta;
  }

  TravelDetails endTravel(Object driver, Gateway exitGateway, int turn) {
    TravelDetails td = tdMap.remove( driver );
    cityCarCount--;
    
    int duration = turn - td.entranceTurn;

    td.entranceGateway.noteTravel( exitGateway, td.length, duration );

    cityTravelCount++;
    cityTravelLength += td.length;
    cityTravelDuration += duration;

    if ( logger.isTraceEnabled() ) {
      logger.trace( "Trip: " + driver + ", len=" + td.length + ", dur=" + duration );
    }
    
    cityAvgCarSpeed += td.length / duration;
    if(cityAvgCarSpeed != 0)
    	cityAvgCarSpeed /= 2;
    	

    return td;
  }
  
  float getCityAvgCarSpeed(){
	  return cityAvgCarSpeed;
  }

  int getCityCarCount() {
    return cityCarCount;
  }

  int getCityTravelCount() {
    return cityTravelCount;
  }

  float getCityAvgVelocity() {
    return cityTravelDuration > 0.0f ? cityTravelLength / cityTravelDuration : 0.0f;
  }

  float getCityTravelLength() {
    return cityTravelLength;
  }

  float getCityTravelDuration() {
    return cityTravelDuration;
  }
}
