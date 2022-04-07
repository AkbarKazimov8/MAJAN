package json;

import java.util.ArrayList;
import java.util.List;

public class Grouping {
	String rank;
	String value;
	List<Group> groups;
	
	public Grouping() {
		groups=new ArrayList<Group>();
	}
	
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<Group> getGroups() {
		return groups;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	public void addGroup(Group group) {
		groups.add(group);
	}
	
}
