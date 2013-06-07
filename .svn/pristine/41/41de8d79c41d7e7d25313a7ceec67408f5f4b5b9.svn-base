package pl.edu.agh.cs.kraksim.sna.centrality;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		List<Node> mainNodes = getMainNodes(allNodes);
		
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
	
	private static List<Node> getMainNodes(List<Node> nodes){
		List<Node> mainNodes = new ArrayList<Node>();
		for(int i=0;i<getClaster_number();i++){
			double maxMeasure = Double.MIN_VALUE;
			Node maxNode = null;
			for(int j=0;j<nodes.size();j++){
				if(!mainNodes.contains(nodes.get(j)) && nodes.get(j).getMeasure() > maxMeasure){
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
