package pl.edu.agh.cs.kraksim.weka.statistics;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.weka.PredictionSetup;
import pl.edu.agh.cs.kraksim.weka.data.LinkInfo;

public class ResultCreator {
	private static final Logger logger = Logger.getLogger(ResultCreator.class);
	private PredictionSetup setup;
	private LinkStats linkStats;
	private Archive<Boolean> congestionArchive;
	private PredictionArchive predictionArchive;
	private long totalItemsAmount;
	private long totalCongestionsAmount;
	private long totalPredictableCongestionsAmount;
	private long falsePositiveCongestions;
	private long falseNegativeCongestions;
	private long truePositiveCongestions;
	private long trueNegativeCongestions;

	public ResultCreator(PredictionSetup setup,
			Archive<Boolean> congestionArchive,
			PredictionArchive predictionArchive) {
		super();
		this.setup = setup;
		this.linkStats = new LinkStats(setup);
		this.congestionArchive = congestionArchive;
		this.predictionArchive = predictionArchive;
	}

	private void resetResults() {
		this.totalItemsAmount = 0;
		this.totalCongestionsAmount = 0;
		this.totalPredictableCongestionsAmount = 0;
		this.falsePositiveCongestions = 0;
		this.falseNegativeCongestions = 0;
		this.truePositiveCongestions = 0;
		this.trueNegativeCongestions = 0;
	}

	public void computePartialResults(Set<LinkInfo> predictableLinks) {
		logger.debug("Congestions: " + congestionArchive);
		logger.debug("Predictions: " + predictionArchive);
		for (Integer turn : predictionArchive) {
			List<Boolean> congestionList = congestionArchive
					.getCongestionListByTurn(turn);
			Set<LinkInfo> predictingCongestionLinks = predictionArchive
					.getDurationListByTurn(turn);
			Set<Integer> predictingLinkNumbers = linkIdsToLinkNumbers(predictingCongestionLinks);
			for (int linkNumber = 0; linkNumber < congestionList.size(); linkNumber++) {
				Boolean congestion = congestionList.get(linkNumber);
				boolean isLinkPredictingCongestion = predictingLinkNumbers
						.contains(linkNumber);

				this.totalItemsAmount++;
				if (congestion) {
					this.totalCongestionsAmount++;
				}

				if (predictableLinks.contains(new LinkInfo(linkNumber, "link", 0))) {
					
					if (congestion) {
						linkStats.countCongestionOnLink(linkNumber);
						this.totalPredictableCongestionsAmount++;
					}

					if (congestion && isLinkPredictingCongestion) {
						linkStats.countTruePositive(linkNumber);
						this.truePositiveCongestions++;
					}
					if (congestion && !isLinkPredictingCongestion) {
						linkStats.countFalseNegative(linkNumber);
						this.falseNegativeCongestions++;
					}
					if (!congestion && isLinkPredictingCongestion) {
						linkStats.countFalsePositive(linkNumber);
						this.falsePositiveCongestions++;
					}
					if (!congestion && !isLinkPredictingCongestion) {
						linkStats.countTrueNegative(linkNumber);
						this.trueNegativeCongestions++;
					}
				}
			}
		}
		congestionArchive.clear();
		predictionArchive.clear();
	}

	Set<Integer> linkIdsToLinkNumbers(Set<LinkInfo> linkIds) {
		Set<Integer> linkNumbers = new HashSet<Integer>();
		for (LinkInfo linkInfo : linkIds) {
			linkNumbers.add(linkInfo.linkNumber);
		}
		return linkNumbers;
	}

	public long getTotalCongestionsAmount() {
		return totalCongestionsAmount;
	}

	public long getFalsePositiveCongestions() {
		return falsePositiveCongestions;
	}

	public long getFalseNegativeCongestions() {
		return falseNegativeCongestions;
	}

	public long getTruePositiveCongestions() {
		return truePositiveCongestions;
	}

	public long getTotalItemsAmount() {
		return totalItemsAmount;
	}
	
	public String getResultText() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Time: " + new Date() + "\n");
		builder.append("Total Items: " + totalItemsAmount + "\n");
		builder.append("Total Congestion: " + totalCongestionsAmount + "\n");
		builder.append("Total predictable congestions: "
				+ totalPredictableCongestionsAmount + "\n");
		builder.append("True Positive: " + truePositiveCongestions + "\n");
		builder.append("True Negative: " + trueNegativeCongestions + "\n");
		builder.append("False Negative " + falseNegativeCongestions + "\n");
		builder.append("False Positive " + falsePositiveCongestions + "\n");
		builder.append(congestionsPercentage());
		builder.append(nonCongestionsPercentage());
	
		builder.append(linkStats);
		return builder.toString();
	}
	
	private String nonCongestionsPercentage() {
		long nonCongestions = falsePositiveCongestions + trueNegativeCongestions;
		double predicted = (double) trueNegativeCongestions / nonCongestions;
		double notPredicted = (double) falsePositiveCongestions / nonCongestions;
		
		DecimalFormat df = new DecimalFormat("#.###");
		String text = "\nNon congestion class\n";
		text += "Positive: " + df.format(predicted) + "\n";
		text += "Negative: " + df.format(notPredicted)+ "\n";
		return text;
	}

	private String congestionsPercentage() {
		long congestions = truePositiveCongestions + falseNegativeCongestions;
		double predicted = (double) truePositiveCongestions / congestions;
		double notPredicted = (double) falseNegativeCongestions / congestions;
		
		DecimalFormat df = new DecimalFormat("#.###");
		String text = "\nCongestion class\n";
		text += "Positive: " + df.format(predicted) + "\n";
		text += "Negative: " + df.format(notPredicted)+ "\n";
		return text;
	}
}
