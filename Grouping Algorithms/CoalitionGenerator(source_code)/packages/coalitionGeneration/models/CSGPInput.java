package coalitionGeneration.models;

import java.util.ArrayList;
import java.util.List;

public class CSGPInput {
	private List<Agent> agents;
//	private List<MustLink> MLs;
	//private List<CannotLink> CLs;
	private List<int[]> cls;
	private List<int[]> mls;
	private int minCoalitionSize, maxCoalitionSize;
	private double sdWeight=0.7;
	
	
	public CSGPInput(List<Agent> agents, List<int[]> mLs, List<int[]> cLs, int minSize, int maxSize) {
		super();
		this.agents = agents;
		mls= mLs;
		cls = cLs;
		this.minCoalitionSize=minSize;
		this.maxCoalitionSize=maxSize;
	}

	public CSGPInput(List<Agent> agents, List<int[]> mLs, List<int[]> cLs, int minSize, int maxSize, double sdWeight) {
		super();
		this.agents = agents;
		mls= mLs;
		cls = cLs;
		this.minCoalitionSize=minSize;
		this.maxCoalitionSize=maxSize;
		this.sdWeight = sdWeight;
	}
	
	public List<Agent> getAgents() {
		return agents;
	}

	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}

	public List<int[]> getMLs() {
		return mls;
	}

	public void setMLs(List<int[]> mLs) {
		mls= mLs;
	}

	public List<int[]> getCLs() {
		return cls;
	}

	public void setCLs(List<int[]> cLs) {
		cls = cLs;
	}

	public int getMinCoalitionSize() {
		return minCoalitionSize;
	}

	public void setMinCoalitionSize(int minCoalitionSize) {
		this.minCoalitionSize = minCoalitionSize;
	}

	public int getMaxCoalitionSize() {
		return maxCoalitionSize;
	}

	public void setMaxCoalitionSize(int maxCoalitionSize) {
		this.maxCoalitionSize = maxCoalitionSize;
	}

	public double getSdWeight() {
		return sdWeight;
	}

	public void setSdWeight(double sdWeight) {
		this.sdWeight = sdWeight;
	}
}
