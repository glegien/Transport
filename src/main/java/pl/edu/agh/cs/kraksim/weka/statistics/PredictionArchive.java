package pl.edu.agh.cs.kraksim.weka.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;

public class PredictionArchive  implements Iterable<Integer> {
	private List<Integer> turnList = new ArrayList<Integer>();
	private Map<Integer, Set<LinkInfo>> predictingCongestionLinksMap = new HashMap<Integer, Set<LinkInfo>>();
	
	public void storePrediction(int turn, Set<LinkInfo> predictedLinks) {
		turnList.add(0, turn);
		predictingCongestionLinksMap.put(turn, predictedLinks);
	}

	@Override
	public Iterator<Integer> iterator() {
		return turnList.iterator();
	}
	
	public Set<LinkInfo> getDurationListByTurn(int turn) {
		return predictingCongestionLinksMap.get(turn);
	}

	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		for (Integer turn : turnList) {
			text.append(turn + ", ");
			for (LinkInfo pred : predictingCongestionLinksMap.get(turn)) {
				text.append(pred.linkId + ", ");
			}
			text.append("\n");
		}
		return text.toString();
	}

	public void clear() {
		turnList.clear();
		predictingCongestionLinksMap.clear();
	}
}
