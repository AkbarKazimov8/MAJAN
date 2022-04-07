package groupingModels;

import java.util.ArrayList;
import java.util.List;

public class Group {
	List<String> ids;
	
	public Group() {
		ids=new ArrayList<String>();
	}
	
	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	
	public void addId(String id) {
		this.ids.add(id);
	}
}
