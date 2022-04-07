package mainSolver;


import inputOutput.*;
import dpSolver.*;
import ipSolver.*;

public class MainSolver
{
 	
	public Result solve( Input input )	
	{
		ComputeErrorBars computeErrorBars = new ComputeErrorBars(input); 
 
		for( int problemID=1; problemID<=input.numOfRunningTimes; problemID++) 
 		{			
			input.problemID = (new Integer(problemID)).toString();
			Result result = new Result(input);
			Output output = new Output();
			output.initOutput( input );

 			if( input.storeCoalitionValuesInFile )
				input.storeCoalitionValuesInFile( problemID );
			
 			 
			 if ( (input.solverName == SolverNames.IP) || (input.solverName == SolverNames.ODPIP)  || (input.solverName == SolverNames.ODPinParallelWithIP) ){
				 IPSolver ipSolver = new IPSolver(); 
				 ipSolver.solve( input, output, result );}

			 // TODO VERY IMPORTANT!
			/*	 try {
				 ipSolver.solve( input, output, result );}
				 catch(Exception e) {
					  TODO Sometimes there is a weird error. In IDPSolver_whenRunning_ODPIP class, 
					 valueOfBestPartitionFound is produced as null. However, accoridng to source code, it should never be 
					 null because it is always created before calling. This means, because of multiple threads are created by 
					 BOSS, something is happening. For example, if IDPSolver_whenRunning_ODPIP thread is executed 2 times in 
					 3 BOSS executions, maybe the algorithm calls the thread which is not executed and therefore, it returns 
					 null. This is a problem for sure. For now, I try to stop all threads if the algorithm produces an error 
					 such that in the next run, the threads would be dead.
					 I tried a lot to find the reason but couldn't find how to solve this. 
					 Another weird thing is, when I use System.out in 
					 IDPSolver_whenRunning_ODPIP.getValueOfBestPartitionFound(int coalition), it never returns null. 
					 Maybe because System.out somehow attaches the detached thread back? It is funny but when 
					 I remove the System.out.print line, the array returns null. When System.out.print is there, 
					 it doesn't return null. 
					 THEREFORE, I USE SYSTEM.OUT.PRINTLN IN updateValueOfBestPartitionFound AND getValueOfBestPartitionFound OF
					 IDPSolver_whenRunning_ODPIP FOR NOW. 
					 ipSolver.finalize(result, input, output);
					 //System.err.println("ERROR::"+e.getLocalizedMessage());
					 e.printStackTrace();
				 }
			}*/
			if( input.numOfRunningTimes == 1 ) {
				return( result );
			}else{
				computeErrorBars.addResults( result, input );
				if( problemID < input.numOfRunningTimes ){
					if( input.readCoalitionValuesFromFile )
						input.readCoalitionValuesFromFile( problemID+1 );
					else
						input.generateCoalitionValues();
				}
				System.out.println(input.numOfAgents+" agents, "+ValueDistribution.toString(input.valueDistribution)+" distribution. The solver just finished solving "+input.problemID+" problems out of  "+input.numOfRunningTimes);
				if ( input.solverName == SolverNames.InclusionExclusion ) System.out.println("runtime for Inclusion-Exclusion (in milliseconds) was: "+result.inclusionExclusionTime);
			}
		}		
		Result averageResult = computeErrorBars.setAverageResultAndConfidenceIntervals( input );
		return( averageResult );
	}
}