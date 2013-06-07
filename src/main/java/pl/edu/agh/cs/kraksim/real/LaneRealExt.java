package pl.edu.agh.cs.kraksim.real;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Action;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.iface.block.LaneBlockIface;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoCursor;
import pl.edu.agh.cs.kraksim.iface.carinfo.LaneCarInfoIface;
import pl.edu.agh.cs.kraksim.iface.mon.CarDriveHandler;
import pl.edu.agh.cs.kraksim.iface.mon.LaneMonIface;

class LaneRealExt implements LaneBlockIface, LaneCarInfoIface, LaneMonIface {

	private static final Logger logger = Logger.getLogger(LaneRealExt.class);

	private final Lane lane;

	private final RealEView realView;
	private final RealSimulationParams params;
	private final int offset;
	protected final LinkedList<Car> cars;
	private boolean blocked;
	private int firstCarPos;
	private boolean carApproaching;
	// MZA: multi-lanes. It had to be changed.
	private List<Car> enteringCars = new LinkedList<Car>();
	private ArrayList<InductionLoop> loops;

	private int speedLimit;

	private boolean wait;

	LaneRealExt(Lane lane, RealEView ev, RealSimulationParams params) {
		if (logger.isTraceEnabled()) {
			logger.trace("Constructing LaneRealExt ");
		}
		this.lane = lane;
		this.realView = ev;
		this.params = params;
		this.speedLimit = lane.getSpeedLimit();

		offset = lane.getOffset();// linkLength() - lane.getLength();
		cars = new LinkedList<Car>();
		blocked = false;
		loops = new ArrayList<InductionLoop>(0);

	}

	int getOffset() {
		return offset;
	}

	private Link owner() {
		return lane.getOwner();
	}

	private int linkLength() {
		return owner().getLength();
	}

	private int absoluteNumber() {
		return lane.getAbsoluteNumber();
	}

	private Node linkEnd() {
		return owner().getEnd();
	}

	/*
	 * Return <0 if lane represented by this object lies on the left of l, 0 if
	 * they are the same lane, >0 if lane represented by this object lies on the
	 * right of l.
	 */
	private int compareLanePositionTo(LaneRealExt l) {
		return absoluteNumber() - l.absoluteNumber();
	}

	private LaneRealExt leftNeighbor() {
		return realView.ext(owner().getLaneAbs(absoluteNumber() - 1));
	}

	private LaneRealExt rightNeighbor() {
		return realView.ext(owner().getLaneAbs(absoluteNumber() + 1));
	}

	public void prepareTurnSimulation() {

		if (logger.isTraceEnabled()) {
			logger.trace(lane);
		}
		Car car = cars.peek();
		if (car != null) {
			firstCarPos = car.pos;
		} else {
			firstCarPos = Integer.MAX_VALUE;
		}
	}

	/* intersection lane only */
	public void findApproachingCar() {
		if (logger.isTraceEnabled()) {
			logger.trace(lane);
		}
		if (blocked || cars.isEmpty() || wait) {
			carApproaching = false;
			return;
		}

		Car car = cars.getLast();
		carApproaching = (car.pos + Math.max(car.velocity, 1)
				* params.priorLaneTimeHeadway >= linkLength());
	}

	boolean hasCarPlace() {
		// CHANGE: MZA - to disable multiple cars entering the same lane 
		if (this.enteringCars.size() > 0) return false;
		return firstCarPos > offset;
	}

	/* assumption: stepsDone < stepsMax */
	boolean pushCar(Car car, int stepsMax, int stepsDone) {
		if (logger.isTraceEnabled()) {
			logger.trace(car + " on " + lane);
		}
		if (hasCarPlace()) {
			driveCar(car, offset - 1, firstCarPos - 1, stepsMax, stepsDone,
					new InductionLoopPointer(), true);
			return true;
		} else {
			return false;
		}
	}

	/*
	 * previous element to ilp.current() (if exists) should be an induction loop
	 * with line <= startPos.
	 * 
	 * the same pointer can be used to the next car on this lane (above
	 * assumption will be true)
	 */
	private boolean driveCar(Car car, int startPos, int freePos, int stepsMax,
			int stepsDone, InductionLoopPointer ilp, boolean entered) {
		
		if (logger.isTraceEnabled()) {
			logger.trace("CARTURN " + car + "on " + lane);
		}
		int range = startPos + stepsMax - stepsDone;
		int pos;
		boolean stay = false;

		LaneRealExt sourceLane;
		Action action = car.getAction();
		
		if (action != null) {
			sourceLane = realView.ext(action.getSource());
			int x = compareLanePositionTo(sourceLane);
			if (x < 0) {
				sourceLane = rightNeighbor();
			} else if (x > 0) {
				sourceLane = leftNeighbor();
			}
		} else {
			sourceLane = this;
		}

		/* last line of this link crossed by the car in this turn */
		int lastCrossedLine;

		if (!equals(sourceLane)) {
			int laneChangePos = sourceLane.offset - 1;

			pos = Math.min(Math.min(range, freePos), laneChangePos);
			
			if (pos == range
					|| pos < laneChangePos
					|| !sourceLane.pushCar(car, stepsMax, stepsDone + pos
							- startPos))
				stay = true;
			
			
			lastCrossedLine = pos;
		} else {
			int lastPos = linkLength() - 1;
			pos = Math.min(Math.min(range, freePos), lastPos);
			if (pos == range
					|| pos < lastPos
					|| blocked
					|| !handleCarAction(car, stepsMax, stepsDone + pos
							- startPos)) {
				stay = true;
				lastCrossedLine = pos;
			} else {
				lastCrossedLine = pos + 1;
			}
		}

		if (stay) {
			car.pos = pos;
			car.velocity = stepsDone + pos - startPos;
			if (entered) {
				enteringCars.add(car);
			}
		}

		if (logger.isTraceEnabled()) {
			logger.trace("CARTURN " + car + " crossed " + lastCrossedLine);
		}
		/* We fire all induction loops in the range (startPos; lastCrossedLine] */
		while (!ilp.atEnd() && ilp.current().line <= lastCrossedLine) {

			if (ilp.current().line > startPos) {
				if (logger.isTraceEnabled()) {
					logger.trace(">>>>>>> INDUCTION LOOP before " + startPos
							+ " and " + lastCrossedLine + " for " + lane);
				}
				
				ilp.current().handler.handleCarDrive(car.velocity, car.driver);
			}

			ilp.forward();
		}

		if (logger.isTraceEnabled()) {
			logger.trace("CARTURN " + car + "on " + lane);
		}
		return stay;
	}

	/* assumption: stepsDone < stepsMax */
	private boolean handleCarAction(Car car, int stepsMax, int stepsDone) {
		if (logger.isTraceEnabled()) {
			logger.trace(car + " on " + lane);
		}

		Action action = car.getAction();

		if (action == null) {
			car.velocity = stepsMax;
			((GatewayRealExt) realView.ext(linkEnd())).acceptCar(car);
			return true;
		}

		if (wait) {
			/* we are waiting one turn */
			wait = false;
			return false;
		} else {
			/* we are approaching an intersection */
			Lane[] pl = action.getPriorLanes();
			// int i;
			for (int i = 0; i < pl.length; i++) {
				if (realView.ext(pl[i]).carApproaching) {
					if (checkDeadlock(action.getSource(), pl[i])) {
						logger.warn(lane + "DEADLOCK situation.");
						deadLockRecovery();
					}
					return false;
				}
			}
			LinkRealExt l = realView.ext(action.getTarget());

			return l.enterCar(car, stepsMax, stepsDone);
		}

	}

	private void deadLockRecovery() {
		// ev.ext( lane.getOwner()).
		if (params.rg.nextFloat() < params.victimProb) {
			if (logger.isTraceEnabled()) {
				logger.trace("Deadlock victim: " + lane + " - recovering.");
			}
			wait = true;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Deadlock: " + lane + " won't be a victim.");
		}
	}

	private boolean checkDeadlock(Lane begin, Lane next) {
		if (logger.isTraceEnabled()) {
			logger.trace("Check for deadlock: " + begin);
		}
		Set<Lane> visited = new HashSet<Lane>();
		visited.add(begin);
		return checkDeadlock(next, visited);
	}

	private boolean checkDeadlock(Lane next, Set<Lane> visited) {
		if (logger.isTraceEnabled()) {
			logger.trace("Check for deadlock: " + next);
		}
		if (visited.contains(next)) {
			if (logger.isTraceEnabled()) {
				logger.trace(visited);
			}
			return true;
		}
		visited.add(next);

		for (Iterator<Action> it = next.actionIterator(); it.hasNext();) {
			Action ac = it.next();
			Lane[] pl = ac.getPriorLanes();

			for (int i = 0; i < pl.length; i++) {
				if (realView.ext(pl[i]).carApproaching) {
					if (checkDeadlock(pl[i], visited)) {
						return true;
					}
				}
			} // for

		}

		return false;
	}

	/**
	 * Nagel-Schreckenberg
	 */
	void simulateTurn() {
		if (logger.isTraceEnabled()) {
			logger.trace(lane);
		}
		ListIterator<Car> cit = cars.listIterator();
		if (cit.hasNext()) {
			InductionLoopPointer ilp = new InductionLoopPointer();
			Car car = cit.next();
			Car nextCar;

			do {
				nextCar = cit.hasNext() ? cit.next() : null;

				// remember starting point
				car.setBeforeLane(lane);
				car.setBeforePos(car.pos);

				// Nagel-Schreckenberg
				// 1. Init velocity variable
				int v = 0;

				// 2. Acceleration
				if (car.velocity < speedLimit) {
					v = car.velocity + 1;
				} else {
					v = car.velocity;
				}

				// 3. Deceleration
				if (params.rg.nextFloat() < params.decelProb) {
					v--;
				}

				// 4. Drive (Move the car)
				int freePos = Integer.MAX_VALUE;
				if (nextCar != null) {
					freePos = nextCar.pos - 1;
				}

				boolean stay = driveCar(car, car.pos, freePos, v, 0, ilp, false);

				if (!stay) {
					if (nextCar != null)
						cit.previous();
					cit.previous();
					cit.remove();
					if (nextCar != null)
						cit.next();
				}

				// remember this car as next (we are going backwards)
				car = nextCar;
			} while (car != null);
		}
	}

	void finalizeTurnSimulation() {
		if (logger.isTraceEnabled()) {
			logger.trace(lane);
		}
		if (enteringCars.size() > 0) {
			for (Car c:enteringCars){
				cars.addFirst(c);
			}
			enteringCars.clear();
		}
	}

	/**
	 * This method returns a total number of cars on the lane 
	 * (both "inside" and those that had just entered)
	 * @author Maciej Zalewski
	 * @return number of cars on the lane
	 */
	public int getAllCarsNumber(){
		return this.cars.size() + this.enteringCars.size();
	}
	
	/**
	 * Gets the position of the nearest car from the entrance to the lane
	 * (how much space there is on the lane to enter)
	 * @author Maciej Zalewski
	 * @return distance from the nearest car
	 */
	public int getFirstCarPosition(){
		// if there are no cars, return length of the lane
		if (this.getAllCarsNumber() == 0) return lane.getLength();
		
		// if there are newly entered cars - show lack of space
		if (! this.enteringCars.isEmpty()) return -1;

		// in any other case, return position of the nearest car on the lane
		return this.cars.peek().pos;
	}
	
	private abstract class CarInfoUniCursorImpl implements CarInfoCursor {

		protected Car car;

		public Object currentDriver() {
			if (car == null)
				throw new NoSuchElementException();
			return car.driver;
		}

		public Lane currentLane() {
			if (car == null)
				throw new NoSuchElementException();
			return lane;
		}

		public int currentPos() {
			if (car == null)
				throw new NoSuchElementException();
			return car.pos - offset;
		}

		public int currentVelocity() {
			if (car == null)
				throw new NoSuchElementException();
			return car.velocity;
		}

		public Lane beforeLane() {
			if (car == null)
				throw new NoSuchElementException();
			return car.getBeforeLane();
		}

		public int beforePos() {
			if (car == null)
				throw new NoSuchElementException();

			return car.getBeforePos() - offset;
		}

		public boolean isValid() {
			return (car != null);
		}
	}

	private class CarInfoCursorForwardImpl extends CarInfoUniCursorImpl {

		private final Iterator<Car> cit;

		private CarInfoCursorForwardImpl() {
			cit = cars.iterator();
			if (cit.hasNext())
				car = cit.next();
			else
				car = null;
		}

		public void next() {
			if (!cit.hasNext())
				car = null;
			else
				car = cit.next();
		}
	}

	private class CarInfoCursorBackwardImpl extends CarInfoUniCursorImpl {

		private final ListIterator<Car> cit;

		private CarInfoCursorBackwardImpl() {
			cit = cars.listIterator(cars.size());
			if (cit.hasPrevious())
				car = cit.previous();
			else
				car = null;
		}

		public void next() {
			if (!cit.hasPrevious())
				car = null;
			else
				car = cit.previous();
		}
	}

	public CarInfoCursor carInfoForwardCursor() {
		return new CarInfoCursorForwardImpl();
	}

	public CarInfoCursor carInfoBackwardCursor() {
		return new CarInfoCursorBackwardImpl();
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void block() {
		blocked = true;
	}

	public void unblock() {
		blocked = false;
	}

	private static class InductionLoop {
		private final int line;
		private final CarDriveHandler handler;

		private InductionLoop(int line, CarDriveHandler handler) {
			this.line = line;
			this.handler = handler;
		}
	}

	private class InductionLoopPointer {
		private int i;

		private InductionLoopPointer() {
			i = 0;
		}

		private boolean atEnd() {
			return i == loops.size();
		}

		private InductionLoop current() {
			return loops.get(i);
		}

		private void forward() {
			if (i < loops.size())
				i++;
		}
	}

	public void installInductionLoop(int line, CarDriveHandler handler)
			throws IndexOutOfBoundsException {
		if (logger.isTraceEnabled()) {
			logger.trace("Instaling IL ona lane " + lane + " at distance: "
					+ line);
		}
		if (line < 0 || line > linkLength()) {
			throw new IndexOutOfBoundsException("line = " + line);
		}

		InductionLoop loop = new InductionLoop(line, handler);

		int i;
		for (i = 0; i < loops.size() && loops.get(i).line <= line; i++) {
			;
		}
		loops.add(i, loop);
	}

}
