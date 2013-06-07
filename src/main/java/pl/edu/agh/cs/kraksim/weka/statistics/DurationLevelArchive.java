package pl.edu.agh.cs.kraksim.weka.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DurationLevelArchive implements Iterable<Integer> {
	private List<Integer> turnList = new ArrayList<Integer>();
	private Map<Integer, List<Double>> congestionList = new HashMap<Integer, List<Double>>();
	
	public void storeStatistics(int turn, double[] durationLevelTable) {
		turnList.add(0, turn);
		List<Double> onePeriodList = new ArrayList<Double>();
		for (double durationLevel : durationLevelTable) {
			onePeriodList.add(durationLevel);
		}
		congestionList.put(turn, onePeriodList);
	}
	
	public Double getCongestionByTimeDistance(int timeDistance, int linkNumber) {
		int turn = turnList.get(timeDistance);
		return congestionList.get(turn).get(linkNumber);
	}

	@Override
	public Iterator<Integer> iterator() {
		return turnList.iterator();
	}

	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		for (Integer turn : turnList) {
			text.append(turn + ", ");
			for (double congestion : congestionList.get(turn)) {
				text.append(congestion + ", ");
			}
			text.append("\n");
		}
		return text.toString();
	}
}
