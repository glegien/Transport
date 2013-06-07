package pl.edu.agh.cs.kraksim.ministat;

public class LastPeriodAvgDuration {
	private int tempDriveCount;
	private float tempDriveDuration;
	
	public void update(float periodDriveDuration) {
		tempDriveCount++;
		tempDriveDuration += periodDriveDuration;
	}
	
	public float getLastPeriodAvgDuration() {
		float result = tempDriveCount > 0 ? tempDriveDuration / tempDriveCount
				: 0.0f;
		tempDriveDuration = 0.0f;
		tempDriveCount = 0;
		return result;
	}
}
