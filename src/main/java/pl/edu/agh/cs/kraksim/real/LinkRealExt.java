package pl.edu.agh.cs.kraksim.real;

import org.apache.log4j.Logger;

import java.util.List;
import pl.edu.agh.cs.kraksim.AssumptionNotSatisfiedException;
import pl.edu.agh.cs.kraksim.core.Action;
import pl.edu.agh.cs.kraksim.core.ExtensionCreationException;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.block.LinkBlockIface;
import pl.edu.agh.cs.kraksim.iface.mon.CarDriveHandler;
import pl.edu.agh.cs.kraksim.iface.mon.LinkMonIface;

class LinkRealExt implements LinkBlockIface, LinkMonIface {

	private static final Logger logger = Logger.getLogger(LinkRealExt.class);
	protected final Link link;
	protected final RealEView ev;

	LinkRealExt(Link link, RealEView ev, RealSimulationParams params)
			throws ExtensionCreationException {
		if (logger.isTraceEnabled()) {
			logger.trace("Creating.");
		}
		if (link.getLength() < params.priorLaneTimeHeadway * params.maxVelocity) {
			throw new ExtensionCreationException(String.format(
					"real module requires link ls at least %d cells long", link
							.getLength()));
		}

		this.link = link;
		this.ev = ev;
	}

	private int laneCount() {
		return link.laneCount();
	}

	/* in absolute numbering, from left to right, starting fom 0 */
	private LaneRealExt laneExt(int n) {
		return ev.ext(link.getLaneAbs(n));
	}

	void prepareTurnSimulation() {
		if (logger.isTraceEnabled()) {
			logger.trace(link);
		}

		for (int i = 0; i < laneCount(); i++) {
			laneExt(i).prepareTurnSimulation();
		}
	}

	/* intersection link only */
	public void findApproachingCars() {
		if (logger.isTraceEnabled()) {
			logger.trace(link);
		}

		for (int i = 0; i < laneCount(); i++) {
			laneExt(i).findApproachingCar();
		}
	}

	/* assumption: stepsDone < stepsMax */
	boolean enterCar(Car car, int stepsMax, int stepsDone) {
		if (logger.isTraceEnabled()) {
			logger.trace(car + " stepsDone:" + stepsDone + " stepsMax: "
					+ stepsMax);
		}

		// obtaining next goal of the entered car
		Link nextLink = null;
		if (car.hasNextTripPoint()){
			nextLink = car.peekNextTripPoint();
		}else{
			// if there is no next point, it means, that the car
			// is heading to a gateway. If this link does not lead 
			// to a gateway - time to throw some exception...
			
		  if (!link.getEnd().isGateway()){
				throw new AssumptionNotSatisfiedException();
			}
		}
		
		// obtaining list of actions heading to the given destination
		List<Action> actions = this.link.findActions(nextLink);
		
		MultiLaneRoutingHelper laneHelper = new MultiLaneRoutingHelper(ev);
		
		// choosing the best action from the given list
		Action nextAction = laneHelper.chooseBestAction(actions, this.link);
		// choosing the best lane to enter in order to get to lane given in action 
		Lane nextLane = laneHelper.chooseBestLaneForAction(nextAction, this.link);
		
		// if no such a lane, just put it into the main lane...
		if (nextLane==null) nextLane = this.link.getMainLane(0);

		LaneRealExt l = ev.ext(nextLane);
		if(l.hasCarPlace()){
			car.refreshTripRoute();
			
			if (!car.hasNextTripPoint()) {
				car.setAction(null);
			} else {
				car.nextTripPoint();
	
				car.setAction(nextAction);
			}
			
			if (l.pushCar(car, stepsMax, stepsDone)) {
				return true;
			} else {
				return false;
			}
		}else{
			return false;
		}
	}

	void simulateTurn() {
		if (logger.isTraceEnabled()) {
			logger.trace(link);
		}

		for (int i = 0; i < laneCount(); i++) {
			laneExt(i).simulateTurn();
		}
	}

	void finalizeTurnSimulation() {
		if (logger.isTraceEnabled()) {
			logger.trace(link);
		}

		for (int i = 0; i < laneCount(); i++) {
			laneExt(i).finalizeTurnSimulation();
		}
	}

	public void block() {
		for (int i = 0; i < laneCount(); i++) {
			laneExt(i).block();
		}
	}

	public void unblock() {
		for (int i = 0; i < laneCount(); i++) {
			laneExt(i).unblock();
		}
	}

	public void installInductionLoops(int line, CarDriveHandler handler)
			throws IndexOutOfBoundsException {
		if (line < 0 || line > link.getLength()) {
			throw new IndexOutOfBoundsException("line = " + line);
		}

		for (int i = 0; i < laneCount(); i++) {
			LaneRealExt l = laneExt(i);
			if (line >= l.getOffset()) {
				laneExt(i).installInductionLoop(line, handler);
			}
		}
	}

}
