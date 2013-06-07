package pl.edu.agh.cs.kraksim.weka.data;

import java.io.Serializable;

public class LinkInfo implements Comparable<LinkInfo>, Info, Serializable {
	private static final long serialVersionUID = -524319807462597044L;
	public int linkNumber;
	public String linkId;
	public int numberOfHops;
	

	public LinkInfo(int linkNumber, String linkId, int numberOfHops) {
		super();
		this.linkNumber = linkNumber;
		this.linkId = linkId;
		this.numberOfHops = numberOfHops;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + linkNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LinkInfo other = (LinkInfo) obj;
		if (linkNumber != other.linkNumber)
			return false;
		return true;
	}


	@Override
	public int compareTo(LinkInfo o) {
		if (this == o) return 0;
		if (this.linkNumber < o.linkNumber) return -1;
		else if (this.linkNumber == o.linkNumber) return 0;
		else return 1;
	}


	@Override
	public String getId() {
		return linkId;
	}

}
