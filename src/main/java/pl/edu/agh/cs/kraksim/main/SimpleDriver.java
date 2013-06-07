package pl.edu.agh.cs.kraksim.main;

import java.awt.Color;
import java.util.ListIterator;
import java.util.Random;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.sim.Route;
import pl.edu.agh.cs.kraksim.routing.NoRouteException;
import pl.edu.agh.cs.kraksim.routing.TimeBasedRouter;
import pl.edu.agh.cs.kraksim.traffic.TravellingScheme;

public class SimpleDriver implements Comparable<SimpleDriver>, Driver {
	private static final Logger logger = Logger.getLogger(SimpleDriver.class);
	final private int id;
	final private TravellingScheme.Cursor cursor;
	private int departureTurn;
	private final TimeBasedRouter router;
	private DecisionHelper decisionHelper;
	private Color color;

	public SimpleDriver(int id, TravellingScheme scheme,
			TimeBasedRouter router, DecisionHelper decisionHelper) {
		this.id = id;
		this.router = router;
		this.decisionHelper = decisionHelper;
		this.color = scheme.getDriverColor();
		cursor = scheme.cursor();
	}

	public void setDepartureTurn(Random rg) {
		departureTurn = cursor.drawDepartureTurn(rg);
	}

	public Gateway srcGateway() {
		return cursor.srcGateway();
	}

	public Gateway destGateway() {
		return cursor.destGateway();
	}

	public boolean nextTravel() {
		cursor.next();
		return cursor.isValid();
	}

	public int compareTo(SimpleDriver driver) {
		return departureTurn - driver.departureTurn;
	}

	@Override
	public String toString() {
		return "[DRIVER " + id + ", from " + srcGateway().getId() + " to "
				+ destGateway().getId() + " ] ";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleDriver)) {
			return false;
		}

		return id == ((SimpleDriver) obj).id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public ListIterator<Link> updateRouteFrom(Link sourceLink) {
		if (chageRoute()) {
			try {
				Route r = router.getRoute(sourceLink, destGateway());
				return r.linkIterator();
			} catch (NoRouteException e) {
				logger.warn("No route", e);
			}
		}

		return null;
	}

	private boolean chageRoute() {
		boolean decision = false;

		if (decisionHelper != null) {
			decision = decisionHelper.decide();
		}

		return decision;
	}

	public int getDepartureTurn() {
		return departureTurn;
	}
	
	public Color getColor() {
	    return this.color;
	}
}
