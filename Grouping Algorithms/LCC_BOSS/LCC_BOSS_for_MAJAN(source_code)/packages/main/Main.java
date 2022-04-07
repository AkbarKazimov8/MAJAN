package main;

import java.io.IOException;
import java.util.List;

import boss.Boss;
import coalitionGeneration.CoalitionGeneration;
import coalitionGeneration.Output;
import coalitionGeneration.models.CSGPInput;
import coalitionGeneration.models.Coalition;

public class Main {
	public final static int infeasibleCValue = -100000000;
	
	public static void main (String[] args) {
	
 	String inputPath="/home/lexi/Desktop/Projects/EclipseWorkspace/Algos In Majan/LCC_BOSS_for_MAJAN/Input-Output/input.txt";
 	String outputPath="/home/lexi/Desktop/Projects/EclipseWorkspace/Algos In Majan/LCC_BOSS_for_MAJAN/Input-Output/output.txt";
	
	//String inputPath = args[0];
	//String outputPath = args[1];
	
	CoalitionGeneration coalitionGeneration=new CoalitionGeneration();
//	CSGPInput csgpInput=coalitionGeneration.readInputFromFile(inputPath);
	CSGPInput csgpInput=coalitionGeneration.readLccInput(inputPath);
	
	List<Coalition> allCoalitions = coalitionGeneration.generateALLCoalitions(csgpInput, infeasibleCValue);

	Output output=new Output();
	try {
		output.deleteContentOfFile(inputPath);
		output.writeToFile(inputPath, csgpInput.getAgents().size(), allCoalitions);
	} catch (IOException e) {
		e.printStackTrace();
		}

	Boss boss = new Boss();
	boss.execute(inputPath, outputPath);

	}
}
