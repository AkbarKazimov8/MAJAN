package coalitionGeneration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


import coalitionGeneration.models.Agent;
import coalitionGeneration.models.CSGPInput;
import coalitionGeneration.models.CannotLink;
import coalitionGeneration.models.Coalition;
import coalitionGeneration.models.MustLink;
import coalitionGeneration.utils.Combinations;
import coalitionGeneration.utils.Constants;

public class CoalitionGeneration {
	public CSGPInput readLccInput(String path) {
		List<Agent> agents = null;
		List<int[]> MLs=new ArrayList<int[]>();
		List<int[]> CLs=new ArrayList<int[]>();
		int minSize = 0, maxSize=0;
		double SDWeight=-1;
		double genderWeight = -1;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			Iterator<String> linesIter = reader.lines().iterator();
			while(linesIter.hasNext()) {
				String line=linesIter.next();

				if(line==null || line.trim().equals("") || line.trim().isEmpty() || line.trim().startsWith("//")) {
					continue;
				}else if (line.toLowerCase().startsWith("ml")) {
					MLs=createMLList(line.substring(line.indexOf("{")));
				}else if (line.toLowerCase().startsWith("cl")) {
					CLs=createCLList(line.substring(line.indexOf("{")));
				}else if (line.toLowerCase().startsWith("group size")) {
					line=line.substring(line.indexOf("{")).trim();
					int[] sizeRange=readGroupSize(line);
					minSize=sizeRange[0];
					maxSize=sizeRange[1];
				}else if(line.toLowerCase().startsWith("sd weight")) {
					line=line.substring(line.indexOf("{")).replace("{", "").replace("}", "").trim();
					//System.out.println("sssd:--"+line);
//					line=line.substring(line.indexOf(">")+1).trim();
					SDWeight=Double.valueOf(line);
				}else if(line.toLowerCase().startsWith("gender weight")) {
					line=line.substring(line.indexOf("{")).replace("{", "").replace("}", "").trim();
					//System.out.println("gender weigt:--"+line);

//					line=line.substring(line.indexOf(">")+1).trim();
					genderWeight = Double.valueOf(line);
				}else if(line.toLowerCase().startsWith("agent profiles")) {
					agents = createAgentsList(line.substring(line.indexOf("{")));
				}
				
				}
			reader.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}

		for (Agent ag : agents) {
			ag.setPreferenceWeights(genderWeight, 1-genderWeight);
		}
		if(SDWeight!=-1) {
			return new CSGPInput(agents, MLs, CLs, minSize,maxSize,SDWeight);
		}
		return new CSGPInput(agents, MLs, CLs, minSize,maxSize);
	}
	
	private List<Agent> createAgentsList(String agentProfilesLine){
		List<Agent> agents=new ArrayList<Agent>();
		String[] agProfilesArray = agentProfilesLine.trim().split("\\|");
		for(String agProfile: agProfilesArray) {
			String[] agentProfileAttributes = agProfile.replace("{", "").replace("}", "").trim().split(",");
			int id, gender, nationality, attendance, genderPref, nationPref;
			double CPL, genderWeight, nationWeight;
			
			id=Integer.valueOf(agentProfileAttributes[0].trim());
			gender = Integer.valueOf(agentProfileAttributes[1].trim());
			nationality = Constants.getNationalityIndex(agentProfileAttributes[2].trim());
			CPL=Double.valueOf(agentProfileAttributes[3].trim());
			attendance=Integer.valueOf(agentProfileAttributes[4].trim());
			genderPref=Integer.valueOf(agentProfileAttributes[5].trim());
			nationPref=Integer.valueOf(agentProfileAttributes[6].trim());
			genderWeight=Double.valueOf(agentProfileAttributes[7].trim());
			nationWeight=Double.valueOf(agentProfileAttributes[8].trim());
			/*System.out.println("11--" + id);
			System.out.println(gender);
			System.out.println(nationality);
			System.out.println(CPL);
			System.out.println(attendance);
			System.out.println(genderPref);
			System.out.println(nationPref);
			System.out.println(genderWeight);
			System.out.println(nationWeight);*/
			
			agents.add(new Agent(id, CPL, attendance, gender, nationality, genderPref, nationPref, genderWeight, nationWeight));
		}
		return agents;
	}
	
	public CSGPInput readInputFromFile(String path) {
		List<Agent> agents=new ArrayList<Agent>();
		List<int[]> MLs=new ArrayList<int[]>();
		List<int[]> CLs=new ArrayList<int[]>();
		int minSize = 0, maxSize=0;
		double SDWeight=-1;
		double genderWeight = -1;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			Iterator<String> linesIter = reader.lines().iterator();
			while(linesIter.hasNext()) {
				String line=linesIter.next();

				if(line==null || line.trim().equals("") || line.trim().isEmpty() || line.trim().startsWith("//")) {
					continue;
	
				}else if(Character.isDigit(line.trim().charAt(0))) {

					agents.add(createAgent(line));					
				}else if (line.startsWith("ML")) {

					MLs=createMLList(line.substring(line.indexOf("{")));
				}else if (line.startsWith("CL")) {

					CLs=createCLList(line.substring(line.indexOf("{")));
				}else if (line.startsWith("Group Size")) {
					line=line.substring(line.indexOf("["));
					int[] sizeRange=readGroupSize(line);
					minSize=sizeRange[0];
					maxSize=sizeRange[1];
				}else if(line.toLowerCase().startsWith("sd weight")) {
					line=line.substring(line.indexOf(">")+1).trim();
					SDWeight=Double.valueOf(line);
				}else if(line.toLowerCase().startsWith("gender weight")) {
					line=line.substring(line.indexOf(">")+1).trim();
					genderWeight = Double.valueOf(line);
				}
				
				}
			reader.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}

		for (Agent ag : agents) {
			ag.setPreferenceWeights(genderWeight, 1-genderWeight);
		}
		if(SDWeight!=-1) {
			return new CSGPInput(agents, MLs, CLs, minSize,maxSize,SDWeight);
		}
		return new CSGPInput(agents, MLs, CLs, minSize,maxSize);
	}
	
	private List<int[]> createMLList(String mlInputLine) {
		List<int[]> mlList = new ArrayList<int[]>();
		String[] mls = mlInputLine.trim().split("\\|");
		for (String ml: mls) {
			String[] pairs=ml.trim().replace("{", "").replace("}", "").split(",");
			int[] mustLinkConnections = new int[pairs.length];
			
			for (int i = 0; i < pairs.length; i++) {
				mustLinkConnections[i]=Integer.valueOf(pairs[i]);
			}

//			int[] mustLinkPairs= new int []{Integer.valueOf(pairs[0]),Integer.valueOf(pairs[1])};
			mlList.add(mustLinkConnections);
			
		//	mlList.add(new MustLink(Integer.valueOf(pairs[0]), Integer.valueOf(pairs[1])));
		}

		return mlList;
	}
	
	private List<int[]> createCLList(String clInputLine) {
		List<int[]> clList = new ArrayList<int[]>();
		String[] cls = clInputLine.trim().split("\\|");
		for (String cl: cls) {

			String[] pairs=cl.trim().replace("{", "").replace("}", "").split(",");
			
			int[] cannotLinkConnections = new int[pairs.length];
			
			for (int i = 0; i < pairs.length; i++) {

				cannotLinkConnections[i]=Integer.valueOf(pairs[i]);
			}
			
			//int[] cannotLinkPairs=new int[] {Integer.valueOf(pairs[0]), Integer.valueOf(pairs[1])};
			clList.add(cannotLinkConnections);

			//clList.add(new CannotLink(Integer.valueOf(pairs[0]), Integer.valueOf(pairs[1])));
			}
		return clList;
	}
	
	private int[] readGroupSize(String groupSizeline) {
		String[] range = groupSizeline.trim().replace("{", "").replace("}", "").trim().split(",");
		int[] sizeRange=new int[2];
		
		sizeRange[0]=Integer.valueOf(range[0].trim());
		sizeRange[1]=Integer.valueOf(range[1].trim());
		
		return sizeRange;
	}
	
	
	private Agent createAgent(String tcnInfo) {
		String[] values = tcnInfo.trim().split("\\|");
		int id,gender,nationality,attendance,genderPref,nationPref;
		double CPL,genderWeight,nationWeight;
		
		
		id=Integer.valueOf(values[0].trim());
		gender = Integer.valueOf(values[1].trim());
		nationality = Constants.getNationalityIndex(values[2].trim());
		CPL=Double.valueOf(values[3].trim());
		attendance=Integer.valueOf(values[4].trim());
		genderPref=Integer.valueOf(values[5].trim());
		nationPref=Integer.valueOf(values[6].trim());
		genderWeight=Double.valueOf(values[7].trim());
		nationWeight=Double.valueOf(values[8].trim());
		
		Agent agent=new Agent(id, CPL, attendance, gender, nationality, genderPref, nationPref, genderWeight, nationWeight);

		return agent;
	}
	
	private double computeUtilityValue(Coalition coalition, Agent agent) {
		double utilityValue=0;
		int numberOfViolations = 0;
		
		for (int i = 0; i < 2; i++) {
			if(agent.getPreferences()[i]==-1
					&& coalition.getCommonValues()[i]==-1) {
				// mixed preference is satisfied
				utilityValue+=agent.getPreferenceWeights()[i]*1;				
			}else if(agent.getPreferences()[i]==1 
					&& coalition.getCommonValues()[i]==agent.getPersonalInfo()[i]) {
				// same preference is satisfied
				utilityValue+=agent.getPreferenceWeights()[i]*1;								
			}else if(agent.getPreferences()[i]==0) {
				// Agent doesn't mind
				utilityValue+=agent.getPreferenceWeights()[i]*1;
			}else {
				// the preference is violated
				utilityValue-=agent.getPreferenceWeights()[i]*1;	
				numberOfViolations++;
			}
		}
		// If both preferences are violated
		//if(numberOfViolations==2) {
		//	utilityValue-=1;
		//}

		return utilityValue;
	}
	
	public List<Coalition> generateCoalitions(CSGPInput csgpInput) {
		int numberOfAgents = csgpInput.getAgents().size();
		List<Coalition> coalitions=new ArrayList<Coalition>();
		List<int[]> mls=csgpInput.getMLs();
		List<int[]> cls=csgpInput.getCLs();
		
		for(int i=1;i<Math.pow(2, numberOfAgents);i++) {
			int[] coalitionInByte = Combinations.convertCombinationFromBitToByteFormat(i, numberOfAgents);
			

			if(coalitionInByte.length<csgpInput.getMinCoalitionSize() || coalitionInByte.length>csgpInput.getMaxCoalitionSize()) {
				continue;
			}
			
			if(clViolated(coalitionInByte, cls) || mlViolated(coalitionInByte, mls)) {
				//System.out.println("girdi"+utils.General.convertArrayToString(coalitionInByte));
				continue;
			}

			List<Agent> agents = new ArrayList<Agent>();
			
			for (int j = 0; j < coalitionInByte.length; j++) {
				int l = coalitionInByte[j];
				
				agents.add(csgpInput.getAgents().get(l-1));
			}
			Coalition coalition=new Coalition(i, agents);
			
			coalition.setValue(computeCoalitionValue(coalition,csgpInput.getSdWeight()));
			
			coalitions.add(coalition);
		}
		return coalitions;
	}
	
	public List<Coalition> generateALLCoalitions(CSGPInput csgpInput, int infeasibleCValue) {
		
		int numberOfAgents = csgpInput.getAgents().size();
		List<Coalition> coalitions=new ArrayList<Coalition>();
		List<int[]> mls=csgpInput.getMLs();
		List<int[]> cls=csgpInput.getCLs();
		
		for(int i=0;i<Math.pow(2, numberOfAgents);i++) {
			int[] coalitionInByte = Combinations.convertCombinationFromBitToByteFormat(i, numberOfAgents);

			if(coalitionInByte.length<csgpInput.getMinCoalitionSize() || 
					coalitionInByte.length>csgpInput.getMaxCoalitionSize() || 
					clViolated(coalitionInByte, cls) || 
					mlViolated(coalitionInByte, mls) || 
					attendanceViolated(coalitionInByte, csgpInput.getAgents())) {
				
				Coalition coalition=new Coalition(i, infeasibleCValue);
				coalitions.add(coalition);
				continue;
			}
			
			List<Agent> agents = new ArrayList<Agent>();
			
			for (int j = 0; j < coalitionInByte.length; j++) {
				int l = coalitionInByte[j];
				
				agents.add(csgpInput.getAgents().get(l-1));
			}
			Coalition coalition=new Coalition(i, agents);
			
			coalition.setValue(computeCoalitionValue(coalition,csgpInput.getSdWeight()));
			System.out.println(i + "---id---value--- "+ coalition.getValue() + " -- sd- "+coalition.getSd());
			for(Agent ag: coalition.getMemberAgents()) {
				System.out.print(ag.getID());
			}
			System.out.println();
			coalitions.add(coalition);
		}
		return coalitions;
	}
	

	
	private double computeCoalitionValue(Coalition coalition, double sdWeight) {
		double satisfactionOfCoalition=0;
		
		for (Agent agent : coalition.getMemberAgents()) {
			double uV = computeUtilityValue(coalition, agent);
			System.out.println("Agent---="+agent.getID() + "-UV---="+uV);
			/*if(coalition.getID()!=0) {
				System.out.println("Agent: "+agent.getID());
				System.out.println("Coalition:"+
				utils.General.convertArrayToString(Combinations.convertCombinationFromBitToByteFormat(coalition.getID(), 
						15)));
				System.out.println("UValue:"+uV);
				}*/
			satisfactionOfCoalition+= uV ;
		}
		
		//Coalition:[3, 12]
//		Value:-0.375
		double sd = coalition.getSd();
		
		/*for (int i : coalitionInByteFormat) {
			satisfactioOfCoalition+=getUtilityValue_LCC(coalitionInfo, agents.get(i));
		}
		
		satisfactioOfCoalition=satisfactioOfCoalition/coalitionInByteFormat.length;
		double sd=coalitionInfo[2];*/
		
		double coalitionValue = (1-sdWeight)*satisfactionOfCoalition-sdWeight*sd;

		return coalitionValue;
		
	}
	private boolean attendanceViolated(int[] coalitionInByte, List<Agent> agents) {
		boolean violated = false;
		int notAttended = 0;
		
		for (int j = 0; j < coalitionInByte.length; j++) {
			int l = coalitionInByte[j];
			notAttended+=agents.get(l-1).getAttendance();			
		}
		
		if (notAttended>1) {
			return true;
		}
		return violated;
		
		
	}
	private boolean clViolated(int[] coalitionInByte, List<int[]> cls) {
		boolean violated = false;
		
		for (int[] cl : cls) {
			int linked = 0;
			for (int i = 0; i < cl.length; i++) {
				for (int j = 0; j < coalitionInByte.length; j++) {
						if(cl[i]==coalitionInByte[j]) {
							linked+=1;
						}
				}
			}
			if(linked>1) {
				return true;
			}
		}

		
		return violated;
	}
	
	private boolean mlViolated(int[] coalitionInByte, List<int[]> mls) {
		boolean violated = false;
		
		for(int[] ml:mls) {
			int linked = 0;
			for(int i =0; i<ml.length;i++) {
				for(int j=0;j<coalitionInByte.length;j++) {
					if(coalitionInByte[j]==ml[i]) {
						linked+=1;
						break;
					}
				}
			}
			if(linked>0 && linked!=ml.length) {
				return true;
			}
			
		}
		
		return violated;
	}
	
	// in the following functions, I tried to find an efficient way to reduce the number of coalitions if any constraint is violated.
	// the idea is described in in comments inside the method. So basically I wanted to ignore some coalitions by making use of 
	// some logic between the bit values of given ML, CL links and bit values of coalitions. This thing works for up to 4 agents. 
	// But not tested for up to 20 agents because the formula is not correct for 20 agents. It must be modified. But I think it 
	// can be done if I put more time into it.
	private boolean cannotLinkViolated(int i, List<int[]> cls) {
		/* x,y given agent numbers (e.g. 1,4)
		 * I find k, b from x,y (e.g. k=1, b=2)
		 * c=bit(x,y)
		 * i yaradilacaq coalition bit di
		 * mod((i-c-pow(2,k)),16)==0
		 * mod((i-c-pow(2,b)),16)==0
		 * mod((i-c-pow(2,k)-pow(2,b)),16)==0
		 * 
		 * */
		boolean violated = false;
		
		for (int[] clPair : cls) {
			int k=0, b=0;
			
			for (int j = 1; j <= 4; j++) {
				if(clPair[0]!=j || clPair[1]!=j) {
					if(k==0) {
						k=j;
					}else {
						b=j;
						break;
					}
				}
			}
			
			k=k-1;
			b=b-1;
			System.out.println("k ve b cl:"+k+"--"+b);
			int c= Combinations.convertCombinationFromByteToBitFormat(clPair);
			if(((i-c-Math.pow(2, k))%16==0) || ((i-c-Math.pow(2, b))%16==0) || ((i-c-Math.pow(2, k)-Math.pow(2, b))%16==0)) {
				return true;
			}
		}
		
		return violated;
	}
	
	private boolean mustLinkViolated(int i, List<int[]> mls) {
		boolean violated = false;
		for (int[] mlPair : mls) {
			int k=0, b=0;
			for (int j = 1; j <= 4; j++) {
				if(mlPair[0]!=j || mlPair[1]!=j) {
					if(k==0) {
						k=j;
					}else {
						b=j;
					}
				}
			}
			System.out.println("k ve b ml:"+k+"--"+b);
			k=k-1;
			b=b-1;
			int c= Combinations.convertCombinationFromByteToBitFormat(mlPair);
			if(((i-c-Math.pow(2, k))%16!=0) & 
					((i-c-Math.pow(2, b))%16!=0) & 
					((i-c-Math.pow(2, k)-Math.pow(2, b))%16!=0)) {
				return true;
			}
		}
		
		return violated;
	}
}
