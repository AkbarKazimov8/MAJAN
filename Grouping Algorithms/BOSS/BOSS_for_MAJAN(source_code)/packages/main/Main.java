package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import general.Combinations;
import general.General;
import inputOutput.Input;
import inputOutput.SolverNames;
import json.Group;
import json.Grouping;
import json.TheResult;
import mainSolver.MainSolver;
import mainSolver.Result;

public class Main {
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		//System.out.println("Started!");
		//String inputPath = args[0];
		//String outputPath = args[1];
		
		//String inputPath="C:\\Users\\akka02\\Desktop\\Thesis (github)\\Software\\EclipseWorkspace\\Algos In Majan\\BOSS_for_baseline\\BOSS algorithm\\Input-Output\\input.txt";
		//String outputPath = "C:\\Users\\akka02\\Desktop\\Thesis (github)\\Software\\EclipseWorkspace\\Algos In Majan\\BOSS_for_baseline\\BOSS algorithm\\Input-Output\\output.txt";
		
		//String base = System.getProperty("user.dir");
		String inputPath = args[0];
		String outputPath = args[1];
		
		Main main=new Main();
		main.execute(inputPath, outputPath);

		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
//		System.out.println("Total Time:"+totalTime);
		System.out.println("Total Execution Time in seconds:"+(double)totalTime/1000000000);
	}

	public void execute (String inputPath, String outputPath){
		Input input = readInputFromFile(inputPath);
		input.outputFolder=outputPath;
		System.out.println("Running...");
		Result result=(new MainSolver()).solve(input);
		System.out.println("Completed!");
		System.out.println("Results:");
		
		printResultOnConsole(input, result);
		try {
			//writeResultToFile(input,result, outputPath);
			writeOutput(input, result, outputPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());		
}
		
	}
	
	private Input readInputFromFile(String path) {
		Input input=new Input();
		input.initInput();
		input.acceptableRatio = 100.0;
		input.readCoalitionValuesFromFile = false;
		input.solverName = SolverNames.ODPIP;
		input.printInterimResultsOfIPToFiles = false;
		input.printTimeTakenByIPForEachSubspace = false;
		input.orderIntegerPartitionsAscendingly = false;
		//input.allAgentsMustBeClustered=true;
		
	//	Map<Integer, Double> coalitions=null;
		//double[] coalitionValuesArray = new double[(int)Math.pow(2,20)];
//		Arrays.fill(coalitionValuesArray, Double.NEGATIVE_INFINITY);
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			Iterator<String> linesIter = reader.lines().iterator();
			
			while(linesIter.hasNext()) {
				String line=linesIter.next();

				if(line==null || line.trim().equals("") || line.trim().isEmpty() || line.trim().startsWith("//")) {
					continue;
	
				}else if (line.toLowerCase().startsWith(("number of agents"))) {
					int numberOfAgents=0;
					line=line.substring(line.indexOf(">")+1).trim();
					if(StringUtils.isNumeric(line)) {
						numberOfAgents=Integer.valueOf(line);
					}
					if( numberOfAgents > 27){
						System.err.println("The number of coalition structures cannot be handled by java for more than 25 agents!");
						return(null); 
			 		}
					input.numOfAgents=numberOfAgents;
				}/*else if(line.toLowerCase().startsWith("sd")) {
					double SDWeight=0;
					line=line.substring(line.indexOf(">")+1).trim();
					SDWeight=Double.valueOf(line);
					input.setTakeIntoAccountSD(true);
					input.setSDWeight(SDWeight);
				}*/else if (line.startsWith("Coalitions")) {
					readCoalitions(input, line.substring(line.indexOf(">")+1).trim(),input.numOfAgents);
				}
			}
			reader.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return input;
	}

	
	private Input readCoalitions(Input input, String coalitionsLine, int numOfAgents){
		
		double[] coalitionValuesArray = new double[(int)Math.pow(2,numOfAgents)];
		//double[] standardDeviations = new double[coalitionValuesArray.length];
		
		String[] coalitionsArray = coalitionsLine.trim().split("\\|");
		for (String cln: coalitionsArray) {
			String[] pairs=cln.trim().replace("{", "").replace("}", "").split(",");
			coalitionValuesArray[Integer.valueOf(pairs[0])]=Double.valueOf(pairs[1]);
			//standardDeviations[Integer.valueOf(pairs[0])]=Double.valueOf(pairs[2]);
		}		
		//input.setStandardDeviations(standardDeviations);
		input.coalitionValues=coalitionValuesArray;
		return input;
	}
	private void printResultOnConsole(Input input, Result result) {
		String resultOutput = "";
		if ((input.solverName == SolverNames.ODPIP) ||( input.solverName == SolverNames.ODPinParallelWithIP ) ){
			resultOutput+="----------------------------------------------------\n    "+SolverNames.toString(input.solverName)+" ("+input.numOfAgents+" agents)\n----------------------------------------------------\n";
			if( input.numOfRunningTimes == 1 ){
				//textArea.append("\nThe time for IP to scan the input (in milliseconds):\n"+result.ipTimeForScanningTheInput+"\n");			
				resultOutput+="\nThe total run time of "+SolverNames.toString(input.solverName)+" (in milliseconds):\n"+result.ipTime+"\n";
				
				resultOutput+="\nThe best coalition structure found by "+SolverNames.toString(input.solverName)+
						" is:\n"+General.convertArrayToString(result.get_ipBestCSFound())+"\n";
				resultOutput+="\nThe value of this coalition structure is:\n"+
						result.getTwoDigitAfterDot(result.get_ipValueOfBestCSFound())+"\n";
				//resultOutput+="\nThe SD of this coalition structure is:\n"+
					//	result.getTwoDigitAfterDot(input.getStandardDeviation(result.get_ipBestCSFound()))+"\n";
				//textArea.append("\nTotal number of CSs:\n"+result.getTotalNumOfCS()+"\n");
				resultOutput+="\nTop "+result.getTopCSs().size()+" CSs:\n";
				for(int i=0;i<result.getTopCSs().size();i++) {
					/*int csInBit = Combinations.convertCombinationFromByteToBitFormat(
							Combinations.convertSetOfCombinationsFromByteToBitFormat(result.getTopCSs().get(i)));
					int[] csInByte = Combinations.convertSetOfCombinationsFromByteToBitFormat(result.getTopCSs().get(i));
*/
					resultOutput+=(i+1)+". CS:"+General.convertArrayToString(result.getTopCSs().get(i))+
							", Value:"+result.getTwoDigitAfterDot(result.getTopCSValues().get(i))+
							"\n";
				/*	if(input.isTakeIntoAccountSD()) {
						resultOutput+=", SD:"+result.getTwoDigitAfterDot(input.getSDofCS(result.getTopTenCSs().get(i)));
					}*/
				}
				
			if(input.solverName == SolverNames.IP ){
				resultOutput+="\nThe number of expansions made by IP:\n"+result.ipNumOfExpansions+"\n";
				resultOutput+="\nBased on this, the percentage of search-space that was searched by "+
				SolverNames.toString(input.solverName)+
				" is:\n"+(double)(result.ipNumOfExpansions*100)/result.totalNumOfExpansions+"%\n";
				}
			}else{
				resultOutput+="\nThe average time for "+SolverNames.toString(input.solverName)+
						" to scan the input (in milliseconds):\n"+result.ipTimeForScanningTheInput+
						" +/- "+result.ipTimeForScanningTheInput_confidenceInterval+"\n";			
				resultOutput+="\nThe average run time of "+SolverNames.toString(input.solverName)+
						" (in milliseconds):\n"+result.ipTime+" +/- "+result.ipTime_confidenceInterval+"\n";
				if( input.solverName == SolverNames.IP ){
					resultOutput+="\nThe average number of expansions made by "+SolverNames.toString(input.solverName)+
							":\n"+result.ipNumOfExpansions+" +/- "+result.ipNumOfExpansions_confidenceInterval+"\n";
					resultOutput+="\nBased on this, the average percentage of search-space that was searched by "+
							SolverNames.toString(input.solverName)+" is:\n"+
							(double)(result.ipNumOfExpansions*100)/result.totalNumOfExpansions+"% +/- "+
							(double)(result.ipNumOfExpansions_confidenceInterval*100)/result.totalNumOfExpansions+"%\n";
				}
			}
		}
		System.out.println(resultOutput);
	}
	
	private void writeResultToFile (Input input, Result result, String path) throws IOException {
		
		FileWriter fileWriter=new FileWriter(new File(path));
		PrintWriter printWriter=new PrintWriter(fileWriter);

		if ((input.solverName == SolverNames.ODPIP) ||( input.solverName == SolverNames.ODPinParallelWithIP ) ){
			printWriter.append("\nThe total run time of "+SolverNames.toString(input.solverName)+" (in milliseconds):\n"+result.ipTime).println();
			printWriter.append("\nThe best coalition structure found by "+SolverNames.toString(input.solverName)+
							" is:\n"+General.convertArrayToString(result.get_ipBestCSFound())).println();
			printWriter.append("\nThe value of this coalition structure is:\n"+
							result.getTwoDigitAfterDot(result.get_ipValueOfBestCSFound())).println();
			printWriter.append("\nTop "+result.getTopCSs().size()+" CSs:").println();
			for(int i=0;i<result.getTopCSs().size();i++) {
				printWriter.append((i+1)+". CS:"+General.convertArrayToString(result.getTopCSs().get(i))+
						", Value:"+result.getTwoDigitAfterDot(result.getTopCSValues().get(i))).println();
			}
				
	
			if(input.solverName == SolverNames.IP ){
				printWriter.append("\nThe number of expansions made by IP:\n"+result.ipNumOfExpansions).println();
				printWriter.append("\nBased on this, the percentage of search-space that was searched by "+
				SolverNames.toString(input.solverName) + " is:\n"+(double)(result.ipNumOfExpansions*100)/result.totalNumOfExpansions+"%").println();
				}
			}else{
				printWriter.append("\nThe average time for "+SolverNames.toString(input.solverName)+
						" to scan the input (in milliseconds):\n"+result.ipTimeForScanningTheInput+
						" +/- "+result.ipTimeForScanningTheInput_confidenceInterval).println();			
				printWriter.append("\nThe average run time of "+SolverNames.toString(input.solverName)+
						" (in milliseconds):\n"+result.ipTime+" +/- "+result.ipTime_confidenceInterval).println();
				if( input.solverName == SolverNames.IP ){
					printWriter.append("\nThe average number of expansions made by "+SolverNames.toString(input.solverName)+
							":\n"+result.ipNumOfExpansions+" +/- "+result.ipNumOfExpansions_confidenceInterval).println();
					printWriter.append("\nBased on this, the average percentage of search-space that was searched by "+
							SolverNames.toString(input.solverName)+" is:\n"+
							(double)(result.ipNumOfExpansions*100)/result.totalNumOfExpansions+"% +/- "+
							(double)(result.ipNumOfExpansions_confidenceInterval*100)/result.totalNumOfExpansions+"%").println();
				}
			}
		printWriter.close();		
		}
	
	private void writeOutput(Input input, Result result, String path) throws IOException {
		FileWriter fileWriter=new FileWriter(new File(path));
		PrintWriter printWriter=new PrintWriter(fileWriter);
		//printWriter.append("Runtime: "+ (result.getTwoDigitAfterDot(result.ipTime/1000.0))).println();
		TheResult theResult=new TheResult();
		theResult.setRuntime(result.getTwoDigitAfterDot(result.ipTime/1000.0));
		theResult.setNumberOfAgents(input.numOfAgents+"");
		//printWriter.append("Groupings:").println();
		int n = result.getTopCSs().size() > 15 ? 15 : result.getTopCSs().size();
		for(int i=0;i<n;i++) {

			Grouping grouping = new Grouping();
			grouping.setRank((i+1) + "");
			grouping.setValue(result.getTwoDigitAfterDot(result.getTopCSValues().get(i)));
			int[][] cs = result.getTopCSs().get(i);
			for (int j = 0; j < cs.length; j++) {
				Group group = new Group();
				for (int j2 = 0; j2 < cs[j].length; j2++) {
					group.addId(cs[j][j2] + "");
				}
				grouping.addGroup(group);
			}
			theResult.addGrouping(grouping);
			/*printWriter.append((i+1) + ". " + General.convertArrayToString(result.getTopCSs().get(i)) + 
					": " + result.getTwoDigitAfterDot(result.getTopCSValues().get(i))).println();*/
		}
		JSONObject jsonObject = new JSONObject(theResult);
		printWriter.append(jsonObject.toString(4));
		printWriter.close();		
		}
	
}
