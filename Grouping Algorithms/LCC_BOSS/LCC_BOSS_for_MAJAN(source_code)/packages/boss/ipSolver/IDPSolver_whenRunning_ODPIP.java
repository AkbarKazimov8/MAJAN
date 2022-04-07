package boss.ipSolver;

import boss.mainSolver.Result;
import boss.dpSolver.DPSolver;
import boss.inputOutput.Input;
import boss.inputOutput.SolverNames;

public class IDPSolver_whenRunning_ODPIP extends Thread
{
	private Input inputToIDPSolver;
	private Result result;
	private double[] valueOfBestPartitionFound;
	private boolean stop = false;
	public DPSolver dpSolver;

	public void setStop( boolean value )
	{
		if( dpSolver != null )  dpSolver.setStop(value);
		stop = value;
	}
	
	 
	public IDPSolver_whenRunning_ODPIP(  Input input, Result result )
	{
		this.stop = false;
		this.result= result;
		this.valueOfBestPartitionFound = new double[ 1 << input.numOfAgents ];
		
		// LCC

		
 		this.inputToIDPSolver = getInputToIDPSolver( input );
		
 		this.dpSolver = new DPSolver( inputToIDPSolver, result );
	}
	
	 
	public synchronized void updateValueOfBestPartitionFound( int coalition, double value ){
		// IMPORTANT! Following line prevents "this.valueOfBestPartitionFound" is null" error
	//	System.out.println("##1" + valueOfBestPartitionFound[coalition]);

		if( valueOfBestPartitionFound[coalition] < value )
			valueOfBestPartitionFound[coalition] = value ;
	}
	 
	public double getValueOfBestPartitionFound( int coalition ){
		//if(valueOfBestPartitionFound==null) {
		//	System.out.println("#NULL#" + 0);
		//	return 0;
		//}
		// IMPORTANT! Following line prevents "this.valueOfBestPartitionFound" is null" error
		//System.out.println("##2" + valueOfBestPartitionFound[coalition]);
		//System.out.print("n"+valueOfBestPartitionFound[coalition]);
		//valueOfBestPartitionFound[coalition]=valueOfBestPartitionFound[coalition];
		double returnV=valueOfBestPartitionFound[coalition];
		return returnV;
	}
	
	 
	public void clearDPTable(){
		valueOfBestPartitionFound = null;
	}

	 
	public void run()
	{
		try {
			this.dpSolver.runDPorIDP();		
					
		}catch (ArrayIndexOutOfBoundsException e) {
			//e.printStackTrace();
			//setStop(true);
			
			//stop();
			
			System.err.println(e.getMessage());
		}
		
	}
	
	 
	public void initValueOfBestPartitionFound(Input input, double[][] maxValueForEachSize)
	{
		long startTime = System.currentTimeMillis();
		
		valueOfBestPartitionFound[0]=0;
		
 		result.init_max_f( input, maxValueForEachSize );
		
		 
		for(int coalitionInBitFormat = valueOfBestPartitionFound.length-1; coalitionInBitFormat >= 1; coalitionInBitFormat--)
			valueOfBestPartitionFound[coalitionInBitFormat] = input.getCoalitionValue(coalitionInBitFormat);

 	}
	
	 
	private Input getInputToIDPSolver( Input input )
	{
		Input inputToDPSolver = new Input();
		inputToDPSolver.coalitionValues = input.coalitionValues;
		inputToDPSolver.problemID = input.problemID;
		
 		inputToDPSolver.solverName = SolverNames.ODPIP;
		
 		inputToDPSolver.storeCoalitionValuesInFile = false;
		inputToDPSolver.printDetailsOfSubspaces = false;
		inputToDPSolver.printNumOfIntegerPartitionsWithRepeatedParts = false;
		inputToDPSolver.printInterimResultsOfIPToFiles = false;
		inputToDPSolver.printTimeTakenByIPForEachSubspace = false;

 		inputToDPSolver.feasibleCoalitions = null;
		inputToDPSolver.numOfAgents = input.numOfAgents;
		inputToDPSolver.numOfRunningTimes = 1;	
		
		/*		// <start> LCC related 
		inputToDPSolver.setAgents(input.getAgents());
		inputToDPSolver.setCoalitionInfoMap(input.getCoalitionInfoMap());
		inputToDPSolver.setTotalMissedPreviousLesson(input.getTotalMissedPreviousLesson());
		// <end> LCC related 
		 */
		return( inputToDPSolver );
	}


	public double[] getValueOfBestPartitionFound() {
		return valueOfBestPartitionFound;
	}
	
	
}