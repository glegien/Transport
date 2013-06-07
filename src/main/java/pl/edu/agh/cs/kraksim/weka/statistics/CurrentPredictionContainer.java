package pl.edu.agh.cs.kraksim.weka.statistics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;

public class CurrentPredictionContainer {
	private Map<Integer, Set<LinkInfo>> predictionMap = new HashMap<Integer, Set<LinkInfo>>();

	public void addPrediction(LinkInfo linkInfo, int congestionTimePrediction) {
		Set<LinkInfo> congestionLinks = predictionMap.get(congestionTimePrediction);
		if(congestionLinks == null) {
			congestionLinks = new HashSet<LinkInfo>();
			predictionMap.put(congestionTimePrediction, congestionLinks);
		}
		congestionLinks.add(linkInfo);
	}

	public Set<LinkInfo> getPreditionForCurrentPeriod() {
		Set<LinkInfo> congestionLinks = predictionMap.get(0);
		if(congestionLinks == null) congestionLinks = new HashSet<LinkInfo>(); 
		return congestionLinks;
	}

	public void nextPeriod() {
		Map<Integer, Set<LinkInfo>> newPredictionMap = new HashMap<Integer, Set<LinkInfo>>();
		for (Integer i : predictionMap.keySet()) {
			if (i != 0) {
				newPredictionMap.put(i - 1, predictionMap.get(i));
			}
		}
		this.predictionMap = newPredictionMap;
	}

	public boolean willAppearTrafficJam(Link link) {
		for (Integer timePrediction : predictionMap.keySet()){
			Set<LinkInfo> congestionLinks = predictionMap.get(timePrediction);
			LinkInfo testedLink = new LinkInfo(link.getLinkNumber(),"",-1);
			if (congestionLinks.contains(testedLink)) {
				return true;
			}
		}
		return false;
	}

}
