package pl.edu.agh.cs.kraksim.real;

import java.util.Iterator;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Link;

class IntersectionRealExt extends NodeRealExt
{
  private static final Logger logger = Logger.getLogger( IntersectionRealExt.class );
  private final Intersection  intersection;

  IntersectionRealExt(Intersection intersection, RealEView ev) {
    super( ev );
    if ( logger.isTraceEnabled() ) {
      logger.trace( "Constructing." );
    }
    this.intersection = intersection;
  }

  void findApproachingCars() {
    if ( logger.isTraceEnabled() ) {
      logger.trace( intersection.getId() );
    }
    for (Iterator<Link> iter = intersection.inboundLinkIterator(); iter.hasNext();) {
      ev.ext( iter.next() ).findApproachingCars();
    }
  }

  public void blockInboundLinks() {
    for (Iterator<Link> iter = intersection.inboundLinkIterator(); iter.hasNext();) {
      ev.ext( iter.next() ).block();
    }
  }

  public void unblockInboundLinks() {
    for (Iterator<Link> iter = intersection.inboundLinkIterator(); iter.hasNext();) {
      ev.ext( iter.next() ).unblock();
    }
  }
}
