package pl.edu.agh.cs.kraksim.weka.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.weka.data.IntersectionInfo;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;

public class NeighbourArrayCreator {
	
	
	public static Map<LinkInfo, Neighbours> createNeighbourArray(City city, int numberOfInfluencedLinks) {
		Map<LinkInfo, Set<LinkInfo>> inversedNeighbours = setupNeighboursArray(city, numberOfInfluencedLinks);
		Map<LinkInfo, Neighbours> neighboursMap = new HashMap<LinkInfo, Neighbours>();
		for (LinkInfo key : inversedNeighbours.keySet()) {
			neighboursMap.put(key, new Neighbours());
		}
		for (LinkInfo key : inversedNeighbours.keySet()) {
			for (LinkInfo value : inversedNeighbours.get(key)) {
				key.numberOfHops = value.numberOfHops;
				value.numberOfHops = -1;
				Neighbours neighbours = neighboursMap.get(value);
				neighbours.roads.add(key);
				addIntersectionId(city, key, neighbours);
			}
		}
		return neighboursMap;
	}

	private static void addIntersectionId(City city, LinkInfo key, Neighbours neighbours) {
		Link link = city.findLink(key.linkId);
		Node node = link.getEnd();
		if (node.isIntersection()) {
			Intersection intersection = (Intersection)node;
			String intersectionId = intersection.getId();
			neighbours.intersections.add(intersectionId);
		}
	}

	private static Map<LinkInfo, Set<LinkInfo>> setupNeighboursArray(City city, int numberOfInfluencedLinks) {
		Map<LinkInfo, Set<LinkInfo>> neighborsArray = new HashMap<LinkInfo, Set<LinkInfo>>();
		Iterator<Link> it = city.linkIterator();
		// for each link in the city...
		while (it.hasNext()) {
			// get this link
			Link link = it.next();
			// prepare set of reachable links
			Set<LinkInfo> set = new HashSet<LinkInfo>();

			// get ready for searching
			int hopsCount = numberOfInfluencedLinks;
			Queue<Link> currentHops = new LinkedList<Link>();
			Queue<Link> nextHops = new LinkedList<Link>();

			currentHops.add(link);
			// now, for all hops...
			while (hopsCount > 0) {
				// and for each link at this distance
				Link currentLink = currentHops.poll();
				while (currentLink != null) {
					// and add all links reachable from it to the next level
					// queue
					for (Iterator<Link> iter = currentLink.reachableLinkIterator(); iter.hasNext();) {
						// add it to reachable links
						Link temp = iter.next();
						set.add(new LinkInfo(temp.getLinkNumber(), temp.getId(), numberOfInfluencedLinks - hopsCount + 1));
						nextHops.add(temp);
					}
					// and pop the next link
					currentLink = currentHops.poll();
				}
				// we are done for this distance, let's swap the queues
				currentHops = nextHops;
				nextHops = new LinkedList<Link>();
				// decrement the number of hops left;
				hopsCount--;
			}
			// we have the last level of distance in currentHops queue
			// let's add it to the set
			for (Link currentLink : currentHops) {
				set.add(new LinkInfo(currentLink.getLinkNumber(), currentLink.getId(), numberOfInfluencedLinks - hopsCount + 1));
			}
			currentHops.clear();
			// now, as we created the set, let's map it
			neighborsArray.put(new LinkInfo(link.getLinkNumber(), link.getId(), -1), set);
		}
		return neighborsArray;
	}

	public static void addAdjacentIntersectionRoads(Map<LinkInfo, Neighbours> neighboursArray, City city) {
		for (LinkInfo linkInfo : neighboursArray.keySet()) {
			Set<LinkInfo> adjacentRoads = findAdjacentRoads(city, linkInfo);
			Neighbours neigbours = neighboursArray.get(linkInfo);
			SortedSet<LinkInfo> roads = neigbours.roads;
			roads.addAll(adjacentRoads);
		}
	}

	private static Set<LinkInfo> findAdjacentRoads(City city, LinkInfo linkInfo) {
		String linkId = linkInfo.linkId;
		Link link = city.findLink(linkId);
		Node node = link.getEnd();
		Set<LinkInfo> adjacentRoads = new HashSet<LinkInfo>();
		if (node.isIntersection()) {
			Intersection intersection = (Intersection)node;
			Iterator<Link> iterator = intersection.inboundLinkIterator();
			while (iterator.hasNext()) {
				Link inboundLink = iterator.next();
				if (!inboundLink.equals(link)) {
					String inboundId = inboundLink.getId();
					int inboundLinkNumber = inboundLink.getLinkNumber();
					LinkInfo inboundLinkInfo = new LinkInfo(inboundLinkNumber,inboundId,-1);
					adjacentRoads.add(inboundLinkInfo);
				}
			}
		}
		return adjacentRoads;
	}

	public static Map<IntersectionInfo, Neighbours> createIntersectionsArray(City city) {
		Map<IntersectionInfo, Neighbours> intersectionsMap = new HashMap<IntersectionInfo, Neighbours>();
		Iterator<Intersection> iterator = city.intersectionIterator();
		while (iterator.hasNext()) {
			Intersection intersection = iterator.next();
			
			String intersectionId = intersection.getId();
			IntersectionInfo intersectionInfo = new IntersectionInfo(intersectionId);
			Neighbours neighbours = new Neighbours();
			Iterator<Link> inboundLinkIterator = intersection.inboundLinkIterator();
			while (inboundLinkIterator.hasNext()) {
				Link link = inboundLinkIterator.next();
				String id = link.getId();
				int linkNumber = link.getLinkNumber();
				LinkInfo inboundLinkInfo = new LinkInfo(linkNumber, id, 1);
				
				neighbours.roads.add(inboundLinkInfo);
			}
			intersectionsMap.put(intersectionInfo, neighbours);
		}
		return intersectionsMap;
	}

}
