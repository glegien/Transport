package pl.edu.agh.cs.kraksim.core;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * City - an entry element to the city topology
 */
public class City extends Element {

	/** maps gateway ids' to nodes */
	private final Map<String, Gateway> gatewayMap;
	/** maps gateway ids' to nodes */
	private final Map<String, Intersection> intersectionMap;
	private final Map<String, Link> linkMap;

	City(Core core) {
		super(core);
		gatewayMap = new HashMap<String, Gateway>();
		intersectionMap = new HashMap<String, Intersection>();
		linkMap = new HashMap<String, Link>();
	}

	/**
	 * Gateway Factory.
	 * 
	 * @throws DuplicateIdentifierException
	 *             if node with specifed id already exists.
	 */
	public Gateway createGateway(String id, Point2D point)
			throws DuplicateIdentifierException {
		if (gatewayMap.containsKey(id) || intersectionMap.containsKey(id)) {
			throw new DuplicateIdentifierException("node with id " + id
					+ " already exists");
		}

		Gateway g = new Gateway(core, id, point);
		gatewayMap.put(id, g);

		return g;
	}

	/**
	 * Intersection Factory.
	 * 
	 * @throws DuplicateIdentifierException
	 *             if node with specifed id already exists.
	 */
	public Intersection createIntersection(String id, Point2D point)
			throws DuplicateIdentifierException {
		if (gatewayMap.containsKey(id) || intersectionMap.containsKey(id)) {
			throw new DuplicateIdentifierException("node with id " + id
					+ " already exists");
		}

		Intersection is = new Intersection(core, id, point);
		intersectionMap.put(id, is);

		return is;
	}

	/**
	 * Link Factory. streetName does not have to be unique in general.
	 * (Directed) links connecting two nodes should have unique streetNames.
	 * 
	 * Lanes length's must decrease from inside to outside. Throws
	 * IllegalArgumentException otherwise.
	 * 
	 * Arrays of lane lenghts are indexed from 0 (the lane nearest to the main
	 * lane).
	 * 
	 * @throws DuplicateIdentifierException
	 *             if link with specified id already exists.
	 * 
	 * @throws LinkAttachmentException.
	 *             See Gateway.attach*Link().
	 */
	public Link createLink(String id, Node begin, Node end, String streetName,
			int[] leftLaneLens, int mainLaneLen, int numberOfLanes,
			int[] rightLaneLens, int speedLimit, double minimalSpeed)
			throws DuplicateIdentifierException, IllegalArgumentException,
			LinkAttachmentException {
		if (linkMap.containsKey(id)) {
			throw new DuplicateIdentifierException(String.format(
					"link with id %s already exists", id));
		}

		Link link = new Link(core, id, begin, end, streetName, leftLaneLens,
				mainLaneLen, numberOfLanes, rightLaneLens, speedLimit, minimalSpeed);

		// If this method throws an exception, no cleanup is needed.
		begin.attachOutboundLink(link);
		try {
			// If an exception is thrown, we must detach, what we have just
			// attached.
			end.attachInboundLink(link);
		} catch (LinkAttachmentException e) {
			begin.detachOutboundLink(link);
			throw e;
		}

		linkMap.put(id, link);

		return link;
	}

	/* returns null if not found */
	public Node findNode(String id) {
		Node node = gatewayMap.get(id);
		if (node != null) {
			return node;
		} else {
			return intersectionMap.get(id);
		}
	}

	public int nodeCount() {
		return gatewayMap.size() + intersectionMap.size();
	}

	public Iterator<Node> nodeIterator() {
		return new NodeIterator();
	}

	private class NodeIterator implements Iterator<Node> {
		Iterator<Gateway> gIter;
		Iterator<Intersection> iIter;

		private NodeIterator() {
			gIter = gatewayMap.values().iterator();
			iIter = intersectionMap.values().iterator();
		}

		public boolean hasNext() {
			return gIter.hasNext() || iIter.hasNext();
		}

		public Node next() {
			if (gIter.hasNext())
				return gIter.next();
			if (iIter.hasNext())
				return iIter.next();
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public int gatewayCount() {
		return gatewayMap.size();
	}

	public Iterator<Gateway> gatewayIterator() {
		return gatewayMap.values().iterator();
	}

	public int intersectionCount() {
		return intersectionMap.size();
	}

	public Iterator<Intersection> intersectionIterator() {
		return intersectionMap.values().iterator();
	}

	/* returns null if not found */
	public Link findLink(String id) {
		return linkMap.get(id);
	}

	public int linkCount() {
		return linkMap.size();
	}

	public Iterator<Link> linkIterator() {
		return linkMap.values().iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class getExtensionClass(Module module) {
		return module.extClassSet.getCityClass();
	}

	/**
	 * Visits all elements (that is objects of Element class or subclass) and
	 * calls a visitor method dependind of an element type.
	 * 
	 * See ElementVisitor and VisitingException.
	 */
	void applyElementVisitor(ElementVisitor visitor) throws VisitingException {
		visitor.visit(this);

		for (Gateway gateway : gatewayMap.values())
			gateway.applyElementVisitor(visitor);
		for (Intersection intersection : intersectionMap.values())
			intersection.applyElementVisitor(visitor);
	}
}
