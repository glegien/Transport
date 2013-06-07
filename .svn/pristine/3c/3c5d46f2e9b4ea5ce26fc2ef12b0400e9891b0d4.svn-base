package pl.edu.agh.cs.kraksim.routing;

import java.util.Iterator;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;
import pl.edu.agh.cs.kraksim.iface.eval.EvalIView;
import pl.edu.agh.cs.kraksim.ministat.LinkMiniStatExt;
import pl.edu.agh.cs.kraksim.ministat.MiniStatEView;
import pl.edu.agh.cs.kraksim.simpledecision.SimpleDecisionEView;
import pl.edu.agh.cs.kraksim.weka.WekaPredictionModule;

public class TimeTableRules implements ITimeTable {
	private static final Logger logger = Logger.getLogger(TimeTable.class);
	private double[] timeArray;
	private Clock clock;
	private City city;

	private long timeArrayLastUpdate = -1;
	private long timeArrayUpdatePeriod = 300;
	private WekaPredictionModule predictor;

	public TimeTableRules(City city, Clock clock, WekaPredictionModule predictionModule) {
		this.city = city;
		this.clock = clock;
		this.predictor = predictionModule;
	}

	public double getTime(Link link) {
		if (timeArrayRefreshNeeded()) {
			refreshTimeArray();
		}
		return getLinkTime(link);
	}

	/*
	 * Refresh array of values used in dijktry algorithm
	 */
	private void refreshTimeArray() {
		timeArray = new double[city.linkCount()];

		for (Iterator<Link> it = city.linkIterator(); it.hasNext();) {
			Link link = it.next();
			refreshLink(link);
		}
	}

	public double getLinkTime(Link link) {
		double avgDuration = timeArray[link.getLinkNumber()];
		avgDuration = predictor.predictAvgDuration(link, avgDuration);
		return avgDuration;
	}

	private void refreshLink(Link link) {
		int linkNumber = link.getLinkNumber();
		double avgDuration = predictor.getLastPeriodAvgDurationForLink(linkNumber);
		// LinkMiniStatExt lmse = statView.ext(link);
		// double avgDuration = lmse.getLastPeriodAvgDuration();

		avgDuration = avgDuration + this.timeArray[linkNumber];
		avgDuration /= 2;

		this.timeArray[linkNumber] = avgDuration;
		// avgDuration==0. if no cars passed recently - so we shall
		// set it manually to the minimal value
		if (avgDuration == 0.) {
			avgDuration = link.getLength() / link.getSpeedLimit();
			this.timeArray[linkNumber] = avgDuration;
		}
	}

	private boolean timeArrayRefreshNeeded() {

		boolean refresh = false;
		int currentTime = clock.getTurn();

		if (timeArrayLastUpdate < 0) {
			refresh = true;
			timeArrayLastUpdate = currentTime;
		} else {
			long difference = currentTime - timeArrayLastUpdate;

			if (difference > timeArrayUpdatePeriod) {
				timeArrayLastUpdate = currentTime;
				refresh = true;
			}
		}

		return refresh;
	}
}
