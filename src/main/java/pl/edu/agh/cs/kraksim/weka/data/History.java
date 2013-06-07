package pl.edu.agh.cs.kraksim.weka.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Link;

public class History {
	private LinkedList<AssociatedWorldState> history;
	private LinkedList<Integer> turns;
	private String[] linkNameTable;
	
	public History(Set<LinkInfo> set, Set<IntersectionInfo> intersections) {
		this.history = new LinkedList<AssociatedWorldState>();
		this.turns = new LinkedList<Integer>();
		
		this.linkNameTable = createLinkNameTable(set);	
	}
	

	public History(History history) {
		this.history = new LinkedList<AssociatedWorldState>(history.history);
		this.turns = new LinkedList<Integer>(history.turns);
		this.linkNameTable = history.linkNameTable.clone();
	}
	
	public double getCongestionByTimeDistance(int timeDistance, int linkNumber) {
		return history.get(timeDistance).roads.getCarsDensity(linkNumber);
	}

	public void add(int turn, AssociatedWorldState associatedWorldState) {
		turns.add(0, turn);
		history.add(0, associatedWorldState);
	}
	
	public void remove() {
		turns.remove();
		history.remove();
	}
	

	public void clear() {
		turns.clear();
		history.clear();
	}

	public int depth() {
		return history.size();
	}

	public AssociatedWorldState poll() {
		return history.poll();
	}

	public AssociatedWorldState getByDepth(int depth) {
		return history.get(depth);
	}

	public String[] getLinkNameTable() {
		return linkNameTable;
	}
	
	public List<Integer> getTurns() {
		return turns;
	}

	public History addAll(History history2) {
		turns.addAll(history2.turns);
		history.addAll(history2.history);
		return this;
	}
	
	private String[] createLinkNameTable(Set<LinkInfo> set) {
		String[] linkNameTable = new String[set.size()];
	
		for (LinkInfo linkInfo : set) {
			int linkNumber = linkInfo.linkNumber;
			linkNameTable[linkNumber] = linkInfo.linkId;
		}
		return linkNameTable;
	}
}
