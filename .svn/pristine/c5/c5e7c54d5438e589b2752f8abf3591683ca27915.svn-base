package pl.edu.agh.cs.kraksim.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Lane extends Element {

	/* link which owns the lane */
	private final Link owner;
	/* absolute number of lane, counting from 0, starting from the left */
	private final int num;
	/* relative number of lane */
	private final int relativeNumber;
	/* length of the lane */
	private final int length;
	private int offset = -1;
	/* actions performable from the lane */
	private final List<Action> actions;
	private int speedLimit;
	/* minimal speed that should be achieved */
	private double minimalSpeed;

	private final static String INVALIDACTION = "trying to add invalid action to";

	Lane(Core core, Link owner, int num, int relativeNumber, int length,
			int speedLimit, double minimalSpeed) {
		super(core);
		this.owner = owner;
		this.num = num;
		this.length = length;
		this.speedLimit = speedLimit;
		this.relativeNumber = relativeNumber;
		this.minimalSpeed = minimalSpeed;
		actions = new ArrayList<Action>();
	}

	public Link getOwner() {
		return owner;
	}

	public int getAbsoluteNumber() {
		return num;
	}

	// TODO: LDZ WIELEPASOW!!!
	//public int getRelativeNumber() {
	//  final int ofset = owner.leftLaneCount();
	//  return (num - ofset);
	//}
	public int getRelativeNumber() {
		return relativeNumber;
	}

	public int getLength() {
		return length;
	}

	public int getOffset() {
		if (offset == -1) {
			offset = owner.getLength() - length;
		}

		return offset;
	}

	/*
	 * Throws UnsupportedLinkOperatorException if lane ends in gateway (actions
	 * can be performed on intersections only). Throws InvalidActionException if
	 * other then the above assumption is not meet. See Action.java for all
	 * assumptions.
	 */
	public void addAction(final Link target, final Lane[] priorLanes)
			throws UnsupportedLinkOperationException, InvalidActionException {
		final Node node = owner.getEnd();
		// check assertions
		if (!node.isIntersection()) {
			throw new UnsupportedLinkOperationException(
					"operation allowed only for lanes going to intersection");
		}

		if (!target.getBeginning().equals(node)) {
			throw new InvalidActionException(INVALIDACTION + this
					+ ": the end of " + owner
					+ " does not meet with the beginning of " + target);
		}

		// Check Action Rules
		checkActionPriorityLanes(priorLanes, node);
		checkPriorityRules(target, priorLanes, node);

/* MZA: commented out to enable multiple lanes.
 * 
		if (owner.findAction(target) != null) {
			throw new InvalidActionException(INVALIDACTION + this + ": "
					+ owner + " has already have an action to " + target);
		}
*/

		actions.add(new Action(this, target, priorLanes));
	}

	private void checkPriorityRules(final Link target, final Lane[] priorLanes,
			final Node node) throws InvalidActionException {
		for (Iterator<Link> iter = node.inboundLinkIterator(); iter.hasNext();) {
			final Link link = iter.next();

			if (!link.equals(owner)) {
				List<Action> actions = link.findActions(target);
				for (Action action:actions){
					if (action != null
							&& !belongsToPriorLanes(priorLanes, action.getSource())
							&& !belongsToPriorLanes(action.getPriorLanes(), this)) {
						throw new InvalidActionException(
								INVALIDACTION
										+ owner
										+ ": conflict with "
										+ action
										+ " (one of the actions should be in 'prior to' relation with the "
										+ "other's source lane");
					} // if action
				} //for actions 

			} // if !link
		}
	}

	private void checkActionPriorityLanes(final Lane[] priorLanes,
			final Node node) throws InvalidActionException {
		for (int i = 0; i < priorLanes.length; i++) {

			if (priorLanes[i].owner.equals(owner)) {
				throw new InvalidActionException(
						INVALIDACTION
								+ this
								+ ": action's priority lanes must belong to other link than "
								+ owner);
			}

			if (!priorLanes[i].owner.getEnd().equals(node)) {
				throw new InvalidActionException(INVALIDACTION + this
						+ ": action's priority lane must end in " + node);
			}
		}
	}

	private boolean belongsToPriorLanes(final Lane[] priorLanes, final Lane lane) {
		for (int i = 0; i < priorLanes.length; i++) {
			if (priorLanes[i].equals(lane)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * Returns the action from the lane to target or null if the lane goes to a
	 * gateway.
	 */
	public Action findAction(final Link target) {
		for (Action action : actions) {
			if (action.getTarget().equals(target)) {
				return action;
			}
		}

		return null;
	}

	/* Returns an interator over empty sequence if the lane goes to a gateway. */
	public Iterator<Action> actionIterator() {
		return actions.iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class getExtensionClass(Module module) {
		return module.extClassSet.getLaneClass();
	}

	/* Should not be used directly. Use City.applyElementVisitor() */
	protected void applyElementVisitor(final ElementVisitor visitor)
			throws VisitingException {
		visitor.visit(this);
	}

	/* used in exception messages */
	@Override
	public String toString() {
		return String.format("{lane %d of %s}", num, owner);
	}

	public int getSpeedLimit() {
		return speedLimit;
	}
	
	public double getMinimalSpeed() {
	    return minimalSpeed;
	}

	public List<Action> getActions() {
		return this.actions;
	}
}
