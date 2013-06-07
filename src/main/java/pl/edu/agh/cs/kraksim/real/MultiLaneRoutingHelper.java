package pl.edu.agh.cs.kraksim.real;

import java.util.List;

import pl.edu.agh.cs.kraksim.core.Action;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;

/**
 * This class is used to help in lane-level routing
 * @author Maciej Zalewski
 *
 */
public class MultiLaneRoutingHelper {
	RealEView ev = null;
	
	/**
	 * Constructor
	 * @author Maciej Zalewski
	 * @param ev object of a type RealEView, used in class methods.
	 */
	MultiLaneRoutingHelper(RealEView ev){
		this.ev = ev;
	}

	/**
	 * This method chooses the best action from the given list
	 * @author Maciej Zalewski
	 * @param actions list of actions to choose from
	 * @param source link into which car is entering
	 * @return action that is the best to set for the car
	 */
	public Action chooseBestAction(List<Action> actions, Link source) {
		
		// the answer may be obvious...
		if (actions.isEmpty()) return null;
		if (actions.size() < 2) return actions.iterator().next();
		
		// well, it is not. Let's start the search
		Action result = null;
		int lowestCarsCount = Integer.MAX_VALUE;
		int nearestCarPosition = 0;
		
		// for each lane (contained by the action)...
		for (Action action: actions){
			Lane lane = action.getSource();
			LaneRealExt laneRE = ev.ext(lane);
			// ... check it's load
			int lSize = laneRE.getAllCarsNumber();
			int lDist = Integer.MAX_VALUE;
			lDist = laneRE.getFirstCarPosition();
			// ... and, if there are less cars on the lane than on the
			// minimum one ...
			if (lSize < lowestCarsCount) {
				// ... set it as the best.
				result = action;
				lowestCarsCount = lSize;
				nearestCarPosition = lDist;
				continue;
			}
			// ... or there is the same number of cars, but there is more
			// space to the nearest one ...
			if ((lSize == lowestCarsCount) && (nearestCarPosition < lDist)) {
				// ... set it as the best.
				result = action;
				lowestCarsCount = lSize;
				nearestCarPosition = lDist;
				continue;
			}
		}
		return result;
	}
	
	/**
	 * Given the next action, method chooses the best lane from the link given 
	 * by the second parameter.
	 * WARNING: if action==null, the best lane from the main ones is picked
	 * @param action the action to be taken on current link
	 * @param link current link to choose lanes from
	 * @return the best lane to make the given action
	 */
	public Lane chooseBestLaneForAction(Action action, Link link) {
		Lane result = null;
		// if action is not null, it means that some action has already been chosen
		// the best thing would be to put the car on the lane that is source of an action
		// If that lane does not start from the intersection, we shall choose main lane 
		// nearest to it
		if(action != null){
			int destinationLaneNo = action.getSource().getAbsoluteNumber();
			// is it left lane?
			if (destinationLaneNo < link.leftLaneCount()){
				result = link.getMainLane(0);
			// or maybe right?
			}else if (destinationLaneNo >= (link.laneCount() - link.rightLaneCount())){
				result = link.getMainLane(link.mainLaneCount() - 1);
			// so it has to be a main lane
			}else{
				result = link.getLaneAbs(destinationLaneNo);
			}
		}else{
		// otherwise, we are heading to a gateway and have no action set. So, we shall
		// choose the least occupied lane
			int lowestCarsCount = Integer.MAX_VALUE;
			int nearestCarPosition = 0;
			
			// yuk... copy-paste source. Unfortunately, we're looking for lane, not
			// for an action this time. For each of main lanes...
			for(Lane lane:link.getMainLanes()){
				LaneRealExt laneRE = ev.ext(lane);
				// ... check it's load
				int lSize = laneRE.cars.size();
				int lDist = Integer.MAX_VALUE;
				if (lSize > 0) {
					lDist = laneRE.cars.peek().pos;
				}
				// ... and, if there are less cars on the lane than on the
				// minimum one ...
				if (lSize < lowestCarsCount) {
					// ... set it as the best.
					result = lane;
					lowestCarsCount = lSize;
					nearestCarPosition = lDist;
					continue;
				}
				// ... or there is the same number of cars, but there is more
				// space to the nearest one ...
				if ((lSize == lowestCarsCount) && (nearestCarPosition < lDist)) {
					// ... set it as the best.
					result = lane;
					lowestCarsCount = lSize;
					nearestCarPosition = lDist;
					continue;
				}
			}
			
		}
		return result;
	}

}
