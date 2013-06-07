package pl.edu.agh.cs.kraksim.ministat;

import pl.edu.agh.cs.kraksim.core.Link;

public class LastPeriodAvgVelocity {
	private Link link;
	private int tempDriveCount;
	private float tempDriveDuration;
	
	public LastPeriodAvgVelocity(Link link) {
		super();
		this.link = link;
	}

	public void update(float periodDriveDuration) {
		tempDriveCount++;
		tempDriveDuration += periodDriveDuration;
	}
	
	public float getLastPeriodAvgVelocity() {
		float avgDuration = tempDriveCount > 0 ? tempDriveDuration / tempDriveCount
				: 0.0f;
		tempDriveDuration = 0.0f;
		tempDriveCount = 0;
		
		if (avgDuration == 0.0) {
	        return -1.0f;
		}
		return link.getLength() / avgDuration;
	}
}
