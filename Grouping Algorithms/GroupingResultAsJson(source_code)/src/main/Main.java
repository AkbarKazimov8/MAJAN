package main;

import java.io.IOException;

import groupingModels.GroupingResult;

public class Main {
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		String outPath = "/path/output.txt";

		int[] clustering = null;		
		Utils.printClusters(clustering);
		GroupingResult groupingResult = Utils.buildGroupingResult(clustering);
		groupingResult.setNumberOfAgents("5");
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
