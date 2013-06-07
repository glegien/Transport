package pl.edu.agh.cs.kraksim.rl;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.City;
import pl.edu.agh.cs.kraksim.core.ExtensionCreationException;
import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.core.PostCreateOp;
import pl.edu.agh.cs.kraksim.iface.eval.CityEvalIface;

class CityRLExt implements CityEvalIface, PostCreateOp {
	private static final Logger logger = Logger.getLogger(CityRLExt.class);

	private City city;
	private RLEView ev;
	private RLParams params;

	private ArrayList<LaneRLExt> toGatewayExts;
	private ArrayList<LaneRLExt> toIsectExts;
	private int halveAfter;

	CityRLExt(City city, RLEView ev, RLParams params) {
		this.city = city;
		this.ev = ev;
		this.params = params;
		toGatewayExts = new ArrayList<LaneRLExt>();
		toIsectExts = new ArrayList<LaneRLExt>();

		if (params.halvePeriod > 0) {
			halveAfter = params.halvePeriod;
		}

		if (logger.isTraceEnabled()) {
			logger.trace("");
		}
	}

	public void turnEnded() {

		if ((params.halvePeriod > 0) && (--halveAfter == 0)) {
			for (LaneRLExt l : toIsectExts)
				l.halveCounters();

			halveAfter = params.halvePeriod;
		}

		for (LaneRLExt l : toGatewayExts)
			l.updateStatsToGateway();
		for (LaneRLExt l : toIsectExts)
			l.updateStatsToIsect();
		for (LaneRLExt l : toIsectExts)
			l.updateValues1();
		for (LaneRLExt l : toIsectExts)
			l.updateValues2();
		for (LaneRLExt l : toIsectExts)
			l.makeEvaluation();
	}

	public void postCreate() throws ExtensionCreationException {
		for (Iterator<Link> linkIter = city.linkIterator(); linkIter.hasNext();) {
			Link link = linkIter.next();
			boolean toGateway;

			if (link.getEnd().isGateway()) {
				toGateway = true;
			} else {
				toGateway = false;
			}

			for (Iterator<Lane> laneIter = link.laneIterator(); laneIter
					.hasNext();) {
				Lane lane = laneIter.next();
				if (toGateway) {
					toGatewayExts.add(ev.ext(lane));
				} else {
					toIsectExts.add(ev.ext(lane));
				}
			} // for laneIter
		}// for linkIter

	}

}
