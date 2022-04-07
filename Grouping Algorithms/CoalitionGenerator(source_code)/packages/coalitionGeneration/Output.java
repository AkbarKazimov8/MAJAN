package coalitionGeneration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import coalitionGeneration.models.Coalition;

public class Output {
	public void writeStrToFile(String path, String string) throws IOException {
		FileWriter fileWriter=new FileWriter(new File(path));
		PrintWriter printWriter=new PrintWriter(fileWriter);
		printWriter.append(string).close();
	}
	public void deleteContentOfFile(String path) throws IOException {
		FileWriter fileWriter=new FileWriter(new File(path));
		PrintWriter printWriter=new PrintWriter(fileWriter);
		
		printWriter.print("");
		printWriter.close();
	}
	public void writeToFile(String path, int numberOfAgents, List<Coalition> coalitions) throws IOException {
		String numberOfAgentsStr = "Number of agents -> ";
		String outputStructureStr = "// Structure: {ID, value}";
		String coalitionsStr = "Coalitions -> ";
		String coalitionStructureTemplateStr = "{%d, %f}";
		FileWriter fileWriter=new FileWriter(new File(path));
		PrintWriter printWriter=new PrintWriter(fileWriter);
		
		//printWriter.print("");
		
		// write number of agents
		printWriter.append(numberOfAgentsStr).append(numberOfAgents+"").println();
		
		// write output structure format
		printWriter.append(outputStructureStr).println();
		
		// write coalitions
		printWriter.append(coalitionsStr);
	//	printWriter.printf(coalitionStructureTemplateStr, 0, Double.NEGATIVE_INFINITY).append(" | ");

		for (int i = 0; i < coalitions.size(); i++) {
			Coalition coalition = coalitions.get(i);
			printWriter.printf(coalitionStructureTemplateStr, coalition.getID(), coalition.getValue());
			if(i!=coalitions.size()-1) {
				printWriter.append(" | ");
			}
		}
		printWriter.close();
	}
	
	public void writeToFileAllCoalitions(String path, int numberOfAgents, List<Coalition> coalitions) throws IOException {
		String numberOfAgentsStr = "Number of agents -> ";
		String outputStructureStr = "// Structure: {ID, value}";
		String coalitionsStr = "Coalitions -> {0, 0} | ";
		String coalitionStructureTemplateStr = "{%d, %f}";
		FileWriter fileWriter=new FileWriter(new File(path));
		PrintWriter printWriter=new PrintWriter(fileWriter);
		
		// write number of agents
		//printWriter.append(numberOfAgentsStr).append(numberOfAgents+"").println();
		
		// write output structure format
	//	printWriter.append(outputStructureStr).println();
		
		// write coalitions
		//printWriter.append(coalitionsStr);
		printWriter.println("0");
		
		for (int i = 0; i < coalitions.size(); i++) {
			Coalition coalition = coalitions.get(i);
			printWriter.println(coalition.getValue());
//			printWriter.printf(coalitionStructureTemplateStr, coalition.getID(), coalition.getValue());
			/*if(i!=coalitions.size()-1) {
				printWriter.append(" | ");
			}*/
		}
		
		printWriter.close();
	}
	
	
}
