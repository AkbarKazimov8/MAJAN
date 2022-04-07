package boss.mainSolver;
import boss.inputOutput.Input;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import boss.general.Combinations;
import boss.general.General;
import boss.ipSolver.IntegerPartitionGraph;
import boss.ipSolver.IDPSolver_whenRunning_ODPIP;
import boss.ipSolver.ODPSolver_runningInParallelWithIP;
public class Result
{
	public Result( Input input )  
	{
		totalNumOfCS = Combinations.getNumOfCS( input.numOfAgents );
		totalNumOfCoalitionsInSearchSpace = Combinations.getNumOfCoalitionsInSearchSpace( input.numOfAgents );
		dpMaxSizeThatWasComputedSoFar = 1;
	}
	public int       numOfAgents;  
	public long      totalNumOfCS; 
	public long      totalNumOfCoalitionsInSearchSpace; 
	public long      totalNumOfExpansions; 
	public IDPSolver_whenRunning_ODPIP idpSolver_whenRunning_ODPIP;
	public ODPSolver_runningInParallelWithIP odpSolver_runningInParallelWithIP;
	public long     inclusionExclusionTime; 
	public double   inclusionExclusionTime_confidenceInterval; 
	public  int[][] inclusionExclusionBestCSFound; 
	public  double  inclusionExclusionValueOfBestCSFound; 
	public double   inclusionExclusionValueOfBestCSFound_confidenceInterval; 
	public long     cplexTime;  
	public double   cplexTime_confidenceInterval; 
	public  int[][] cplexBestCSFound; 
	public  double  cplexValueOfBestCSFound; 
	public double   cplexValueOfBestCSFound_confidenceInterval; 
	private  int     dpMaxSizeThatWasComputedSoFar;  
	private  boolean dpHasFinished;  
	private  int[][] dpBestCSFound;  
	private  double  dpValueOfBestCSFound;  
	public long      dpTime;  
	public long[]    dpTimeForEachSize ;  
	private  int[][] ipBestCSFound;  
	private  double  ipValueOfBestCSFound;  
	public double    ipValueOfBestCS_confidenceInterval;  
	public long      ipStartTime;  
	public double    ipTimeForScanningTheInput_confidenceInterval;  
	public long      ipTime;  
	public double    ipTime_confidenceInterval;  
	public long      ipTimeForScanningTheInput;  
	public long      ipNumOfExpansions;  
	public double    ipNumOfExpansions_confidenceInterval;  
	public double    ipUpperBoundOnOptimalValue;  
	public double    ipUpperBoundOnOptimalValue_confidenceInterval;  		
	public double    ipLowerBoundOnOptimalValue;  
	public IntegerPartitionGraph ipIntegerPartitionGraph;  
	private double[]  max_f; 
	// <start> LCC related 

	private List<int[][]> topCSs;
	private List<Integer> topCSInBit;
	private List<int[]> topCSInByte;
	private List<Double> topCSValues;
	// <end> LCC related 

	public void initializeIPResults(){
		ipStartTime=System.currentTimeMillis();
		ipNumOfExpansions=0;
		ipValueOfBestCSFound=Double.NEGATIVE_INFINITY;
		ipBestCSFound = null;
		totalNumOfExpansions = 0;
		// <start> LCC related 
		topCSs=new ArrayList<int[][]>();
		topCSValues=new ArrayList<Double>();
		dpValueOfBestCSFound = Double.NEGATIVE_INFINITY;
		topCSInBit=new ArrayList<Integer>();
		topCSInByte=new ArrayList<int[]>();
		
		int[][] initialDummyCs = new int[1][1];
		double initialDummyValue = -100000000;
		int initialDummyCsInBit = -100;
		int[] initialDummyCsInByte = new int[1];
		topCSs.add(initialDummyCs);
		topCSValues.add(initialDummyValue);
		topCSInBit.add(initialDummyCsInBit);
		topCSInByte.add(initialDummyCsInByte);
		// <end> LCC related 

	}
	public void updateDPSolution( int[][] CS, double value ){
//		System.out.println("CS to be added as the best DP solution\n:::"+General.convertArrayToString(CS));

		if( get_dpValueOfBestCSFound() <= value ){
			set_dpValueOfBestCSFound( value );
			set_dpBestCSFound( General.copyArray(CS) );
		}
	}
	public synchronized void updateIPSolution( int[][] CS, double value ){
//		System.out.println("CS to be added as the best IP solution\n:::"+General.convertArrayToString(CS));
    	
		if( get_ipValueOfBestCSFound() <= value ){
			set_ipValueOfBestCSFound( value );
			set_ipBestCSFound( General.copyArray(CS) );
		}
	}
	// <start> LCC related 

	
	// Each CS with its value comes here. Top ten best CSs are stored
	public synchronized void updateSolutionList_old(int[][] CS, double value) {
		int csInBit = Combinations.convertCombinationFromByteToBitFormat(Combinations.convertSetOfCombinationsFromByteToBitFormat(CS));
		//System.out.println("CS to be added to IP solutions list:::"+General.convertArrayToString(CS)+"-bitFormat:"+csInBit);
		if(!topCSInBit.contains(csInBit)) {
			int limit= 30;
		

		if(topCSValues.size()==0) {
			topCSs.add(CS);
			topCSValues.add(value);
			topCSInBit.add(csInBit);
		}else {
			int size = ((topCSs.size()>limit) ?  limit : topCSs.size());
			boolean added = false;
			for (int i = 0; i < size; i++) {
				if(value>=topCSValues.get(i)) {
					for (int j = size-1; j >= i; j--) {
						if(size!=j+1) {
							topCSs.set(j+1, topCSs.get(j));
							topCSValues.set(j+1, topCSValues.get(j));
							topCSInBit.set(j+1, topCSInBit.get(j));
						}else {
							topCSs.add(j+1, topCSs.get(j));
							topCSValues.add(j+1, topCSValues.get(j));
							topCSInBit.add(j+1, topCSInBit.get(j));
						}
					}
					topCSs.set(i,CS);
					topCSValues.set(i, value);
					topCSInBit.set(i,csInBit);
					added=true;
					break;
				}
			}
			if(added) {
				//System.out.println("adds this:" + value);
			}else if(!added && value>-34){
				System.out.println("not added this:" + value);
			}
			if(!added && topCSs.size()<limit) {
				topCSs.add(topCSs.size(),CS);
				topCSValues.add(topCSValues.size(), value);
				topCSInBit.add(topCSInBit.size(), csInBit);
			}
		}
		if(topCSs.size()>limit) {
			for (int i = limit; i < topCSs.size(); i++) {
				topCSs.remove(i);
				topCSValues.remove(i);
				topCSInBit.remove(i);
				
			}
		}
		
		}
	}
	
	public synchronized void updateSolutionList(int[][] CS, double value) {		
		value = Utils.round(3, value);
		
		if(!topCSValues.contains(value)) {
			int limit= 30;
			boolean added=false;
			for(int i=0; i<topCSs.size();i++) {
				if(value>topCSValues.get(i)) {
					topCSs.add(i, CS);
					topCSValues.add(i, value);
					//topCSInBit.add(i, csInBit);
					//topCSInByte.add(csInByte);
					added=true;
					break;
				}
			}
			
			if(!added && topCSValues.size()<limit) {
				topCSs.add(CS);
				topCSValues.add(value);
			}
			
			if(topCSs.size()>limit) {
				for (int i = limit; i < topCSs.size(); i++) {
					topCSs.remove(i);
					topCSValues.remove(i);
					//topCSInBit.remove(i);
					//topCSInByte.remove(i);
				}
			}
		}
	}
	// <end> LCC related 

	private static boolean csAlreadyExists(int[][] cs, List<int[][]> existingCsList) {		

		int[] newCsInByte = Combinations.convertSetOfCombinationsFromByteToBitFormat(cs);
		System.out.println("new---size:"+newCsInByte.length);
		if(!existingCsList.isEmpty()) {
			int[] exCs = Combinations.convertSetOfCombinationsFromByteToBitFormat(existingCsList.get(0));
			System.out.println("---size:"+exCs.length);
		}
		
		
		
		return false;
	}
	
	private static boolean csAlreadyExists(int[] csInByte, List<int[]> csInByteList) {		
		
		for(int i=0; i<csInByteList.size(); i++) {
			System.out.println("newLeng: "+csInByte.length);
			System.err.println("oldLeng: "+csInByteList.get(i).length);
			System.out.println(General.convertArrayToString(csInByteList.get(i)));
			/*if(csInByte.length != existingCsInByte.length) {
				System.out.println("11111----"+csInByte.length+"---"+existingCsInByte.length);
				return false;
			}*/
			
			for(int j=0; j<csInByteList.get(i).length; j++) {
				boolean coalitionExists = false;
				for(int k=0; k<csInByte.length; k++) {
					//System.out.println("new:"+j+"-"+csInByte[j]);
					//System.out.println("old:"+i+"-"+existingCsInByte[i]);
					if(csInByteList.get(i)[j]==csInByte[k]) {
						//System.out.println("new:"+csInByte[j]);
						//System.out.println("old:"+existingCsInByte[i]);
						coalitionExists = true;
						break;
					}
				}
				if(!coalitionExists) {
					return false;
				}
				
				/*if(existingCsInByte[i]!=csInByte[i]) {
					return false;
				}*/
				
			}
		}
		
		return true;
	}
	
	 public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
	        
	        list.sort(Entry.comparingByValue());

	        Map<K, V> result = new LinkedHashMap<>();
	        for (Entry<K, V> entry : list) {
	            result.put(entry.getKey(), entry.getValue());
	        }

	        return result;
	    }
	
	public synchronized void set_dpHasFinished( boolean dpHasFinished ){
		this.dpHasFinished = dpHasFinished;
	}
	public synchronized boolean get_dpHasFinished(){
		return dpHasFinished;
	}
	public synchronized void set_dpMaxSizeThatWasComputedSoFar( int size ){
		dpMaxSizeThatWasComputedSoFar = size;
	}
	public synchronized int get_dpMaxSizeThatWasComputedSoFar(){
		return dpMaxSizeThatWasComputedSoFar;
	}
	public synchronized void set_dpBestCSFound( int[][] CS ){
		dpBestCSFound = General.copyArray( CS );		
	}
	public synchronized int[][] get_dpBestCSFound(){
		return dpBestCSFound;		
	}
	public synchronized void set_dpValueOfBestCSFound(double value){
		dpValueOfBestCSFound = value;				
	}
	public synchronized double get_dpValueOfBestCSFound(){
		return dpValueOfBestCSFound;				
	}
	public   void set_ipBestCSFound( int[][] CS ){
		ipBestCSFound = General.copyArray( CS );		
	}
	public   int[][] get_ipBestCSFound(){
		return ipBestCSFound;		
	}
	public   void set_ipValueOfBestCSFound(double value){
		ipValueOfBestCSFound = value;				
	}
	public   double get_ipValueOfBestCSFound(){
		return ipValueOfBestCSFound;				
	}
	public void set_max_f( int index, double value){
		max_f[ index ] = value;
	}
	public double get_max_f( int index ){
		return( max_f[index] );
	}
	public void init_max_f( Input input, double[][] maxValueForEachSize ){
		max_f = new double[input.numOfAgents];
    	for( int i=0; i<input.numOfAgents; i++ )
    		set_max_f( i, 0 );
		for(int i=0; i<input.numOfAgents; i++){
			double value = input.getCoalitionValue( (1<<i) );  
			if( get_max_f( 0 ) < value )
				set_max_f( 0 ,   value );
		}
		for(int i=1; i<input.numOfAgents; i++)  
			set_max_f( i, maxValueForEachSize[i][0] );
	}
	public long getTotalNumOfCS() {
		return totalNumOfCS;
	}
	public List<int[][]> getTopCSs() {
		return topCSs;
	}
	public List<Double> getTopCSValues() {
		return topCSValues;
	}
	
	public String getTwoDigitAfterDot(double n) {
		return new DecimalFormat("###.###").format(n).replace(",", ".");
	}
}