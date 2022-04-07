package chc.problem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;


import ca.ualberta.cs.hdbscanstar.Constraint;
import ca.ualberta.cs.hdbscanstar.Constraint.CONSTRAINT_TYPE;
import ca.ualberta.cs.hdbscanstar.HDBSCANStarRunner;
//import clustering.evaluationsilhouette.SilhouetteCalculator;
//import clustering.evaluationsilhouette.Silhouettes;
import groupingModels.Group;
import groupingModels.Grouping;
import groupingModels.TheResult;

public class CHCSimilaritiesFinder {
	private final static double matchScore = 1;
	private final static double unMatchScore = -1;
	private final static double numberOfPreferences = 10;
	private final static double perfectMatchScore = (matchScore * 
			(CHCInput.ageWeight + CHCInput.genderWeight + CHCInput.familyWeight + CHCInput.nationWeight + 
					CHCInput.religionWeight + CHCInput.ethnicWeight + CHCInput.locationWeight + CHCInput.accessWeight + 
					CHCInput.rentPeriodWeight + CHCInput.shareWithWeight)) / 
			numberOfPreferences;
	
	public static void run(String[] args) {
		long startTime = System.nanoTime();
		System.out.println("Started!");

		String inputPath = args[0];
		String outputPath = args[1];
		//String inputPath = "/home/lexi/Desktop/Projects/EclipseWorkspace/Algos In Majan/HDBSCAN_CHC_for_MAJAN/Input-Output/input.txt";
		//String outputPath = "/home/lexi/Desktop/Projects/EclipseWorkspace/Algos In Majan/HDBSCAN_CHC_for_MAJAN/Input-Output/output.txt";
		
		CHCInput chcInput=new CHCInput();
		List<Agent> agents=null;
		ArrayList<Constraint> constraints = null;

		Double[][] similarityScores = null;
		Double[][] distanceScores = null; 
		try {
			//chcInput = chcInput.readInputFromFile(inputPath, chcInput);
			chcInput = chcInput.readChcInput(inputPath, chcInput);
			agents=chcInput.getAgents();
			constraints = chcInput.getConstraints();

			
			similarityScores = new Double[agents.size()][agents.size()];
			distanceScores = new Double[agents.size()][agents.size()];
			//System.out.println("-----=====perfccscore::"+ perfectMatchScore);
			CHCSimilaritiesFinder chcSimilaritiesFinder=new CHCSimilaritiesFinder();
			int k = 0;
			for (int i = 0; i < agents.size(); i++) {
				for (int j = i; j < agents.size(); j++) {
					if(i==j) {
						
						similarityScores[agents.get(i).getId()-1][agents.get(j).getId()-1] = perfectMatchScore;
						distanceScores[agents.get(i).getId()-1][agents.get(j).getId()-1] = 0.0;
					}else {
						double similarityScore1 = chcSimilaritiesFinder.computeDistanceScore(agents.get(i), agents.get(j));
						double similarityScore2 = chcSimilaritiesFinder.computeDistanceScore(agents.get(j), agents.get(i)); 
						double reciprocalScore = chcSimilaritiesFinder.computeReciprocalScore(
								similarityScore1, 
								similarityScore2);
						//System.out.println((agents.get(i).getId())+" vs "+(agents.get(j).getId())+" sim score: " +  similarityScore1);
						//System.out.println((agents.get(j).getId())+" vs "+(agents.get(i).getId())+" sim score: " +  similarityScore2);

						//System.out.println((agents.get(i).getId())+" vs "+(agents.get(j).getId())+": " + distanceScore1);
						//System.out.println((agents.get(j).getId())+" vs "+(agents.get(i).getId())+": " + distanceScore2);
						//System.out.println((agents.get(i).getId())+" vs "+(agents.get(j).getId())+" r score: " +  reciprocalScore);
						similarityScores[agents.get(i).getId()-1][agents.get(j).getId()-1]=reciprocalScore;
						similarityScores[agents.get(j).getId()-1][agents.get(i).getId()-1]=reciprocalScore;
						
						double distance = Utils.round(4, (perfectMatchScore - reciprocalScore));
						distanceScores[agents.get(i).getId()-1][agents.get(j).getId()-1]=distance;
						distanceScores[agents.get(j).getId()-1][agents.get(i).getId()-1]=distance;
						//System.out.println((agents.get(i).getId())+" vs "+(agents.get(j).getId())+" distance: " +  distance);

					}
				}	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Find rank list solution
		//computeRankLists(similarityScores);
		// Find group solution
		HDBSCANStarRunner hdbscanStarRunner = new HDBSCANStarRunner();
		distanceScores = applyConstraints(distanceScores, constraints);
		/*System.out.println("-----");
		System.out.println(perfectMatchScore);*/

		/*for (int i = 0; i < distanceScores.length; i++) {
			for (int j = 0; j < distanceScores.length; j++) {
				System.out.println(i+"-"+j+":"+distanceScores[i][j]);
			}
			System.out.println("\n");
		}*/
		
		int[] clustering = hdbscanStarRunner.execute(distanceScores, null, 2, 2);
		printClusters(clustering);
		TheResult theResult = buildGroupingResult(clustering);
		theResult.setNumberOfAgents(agents.size()+"");
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		theResult.setRuntime(((double)totalTime/1000000000)+"");
		
		try {
			writeOutput(theResult, outputPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		/*double[][] dists = new double[distanceScores.length][distanceScores.length];
		for (int i = 0; i < distanceScores.length; i++) {
			dists[i]=ArrayUtils.toPrimitive(distanceScores[i]);
		}
		Silhouettes sl = SilhouetteCalculator.calculate(dists, clustering);
		for (double ds : clustering) {
			//System.out.println(ds);
		}
		//System.out.println("11==="+dists.length);
		//System.out.println("22=="+clustering.length);
		//System.out.println("====="+sl.silhouetteValues.size());
		
		double avrSv = 0;
		int u = 1;
		for (double sv : sl.silhouetteValues) {
			//System.out.print(u+":");
			//System.out.println(sv);
			avrSv+=sv;
			u++;
		}
		
		//System.out.println("aver sv:" + (avrSv/sl.silhouetteValues.size()));
	//	printWeights();

		//printDistanceScores(distanceScores);

		/*try {
			Utils.writeRandomCHCInputToFile(100);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		 endTime   = System.nanoTime();
		 totalTime = endTime - startTime;
		System.out.println("Completed!");
		System.out.println("Total Time:"+totalTime);
		System.out.println("Total Time in seconds:"+(double)totalTime/1000000000);
	}
	
	private static void printWeights() {
		System.out.println("CHCInput.ageWeight :"+ CHCInput.ageWeight );
		System.out.println("CHCInput.genderWeight:"+CHCInput.genderWeight);
		System.out.println("CHCInput.familyWeight:"+CHCInput.familyWeight);
		System.out.println("CHCInput.nationWeight:"+CHCInput.nationWeight);
		System.out.println("CHCInput.religionWeight:"+CHCInput.religionWeight);
		System.out.println("CHCInput.ethnicWeight:"+CHCInput.ethnicWeight);
		System.out.println("CHCInput.locationWeight:"+CHCInput.locationWeight);
		System.out.println("CHCInput.accessWeight:"+CHCInput.accessWeight);
		System.out.println("CHCInput.rentPeriodWeight:"+CHCInput.rentPeriodWeight);
		System.out.println("CHCInput.shareWithWeight:"+CHCInput.shareWithWeight);		
	}
	
	private static void writeOutput(TheResult theResult, String path) throws IOException {
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
	
	private static Double[][] applyConstraints(Double[][] distances, ArrayList<Constraint> constraints) {
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
	
	private static void printClusters(int[] clustering) {
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
	
	private static TheResult buildGroupingResult(int[] clustering) {
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
		TheResult theResult=new TheResult();
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
	
	private static void computeRankLists(Double [][] distanceScores) {

		int[][] rankList = new int[distanceScores.length][distanceScores.length];
		
		for (int k = 0; k < rankList.length; k++) {
			rankList[k]=Utils.reverse(Utils.getSortedIndicesFromSmallerToHigher(distanceScores[k]));
		}
		
		System.out.println("*****************************************************");
		System.out.println("Rank List of Most Similar Agents");
		for (int k = 0; k < rankList.length; k++) {
			System.out.print("TCN"+(k+1)+": [");
			for (int k2 = 0; k2 < ((rankList[k].length > 10) ? 10 : rankList[k].length) ; k2++) {
				if(k==rankList[k][k2]){
					continue;
				}
				System.out.print("tcn"+(rankList[k][k2]+1)+": "+
				Utils.decimalFormat(Utils.ThreeDigitAfterDecimalFormat, distanceScores[k][rankList[k][k2]])+", ");
			}
			System.out.println("]");
		}
		System.out.println("*****************************************************");

	}
	private static void printDistanceScores(Double[][] distanceScores) {
		for (int i = 0; i < distanceScores.length; i++) {
			for (int j = 0; j < distanceScores.length; j++) {
				System.out.println("agent"+(i+1)+" vs agent"+(j+1)+" score:"+distanceScores[i][j]);
			}
		}
	}
	private double computeReciprocalScore(double similarityScore1, double similarityScore2) {
		double reciprocalScore = 0;
		
		if(similarityScore1 != 0 && similarityScore2 != 0) {
			reciprocalScore = 2/((1/similarityScore1)+(1/similarityScore2));
		}
		
		return reciprocalScore;
	}
	
	private double computeDistanceScore(Agent subjectAgent, Agent objectAgent) {
		// match each preference attribute of subjectAgent with the corresponding attribute of objectAgent
		
		double similarityScore = 0;
		//System.out.println("------------Agent"+subjectAgent.getId()+" vs Agent"+objectAgent.getId()+"---------------");
		double temp = computeAgePrefSimilarity(subjectAgent, objectAgent)*CHCInput.ageWeight;
		//System.out.println("Age:"+temp);
		similarityScore+= temp;
		temp=computeGenderPrefSimilarity(subjectAgent, objectAgent)*CHCInput.genderWeight;
		//System.out.println("gender:"+temp);
		similarityScore+= temp;
		temp=computeFamilyPrefSimilarity(subjectAgent, objectAgent)*CHCInput.familyWeight;
		//System.out.println("family:"+temp);
		similarityScore+= temp;
		temp = computeNationPrefSimilarity(subjectAgent, objectAgent)*CHCInput.nationWeight;
		//System.out.println("Nation:"+temp);
		similarityScore+= temp;
		temp=computeReligionPrefSimilarity(subjectAgent, objectAgent)*CHCInput.religionWeight;
		//System.out.println("Religion:"+temp);
		similarityScore+= temp;
		temp = computeEthnicPrefSimilarity(subjectAgent, objectAgent)*CHCInput.ethnicWeight;
		//System.out.println("Ethnic:"+temp);
		similarityScore+= temp;
		temp = computeLocationPrefSimilarity(subjectAgent, objectAgent)*CHCInput.locationWeight;
		//System.out.println("Location:"+temp);
		similarityScore+= temp;
		temp = computeAccessPrefSimilarity(subjectAgent, objectAgent)*CHCInput.accessWeight;
		//System.out.println("Access:"+temp);
		similarityScore+= temp;
		temp=computeRentPeriodPrefSimilarity(subjectAgent, objectAgent)*CHCInput.rentPeriodWeight;
		//System.out.println("Rent:"+temp);
		similarityScore+= temp;
		temp=computeShareWithPrefSimilarity(subjectAgent, objectAgent)*CHCInput.shareWithWeight;
		//System.out.println("Share:"+temp);
		similarityScore+= temp;
		
		// there are 10 attributes, so divide it with 10
		similarityScore = similarityScore/numberOfPreferences;
		
		
		return similarityScore;
	}
	
	
	private double computeAgePrefSimilarity(Agent subjectAgent, Agent objectAgent) {
		if(subjectAgent.isAgePrefDontMind()) {
			return matchScore;
		}
		List<Range> ageRanges = subjectAgent.getAgePrefList();
		for (Range range : ageRanges) {
			if(objectAgent.getAge()>=range.getLowerBound() && objectAgent.getAge()<=range.getHigherBound()) {
				return matchScore;
			}
		}
		
		return unMatchScore;
	}

	private double computeGenderPrefSimilarity(Agent subjectAgent, Agent objectAgent) {
		
		if(subjectAgent.isGenderPrefDontMind()) {
			return matchScore;
		}
		
		if(subjectAgent.getGenderPref().equals(objectAgent.getGender())) {
			return matchScore;
		}
		return unMatchScore;
		
	}
	
	private double computeFamilyPrefSimilarity(Agent subjectAgent, Agent objectAgent) {
		if(subjectAgent.isFamilyPrefDontMind()) {
			return matchScore;
		}
		
		List<String> familyPrefs = subjectAgent.getFamilyPrefList();
		for (String familyPref : familyPrefs) {
			//System.out.println("subFamil:"+familyPref);
			//System.out.println("objFam:"+objectAgent.getFamily());
			if(objectAgent.getFamily().equals(familyPref)) {
				return matchScore;
			}
		}
		return unMatchScore;		
	}
	
	private double computeNationPrefSimilarity(Agent subjectAgent, Agent objectAgent) {
		if(subjectAgent.isNationPrefDontMind()) {
			return matchScore;
		}
		
		if(subjectAgent.getNationPref().equals(CHCInput.same) && 
				subjectAgent.getNationality().equals(objectAgent.getNationality())) {
			return matchScore;
		}
		if(subjectAgent.getNationPref().equals(CHCInput.mixed) && 
				!subjectAgent.getNationality().equals(objectAgent.getNationality())) {
			return matchScore;
		}
		
		return unMatchScore;		
	}
	
	private double computeReligionPrefSimilarity(Agent subjectAgent, Agent objectAgent) {
		if(subjectAgent.isReligionPrefDontMind()) {
			return matchScore;
		}
		
		//System.out.println("-----relll--"+subjectAgent.getReligionPref());
		//System.out.println("----relll2222--" + subjectAgent.getReligion()+"-----"+objectAgent.getReligion());
		if(subjectAgent.getReligionPref().equals(CHCInput.same) && 
				subjectAgent.getReligion().equals(objectAgent.getReligion())) {
			return matchScore;
		}
		if(subjectAgent.getReligionPref().equals(CHCInput.mixed) && 
				!subjectAgent.getReligion().equals(objectAgent.getReligion())) {
			return matchScore;
		}
		
		return unMatchScore;		
	}
	
	private double computeEthnicPrefSimilarity(Agent subjectAgent, Agent objectAgent) {
		if(subjectAgent.isEthnicPrefDontMind()) {
			return matchScore;
			}
		
		if(subjectAgent.getEthnicPref().equals(CHCInput.same) && 
				subjectAgent.getEthnicity().equals(objectAgent.getEthnicity())) {
			return matchScore;
			}
		if(subjectAgent.getEthnicPref().equals(CHCInput.mixed) && 
				!subjectAgent.getEthnicity().equals(objectAgent.getEthnicity())) {
			return matchScore;
			}
		
		return unMatchScore;
	}
	
	private double computeLocationPrefSimilarity(Agent subjectAgent, Agent objectAgent) {
		if(subjectAgent.isLocationPrefDontMind() || objectAgent.isLocationPrefDontMind()) {
			return matchScore;
			}
		
		List<String> subjectLocPrefs = subjectAgent.getLocationPrefList();
		List<String> objectLocPrefs = objectAgent.getLocationPrefList();
		
		for (String subjectLocPref : subjectLocPrefs) {
			for (String objectLocPref : objectLocPrefs) {
				if(subjectLocPref.equals(objectLocPref)) {
					return matchScore;
					}
			}
		}
		
		return unMatchScore;	
	}
	
	private double computeAccessPrefSimilarity(Agent subjectAgent, Agent objectAgent) {
		if(subjectAgent.isAccessibilityPrefDontMind() || objectAgent.isAccessibilityPrefDontMind()) {
			return matchScore;
			}
		
		if(subjectAgent.getAccessibilityPref().equals(objectAgent.getAccessibilityPref())) {
			return matchScore;
			}

		return unMatchScore;
	}
	
	private double computeRentPeriodPrefSimilarity(Agent subjectAgent, Agent objectAgent) {
		if(subjectAgent.isRentalPeriodPrefDontMind() || objectAgent.isRentalPeriodPrefDontMind()) {
			return matchScore;
			}
		
		Range subjectRentPeriodRange = subjectAgent.getRentalPeriodPref();
		Range objectRentPeriodRange = objectAgent.getRentalPeriodPref();

		if(subjectRentPeriodRange.getLowerBound()>objectRentPeriodRange.getHigherBound() ||
				objectRentPeriodRange.getLowerBound() > subjectRentPeriodRange.getHigherBound()) {
			
			// they dont overlap, so return unMatch
			return unMatchScore;
		}
		
		// find the amount of overlap and total period
		long overlap = Math.min(subjectRentPeriodRange.getHigherBound(), objectRentPeriodRange.getHigherBound()) -
				Math.max(subjectRentPeriodRange.getLowerBound(), objectRentPeriodRange.getLowerBound()) + 1;
		long total = Math.max(subjectRentPeriodRange.getHigherBound(), objectRentPeriodRange.getHigherBound()) - 
				Math.min(subjectRentPeriodRange.getLowerBound(), objectRentPeriodRange.getLowerBound()) + 1;
		float rentSimilarity = (float) overlap/total;
		//System.out.println("----------RENT---------"+rentSimilarity);
		
		return rentSimilarity;		
	}
	
	private double computeShareWithPrefSimilarity(Agent subjectAgent, Agent objectAgent) {
		if(subjectAgent.isShareWithPrefDontMind() || objectAgent.isShareWithPrefDontMind()) {
			return matchScore;
			}
		
		Range subjectShareWithRange = subjectAgent.getShareWithPref();
		Range objectShareWithRange = objectAgent.getShareWithPref();
		
		if(subjectShareWithRange.getLowerBound()>objectShareWithRange.getHigherBound() ||
				objectShareWithRange.getLowerBound() > subjectShareWithRange.getHigherBound()) {
			
			// they dont overlap, so return unmatch
			return unMatchScore;
		}
		
		// find the amount of overlap and total period
		long overlap = Math.min(subjectShareWithRange.getHigherBound(), objectShareWithRange.getHigherBound()) -
				Math.max(subjectShareWithRange.getLowerBound(), objectShareWithRange.getLowerBound()) + 1;
		
		long total = Math.max(subjectShareWithRange.getHigherBound(), objectShareWithRange.getHigherBound()) - 
				Math.min(subjectShareWithRange.getLowerBound(), objectShareWithRange.getLowerBound()) + 1;

		float shareSimilarity = (float) overlap/total;
		//System.out.println("----------SHARE---------"+shareSimilarity);
		
		return shareSimilarity;		
	}
}
