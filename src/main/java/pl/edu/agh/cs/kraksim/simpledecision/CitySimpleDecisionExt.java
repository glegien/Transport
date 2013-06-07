package pl.edu.agh.cs.kraksim.simpledecision;

import java.util.Iterator;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.iface.decision.CityDecisionIface;
import pl.edu.agh.cs.kraksim.sna.SnaConfigurator;
import pl.edu.agh.cs.kraksim.sna.centrality.KmeansClustering;

class CitySimpleDecisionExt implements CityDecisionIface
{
  private static final Logger       logger = Logger.getLogger( CitySimpleDecisionExt.class );
  private final City                city;
  private final SimpleDecisionEView ev;
  
  private static int turn = 1;

  CitySimpleDecisionExt(City city, SimpleDecisionEView ev) {
    this.city = city;
    this.ev = ev;
  }

  public void initialize() {
    
    for (Iterator<Intersection> iter = city.intersectionIterator(); iter.hasNext();) {
      ev.ext( iter.next() ).initialize();
    }
  }

  public void turnEnded() {
    if ( logger.isTraceEnabled() ) {
      logger.trace( "Changing Lights" );
    }

    //Uruchamiamy negocjacje oraz optymalizacje co okreœlon¹ liczbê iteracji
    if(turn % SnaConfigurator.getSnaRefreshInterval() == 0){
			for (Node inter : KmeansClustering.currentClustering.keySet()) {
				((Intersection) inter).optimalizeLights();
			}
			for (Node inter : KmeansClustering.currentClustering.keySet()) {
				((Intersection) inter).optimalizeLights();
			}
			
			for (Iterator<Intersection> iter = city.intersectionIterator(); iter.hasNext();) {
				iter.next().minorLightOptimalization();
			}
			turn = 1;
    }
    turn++;	
    
    for (Iterator<Intersection> iter = city.intersectionIterator(); iter.hasNext();) {
      ev.ext( iter.next() ).makeDecision();
    }
  }
}
