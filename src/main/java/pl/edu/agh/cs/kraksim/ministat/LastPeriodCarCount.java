package pl.edu.agh.cs.kraksim.ministat;

public class LastPeriodCarCount {
	private int tempDriveCount;
	
	public void update() {
		tempDriveCount++;
	}
	
	public int getLastPeriodCarCount() {
		int result = tempDriveCount;
		tempDriveCount = 0;
		return result;
	}
}
