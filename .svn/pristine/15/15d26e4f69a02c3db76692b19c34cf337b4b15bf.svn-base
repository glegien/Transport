package pl.edu.agh.cs.kraksim.weka.data;

import java.io.Serializable;

public class IntersectionInfo implements Comparable<IntersectionInfo>, Info, Serializable {
	private static final long serialVersionUID = -524319807462597044L;
	public String intersectionId;
	
	public IntersectionInfo(String intersectionId) {
		this.intersectionId = intersectionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((intersectionId == null) ? 0 : intersectionId.hashCode());
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
		IntersectionInfo other = (IntersectionInfo) obj;
		if (intersectionId == null) {
			if (other.intersectionId != null)
				return false;
		} else if (!intersectionId.equals(other.intersectionId))
			return false;
		return true;
	}

	@Override
	public int compareTo(IntersectionInfo o) {
		if (this == o) return 0;
		return intersectionId.compareTo(o.intersectionId);
	}

	@Override
	public String getId() {
		return intersectionId;
	}
}
