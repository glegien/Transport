package pl.edu.agh.cs.kraksim.rl;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.iface.block.LaneBlockIface;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoCursor;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;
import pl.edu.agh.cs.kraksim.iface.carinfo.LaneCarInfoIface;
import pl.edu.agh.cs.kraksim.iface.eval.LaneEvalIface;

class LaneRLExt implements LaneEvalIface {
	private static final Logger logger = Logger.getLogger(LaneRLExt.class);

	private static final int ZONELENGHT = 20;

	private Lane lane;

	private RLEView ev;
	private RLParams params;

	private LaneCarInfoIface laneCarInfoExt;
	private LaneBlockIface laneBlockExt;

	private int n;
	private float[] V;
	private float[] Qblocked;
	private float[] Qunblocked;
	private CellStat[] cells;
	private float evaluation;
	private int queueLength;

	private String id;

	final private static class TransitionStat {
		private Lane destLane;
		private int destPos;
		private int count;

		private TransitionStat(Lane destLane, int destPos, int count) {
			this.destLane = destLane;
			this.destPos = destPos;
			this.count = count;
		}
	}

	final private static class CellStat {
		private int carOnBlockedCount;
		private TransitionStat[] transOnBlockedStats;
		private int carOnUnblockedCount;
		private TransitionStat[] transOnUnblockedStats;

		private void countCarOnBlocked(Lane destLane, int destPos) {
			carOnBlockedCount++;

			if (transOnBlockedStats != null) {
				for (int i = 0; i < transOnBlockedStats.length; i++) {
					TransitionStat ts = transOnBlockedStats[i];
					if (ts.destLane == destLane && ts.destPos == destPos) {
						ts.count++;
						return;
					}
				}
			}

			transOnBlockedStats = extend(transOnBlockedStats,
					new TransitionStat(destLane, destPos, 1));
		}

		private void countCarOnUnblocked(Lane destLane, int destPos) {
			carOnUnblockedCount++;

			if (transOnUnblockedStats != null) {
				for (int i = 0; i < transOnUnblockedStats.length; i++) {
					TransitionStat ts = transOnUnblockedStats[i];
					if (ts.destLane == destLane && ts.destPos == destPos) {
						ts.count++;
						return;
					}
				}
			}

			transOnUnblockedStats = extend(transOnUnblockedStats,
					new TransitionStat(destLane, destPos, 1));
		}

		private void halveCounters() {
			if (carOnBlockedCount > 0) {
				carOnBlockedCount = 0;
				for (int i = 0; i < transOnBlockedStats.length; i++) {
					transOnBlockedStats[i].count /= 2;
					carOnBlockedCount += transOnBlockedStats[i].count;
				}
			}

			if (carOnUnblockedCount > 0) {
				carOnUnblockedCount = 0;
				for (int i = 0; i < transOnUnblockedStats.length; i++) {
					transOnUnblockedStats[i].count /= 2;
					carOnUnblockedCount += transOnUnblockedStats[i].count;
				}
			}
		}

		private TransitionStat[] extend(TransitionStat[] a, TransitionStat ts) {
			if (a == null)
				return new TransitionStat[] { ts };
			TransitionStat[] b = new TransitionStat[a.length + 1];
			for (int i = 0; i < a.length; i++)
				b[i] = a[i];
			b[a.length] = ts;
			return b;
		}
	}

	LaneRLExt(Lane lane, RLEView ev, CarInfoIView carInfoView,
			BlockIView blockView, RLParams params) {
		this.lane = lane;
		this.ev = ev;
		this.params = params;

		n = lane.getLength();
		this.id = lane.getOwner().getId() + ":" + lane.getAbsoluteNumber()
				+ ":" + n;
		V = new float[n];
		/* for lanes going to gateway, we may assume that V[i] = 0.0 for all i */

		laneCarInfoExt = carInfoView.ext(lane);

		if (lane.getOwner().getEnd().isIntersection()) {
			laneBlockExt = blockView.ext(lane);

			Qblocked = new float[n];
			Qunblocked = new float[n];

			cells = new CellStat[n];
			for (int pos = 0; pos < n; pos++)
				cells[pos] = new CellStat();
		}

		if (logger.isTraceEnabled()) {
			logger.trace(lane + ", lengt (n)=" + n);
		}
	}

	private void countCar(int pos, Lane destLane, int destPos) {
		if (logger.isTraceEnabled()) {
			logger.trace(id + ": from=" + pos + ", to lane " + destLane
					+ " newPos" + destPos);
		}
		try {
			if (laneBlockExt.isBlocked()) {
				cells[pos].countCarOnBlocked(destLane, destPos);
			} else {
				cells[pos].countCarOnUnblocked(destLane, destPos);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	void halveCounters() {
		int n = lane.getLength();
		for (int pos = 0; pos < n; pos++)
			cells[pos].halveCounters();
	}

	void updateStatsToGateway() {
		CarInfoCursor cursor = laneCarInfoExt.carInfoForwardCursor();
		if (cursor.isValid()) {
			Lane l = cursor.beforeLane();
			if (l != null && l != lane) {
				ev.ext(l).countCar(cursor.beforePos(), lane,
						cursor.currentPos());
			}
		}
	}

	void updateStatsToIsect() {
		// CarInfoCursor cursor = laneCarInfoExt.carInfoForwardCursor();
		CarInfoCursor cursor = laneCarInfoExt.carInfoBackwardCursor();
		while (cursor.isValid()) {
			Lane l = cursor.beforeLane();
			// optimalization of the frequent case: don't use module view when
			// the
			// car hasn't changed the lane
			int pos = cursor.currentPos();
			if ((n - pos) > ZONELENGHT) {
				break;
			}
			if (l == lane) {
				countCar(cursor.beforePos(), lane, pos);
			} else if (l != null) {
				ev.ext(l).countCar(cursor.beforePos(), lane, pos);
			}
			cursor.next();
		}
	}

	void updateValues1() {
		for (int pos = 0; pos < n; pos++) {
			CellStat c = cells[pos];

			if (c.carOnBlockedCount == 0)
				Qblocked[pos] = 0.0f;
			else {
				float q = 0.0f;
				for (int i = 0; i < c.transOnBlockedStats.length; i++) {
					TransitionStat ts = c.transOnBlockedStats[i];
					float p = (float) ts.count / (float) c.carOnBlockedCount;
					float v;
					// optimalization of the frequent case: don't use module
					// view when the
					// car hasn't changed the lane
					v = 1.0f;
					if (ts.destLane == lane) {
						v = params.discount * V[ts.destPos];
						if (ts.destPos == pos)
							v += 1.0f;
					} else {
						try {
							v = params.discount
									* ev.ext(ts.destLane).V[ts.destPos];
						} catch (Exception e) {
							;// TODO
						}
					}
					// TODO: lepsza metoda karania
					q += p * v;
				}
				Qblocked[pos] = q;
			}

			if (c.carOnUnblockedCount == 0)
				Qunblocked[pos] = 0.0f;
			else {
				float q = 0.0f;
				for (int i = 0; i < c.transOnUnblockedStats.length; i++) {
					TransitionStat ts = c.transOnUnblockedStats[i];
					float p = (float) ts.count / (float) c.carOnUnblockedCount;
					float v;
					// optimalization of the frequent case: don't use module
					// view when the
					// car hasn't changed the lane
					v = 1.0f;
					if (ts.destLane == lane) {
						v = params.discount * V[ts.destPos];
						if (ts.destPos == pos)
							q += 1.0f;
					} else {
						try {
							v = params.discount
									* ev.ext(ts.destLane).V[ts.destPos];
						} catch (Exception e) {
							;// TODO
						}
					}
					// TODO: lepsza metoda karania
					q += p * v;
				}
				Qunblocked[pos] = q;
			}
		}
	}

	void updateValues2() {
		for (int pos = 0; pos < n; pos++) {
			CellStat c = cells[pos];

			int totalCount = c.carOnBlockedCount + c.carOnUnblockedCount;

			if (totalCount > 0) {
				float pb = (float) c.carOnBlockedCount / (float) totalCount;
				V[pos] = pb * Qblocked[pos] + (1 - pb) * Qunblocked[pos];
			} else
				V[pos] = 0.0f;
		}
	}

	void makeEvaluation() {
		evaluation = 0.0f;

		int queueEnd = lane.getLength();
		if (logger.isTraceEnabled()) {
			logger.trace(id + ", blocked=" + laneBlockExt.isBlocked()
					+ ", queueEnd=" + queueEnd);
		}
		CarInfoCursor cursor = laneCarInfoExt.carInfoBackwardCursor();

		while (cursor.isValid()) {
			// TODO: currentPos?
			// int pos = cursor.currentPos();

			int pos = cursor.currentPos();
			// System.out.println( n + ", " + pos + " = " + (n - pos) );
			if ((n - pos) > ZONELENGHT) {
				break;
			}

			if (logger.isTraceEnabled()) {
				logger.trace("      position=" + pos);
			}
			if (pos + 1 < queueEnd) {
				break;
			}

			queueEnd = pos;

			// try {
			evaluation += Qblocked[pos] - Qunblocked[pos];
			if (logger.isTraceEnabled()) {
				logger.trace("      queueEnd := position=" + pos);
				logger.trace("      evaluation=" + evaluation + ": Qb="
						+ Qblocked[pos] + " - Qu" + Qunblocked[pos]);
			}
			// if ( evaluation < 0.0 ) {
			// System.out.println( lane + "  " + evaluation );
			// }
			// }
			// catch (Exception e) {
			// logger.warn( e.getMessage() );
			// }

			cursor.next();
		}

		queueLength = lane.getLength() - queueEnd;
		if (logger.isTraceEnabled()) {
			logger.trace("      queueLength=" + queueLength);
		}
	}

	public float getEvaluation() {
		if (logger.isTraceEnabled()) {
			logger.trace(id + " queue=" + queueLength + ", evaluation="
					+ evaluation + ", blocked=" + laneBlockExt.isBlocked());
		}
		return evaluation;
	}

	public int getMinGreenDuration() {
		int ret = (int) ((queueLength - 1) * (float) params.carStartDelay + (queueLength / (float) params.carMaxVelocity));
		return Math.max(ret, params.minimumGreen);
	}

}
