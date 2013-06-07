package pl.edu.agh.cs.kraksim.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import pl.edu.agh.cs.kraksim.util.ArrayIterator;

public class Link extends Element {
	private final String id;
	/** node the link begins in */
	private final Node beginning;
	/** node the link ends in */
	private final Node end;
	private final String streetName;
	/** lanes owned by (belonging to) the link */
	protected final Lane[] lanes;
	/**
	 * absolute number (counting from 0, starting from the left) of the main
	 * lane
	 */
	/** number of first from left main number */
	// private final int mainLaneNum;
	private final int leftMainLaneNum;
	private final int rightMainLaneNum;
	/** number of main lanes */
	private final int numberOfMainLanes;
	private final int linkNumber;
	private String direction;
	private int speedLimit;
    private double minimalSpeed;

    private double weight;
    private double load;
    
	/**
	 * Throws IllegalArgumentException if lanes length's are not decreasing from
	 * inside to outside.
	 * 
	 * See City.createLink()
	 */
	Link(Core core, String id, Node beginning, Node end, String streetName,
			int[] leftLaneLens, int mainLaneLen, int numberOfLanes,
			int[] rightLaneLens, int speedLimit, double minimalSpeed)
			throws IllegalArgumentException {
		super(core);
		linkNumber = core.getNextNumber();
		this.id = id;
		this.beginning = beginning;
		this.end = end;
		this.streetName = streetName;
		this.speedLimit = speedLimit;
		this.minimalSpeed = minimalSpeed;
		this.numberOfMainLanes = numberOfLanes;

		final int laneCount = leftLaneLens.length + numberOfMainLanes
				+ rightLaneLens.length;
		lanes = new Lane[laneCount];
		leftMainLaneNum = leftLaneLens.length;
		rightMainLaneNum = leftMainLaneNum + (numberOfMainLanes - 1);

		initializeLeftLanes(core, leftLaneLens, mainLaneLen);
		initializeMainLane(core, mainLaneLen);
		initializeRightLanes(core, rightLaneLens, mainLaneLen);
	}

	private void initializeRightLanes(final Core core,
			final int[] rightLaneLens, final int mainLaneLen) {
		for (int i = 0; i < rightLaneLens.length; i++) {
			if (rightLaneLens[i] <= 0) {
				throw new IllegalArgumentException(
						"length of lane must be positive");
			}
			if (rightLaneLens[i] >= (i == rightLaneLens.length - 1 ? mainLaneLen : rightLaneLens[i + 1])) {
				throw new IllegalArgumentException(
						"an outer lane must be shorter than an inner lane");
			}
			int laneNum = rightMainLaneNum + i + 1;
			lanes[laneNum] = new Lane(core, this, laneNum, i + 1,
					rightLaneLens[i], speedLimit,minimalSpeed);
		}
	}
	
//	private void initializeRightLanes(final Core core,
//			final int[] rightLaneLens, final int mainLaneLen) {
//		for (int i = 0; i < rightLaneLens.length; i++) {
//			if (rightLaneLens[i] <= 0) {
//				throw new IllegalArgumentException(
//						"length of lane must be positive");
//			}
//			if (rightLaneLens[i] >= (i == 0 ? mainLaneLen
//					: rightLaneLens[i - 1])) {
//				throw new IllegalArgumentException(
//						"an outer lane must be shorter than an inner lane");
//			}
//			int laneNum = rightMainLaneNum + i + 1;
//			lanes[laneNum] = new Lane(core, this, laneNum, i + 1,
//					rightLaneLens[i], speedLimit,minimalSpeed);
//		}
//	}

	private void initializeMainLane(final Core core, final int mainLaneLen) {
		if (mainLaneLen <= 0) {
			throw new IllegalArgumentException(
					"length of lane must be positive");
		}

		for (int i = leftMainLaneNum; i <= rightMainLaneNum; i++) {
			lanes[i] = new Lane(core, this, i, 0, mainLaneLen, speedLimit,minimalSpeed);
		}
	}

	private void initializeLeftLanes(final Core core, final int[] leftLaneLens,
			final int mainLaneLen) {
		for (int i = 0; i < leftLaneLens.length; i++) {
			if (leftLaneLens[i] <= 0) {
				throw new IllegalArgumentException(
						"length of lane must be positive");
			}
			if(leftLaneLens[i] >= (i == 0 ? mainLaneLen : leftLaneLens[i - 1])) {
				throw new IllegalArgumentException(
						"an outer lane must be shorter than an inner lane");
			}
			
			int laneNum = leftMainLaneNum - i - 1;

			lanes[laneNum] = new Lane(core, this, laneNum, -i - 1,
					leftLaneLens[i], speedLimit,minimalSpeed);
		}
	}

	public String getId() {
		return id;
	}

	public Node getBeginning() {
		return beginning;
	}

	public Node getEnd() {
		return end;
	}

	public String getStreetName() {
		return streetName;
	}

	public int laneCount() {
		return lanes.length;
	}

	public Iterator<Lane> laneIterator() {
		return new ArrayIterator<Lane>(lanes);
	}

	public int leftLaneCount() {
		return leftMainLaneNum;
	}

	public int rightLaneCount() {
		return lanes.length - rightMainLaneNum - 1;
	}

	public int mainLaneCount() {
		return numberOfMainLanes;
	}

	public Lane getMainLane(int n) {
		if (n < 0 || n > numberOfMainLanes - 1) {
			throw new IndexOutOfBoundsException(getId() + " " + n);
		}

		return lanes[leftMainLaneNum + n];
	}

	/* Throws IndexOutOfBoundsException */
	public Lane getLeftLane(int n) {
		if (n < 0 || n > leftMainLaneNum - 1) {
			throw new IndexOutOfBoundsException(getId() + " " + n);
		}

		return lanes[leftMainLaneNum - n - 1];
	}

	/* Throws IndexOutOfBoundsException */
	public Lane getRightLane(int n) {
		if (n < 0 || n > lanes.length - rightMainLaneNum - 2) {
			throw new IndexOutOfBoundsException();
		}

		return lanes[rightMainLaneNum + n + 1];
	}

	/*
	 * Get lane by absolute number.
	 * 
	 * Absolute numbering start from 0, counting from the left.
	 * 
	 * Throws IndexOutOfBoundsException
	 */
	public Lane getLaneAbs(int n) {
		return lanes[n];
	}

	public int getLength() {
		return lanes[leftMainLaneNum].getLength();
	}

	/*
	 * Returns the action to target which source belongs to the link or null if
	 * the link goes to a gateway.
	 */
	@Deprecated
	public Action findAction(final Link target) {
		Action action = null;
		for (int i = 0; i < lanes.length; i++) {
			action = lanes[i].findAction(target);
			if (action != null) {
				break;
			}
		}

		return action;
	}

	/*
	 * Returns an iterator over empty sequence if the link goes to a gateway,
	 * because actionIterator() for all link's lanes returns an iterator over
	 * empty sequence
	 */
	public Iterator<Link> reachableLinkIterator() {
		return new ReachableLinkIterator();
	}

	private class ReachableLinkIterator implements Iterator<Link> {
		private int i;
		private Iterator<Action> actionIter;

		private ReachableLinkIterator() {
			i = 0;
			if (lanes.length > 0) {
				actionIter = lanes[i].actionIterator();
				preNext();
			}
		}

		private void preNext() {
			while (i < lanes.length && !actionIter.hasNext()) {
				if (++i < lanes.length) {
					actionIter = lanes[i].actionIterator();
				}
			}
		}

		public boolean hasNext() {
			return i < lanes.length;
		}

		public Link next() {
			if (i >= lanes.length) {
				throw new NoSuchElementException();
			}
			Link link = actionIter.next().getTarget();
			preNext();

			return link;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class getExtensionClass(final Module module) {
		return module.extClassSet.getLinkClass();
	}

	/* Should not be used directly. Use City.applyElementVisitor() */
	protected void applyElementVisitor(final ElementVisitor visitor)
			throws VisitingException {
		visitor.visit(this);
		for (int i = 0; i < lanes.length; i++) {
			lanes[i].applyElementVisitor(visitor);
		}
	}

	/* used in exception messages */
	@Override
	public String toString() {
		return String.format("[link %s (street: %s)]", id, streetName);
	}

	public int getLinkNumber() {
		return linkNumber;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String dir) {
		// System.out.println(id+": dir="+ dir );
		this.direction = dir;
	}

	/**
	 * @author Maciej Zalewski
	 * Returns the list of main lanes in INVERTED ORDER!
	 * @return list of main lanes (from the RIGHTMOST)
	 */
	public List<Lane> getMainLanes() {
		List<Lane> result = new LinkedList<Lane>();
		for (int i = 0; i < this.mainLaneCount(); i++) {
			result.add(this.getMainLane(i));
		}
		return result;
	}
	
	/**
	 * Method returns list of actions leading to the given link
	 * WARNING: actions are ordered from the RIGHTMOST lane
	 * @author Maciej Zalewski
	 * @param target target link
	 * @return list of actions to the target link
	 */
	public List<Action> findActions(Link target) {
		List<Action> result = new LinkedList<Action>();
		if (target == null) return result;
		Action action = null;
		for (int i = lanes.length - 1; i > -1; i--) {
			action = lanes[i].findAction(target);
			if (action != null) {
				result.add(action);
			}
		}
		return result;
	}

	/**
	 * @return the speedLimit
	 */
	public int getSpeedLimit() {
		return speedLimit;
	}

	public int getLeftMainLaneNum() {
		return leftMainLaneNum;
	}

	public int getRightMainLaneNum() {
		return rightMainLaneNum;
	}
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void calculateWeight(double load){
		this.setLoad(load);
		weight = 2*mainLaneCount() + leftMainLaneNum + rightMainLaneNum + 0.01*minimalSpeed + 0.01*speedLimit + 0.1*load;
	}

	public double getLoad() {
		return load;
	}

	public void setLoad(double load) {
		this.load = load;
	}
	
}
