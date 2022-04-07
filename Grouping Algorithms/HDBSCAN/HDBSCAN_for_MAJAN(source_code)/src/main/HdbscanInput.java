package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import ca.ualberta.cs.hdbscanstar.Constraint;
import utils.Utils;

public class HdbscanInput {
	private Double[][] distanceScores = null; 
	private ArrayList<Constraint> constraints = null;
	private int numOfObjs = 0, minPts = 2, minClSize = 2;
	private boolean outliers = false;
	
	public boolean isOutliers() {
		return outliers;
	}
	public void setOutliers(boolean outliers) {
		this.outliers = outliers;
	}
	public void setNumOfObjs(int numOfObjs) {
		this.numOfObjs = numOfObjs;
	}
	public Double[][] getDistanceScores() {
		return distanceScores;
	}
	public void setDistanceScores(Double[][] distanceScores) {
		this.distanceScores = distanceScores;
	}
	public ArrayList<Constraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(ArrayList<Constraint> constraints) {
		this.constraints = constraints;
	}
	public int getNumOfObjs() {
		return numOfObjs;
	}
	public void setNumOfObjects(int numOfObjs) {
		this.numOfObjs = numOfObjs;
	}
	public int getMinPts() {
		return minPts;
	}
	public void setMinPts(int minPts) {
		this.minPts = minPts;
	}
	public int getMinClSize() {
		return minClSize;
	}
	public void setMinClSize(int minClSize) {
		this.minClSize = minClSize;
	}	

}
