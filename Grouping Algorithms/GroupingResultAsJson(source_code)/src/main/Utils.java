package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import groupingModels.Group;
import groupingModels.Grouping;
import groupingModels.GroupingResult;

public class Utils {
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
}
