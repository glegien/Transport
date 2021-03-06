package pl.edu.agh.cs.kraksim.sna.centrality;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hamcrest.core.IsInstanceOf;

import pl.edu.agh.cs.kraksim.KraksimConfigurator;
import pl.edu.agh.cs.kraksim.core.Gateway;
import pl.edu.agh.cs.kraksim.core.Intersection;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.Node;
import pl.edu.agh.cs.kraksim.sna.SnaConfigurator;
import edu.uci.ics.jung.graph.Graph;

/**
 * Klastrowanie kmeans
 */
public class KmeansClustering {
	
	public static Map<Node, Set<Node>> currentClustering;

	public static List<Set<Node>> clusterGraph(Graph<Node, Link> graph){
		
		List<Node> allNodes = new ArrayList<Node>(graph.getVertices());
		List<Node> mainNodes = getMainNodes(allNodes, graph);
		
		List<Set<Node>> clusters = new ArrayList<Set<Node>>();
		
		//wrzucamy g��wne w�z�y jako oddzielne clustry
		for(Node main : mainNodes){
			Set<Node> cluster = new HashSet<Node>();
			cluster.add(main);
			clusters.add(cluster);
		}
		for(Node node : allNodes){
			if(mainNodes.contains(node))
				continue;
			Node closestMean = findClosestMean(node, mainNodes);
			for(Set<Node> cluster : clusters)
				if(cluster.contains(closestMean)){
					cluster.add(node);
					break;
				}	
		}
		
		//wype�nianie mapy �rodek clastra - claster
		currentClustering = new LinkedHashMap<Node, Set<Node>>();
		for(Node n : mainNodes){
			for(Set<Node> cluster : clusters)
				if(cluster.contains(n)){
					currentClustering.put(n, cluster);
					break;
				}	
		}
		
		
		for(Node n : mainNodes)
			System.out.println(n.getId());
		printClusters(clusters);
		
		return clusters;
	}
	
	public static int getClaster_number() {
		return SnaConfigurator.getSnaClusters();
	}
	
	/**
	 * Wyznacza odlęgłość między dwoma sąsiadującymi węzłami w grafie. 
	 * @param nodeA
	 * @param nodeB
	 * @param graph
	 * @param type Sposób obliczania odległości mięzy węzłami.
	 * @return
	 */
	private static int distanceToAdd(Node nodeA, Node nodeB, Graph<Node, Link> graph, SNADistanceType type)
	{
		switch(type){
			case CrossroadsNumber : return 1;
			case Weight : return (int) graph.findEdge(nodeA, nodeB).getWeight()*100;
			case Load : return (int) graph.findEdge(nodeA, nodeB).getLoad()*100;
		default:
			break;
		}		
		return 0;
	}
	
	/**
	 * Oblicza wartość najkrótszej ścieżki w grafie między dwoma wezłami. Koszt krawędzi zalezy od typu
	 * podanego jako parametr 'type'. Wykorzystuje algorytm Dijkstry.
	 * @param nA
	 * @param nB
	 * @param graph
	 * @param type Sposób obliczania odległości miedzy wezłami.
	 * @return
	 */
	private static int getDistance(Node nA, Node nB, Graph<Node, Link> graph, SNADistanceType type){
		if(nA == nB)
			return 0;
		
		HashMap<Node, Integer> distance = new HashMap<Node, Integer>();
		for(Node n: graph.getVertices())
			distance.put(n, Integer.MAX_VALUE);
		distance.put(nA, 0);
		
		List<Node> visited = new LinkedList<Node>();
		Node current = nA;
		while(!visited.contains(nB)){
			visited.add(current);
			for(Node neig: graph.getNeighbors(current)){ //aktualizaja odległości
				if(!visited.contains(neig)){
					//obliczanie odległosci miedzy sąsiadami - "current" i "neig"
					int dist = distanceToAdd(current, neig, graph, type);
					//doliczanie odległosci
					distance.put(neig, new Integer((Integer) distance.get(current) + dist)); 
				}
			}
			
			double minMeasure = Double.MAX_VALUE; //nowy current
			Node minNode = null;
			for(Node n: distance.keySet()){
				if(!visited.contains(n)){
					if(distance.get(n) < minMeasure){
						minMeasure = distance.get(n);
						minNode = n;
					}
				}
			}
			current = minNode;
		}
		return (Integer) distance.get(nB);
	}
	
	/**
	 * Dla danego węzła znajduje kandydatów, na których może on głosować. W obecnej wersji
	 * kazdy z węzłów(nie będący typu Gateway) posiada bierne prawo wyborcze. Oczywiście 
	 * węzeł nie może głosować na samego siebie. 
	 * @param node Wezeł dla którego wyznaczamy kandydatów
	 * @param nodes
	 * @param graph
	 * @return Lista wezłów na które można głosować
	 */
	private static List<Node> getCandidates(Node node, List<Node> nodes, Graph<Node, Link> graph) {
		List<Node> candidates = new ArrayList<Node>();
		candidates = new LinkedList<Node>(nodes);
		candidates.remove(node);
		synchronized(candidates){
			Iterator<Node> it = candidates.iterator();
			while(it.hasNext()){
				Node n = it.next();
				if(n instanceof Gateway)
					it.remove();
			}
		}
		return candidates;
	}
	
	/**
	 * Na podstawie dwóch list - listy węzłów i odpowiadających im współczynników
	 * atrakcyjności wybierany jest jeden węzeł o największej wartości. W modelu 
	 * tym każdy z węzłów wybiera jednego zwycięzcę. Listy wejściowe sa równoliczne.
	 * @param candidates Lista węzłów
	 * @param results Obliczona "atrakcyjność" danego węzła
	 * @return Lista ze zwycięzcami głosowania. Zawiera jeden element
	 */
	private static List<Node> getWinners(List<Node> candidates, List<Double> results) {
		List<Node> winners = new LinkedList<Node>();
		Double max = results.get(0);
		Node winner = candidates.get(0);
		for(int i=1;i<candidates.size();i++)
			if(results.get(i) > max){
				max = results.get(i);
				winner = candidates.get(i);
			}
		winners.add(winner);
		return winners;
	}
	
	/**
	 * Procedura głosowania. Kazdy z wezłów głosuje na jenego kandydata. Wyniki sa zliczane,
	 * zwycięża ten z największa liczbą głosów.
	 * @param nodes Wezły
	 * @param graph
	 * @param type
	 * @return Lista zawierająca zwycięzców - główne węzły w klastrach. Liczba zwycięzców 
	 * wyznaczana przez "getClaster_number".
	 */
	private static List<Node> voting(List<Node> nodes, Graph<Node, Link> graph, SNADistanceType type) {
		
		List<Node> winners = new LinkedList<Node>();
		//struktura do zliczania głosów
		HashMap<Node, Integer> results = new HashMap<Node, Integer>();
		for(Node n: nodes)
			if(n instanceof Intersection)
				results.put(n,  0);
			else results.put(n,  -1*Integer.MAX_VALUE);
		
		// każdy wezeł głosuje
		for(Node node: nodes){ 
			List<Node> localWinners = null;
			List<Node> candidates = getCandidates(node,nodes,graph);
			List<Double> values = new LinkedList<Double>(); 
			for(Node candidate: candidates){
				double measure = candidate.getMeasure();
				double distance = getDistance(node, candidate, graph, type);
				double value = measure / distance;
				values.add(value);
			}			
			localWinners = getWinners(candidates, values);
			for(Node n: localWinners)
					results.put(n, results.get(n)+1);
		}
		//po głosowaniu, wyłaniamy zwycięzców
		for(int i=0;i<getClaster_number();i++){
			double maxMeasure = -1*Double.MIN_VALUE;
			Node maxNode = null;
			for(Node n: results.keySet()){
				if(results.get(n) > maxMeasure && n instanceof Intersection){
					maxMeasure = results.get(n);
					maxNode = n;
				}
			}
			winners.add(maxNode);
			results.put(maxNode, -1*Integer.MAX_VALUE); // żeby nie wybrać ponownie tego samego
		}
		return winners; 
	}
	
	/**
	 * W zależności od sposobu wyboru głównych węzłów w grafie(ustawione w pliku konfiguracyjnym)
	 * przechodzi do procedury głosowania lub metody opartej wyłącznie o wagi wezłów.
	 * @param nodes
	 * @param graph
	 * @return
	 */
	private static List<Node> getMainNodes(List<Node> nodes, Graph<Node, Link> graph){
		String distanceType = KraksimConfigurator.getSNADistanceType();
		SNADistanceType type = null;
		try{
			type = SNADistanceType.valueOf(distanceType);
		}catch(Exception e){
			//Intentially empty
		}
		if(type == null || type == SNADistanceType.Lack)
			return getMainNodes(nodes);
		List<Node> mainNodes = voting(nodes, graph, type);
		return mainNodes;
	}
	
	/**
	 * Wyznaczanie głównych węzłów bez użycia algorytmu głosujacego.
	 * @param nodes
	 * @return
	 */
	private static List<Node> getMainNodes(List<Node> nodes){
		List<Node> mainNodes = new ArrayList<Node>();
		for(int i=0;i<getClaster_number();i++){
			double maxMeasure = Double.MIN_VALUE;
			Node maxNode = null;
			for(int j=0;j<nodes.size();j++){
				double measure = nodes.get(j).getMeasure(); 
				if(!mainNodes.contains(nodes.get(j)) && measure > maxMeasure){
					maxMeasure = nodes.get(j).getMeasure();
					maxNode = nodes.get(j);
				}
			}
			mainNodes.add(maxNode);
		}
		return mainNodes;
		
	}
	
	private static Node findClosestMean(Node node, List<Node> means){
		Node closest = null;
		double minDist = Double.MAX_VALUE;
		for(Node mean : means){
			double distance = node.getPoint().distance(mean.getPoint());
			if(distance < minDist){
				minDist =  distance;
				closest = mean;
			}
		}
		
		return closest;
	}
	
	private static void printClusters(List<Set<Node>> clusters){
		int i = 1;
		for(Set<Node> cluster : clusters){
			System.out.println("Cluster nr. " + i);
			for(Node node : cluster)
				System.out.println(node.getId());
			i++;
		}
				
	}
	
	public static Set<Node> findMyCluster(Node node){
		for(Set<Node> cluster : currentClustering.values())
			for(Node n : cluster)
				if(n == node)
					return cluster;
			
		return null;
	}
	
	public static Node findMyMainNode(Node node){
		for(Node boss : currentClustering.keySet())
			for(Node n : currentClustering.get(boss))
				if(n == node)
					return boss;
			
		return null;
	}
	
}
