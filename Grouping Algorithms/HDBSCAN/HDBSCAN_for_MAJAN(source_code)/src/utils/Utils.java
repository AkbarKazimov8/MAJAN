package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;

import ca.ualberta.cs.hdbscanstar.Constraint;
import ca.ualberta.cs.hdbscanstar.Constraint.CONSTRAINT_TYPE;
import groupingModels.Group;
import groupingModels.Grouping;
import groupingModels.GroupingResult;
import main.HdbscanInput;

public class Utils {
	public static void printClusters(int[] clustering) {
		Map<Integer, ArrayList<Integer> > clusteringMap = new HashMap<>();
		
		for (int i = 0; i < clustering.length; i++) {
			if(clusteringMap.containsKey(clustering[i])) {
				ArrayList<Integer> members = clusteringMap.get(clustering[i]);				
				members.add(i);
				clusteringMap.replace(clustering[i], members);
			}else {
				ArrayList<Integer> members = new ArrayList<Integer>();
				members.add(i);
				clusteringMap.put(clustering[i], members);
			}
		}
		
		System.out.println("--------------Clustering Result--------------");
		
		int clusterId = 1;
		for (int i : clusteringMap.keySet()) {
			if(i==0) {
				System.out.print("Singleton: size - "+clusteringMap.get(i).size()+", [");
			}else {
				System.out.print("Group"+clusterId+": size - "+clusteringMap.get(i).size()+", [");
				clusterId++;
			}
			
			for (int j : clusteringMap.get(i)) {
				System.out.print("tcn"+(j+1)+", ");
			}
			System.out.println("]");
		}
		
		System.out.println("--------------Clustering Result END--------------");
		
		
	}
	public static Double[][] applyConstraints(Double[][] distances, ArrayList<Constraint> constraints) {
	//	System.out.println("-----cnssts---" + constraints.size());
		for (Constraint constraint : constraints) {
			if (constraint.getType().equals(CONSTRAINT_TYPE.MUST_LINK)) {
				distances[constraint.getPointA()][constraint.getPointB()] = 0.0;
				distances[constraint.getPointB()][constraint.getPointA()] = 0.0;
			//	System.out.println("---ML---"+constraint.getPointA()+"--"+constraint.getPointB());
			}else if (constraint.getType().equals(CONSTRAINT_TYPE.CANNOT_LINK)){
				distances[constraint.getPointA()][constraint.getPointB()] = Double.POSITIVE_INFINITY;
				distances[constraint.getPointB()][constraint.getPointA()] = Double.POSITIVE_INFINITY;				
			//	System.out.println("---CL---"+constraint.getPointA()+"--"+constraint.getPointB());
			}
		}
		return distances;
	}
	public static void writeOutput(GroupingResult theResult, String path) throws IOException {
		FileWriter fileWriter=new FileWriter(new File(path));
		PrintWriter printWriter=new PrintWriter(fileWriter);
		
		JSONObject jsonObject = new JSONObject(theResult);
		//System.out.println(jsonObject.toString(4));
		/*System.out.println("444-"+jsonObject.toString());
		JSONObject jsonObject2 = new JSONObject(input);
		System.out.println("555-"+jsonObject2.toString());*/

		printWriter.append(jsonObject.toString(4));
		/*Gson gson = new Gson();
		System.out.println("666-"+ gson.toJson(theResult));*/
		
		
		printWriter.close();
	}
	public static GroupingResult buildGroupingResult(int[] clustering) {
		Map<Integer, ArrayList<Integer> > clusteringMap = new HashMap<>();
		
		for (int i = 0; i < clustering.length; i++) {
			if(clusteringMap.containsKey(clustering[i])) {
				ArrayList<Integer> members = clusteringMap.get(clustering[i]);				
				members.add(i);
				clusteringMap.replace(clustering[i], members);
			}else {
				ArrayList<Integer> members = new ArrayList<Integer>();
				members.add(i);
				clusteringMap.put(clustering[i], members);
			}
		}
		GroupingResult theResult=new GroupingResult();
		Grouping grouping = new Grouping();
		
		//int clusterId = 1;
		for (int i : clusteringMap.keySet()) {
			/*if(i==0) {
				System.out.print("Singleton: size - "+clusteringMap.get(i).size()+", [");
			}else {
				System.out.print("Group"+clusterId+": size - "+clusteringMap.get(i).size()+", [");
				clusterId++;
			}*/
			Group group=new Group();
			for (int j : clusteringMap.get(i)) {
				group.addId((j+1)+"");
				//System.out.print("tcn"+(j+1)+", ");
			}
			//System.out.println("]");
			
			
			//clusterId++;
			grouping.addGroup(group);
		}
		theResult.addGrouping(grouping);
		
		return theResult;
	}
	
	public static ArrayList<Constraint> createMLConstraints(String inputLine){
		String[] mls = inputLine.trim().split("\\|");
		ArrayList<Constraint> constraints = new ArrayList<Constraint>();
		for (String ml: mls) {
			
			String[] pairs=ml.trim().replace("{", "").replace("}", "").split(",");
			Constraint constraint = new Constraint(Integer.valueOf(pairs[0].trim())-1, Integer.valueOf(pairs[1].trim())-1, CONSTRAINT_TYPE.MUST_LINK);
			constraints.add(constraint);
		}
		return constraints;
	}
	public static Double[][] readDistances(String inputLine) throws Exception{
		Double[][] distances = null;		
		
		String[] distancesArray = inputLine.trim().split("\\|");
		if(distancesArray.length>0) {
			int numOfObjs = distancesArray.length+1;
			distances = new Double[numOfObjs][numOfObjs];
			distances[numOfObjs-1][numOfObjs-1] = 0.0;
		}else {
			throw new Exception("Either no Distance is specified or they don't comply with the input structure rules!");
		}
		for (int i = 0; i < distancesArray.length; i++) {
			String[] distanceScores = distancesArray[i].trim().replace("{", "").replace("}", "").split(",");
			// distance from object to itself is zero. thus set zero below
			distances[i][i] = 0.0;
			for (int j = 0; j < distanceScores.length; j++) {
				if(NumberUtils.isParsable(distanceScores[j].trim())) {
					distances[i][i+j+1] = Double.valueOf(distanceScores[j]);
					distances[i+j+1][i] = Double.valueOf(distanceScores[j]);					
				}else {
					throw new Exception(distanceScores[j] + ": is not a double!");
				}
			}
		}
		return distances;
	}
	public static ArrayList<Constraint> createCLConstraints(String inputLine){
		String[] cls = inputLine.trim().split("\\|");
		ArrayList<Constraint> constraints = new ArrayList<Constraint>();

		for (String cl: cls) {

			String[] pairs=cl.trim().replace("{", "").replace("}", "").split(",");
			Constraint constraint = new Constraint(Integer.valueOf(pairs[0].trim())-1, Integer.valueOf(pairs[1].trim())-1, CONSTRAINT_TYPE.CANNOT_LINK);
			constraints.add(constraint);
			}
		return constraints;
	}
	
	public static HdbscanInput readHdbscanInputFromFile(String inputPath) throws Exception {
		HdbscanInput hdbscanInput = new HdbscanInput();
		ArrayList<Constraint> constraints = new ArrayList<Constraint>();
		
		BufferedReader reader = new BufferedReader(new FileReader(inputPath));
		Iterator<String> linesIter = reader.lines().iterator();
		while(linesIter.hasNext()) {
			String line=linesIter.next();

			if(line==null || line.trim().equals("") || line.trim().isEmpty() || line.trim().startsWith("//")) {
				continue;
			}else if (line.toLowerCase().startsWith("number of objects")) {
				line = line.substring(line.indexOf("{")).replace("{", "").replace("}", "").trim();
				
				if(StringUtils.isNumeric(line)) {
					hdbscanInput.setNumOfObjects(Integer.valueOf(line));
				}else {
					throw new Exception("Value given for Number of Objects is not an Integer!");
				}
			}else if (line.toLowerCase().startsWith("outliers")) {
				line = line.substring(line.indexOf("{")).replace("{", "").replace("}", "").trim();
				if(line.toLowerCase().equals("true")) {
					hdbscanInput.setOutliers(true);
				}else if(line.toLowerCase().equals("false")){
					hdbscanInput.setOutliers(false);
				}else {
					throw new Exception("Unknown value: '"+line.toLowerCase() + "' given for Outliers!");
				}
			}else if (line.toLowerCase().startsWith("minimum points")) {
				line = line.substring(line.indexOf("{")).replace("{", "").replace("}", "").trim();
				
				if(StringUtils.isNumeric(line)) {
					hdbscanInput.setMinPts(Integer.valueOf(line));
				}else {
					throw new Exception("Value given for Minimum Points is not an Integer!");
				}
			}else if (line.toLowerCase().startsWith("Minimum Cluster Size")) {
				line = line.substring(line.indexOf("{")).replace("{", "").replace("}", "").trim();
				
				if(StringUtils.isNumeric(line)) {
					hdbscanInput.setMinClSize(Integer.valueOf(line));
				}else {
					throw new Exception("Value given for Minimum Cluster Size is not an Integer!");
				}
			}else if (line.toLowerCase().startsWith("ml")) {
				constraints.addAll(Utils.createMLConstraints(line.substring(line.indexOf("{"))));
			}else if(line.toLowerCase().startsWith("cl")) {
				constraints.addAll(Utils.createCLConstraints(line.substring(line.indexOf("{"))));				
			}else if(line.toLowerCase().startsWith("distances")) {
				hdbscanInput.setDistanceScores(Utils.readDistances(line.substring(line.indexOf("{"))));
			}
		}

		if(hdbscanInput.getNumOfObjs() > hdbscanInput.getDistanceScores().length) {
			throw new Exception("Given Number of Objects value is greater than the amount of objects in Distances! They should be equal!");
		}else if(hdbscanInput.getNumOfObjs() < hdbscanInput.getDistanceScores().length){
			throw new Exception("Given Number of Objects value is less than the amount of objects in Distances! They should be equal!");
		}
		

		hdbscanInput.setConstraints(constraints);
		return hdbscanInput;
	}
}
