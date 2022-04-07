package inputOutput;
import general.General;
import general.Combinations;
import general.RandomPartition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.BetaDistributionImpl;
import org.apache.commons.math.distribution.ExponentialDistributionImpl;
import org.apache.commons.math.distribution.GammaDistributionImpl;
public class Input
{
 	public final int maxAllowedCoalitionValue = Integer.MAX_VALUE; 
 	public final boolean ipUsesBranchAndBound = true;
	public final boolean ipPruneSubspaces = true; 
 	public final boolean printPercentageOf_v_equals_f = false; 
 	public final boolean printDistributionOfThefTable = false; 
 	public final boolean printCoalitionValueDistribution = false; 
 	public final boolean printCoalitionStructureValueDistribution = false; 
 	public final boolean printTheSubspaceThatIsCurrentlyBeingSearched = false; 
 	public final boolean printTheCoalitionsWhenPrintingTheirValues = true;
	public final boolean useSamplingWhenSearchingSubspaces = false; 
 	public final boolean samplingDoneInGreedyFashion = false;
	public final boolean useEfficientImplementationOfIDP = true; 
 	private final double sigma = 0.1; 
	public boolean odpipUsesLocalBranchAndBound=true; 
 	public boolean odpipSearchesMultipleSubspacesSimultaneiously=true; 
	public SolverNames solverName;
	public boolean readCoalitionValuesFromFile;
	public boolean storeCoalitionValuesInFile;
 	public boolean orderIntegerPartitionsAscendingly; 
 	public double  acceptableRatio; 
 	public boolean printDetailsOfSubspaces; 
 	public boolean printNumOfIntegerPartitionsWithRepeatedParts;
	public boolean printInterimResultsOfIPToFiles; 
 	public boolean printTimeTakenByIPForEachSubspace; 
 	//private boolean takeIntoAccountSD;
 	public TreeSet<Integer> feasibleCoalitions;
	public int numOfAgents;
	public long numOfRunningTimes;	
	public double[] coalitionValues;
	//private double[] standardDeviations;
	public String outputFolder;
	public ValueDistribution valueDistribution;
	public String folderInWhichCoalitionValuesAreStored = "E:/coalitionValues"; 
	public String problemID = "";
	//private double SDWeight=0;

	public void initInput()
	{
		feasibleCoalitions = null; 
 		numOfRunningTimes = 1; 
 		storeCoalitionValuesInFile = false; 
 		printDetailsOfSubspaces = false;
		valueDistribution = ValueDistribution.UNKNOWN;
		//this.takeIntoAccountSD=false;
		}
	
/*	public double getStandardDeviation(int[] coalitionInByteFormat ) {
		int coalitionInBitFormat = Combinations.convertCombinationFromByteToBitFormat( coalitionInByteFormat );

		return getStandardDeviation(coalitionInBitFormat);
	}
	
	public double getStandardDeviation(int coalitionInBitFormat ) {
		return getStandardDeviations()[coalitionInBitFormat];
	}*/
	
	public double getCoalitionValue( int coalitionInBitFormat ) {


		if(coalitionInBitFormat<0) {

			throw new ArrayIndexOutOfBoundsException("No coalition exists in the coalition array for the coalitionInBitFormat value: "+
		coalitionInBitFormat+".");
		}
		return coalitionValues[ coalitionInBitFormat ];
	}

	public double getCoalitionValue( int[] coalitionInByteFormat ){
	
		int coalitionInBitFormat = Combinations.convertCombinationFromByteToBitFormat( coalitionInByteFormat );
	
		return getCoalitionValue(coalitionInBitFormat);
	}
/*	
	// <start> LCC related methods
	// !!! assign these variables to DPSOlvers
	private Map<int[], double[]> coalitionInfoMap = new HashMap<int[], double[]>();
	//private List<Agent> agents=new ArrayList<Agent>();
	private double totalMissedPreviousLesson, allowedTo;
	
	public double getTotalMissedPreviousLesson() {
		return totalMissedPreviousLesson;
	}

	public void setTotalMissedPreviousLesson(double totalMissedPreviousLesson) {
		this.totalMissedPreviousLesson = totalMissedPreviousLesson;
	}

	public Map<int[], double[]> getCoalitionInfoMap() {
		return coalitionInfoMap;
	}
	public void setCoalitionInfoMap(Map<int[], double[]> coalitionInfoMap) {
		this.coalitionInfoMap = coalitionInfoMap;
	}
/*	public List<Agent> getAgents() {
		return agents;
	}
	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}
	public double getCoalitionValue_LCC( int[] coalitionInByteFormat ){
		// Create a hashmap for coalitionInByte:coalitionInfo
		// Check if the map already has the coalitionInfo for coalitionInByteFormat
		// If No:
		// Build coalition info and add to the map
		// For each agent in coalition, call getUtilityValue(coalitionInfo, agent) and sum the values as satisfactionOfCoalition
		// Calculate SD of the coalition
		// Calculate coalition value
		// How to save coalitionInfo? each coalitin has gender, nationality info. -1:mixed, 0:male, 1:female,,,,-1:mixed,n:index of nation.
		
		// Experiment to test whether the algorithm checks all possible CSs
		//int convertedToBit = Combinations.convertCombinationFromByteToBitFormat(coalitionInByteFormat);
	//	if(convertedToBit==1 || convertedToBit==2 || convertedToBit==12) {
		//	return 10;
	//	}
		
		
		if(coalitionInfoMap.get(coalitionInByteFormat)==null || !coalitionInfoMap.containsKey(coalitionInByteFormat)) {
			coalitionInfoMap.put(coalitionInByteFormat,buildCoalitionInfo_LCC(coalitionInByteFormat));
		}
		
		double[] coalitionInfo = coalitionInfoMap.get(coalitionInByteFormat);
		
		if(coalitionInfo.length==4) {
			return coalitionInfo[3];
		}
		double satisfactioOfCoalition=0;
		
		
		for (int i : coalitionInByteFormat) {
			satisfactioOfCoalition+=getUtilityValue_LCC(coalitionInfo, agents.get(i));
		}
		satisfactioOfCoalition=satisfactioOfCoalition/coalitionInByteFormat.length;
		double sd=coalitionInfo[2];
		
		double coalitionValue = 0.3*satisfactioOfCoalition-0.7*sd;
		//double coalitionValue = satisfactioOfCoalition-sd/10;
		//double coalitionValue = satisfactioOfCoalition/sd;
		//double coalitionValue = satisfactioOfCoalition+1/sd;
		//double coalitionValue = 2*satisfactioOfCoalition+1/sd;
		//double coalitionValue = satisfactioOfCoalition;
		//double coalitionValue = 0.3*satisfactioOfCoalition+0.7/sd;
		
	//	System.out.println("Coalition:" + General.convertArrayToString(coalitionInByteFormat)+
	//			"--Coalition Info:" + General.convertArrayToString(coalitionInfo)+
	//			"--Coalition Value:" + coalitionValue);

		coalitionInfo=new double[] {coalitionInfo[0],coalitionInfo[1],coalitionInfo[2],coalitionValue};
		coalitionInfoMap.put(coalitionInByteFormat, coalitionInfo);
		
		return coalitionValue;
		}
	

	private double[] buildCoalitionInfo_LCC(int[] coalition) {
		// CoalitionInfo-- 0:gender {-1:mixed, 0:male, 1:female}, 1:nationality {-1:mixed, n:nationID}, 2:sd {d:double}, 3:CV {d2:double}
		double gender=-2, nationality=-2;
		double totalCPL=0;
		double mean=0;
		double sd = 0;
		int missedPreviousLessonCount=0;
		double lesson_max_size = 10, group_min_size=2, group_max_size=5;
		final int allowedToMissPreviousLesson = (int)Math.ceil((totalMissedPreviousLesson*group_min_size)/numOfAgents);
		
		// TODO: Add ML and CL check here. Replace the following conditions with ML and CL. 
		
		if(coalition.length>group_max_size || coalition.length<group_min_size) {
			return new double[] {0,0,0,Double.NEGATIVE_INFINITY};
		}
		for (int i : coalition) {
			Agent agent=agents.get(i);
			missedPreviousLessonCount+=agent.getAttendance();
			if(missedPreviousLessonCount>allowedToMissPreviousLesson) {
				return new double[] {0,0,0,Double.NEGATIVE_INFINITY};
			}
			totalCPL+=agent.getCPL();
			if(gender==-2) {
				gender=agent.getGender();		
			}else if (gender != agent.getGender() && gender!=-1) {
				gender=-1;
			}
			
			if(nationality==-2) {
				nationality=agent.getNationality();		
			}else if (nationality!= agent.getNationality() && nationality!=-1) {
				nationality=-1;
			}
		}
		mean=totalCPL/coalition.length;
		
		for (int i : coalition) {
			Agent agent=agents.get(i);
			sd+=Math.pow((agent.getCPL()-mean),2);
		}
		sd=sd/coalition.length;
		sd=Math.sqrt(sd);
		
		//if(sd==0) {
		//	sd=0.3;
	//	}
		
		return new double[] {gender,nationality,sd};
	}
	

	private double getSDofCoalition(int[] coalition) {
		double totalCPL=0;
		double mean=0;
		double sd = 0;

		for (int i : coalition) {
			Agent agent=agents.get(i);
			totalCPL+=agent.getCPL();
		}
		mean=totalCPL/coalition.length;
		
		for (int i : coalition) {
			Agent agent=agents.get(i);
			sd+=Math.pow((agent.getCPL()-mean),2);
		}
		sd=sd/coalition.length;
		sd=Math.sqrt(sd);
		
		if(sd<=1) {
			return sd*100;
		}

		return sd;
		
	}
	
	private double getUtilityValue_LCC(double[] coalitionInfo, Agent agent) {
		double utilityValue=0;
		int numberOfViolations = 0;
		for (int i = 0; i < 2; i++) {
			if(agent.getPreferences()[i]==-1
					&& coalitionInfo[i]==-1) {
				// mixed preference is satisfied
				utilityValue+=agent.getPreferenceWeights()[i]*1;				
			}else if(agent.getPreferences()[i]==1 && coalitionInfo[i]==agent.getPersonalInfo()[i]) {
				// same preference is satisfied
				utilityValue+=agent.getPreferenceWeights()[i]*1;								
			}else if(agent.getPreferences()[i]==0) {
				// Agent doesn't mind
				utilityValue+=0;
			}else {
				// the preference is violated
				utilityValue-=agent.getPreferenceWeights()[i]*1;	
				numberOfViolations++;
			}
		}
		// If both preferences are violated
		if(numberOfViolations==2) {
			utilityValue-=1;
		}

		return utilityValue;
	}
	
	public void readTCNProfilesFromFile(String path) {
		String fileName = "TCNProfiles.txt";
		path = path+fileName;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			totalMissedPreviousLesson=0;
			for(int i=0;i<numOfAgents;i++) {
				String line = reader.readLine();
				while(line==null || line.trim().equals("") || line.trim().isEmpty() || line.trim().startsWith("//")) {
					line=reader.readLine();					
				}
				
				Agent agent=createAgent(line);
				totalMissedPreviousLesson+=agent.getAttendance();
			}
			reader.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Agent createAgent(String tcnInfo) {
		String[] values = tcnInfo.trim().split("\\|");
		int id,gender,nationality,attendance,genderPref,nationPref;
		double CPL,genderWeight,nationWeight;
		
		
		id=Integer.valueOf(values[0].trim());
		gender = Integer.valueOf(values[1].trim());
		nationality = Storage.getNationalityIndex(values[2].trim());
		CPL=Double.valueOf(values[3].trim());
		attendance=Integer.valueOf(values[4].trim());
		genderPref=Integer.valueOf(values[5].trim());
		nationPref=Integer.valueOf(values[6].trim());
		genderWeight=Double.valueOf(values[7].trim());
		nationWeight=Double.valueOf(values[8].trim());
		
		Agent agent=new Agent(id, CPL, attendance, gender, nationality, genderPref, nationPref, genderWeight, nationWeight);

		if(agents.isEmpty()) {
			agents.add(null);
		}
		agents.add(agent.getID(), agent);
		return agent;
	}
	
	public double getSDofCS(int[][] CS) {
		double totalSD = 0;
		for(int i=0; i<CS.length; i++) {
				totalSD +=getSDofCoalition(CS[i]);
		}
		//System.out.println("vvv:"+(valueOfCS*0.3-0.7*totalSD));
		return totalSD;
	}

	
	// <end> LCC related methods
*/
	
	public double getCoalitionStructureValue( int[][] coalitionStructure ){

		double valueOfCS = 0;

		/*if(this.takeIntoAccountSD) {
			double totalSD = 0;
			for(int i=0; i<coalitionStructure.length; i++) {
					valueOfCS += getCoalitionValue( coalitionStructure[i] );
					totalSD +=getStandardDeviation(coalitionStructure[i]);
			}
			
			return valueOfCS*(1-this.SDWeight)-this.SDWeight*totalSD;
		}else {*/
			for(int i=0; i<coalitionStructure.length; i++) {
				valueOfCS += getCoalitionValue(coalitionStructure[i]);}
		
			/*if(valueOfCS>-40) {
				System.out.println("a CS value: " + valueOfCS);

			}*/
				
		return (valueOfCS);
		//}

	}	
	
	public double getCoalitionStructureValue( int[] coalitionStructure ){
		
		double valueOfCS = 0;

	/*	if(this.takeIntoAccountSD) {
			double totalSD = 0;
			for(int i=0; i<coalitionStructure.length; i++) {
					valueOfCS += getCoalitionValue( coalitionStructure[i] );
					totalSD +=getStandardDeviation(coalitionStructure[i]);
			}
			
			return valueOfCS*(1-this.SDWeight)-this.SDWeight*totalSD;
		}else {*/
			for(int i=0; i<coalitionStructure.length; i++) {
				valueOfCS += getCoalitionValue( coalitionStructure[i] );
		}
			/*if(valueOfCS>-40) {
				System.out.println("a CS value: " + valueOfCS);
			}*/
		return( valueOfCS );
		//}

	}	
	
	public void generateCoalitionValues()
	{

 		double maxCoalitionValue = Double.MIN_VALUE;
		Random random = new Random();
		long time = System.currentTimeMillis();
		if(( coalitionValues == null )||( coalitionValues.length != (int)Math.pow(2,numOfAgents) ))
			coalitionValues = new double[ (int)Math.pow(2,numOfAgents) ];
		coalitionValues[0] = 0;  
 		double[] agentStrength_normal  = new double[numOfAgents];
		double[] agentStrength_uniform = new double[numOfAgents];
		for( int agent=1; agent<=numOfAgents; agent++){
			agentStrength_normal [agent-1] = Math.max( 0, General.getRandomNumberFromNormalDistribution( 10,sigma,random) );
			agentStrength_uniform[agent-1] = random.nextDouble() * 10;
		}
 		if( valueDistribution == ValueDistribution.UNIFORM )
			for(int coalition = coalitionValues.length-1; coalition>0; coalition--){
				coalitionValues[ coalition ] = Math.round( (Integer.bitCount(coalition) * random.nextDouble()) * 10000000 );
				if( maxCoalitionValue < coalitionValues[ coalition ] ) maxCoalitionValue = coalitionValues[ coalition ]; 
			}
 		if( valueDistribution == ValueDistribution.NORMAL )
			for(int coalition = coalitionValues.length-1; coalition>0; coalition--){
				coalitionValues[ coalition ] = Math.max( 0, Integer.bitCount(coalition) * General.getRandomNumberFromNormalDistribution( 10,sigma,random) );
				coalitionValues[ coalition ] = Math.round( coalitionValues[ coalition ] * 10000000 );
				if( maxCoalitionValue < coalitionValues[ coalition ] ) maxCoalitionValue = coalitionValues[ coalition ];
			}
 		if( valueDistribution == ValueDistribution.MODIFIEDUNIFORM )
			for(int coalition = coalitionValues.length-1; coalition>0; coalition--){
				coalitionValues[ coalition ] = random.nextDouble()*10*Integer.bitCount(coalition);
				int probability = random.nextInt(100);
				if(probability <=20) coalitionValues[ coalition ] += random.nextDouble() * 50;
				coalitionValues[ coalition ] = Math.round( coalitionValues[ coalition ] * 10000000 );
				if( maxCoalitionValue < coalitionValues[ coalition ] ) maxCoalitionValue = coalitionValues[ coalition ];
			}
 		if( valueDistribution == ValueDistribution.MODIFIEDNORMAL )
			for(int coalition = coalitionValues.length-1; coalition>0; coalition--){
				coalitionValues[ coalition ] = Math.max( 0, Integer.bitCount(coalition) * General.getRandomNumberFromNormalDistribution( 10,sigma,random) );
				int probability = random.nextInt(100);
 				if(probability <=20) coalitionValues[ coalition ] += General.getRandomNumberFromNormalDistribution( 25,sigma,random);
				coalitionValues[ coalition ] = Math.round( coalitionValues[ coalition ] * 10000000 );
				if( maxCoalitionValue < coalitionValues[ coalition ] ) maxCoalitionValue = coalitionValues[ coalition ];
			}
 		if( valueDistribution == ValueDistribution.EXPONENTIAL ){
			ExponentialDistributionImpl exponentialDistributionImpl = new ExponentialDistributionImpl(1);
			for(int coalition = coalitionValues.length-1; coalition>0; coalition--){
				boolean repeat = false;
				do{
					try { coalitionValues[ coalition ] = Math.max( 0, Integer.bitCount(coalition) * exponentialDistributionImpl.sample() );
					} catch (MathException e) { repeat = true; }
				}while( repeat == true );
				coalitionValues[ coalition ] = Math.round( coalitionValues[ coalition ] * 10000000 );
				if( maxCoalitionValue < coalitionValues[ coalition ] ) maxCoalitionValue = coalitionValues[ coalition ];
			}
		}
 		if( valueDistribution == ValueDistribution.BETA ){
			BetaDistributionImpl betaDistributionImpl =new BetaDistributionImpl(0.5, 0.5);
			for(int coalition = coalitionValues.length-1; coalition>0; coalition--){
				boolean repeat = false;
				do{
					try { coalitionValues[ coalition ] = Math.max( 0, Integer.bitCount(coalition) * betaDistributionImpl.sample() );
					} catch (MathException e) { repeat = true; }
				}while( repeat == true );
				coalitionValues[ coalition ] = Math.round( coalitionValues[ coalition ] * 10000000 );
				if( maxCoalitionValue < coalitionValues[ coalition ] ) maxCoalitionValue = coalitionValues[ coalition ];
			}
		}
 		if( valueDistribution == ValueDistribution.GAMMA ){
			GammaDistributionImpl gammaDistributionImpl = new GammaDistributionImpl(2, 2);
			for(int coalition = coalitionValues.length-1; coalition>0; coalition--){
				boolean repeat = false;
				do{
					try { coalitionValues[ coalition ] = Math.max( 0, Integer.bitCount(coalition) * gammaDistributionImpl.sample() );
					} catch (MathException e) { repeat = true; }
				}while( repeat == true );
				coalitionValues[ coalition ] = Math.round( coalitionValues[ coalition ] * 10000000 );
				if( maxCoalitionValue < coalitionValues[ coalition ] ) maxCoalitionValue = coalitionValues[ coalition ];
			}
		}
 		if( valueDistribution == ValueDistribution.AGENTBASEDUNIFORM )
			for(int coalition = coalitionValues.length-1; coalition>0; coalition--){
				int[] members = Combinations.convertCombinationFromBitToByteFormat(coalition, numOfAgents);
 				double percentage = 100;
				coalitionValues[ coalition ] = 0;
				for( int m=0; m<Integer.bitCount(coalition); m++){
					double rangeSize = (percentage/(double)100) * agentStrength_uniform[members[m]-1] * 2;
					double startOfRange = ((100 - percentage)/(double)100) * agentStrength_uniform[members[m]-1];
					coalitionValues[ coalition ] += startOfRange + (random.nextDouble()*rangeSize);
				}
				coalitionValues[ coalition ] = Math.round( coalitionValues[ coalition ] * 10000000 );
				if( maxCoalitionValue < coalitionValues[ coalition ] ) maxCoalitionValue = coalitionValues[ coalition ];
			}		
 		if( valueDistribution == ValueDistribution.AGENTBASEDNORMAL )
			for(int coalition = coalitionValues.length-1; coalition>0; coalition--){
				int[] members = Combinations.convertCombinationFromBitToByteFormat(coalition, numOfAgents);
				coalitionValues[ coalition ] = 0;
				for( int m=0; m<Integer.bitCount(coalition); m++){
					double newValue;
					newValue = General.getRandomNumberFromNormalDistribution(agentStrength_normal[members[m]-1], sigma, random);
					coalitionValues[ coalition ] += Math.max( 0, newValue );
				}
				coalitionValues[ coalition ] = Math.round( coalitionValues[ coalition ] * 10000000 );	
				if( maxCoalitionValue < coalitionValues[ coalition ] ) maxCoalitionValue = coalitionValues[ coalition ];
			}
 		if( valueDistribution == ValueDistribution.NDCS )
			for(int coalition = coalitionValues.length-1; coalition>0; coalition--){
				do{ 
					coalitionValues[ coalition ] = (General.getRandomNumberFromNormalDistribution(Integer.bitCount(coalition),Math.sqrt(Integer.bitCount(coalition)),random));
				}while( coalitionValues[ coalition ] <= 0 );
				coalitionValues[ coalition ] = Math.round( coalitionValues[ coalition ] * 10000000 );	
				if( maxCoalitionValue < coalitionValues[ coalition ] ) maxCoalitionValue = coalitionValues[ coalition ];
			}
 		if( maxCoalitionValue > maxAllowedCoalitionValue )
			for(int c=coalitionValues.length-1; c>=0; c--)  
				coalitionValues[c] = (int)Math.round(  coalitionValues[c]  *  maxAllowedCoalitionValue / maxCoalitionValue  );
		System.out.println(numOfAgents+" agents, "+ValueDistribution.toString(valueDistribution)+" distribution. The time required to generate the coalition values (in milli second): "+(System.currentTimeMillis()-time));
 		if( printCoalitionValueDistribution ) printCoalitionValueDistribution();
 		if( printCoalitionStructureValueDistribution ) printCoalitionStructureValueDistribution( numOfAgents );
	}	
	public void storeCoalitionValuesInFile( int problemID )
	{
 		General.createFolder( folderInWhichCoalitionValuesAreStored );
 		String filePathAndName = folderInWhichCoalitionValuesAreStored;
		filePathAndName += "/"+numOfAgents+"Agents_"+ValueDistribution.toString(valueDistribution)+"_"+problemID+".txt";
		General.clearFile( filePathAndName );
 		StringBuffer tempStringBuffer = new StringBuffer();
		for(int coalition = 0; coalition<coalitionValues.length; coalition++)
			tempStringBuffer.append( coalitionValues[ coalition ]+"\n");
 		General.printToFile( filePathAndName, tempStringBuffer.toString(), false);
		tempStringBuffer.setLength(0);
	}
	public void readCoalitionValuesFromFile( int problemID )
	{
		//System.out.println("111111111111111111111");
		coalitionValues = new double[ (int)Math.pow(2,numOfAgents) ];
 		String filePathAndName = folderInWhichCoalitionValuesAreStored;
		filePathAndName += "/"+numOfAgents+"Agents_"+ValueDistribution.toString(valueDistribution)+"_"+problemID+".txt";
		try{
			BufferedReader bufferedReader = new BufferedReader( new FileReader(filePathAndName) );
			for(int coalition = 0; coalition<coalitionValues.length; coalition++)
				coalitionValues[ coalition ] = (new Double(bufferedReader.readLine())).doubleValue();			
			bufferedReader.close();
		}
		catch (Exception e){
			System.out.println(e);
		}		
	}	
	public void readCoalitionValuesFromFile( String fileName )
	{
		coalitionValues = new double[ (int)Math.pow(2,numOfAgents) ];
 		String filePathAndName = folderInWhichCoalitionValuesAreStored;
		filePathAndName += "/"+fileName;
		try{
			BufferedReader bufferedReader = new BufferedReader( new FileReader(filePathAndName) );
			for(int coalition = 0; coalition<coalitionValues.length; coalition++) {
				coalitionValues[ coalition ] = (new Double(bufferedReader.readLine())).doubleValue();	
			}
			bufferedReader.close();
		}
		catch (Exception e){
			System.out.println(e);
		}
/*
		coalitionValues = new double[ (int)Math.pow(2,numOfAgents) ];
	//	System.out.println("yummemmeme:"+coalitionValues.length);

 		String filePathAndName = folderInWhichCoalitionValuesAreStored;
		filePathAndName += "/"+fileName;
		try{
			//BufferedReader bufferedReader = new BufferedReader( new FileReader(filePathAndName) );
			for(int coalition = 0; coalition<coalitionValues.length; coalition++) {
//				System.out.println(coalition);

				if(coalition==0) {
					coalitionValues[coalition]=0;
				}else {
					
				int[] coalitionInByte = Combinations.convertCombinationFromBitToByteFormat(coalition, numOfAgents);
				//System.out.println("coal:"+coalition);

				// If coalition has more than 5 or less than 2 agents, then its value is infeasible

				coalitionValues[coalition]=getCoalitionValue(coalitionInByte);
				//coalitionValues[ coalition ] = (new Double(bufferedReader.readLine())).doubleValue();		
				
				}	
			//bufferedReader.close();
				}
		}
		catch (Exception e){
e.printStackTrace();		}	*/	
	}	
	public void printCoalitionValueDistribution()
	{
 		int[] counter = new int[40];
		for(int i=0; i<counter.length; i++){
			counter[i]=0;
		}		
 		long min = Integer.MAX_VALUE;
		long max = Integer.MIN_VALUE;
		for(int coalition=1; coalition<coalitionValues.length; coalition++){
			long currentWeightedValue = (long)Math.round( coalitionValues[coalition] / Integer.bitCount(coalition) );
			if( min > currentWeightedValue )
				min = currentWeightedValue ;
			if( max < currentWeightedValue )
				max = currentWeightedValue ;
		}
		System.out.println("The maximum weighted coalition value (i.e., value of coalition divided by size of that coalition) is  "+max+"  and the minimum one is  "+min);
 		for(int coalition=1; coalition<coalitionValues.length; coalition++){
			long currentWeightedValue = (long)Math.round( coalitionValues[coalition] / Integer.bitCount(coalition) );
			int percentageOfMax = (int)Math.round( (currentWeightedValue-min) * (counter.length-1) / (max-min) );
			counter[percentageOfMax]++;
		}
 		System.out.println("The distribution of the weighted coalition values (i.e., every value of coalition is divided by size of that coalition) is:");
		System.out.print(ValueDistribution.toString(valueDistribution)+"_coalition = [");
		for(int i=0; i<counter.length; i++)
			System.out.print(counter[i]+" ");
		System.out.println("]");
	}
	public void printCoalitionStructureValueDistribution( int n )
	{
		RandomPartition randomPartition = new RandomPartition(n);
		int numOfSamples = 10000000;		
		long[] sampleValues = new long[numOfSamples];
		for(int i=0; i<numOfSamples; i++){
			int[] currentCoalitionStructure = randomPartition.get();
			long value = 0;
			for(int j=0; j<currentCoalitionStructure.length; j++)
				value += coalitionValues[ currentCoalitionStructure[j] ];
 			sampleValues[i] = value;
		}
 		int[] counter = new int[200];
		for(int i=0; i<counter.length; i++){
			counter[i]=0;
		}		
 		long min = Integer.MAX_VALUE;
		long max = Integer.MIN_VALUE;
		for(int i=1; i<sampleValues.length; i++){
			long currentValue = sampleValues[i];
			if( min > currentValue )
				min = currentValue ;
			if( max < currentValue )
				max = currentValue ;
		}
		System.out.println("The maximum weighted coalition value (i.e., value of coalition divided by size of that coalition) is  "+max+"  and the minimum one is  "+min);
 		for(int i=1; i<sampleValues.length; i++){
			long currentValue = sampleValues[i];
			int percentageOfMax = (int)Math.round( (currentValue-min) * (counter.length-1) / (max-min) );
			counter[percentageOfMax]++;
		}
 		System.out.println("The distribution of the weighted coalition STRUCTURE values  (i.e., every value of coalition is divided by size of that coalition) is:");
		System.out.print(ValueDistribution.toString(valueDistribution)+"_structure = [");
		for(int i=0; i<counter.length; i++)
			System.out.print(counter[i]+" ");
		System.out.println("]");
	}

	/*public boolean isTakeIntoAccountSD() {
		return takeIntoAccountSD;
	}

	public void setTakeIntoAccountSD(boolean takeIntoAccountSD) {
		this.takeIntoAccountSD = takeIntoAccountSD;
	}

	public double[] getStandardDeviations() {
		return standardDeviations;
	}

	public void setStandardDeviations(double[] standardDeviations) {
		this.standardDeviations = standardDeviations;
	}

	public double getSDWeight() {
		return SDWeight;
	}

	public void setSDWeight(double sDWeight) {
		SDWeight = sDWeight;
	}*/
	
	
}