package coalitionGeneration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import coalitionGeneration.models.CSGPInput;
import coalitionGeneration.models.Coalition;


public class Main {
	public final static double infeasibleCValue = Double.NEGATIVE_INFINITY;

	public static void main(String[] args) {
		String inputPath="/home/lexi/Desktop/MAJAN/Grouping Algorithms/CoalitionGenerator(source_code)/Input-Output/input.txt";
	 	String outputPath="/home/lexi/Desktop/MAJAN/Grouping Algorithms/CoalitionGenerator(source_code)/Input-Output/output.txt";
		
		//String inputPath = args[0];
		//String outputPath = args[1];
		
		CoalitionGeneration coalitionGeneration=new CoalitionGeneration();
//		CSGPInput csgpInput=coalitionGeneration.readInputFromFile(inputPath);
		CSGPInput csgpInput=coalitionGeneration.readLccInput(inputPath);
		
		List<Coalition> allCoalitions = coalitionGeneration.generateALLCoalitions(csgpInput, infeasibleCValue);
		Output output=new Output();
		try {
			output.deleteContentOfFile(outputPath);
			output.writeToFile(outputPath, csgpInput.getAgents().size(), allCoalitions);
		} catch (IOException e) {
			e.printStackTrace();
			}
	}
}
