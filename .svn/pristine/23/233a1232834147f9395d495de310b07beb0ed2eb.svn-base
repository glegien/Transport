package pl.edu.agh.cs.kraksim.routing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.iface.sim.Route;

public class TimeBasedRouter implements Router {

	private static final Logger logger = Logger
			.getLogger(TimeBasedRouter.class);

	private final City city;

	private static class D {
		double d;

		D() {
			d = Double.MAX_VALUE;
		}

		D(double toBeD) {
			d = toBeD;
		}


		public String toString() {
			return Double.toString(d);
		}
	}

	private ITimeTable timeTable;

	public TimeBasedRouter(City city) {
		this.city = city;
	}

	// Todo:IMPORTANT, work on this
	public TimeBasedRouter(City city, ITimeTable timeTable) {
		this.city = city;
		this.timeTable = timeTable;
	}

	public Route getRoute(Link sourceLink, Node targetNode)
			throws NoRouteException {
		Node sourceNode = sourceLink.getBeginning();

		if (sourceNode == null)
			throw new IllegalArgumentException("null source");
		if (targetNode == null)
			throw new IllegalArgumentException("null target");

		assert sourceLink != null;
		assert sourceLink.getBeginning() == sourceNode;

		List<Link> routeList = dijkstra(sourceLink, (Gateway) targetNode);

		if (routeList == null)
			throw new NoRouteException("from " + sourceNode.getId() + " to "
					+ targetNode.getId());

		DijkstraRoute route = new DijkstraRoute(routeList);
		return route;
	}

	private List<Link> dijkstra(Link sourceLink, Gateway targetNode) {
		if (logger.isTraceEnabled()) {
			logger.trace("Dijkstra from " + sourceLink + " to " + targetNode + "\n");
		}
		
		assert this.timeTable != null;
		assert sourceLink != null;
		assert targetNode != null;
		assert this.city != null;
		
		Link currentlyProcessedLink = sourceLink;
		double distanceSoFar = 0.;
		Map<Link, D> notReachedLinks = new HashMap<Link, D>();
		Map<Link, D> reachedLinks = new HashMap<Link, D>();
		Map<Link, Link> pathRecovery = new HashMap<Link, Link>();

		// initialisation of set of not reached links
		Link temp = null;
		for (Iterator<Link> linkIter = this.city.linkIterator(); linkIter.hasNext(); ){
			temp = linkIter.next();
			if(temp.getId().equals(sourceLink.getId())) continue;
			
			notReachedLinks.put(temp, new D());
		}
		reachedLinks.put(currentlyProcessedLink, new D(0));

		// processing the closest link 
		while (currentlyProcessedLink != null){
			// maybe it ends in the desired gateway?
			if (currentlyProcessedLink.getEnd().getId().equals(targetNode.getId())){
				List<Link> result = generateRoute(pathRecovery, currentlyProcessedLink);
				notReachedLinks.clear();
				pathRecovery.clear();
				return result;			
			}
			
			// if not, update the closest ways from it to each of the links it reaches 
			for (Iterator<Link> linkIter = currentlyProcessedLink.reachableLinkIterator(); linkIter.hasNext(); ){
				temp = linkIter.next();
				if (reachedLinks.containsKey(temp)) continue;
				double distanceFromCurrentLink = distanceSoFar + this.timeTable.getTime(temp);
				if (notReachedLinks.get(temp).d > distanceFromCurrentLink){
					notReachedLinks.get(temp).d = distanceFromCurrentLink;
					pathRecovery.put(temp, currentlyProcessedLink);
				}
			}

			// now, let's search for the next nearest link
			double minDistance = Double.MAX_VALUE;
			Link nearestLink = null;
			for (Entry<Link, D> entry: notReachedLinks.entrySet()){
				if (entry.getValue().d < minDistance){
					minDistance = entry.getValue().d;
					nearestLink = entry.getKey();
				}
			}
			
			currentlyProcessedLink = nearestLink;
			notReachedLinks.remove(currentlyProcessedLink);
			reachedLinks.put(currentlyProcessedLink, new D(minDistance));
			distanceSoFar = minDistance;
		}
		
		// if nothing found, return null
		return null;
	}

	private final static class DijkstraRoute implements Route {

		private final List<Link> route;

		DijkstraRoute(List<Link> route) {
			this.route = route;
		}

		public Gateway getSource() {
			// TODO Node
			return (Gateway) route.get(0).getBeginning();
		}

		public Gateway getTarget() {
			return (Gateway) route.get(route.size() - 1).getEnd();
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(route.get(0).getBeginning().getId());
			for (Link lnk : route) {
				sb.append("-").append(lnk.getEnd().getId());
			}
			return sb.toString();
			// return getSource().getId() + " -> " + route.get( route.size() - 1
			// ).getEnd().getId();//getTarget().getId();
		}

		public ListIterator<Link> linkIterator() {
			return route.listIterator();
		}
	}

	private static List<Link> generateRoute(Map<Link, Link> prevMap, Link target) {

		List<Link> result = new LinkedList<Link>();
		result.add(0, target);
		for (Link prev = prevMap.get(target); prev != null; prev = prevMap
				.get(prev)) {
			result.add(0, prev);
		}
		return result;
	}
}
