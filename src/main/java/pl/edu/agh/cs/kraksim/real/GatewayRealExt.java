package pl.edu.agh.cs.kraksim.real;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.iface.mon.CarEntranceHandler;
import pl.edu.agh.cs.kraksim.iface.mon.CarExitHandler;
import pl.edu.agh.cs.kraksim.iface.mon.GatewayMonIface;
import pl.edu.agh.cs.kraksim.iface.sim.GatewaySimIface;
import pl.edu.agh.cs.kraksim.iface.sim.TravelEndHandler;
import pl.edu.agh.cs.kraksim.main.Simulation;

class GatewayRealExt extends NodeRealExt implements GatewaySimIface,
		GatewayMonIface {

	private static final Logger logger = Logger.getLogger(GatewayRealExt.class);
	
	private final Gateway gateway;
	private final RealSimulationParams params;

	private TravelEndHandler travelEndHandler;
	private final ArrayList<CarEntranceHandler> entranceHandlers;
	private final ArrayList<CarExitHandler> exitHandlers;
	private final LinkedList<Car> cars;

	private int enqueuedCarCount;
	// CHANGE: MZA: to enable multiple lanes and multiple cars
	// leaving each link
	private List<Car> acceptedCars = null;

	GatewayRealExt(Gateway gateway, RealEView ev, RealSimulationParams params) {
		super(ev);
		this.gateway = gateway;
		this.params = params;

		entranceHandlers = new ArrayList<CarEntranceHandler>();
		exitHandlers = new ArrayList<CarExitHandler>();

		cars = new LinkedList<Car>();

		enqueuedCarCount = 0;
		// CHANGE: MZA: to enable multiple lanes
		acceptedCars = new LinkedList<Car>();
	}

	public void setTravelEndHandler(TravelEndHandler handler) {
		travelEndHandler = handler;
	}

	public TravelEndHandler getTravelEndHandler() {
		return travelEndHandler;
	}

	void enqueueCar(Car car) {
		cars.add(car);

		enqueuedCarCount++;
	}

	void simulateTurn() {
		ListIterator<Car> iter = cars.listIterator(cars.size());
		while (enqueuedCarCount > 0) {
			Object d = iter.previous().driver;
			for (CarEntranceHandler h : entranceHandlers)
				h.handleCarEntrance(d);

			enqueuedCarCount--;
		}

		Car car = cars.peek();
		if (car != null)
			if (params.rg.nextDouble() > params.decelProb)
				if (gateway.getOutboundLink() != null)
					if (ev.ext(gateway.getOutboundLink()).enterCar(car, 1, 0))
						cars.poll();
	}

	void acceptCar(Car car) {
		// CHANGE: MZA: to enable multiple lanes
		acceptedCars.add(car);
	}

	void finalizeTurnSimulation() {
		// CHANGE: MZA: to enable multiple lanes
		if (acceptedCars.size() != 0) {			
			for(Car car: acceptedCars){
				for (CarExitHandler h : exitHandlers)
					h.handleCarExit(car.driver);

				if (travelEndHandler != null)
					travelEndHandler.handleTravelEnd(car.driver);
			}

			acceptedCars.clear();
		}
	}

	public void blockInboundLinks() {
		ev.ext(gateway.getInboundLink()).block();
	}

	public void unblockInboundLinks() {
		ev.ext(gateway.getInboundLink()).unblock();
	}

	public void installEntranceSensor(CarEntranceHandler handler) {
		entranceHandlers.add(handler);
	}

	public void installExitSensor(CarExitHandler handler) {
		exitHandlers.add(handler);
	}
}
