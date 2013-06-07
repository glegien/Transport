package pl.edu.agh.cs.kraksim.real;

import java.util.ListIterator;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Action;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.sim.Route;
import pl.edu.agh.cs.kraksim.main.Driver;

final class Car {
	private static final Logger logger = Logger.getLogger(Car.class);

	final Driver driver;
	/*
	 * Iterator through route's link. linkIterator.next() is the next (not
	 * current!) link, the car will drive.
	 */
	protected ListIterator<Link> linkIterator;
	//  private ListIterator<Link> copyLinkIterator;

	protected Action action;
	int pos;
	int velocity;
	private Lane beforeLane;
	private int beforePos;

	private boolean rerouting = false;

	Car(Driver driver, Route route, boolean rerouting) {
		this.driver = driver;
		this.rerouting = rerouting;
		linkIterator = route.linkIterator();
		//    copyLinkIterator = route.linkIterator();
		// Important. See the notice above.
		linkIterator.next();
		//    copyLinkIterator.next();

		beforeLane = null;
		setBeforePos(0);

		if (logger.isTraceEnabled()) {
			logger.trace("\n Driver= " + driver + "\n rerouting= " + rerouting);
		}
	}

	//TODO: change this
	public boolean hasNextTripPoint() {
		return linkIterator.hasNext();
	}

	public Link nextTripPoint() {
		//    copyLinkIterator.next();
		return linkIterator.next();
	}

	public Link peekNextTripPoint() {
		//    copyLinkIterator.next();
		if (linkIterator.hasNext()) {
			Link result = linkIterator.next();
			linkIterator.previous();
			return result;
		} else
			return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(driver.toString());
		sb.append(" in [ CAR bPos=").append(getBeforePos());
		sb.append(",cPos=").append(pos);
		sb.append(",v=").append(velocity);
		sb.append("] ");

		return sb.toString();
	}

	public Action getAction() {
		if (logger.isTraceEnabled()) {
			logger.trace("\n Action= " + action + "\n Driver= " + driver);
		}
		return action;
	}

	public void setAction(Action action) {
		if (logger.isTraceEnabled()) {
			logger.trace("\n Action= " + action + "\n Driver= " + driver);
		}
		this.action = action;
	}

	public void refreshTripRoute() {
		// TODO: make it configurable from properties file
		//    ListIterator<Link> copyLinkIter = linkIterator;
		if (!linkIterator.hasNext())
			return;
		ListIterator<Link> newlinkIterator = null;
		if (rerouting) {
			newlinkIterator = driver.updateRouteFrom(linkIterator.next());
			linkIterator.previous();
			if (newlinkIterator != null) {
				linkIterator = newlinkIterator;

				if (logger.isTraceEnabled()) {
					logger.trace("New Route ");
				}
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("OLD Route ");
				}
			}
		}

		//        int li = 0;
		//        System.err.println( "\n-----" );
		//        System.err.println( "Distance:  " );
		//        for (Iterator<Link> iter = copyLinkIter; iter.hasNext();) {
		//          Link element = iter.next();
		//          li++;
		//          System.err.print( element.getId() + " " );
		//    
		//        }
		//        while ( li-- > 0 ) {
		//          copyLinkIter.previous();
		//        }
		//        System.err.println( " " );
		//    
		//        System.err.println( "    Time:  " );
		//        for (Iterator<Link> iter = newlinkIterator; iter.hasNext();) {
		//          Link element = iter.next();
		//          li++;
		//          System.err.print( element.getId() + " " );
		//        }
		//        while ( li-- > 0 ) {
		//          newlinkIterator.previous();
		//        }
		//    
	}

	public Lane getBeforeLane() {
		return beforeLane;
	}

	public void setBeforeLane(Lane beforeLane) {
		this.beforeLane = beforeLane;
	}

	void setBeforePos(int beforePos) {
		this.beforePos = beforePos;
	}

	int getBeforePos() {
		return beforePos;
	}

}
