package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import ca.ualberta.cs.hdbscanstar.Constraint;
import ca.ualberta.cs.hdbscanstar.HDBSCANStarRunner;
import groupingModels.GroupingResult;
import utils.Utils;

public class Main {

	public static void main(String[] args) {
		long startTime = System.nanoTime();

		String inputPath = "/home/lexi/Desktop/Projects/EclipseWorkspace/Algos In Majan/HDBSCAN_for_MAJAN/Input-Output/input.txt";
		String outPath = "/home/lexi/Desktop/Projects/EclipseWorkspace/Algos In Majan/HDBSCAN_for_MAJAN/Input-Output/output.txt";
		//String inputPath = args[0];
		//String outPath = args[1];
		HdbscanInput hdbscanInput = null;
		try {
			hdbscanInput = Utils.readHdbscanInputFromFile(inputPath);
			hdbscanInput.setDistanceScores(
					Utils.applyConstraints(hdbscanInput.getDistanceScores(), 
							hdbscanInput.getConstraints()));
			/*System.out.println("11--"+hdbscanInput.getNumOfObjs());
			System.out.println("22--"+hdbscanInput.getMinPts());
			System.out.println("33--"+hdbscanInput.getMinClSize());
			for(Constraint cns: hdbscanInput.getConstraints()) {
				System.out.println("44--" + cns.getPointA() + ">" + cns.getPointB());
			}
			for (int i = 0; i < hdbscanInput.getDistanceScores().length; i++) {
				for (int j = 0; j < hdbscanInput.getDistanceScores().length; j++) {
					System.out.println("55--" + (i+1) + (j+1) + "-> " +hdbscanInput.getDistanceScores()[i][j]);
				}
			}*/

		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return;
		}
		
		HDBSCANStarRunner hdbscanStarRunner = new HDBSCANStarRunner();
		int[] clustering = hdbscanStarRunner.execute(hdbscanInput.getDistanceScores(),
				hdbscanInput.getConstraints(), 
				hdbscanInput.getMinPts(), hdbscanInput.getMinClSize(), hdbscanInput.isOutliers());
		
		Utils.printClusters(clustering);
		GroupingResult groupingResult = Utils.buildGroupingResult(clustering);
		groupingResult.setNumberOfAgents(hdbscanInput.getNumOfObjs()+"");
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		groupingResult.setRuntime(((double)totalTime/1000000000)+"");
		
		try {
			Utils.writeOutput(groupingResult, outPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
