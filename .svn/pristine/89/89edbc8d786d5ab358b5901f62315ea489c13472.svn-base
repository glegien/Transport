package pl.edu.agh.cs.kraksim.weka.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WorldStateIntersections implements Serializable {
	private static final long serialVersionUID = -3587599118768810135L;
	private Map<String, Integer> actualPhaseMap;
	private Map<String, Long> phaseWillLastMap;
	private Map<String, Long> phaseLastMap;
	
	public WorldStateIntersections() {
		
	}
	
	public WorldStateIntersections(WorldStateIntersections oldState) {
		this.actualPhaseMap = new HashMap<String, Integer>(oldState.actualPhaseMap);
		this.phaseWillLastMap = new HashMap<String, Long>(oldState.phaseWillLastMap);
		this.phaseLastMap = new HashMap<String, Long>(oldState.phaseLastMap);
	}

	public Map<String, Integer> getActualPhaseMap() {
		return actualPhaseMap;
	}

	public void setActualPhaseMap(Map<String, Integer> actualPhaseMap) {
		this.actualPhaseMap = actualPhaseMap;
	}

	public Map<String, Long> getPhaseWillLastMap() {
		return phaseWillLastMap;
	}

	public void setPhaseWillLastMap(Map<String, Long> phaseWillLastMap) {
		this.phaseWillLastMap = phaseWillLastMap;
	}

	public Map<String, Long> getPhaseLastMap() {
		return phaseLastMap;
	}

	public void setPhaseLastMap(Map<String, Long> phaseLastMap) {
		this.phaseLastMap = phaseLastMap;
	}

	public Integer getActualPhase(String intersectionId) {
		return actualPhaseMap.get(intersectionId);
	}
	
	public Long getPhaseWillLast(String intersectionId) {
		return phaseWillLastMap.get(intersectionId);
	}
	
	public Long getPhaseLast(String intersectionId) {
		return phaseLastMap.get(intersectionId);
	}
}
