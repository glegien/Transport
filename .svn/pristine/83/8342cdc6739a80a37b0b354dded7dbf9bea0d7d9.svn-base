package pl.edu.agh.cs.kraksim.weka;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Phase;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoCursor;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;
import pl.edu.agh.cs.kraksim.iface.carinfo.LaneCarInfoIface;
import pl.edu.agh.cs.kraksim.iface.eval.EvalIView;
import pl.edu.agh.cs.kraksim.iface.eval.LaneEvalIface;
import pl.edu.agh.cs.kraksim.ministat.LinkMiniStatExt;
import pl.edu.agh.cs.kraksim.ministat.MiniStatEView;
import pl.edu.agh.cs.kraksim.simpledecision.IntersectionSimpleDecisionExt;
import pl.edu.agh.cs.kraksim.simpledecision.SimpleDecisionEView;
import pl.edu.agh.cs.kraksim.weka.data.AssociatedWorldState;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateIntersections;
import pl.edu.agh.cs.kraksim.weka.data.WorldStateRoads;

public class DataPicker {
	private City city;
	private final Clock clock;
	private SimpleDecisionEView simpleDecisionEView;
	private MiniStatEView statView;
	private EvalIView evalView;
	private CarInfoIView carInfoView;
	private MovingAverage carDensityMovingAverage;
	private MovingAverage durationLevelMovingAverage;
	private double[] tempDurationTable;

	public DataPicker(City city, Clock clock, MiniStatEView statView, CarInfoIView carInfoView) {
		super();
		this.city = city;
		this.clock = clock;
		this.tempDurationTable = new double[city.linkCount()];
		this.statView = statView;
		this.carInfoView = carInfoView;
		this.carDensityMovingAverage = new MovingAverage(60);
		this.durationLevelMovingAverage = new MovingAverage(60);
		
	}

	public void refreshDurationTable(double durationTable[]) {
		
		for (Iterator<Link> it = city.linkIterator(); it.hasNext();) {
			Link link = it.next();
			LinkMiniStatExt lmse = statView.ext(link);
			int linkNo = link.getLinkNumber();
			tempDurationTable[linkNo] = lmse.getLastPeriodAvgDuration();
			durationTable[linkNo] = tempDurationTable[linkNo];
		}
	}

	public AssociatedWorldState createWorldState() {
		AssociatedWorldState worldState = new AssociatedWorldState();
		worldState.roads = createRoadState();
		worldState.intersections = addIntersectionState();
		
		return worldState;
	}

	private WorldStateIntersections addIntersectionState() {
		WorldStateIntersections intersectionsState = new WorldStateIntersections();
		Map<String, Integer> actualPhaseMap = new HashMap<String, Integer>();
		Map<String, Long> phaseWillLastMap = new HashMap<String, Long>();
		Map<String, Long> phaseLastMap = new HashMap<String, Long>();
		

		for (Iterator<Intersection> it = city.intersectionIterator(); it.hasNext();) {
			Intersection intersection = it.next();
			String intersectionId = intersection.getId();
			IntersectionSimpleDecisionExt isde = simpleDecisionEView.ext(intersection);
			Phase phase = isde.getPhase();
			
			List<Phase> phases = intersection.trafficLightPhases();
			int phaseNumber = phases.indexOf(phase);
			actualPhaseMap.put(intersectionId, phaseNumber);
			
			long endTurn = isde.getStateEndTurn();
			long phaseWillLast = endTurn - clock.getTurn();
			if (phaseWillLast < 0) {
				phaseWillLast = 0;
			}
			phaseWillLastMap.put(intersectionId, phaseWillLast);
			
			long lastPhaseChange = isde.getTurnOfLastPhaseChange();
			long phaseLast = clock.getTurn() - lastPhaseChange;
			phaseLastMap.put(intersectionId, phaseLast);
		}
		
		intersectionsState.setActualPhaseMap(actualPhaseMap);
		intersectionsState.setPhaseWillLastMap(phaseWillLastMap);
		intersectionsState.setPhaseLastMap(phaseLastMap);
		return intersectionsState;
	}

	private WorldStateRoads createRoadState() {
		WorldStateRoads worldStateRoads = new WorldStateRoads();
		double[] durationLevelTable = new double[city.linkCount()];
		double[] carsOutLinkTable = new double[city.linkCount()];
		double[] carsInLinkTable = new double[city.linkCount()];
		double[] carsOnLinkTable = new double[city.linkCount()];
		double[] carsDensityTable = new double[city.linkCount()];
		double[] maxEvaluationTable = new double[city.linkCount()];
		double[] greenDurationTable = new double[city.linkCount()];

		for (Iterator<Link> it = city.linkIterator(); it.hasNext();) {
			Link link = it.next();

			LinkMiniStatExt lmse = statView.ext(link);
			int linkNo = link.getLinkNumber();

			durationLevelTable[linkNo] = computeDurationLevel(link, tempDurationTable[linkNo]);
			carsOutLinkTable[linkNo] = lmse.getLastPeriodCarOutCount();
			carsInLinkTable[linkNo] = lmse.getLastPeriodCarInCount();
			carsOnLinkTable[linkNo] = lmse.getCarCount();
			carsDensityTable[linkNo] = countCarsDensity(link);
			maxEvaluationTable[linkNo] = getMaxEvaluation(link);
			greenDurationTable[linkNo] = getGreenDuration(link);
		}

		double[] carsDensityMovingAvgTable = carDensityMovingAverage.computeAverage(carsDensityTable);
		double[] durationLevelMovingAvgTable = durationLevelMovingAverage.computeAverage(durationLevelTable);
		worldStateRoads.setCarsDensityTable(carsDensityTable);
		worldStateRoads.setDurationLevelTable(durationLevelTable);
		worldStateRoads.setGreenDurationTable(greenDurationTable);
		worldStateRoads.setEvaluationTable(maxEvaluationTable);
		worldStateRoads.setCarsDensityMovingAvgTable(carsDensityMovingAvgTable);
		worldStateRoads.setDurationLevelMovingAvgTable(durationLevelMovingAvgTable);
		worldStateRoads.setCarsInLinkTable(carsInLinkTable);
		worldStateRoads.setCarsOnLinkTable(carsOnLinkTable);
		worldStateRoads.setCarsOutLinkTable(carsOutLinkTable);
		return worldStateRoads;
	}



	private double getGreenDuration(Link link) {
		double maxEvaluation = 0;
		double greenDuration = 0;
		Iterator<Lane> laneIterator = link.laneIterator();
		while (laneIterator.hasNext()) {
			Lane lane = laneIterator.next();
			LaneEvalIface laneEval = evalView.ext(lane);
			float evaluation = laneEval.getEvaluation();
			if (evaluation > maxEvaluation) {
				maxEvaluation = evaluation;
				greenDuration = laneEval.getMinGreenDuration();
			}
		}
		return greenDuration;
	}

	private double computeDurationLevel(Link link, double averageDuration) {
		if (averageDuration == 0.0d) {
			return 1.0d;
		}
		double minimumDuration = link.getLength() / link.getSpeedLimit();
		double durationLevel = averageDuration / minimumDuration;
		return durationLevel;
	}

	private double getMaxEvaluation(Link link) {
		double maxEvaluation = 0;
		Iterator<Lane> laneIterator = link.laneIterator();
		while (laneIterator.hasNext()) {
			Lane lane = laneIterator.next();
			float evaluation = evalView.ext(lane).getEvaluation();
			if (evaluation > maxEvaluation) {
				maxEvaluation = evaluation;
			}
		}
		return maxEvaluation;
	}

	private double countCars(Link link) {
		long cars = 0;
		for (Iterator<Lane> iterator = link.laneIterator(); iterator.hasNext();) {
			Lane lane = iterator.next();
			LaneCarInfoIface laneCarInfo = carInfoView.ext(lane);
			CarInfoCursor infoForwardCursor = laneCarInfo.carInfoForwardCursor();
			while (infoForwardCursor != null && infoForwardCursor.isValid()) {
				cars++;
				infoForwardCursor.next();
			}
		}
		return cars;
	}
	
	private double countCarsDensity(Link link) {
		long cars = 0;
		long length = 0;
		for (Iterator<Lane> iterator = link.laneIterator(); iterator.hasNext();) {
			Lane lane = iterator.next();
			LaneCarInfoIface laneCarInfo = carInfoView.ext(lane);
			CarInfoCursor infoForwardCursor = laneCarInfo.carInfoForwardCursor();
			while (infoForwardCursor != null && infoForwardCursor.isValid()) {
				cars++;
				infoForwardCursor.next();
			}
			length += lane.getLength();
		}
		return (double) cars / length;
	}

	public void setEvalView(EvalIView evalView) {
		this.evalView = evalView;
	}

	public void setSimpleDecisionView(SimpleDecisionEView simpleDecisionView) {
		this.simpleDecisionEView = simpleDecisionView;
	}
}
