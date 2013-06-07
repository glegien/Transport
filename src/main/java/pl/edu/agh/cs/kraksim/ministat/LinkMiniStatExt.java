package pl.edu.agh.cs.kraksim.ministat;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Link;
import pl.edu.agh.cs.kraksim.iface.Clock;
import pl.edu.agh.cs.kraksim.iface.mon.CarDriveHandler;
import pl.edu.agh.cs.kraksim.iface.mon.LinkMonIface;
import pl.edu.agh.cs.kraksim.iface.mon.MonIView;

public class LinkMiniStatExt {
	private static final Logger logger = Logger.getLogger(LinkMiniStatExt.class);
	private Map<Object, Integer> entranceTurnMap;

	private int carCount;
	private int driveCount;
	private double totalDriveLength;
	private double totalDriveDuration;
	private float s;
	private LastPeriodAvgDuration lastPeriodAvgDuration;
	private LastPeriodAvgVelocity lastPeriodAvgVelocity;
	private LastPeriodCarCount lastPeriodCarOutCount;
	private LastPeriodCarCount lastPeriodCarInCount;
	private Link link;
	private double lastLastPeriodAvgDuration;

	LinkMiniStatExt(final Link link, MonIView monView, final Clock clock, final StatHelper helper) {
		if (logger.isTraceEnabled()) {
			logger.trace("LinkMiniStatExt init() " + link);
		}
		entranceTurnMap = new HashMap<Object, Integer>();
		this.link = link;
		
		lastPeriodAvgDuration = new LastPeriodAvgDuration();
		lastPeriodAvgVelocity = new LastPeriodAvgVelocity(link);
		lastPeriodCarOutCount = new LastPeriodCarCount();
		lastPeriodCarInCount = new LastPeriodCarCount();
		
		LinkMonIface l = monView.ext(link);
		l.installInductionLoops(0, new CarDriveHandler() {

			public void handleCarDrive(int velocity, Object driver) {
				carCount++;

				lastPeriodCarInCount.update();
				entranceTurnMap.put(driver, clock.getTurn());
			}
		});

		l.installInductionLoops(link.getLength(), new CarDriveHandler() {

			public void handleCarDrive(int velocity, Object driver) {
				carCount--;

				int length = link.getLength();

				int duration = clock.getTurn() - entranceTurnMap.remove(driver);

				helper.incTravelLength(driver, length);

				driveCount++;
				totalDriveLength += length;
				totalDriveDuration += duration;
				lastPeriodAvgDuration.update(duration);
				lastPeriodAvgVelocity.update(duration);
				lastPeriodCarOutCount.update();

				s += duration * duration;
			}
		});
	}

	void clear() {
		if (logger.isTraceEnabled()) {
			logger.trace("LinkMiniStatExt clear() ");
		}
		entranceTurnMap.clear();
		carCount = 0;
		driveCount = 0;
		totalDriveLength = 0.0f;
		totalDriveDuration = 0.0f;
		s = 0.0f;
	}

	public int getCarCount() {
		return carCount;
	}

	public int getDriveCount() {
		return driveCount;
	}

	public float getAvgVelocity() {
		return totalDriveLength > 0.0f ? (float) (totalDriveLength / totalDriveDuration) : 0.0f;
	}

	public float getAvgDuration() {
		return driveCount > 0 ? (float) (totalDriveDuration / driveCount) : 0.0f;
	}

	public float getLastPeriodAvgDuration() {
		float avgDuration = lastPeriodAvgDuration.getLastPeriodAvgDuration();
		lastLastPeriodAvgDuration = avgDuration;
		return avgDuration;
	}

	/**
	 * Zwraca średnią prędkość za ostatni okres czasu lub -1.0f jeśli
	 * żaden pojazd nie wyjechał
	 * 
	 * @return
	 */
	public double getLastLastPeriodAvgVelocity() {
		if (lastLastPeriodAvgDuration == 0.0) {
			return -1.0f;
		}
		return link.getLength() / lastLastPeriodAvgDuration;
	}

	public float getLastPeriodAvgVelocity() {
		return lastPeriodAvgVelocity.getLastPeriodAvgVelocity();
	}

	public int getLastPeriodCarOutCount() {
		return lastPeriodCarOutCount.getLastPeriodCarCount();
	}
	
	public int getLastPeriodCarInCount() {
		return lastPeriodCarInCount.getLastPeriodCarCount();
	}

	public float getStdDevDuration() {
		if (driveCount > 1)
			return (float) Math.sqrt((s - totalDriveDuration / driveCount * totalDriveDuration) / (driveCount - 1));
		else
			return 0.0f;
	}

}
