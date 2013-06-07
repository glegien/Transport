package pl.edu.agh.cs.kraksim.weka.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;
import pl.edu.agh.cs.kraksim.weka.utils.Neighbours;

public class LinkStats {
	private Map<LinkInfo, Long> linkCongestions = new HashMap<LinkInfo, Long>();
	private Map<LinkInfo, Long> linkTPCongestions = new HashMap<LinkInfo, Long>();
	private Map<LinkInfo, Long> linkTNCongestions = new HashMap<LinkInfo, Long>();
	private Map<LinkInfo, Long> linkFNCongestions = new HashMap<LinkInfo, Long>();
	private Map<LinkInfo, Long> linkFPCongestions = new HashMap<LinkInfo, Long>();

	public LinkStats(PredictionSetup setup) {
		Map<LinkInfo, Neighbours> neighbourArray = setup.getNeighbourArray();
		Set<LinkInfo> links = neighbourArray.keySet();
		for (LinkInfo link : links) {
			linkCongestions.put(link, 0L);
			linkTPCongestions.put(link, 0L);
			linkTNCongestions.put(link, 0L);
			linkFNCongestions.put(link, 0L);
			linkFPCongestions.put(link, 0L);
		}
	}

	public void countCongestionOnLink(int linkNumber) {
		LinkInfo link = new LinkInfo(linkNumber, "", 0);
		linkCongestions.put(link, linkCongestions.get(link) + 1);
	}

	@Override
	public String toString() {
		String str = "\nLINK STATS\n\n";
		List<Entry<LinkInfo, Long>> links = sortLinksByCongestions();
		for (Entry<LinkInfo, Long> entry : links) {
			LinkInfo link = entry.getKey();
			Long congestions = entry.getValue();
			str += link.linkId + ": " + formatCongestions(congestions) + "  "
					+ tpString(link) + " " + fnString(link) + " " + tnString(link) + " " + fpString(link)+ " \n";
		}
		return str;
	}
	
	
	



	private List<Entry<LinkInfo, Long>> sortLinksByCongestions() {
		List<Entry<LinkInfo, Long>> sortedLinks = new ArrayList<Entry<LinkInfo, Long>>(linkCongestions.entrySet());
		Collections.sort(sortedLinks, new Comparator<Entry<LinkInfo, Long>>() {

			@Override
			public int compare(Entry<LinkInfo, Long> e1, Entry<LinkInfo, Long> e2) {
				Long first = (Long) e1.getValue();
				Long second = (Long) e2.getValue();
				return second.compareTo(first);
			}
		});

		return sortedLinks;
	}

	public void countTruePositive(int linkNumber) {
		LinkInfo link = new LinkInfo(linkNumber, "", 0);
		linkTPCongestions.put(link, linkTPCongestions.get(link) + 1);
	}

	public void countFalseNegative(int linkNumber) {
		LinkInfo link = new LinkInfo(linkNumber, "", 0);
		linkFNCongestions.put(link, linkFNCongestions.get(link) + 1);
	}

	public void countFalsePositive(int linkNumber) {
		LinkInfo link = new LinkInfo(linkNumber, "", 0);
		linkFPCongestions.put(link, linkFPCongestions.get(link) + 1);
	}

	public void countTrueNegative(int linkNumber) {
		LinkInfo link = new LinkInfo(linkNumber, "", 0);
		linkTNCongestions.put(link, linkTNCongestions.get(link) + 1);
	}
	
	public String tnString(LinkInfo link) {
		Long tn = linkTNCongestions.get(link);
		Long fp = linkFPCongestions.get(link);
		
		long nonCongestions = tn + fp;
		double predicted = (double) tn / nonCongestions;

		return format("TN", tn, predicted);
	}
	
	public String fpString(LinkInfo link) {
		Long tn = linkTNCongestions.get(link);
		Long fp = linkFPCongestions.get(link);
		
		long nonCongestions = tn + fp;
		double nonPredicted = (double) fp / nonCongestions;
		
		return format("FP", fp, nonPredicted);
	}
	
	public String tpString(LinkInfo link) {
		Long tp = linkTPCongestions.get(link);
		Long fn = linkFNCongestions.get(link);
		
		long congestions = tp + fn;
		double predicted = (double) tp / congestions;

		return format("TP", tp, predicted);
	}
	public String fnString(LinkInfo link) {
		Long tp = linkTPCongestions.get(link);
		Long fn = linkFNCongestions.get(link);
		
		long congestions = tp + fn;
		double nonPredicted = (double) fn / congestions;
		
		return format("FN", fn, nonPredicted);
	}
	
	
	public String format(String s, long l, double d) {
		return String.format(s + ": %8d[%.3f]", l, d);
	}
	
	private String formatCongestions(Long congestions) {
		return String.format("%8d", congestions);
	}

}
