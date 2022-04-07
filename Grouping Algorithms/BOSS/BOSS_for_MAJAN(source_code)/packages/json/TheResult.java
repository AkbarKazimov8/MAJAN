package json;

import java.util.ArrayList;
import java.util.List;

public class TheResult {
	String runtime;
	List<Grouping> groupings;
	String numberOfAgents;
	
	public TheResult() {
		groupings=new ArrayList<Grouping>();
	}
	
	public String getRuntime() {
		return runtime;
	}
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
	public List<Grouping> getGroupings() {
		return groupings;
	}
	public void setGroupings(List<Grouping> groupings) {
		this.groupings = groupings;
	}
	public void addGrouping(Grouping grouping) {
		groupings.add(grouping);
	}

	public String getNumberOfAgents() {
		return numberOfAgents;
	}

	public void setNumberOfAgents(String numberOfAgents) {
		this.numberOfAgents = numberOfAgents;
	}
	
	
}
