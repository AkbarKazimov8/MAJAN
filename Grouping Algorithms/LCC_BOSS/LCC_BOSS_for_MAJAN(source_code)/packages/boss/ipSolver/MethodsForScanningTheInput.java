package boss.ipSolver;

import java.util.TreeSet;

import boss.general.Combinations;
import boss.inputOutput.Input;
import boss.inputOutput.Output;
import boss.mainSolver.Result;

public class MethodsForScanningTheInput
{
 	protected static class CheckFeasibilityOfCoalitions
	{
 		public boolean firstCoalitionIsFeasible =true;  public boolean firstCoalitionAsSingletonsIsFeasible =true;
		public boolean secondCoalitionIsFeasible=true;  public boolean secondCoalitionAsSingletonsIsFeasible=true;		
		 
		public void setDataMembers( int numOfAgents, int[] firstCoalition, int[] secondCoalition, TreeSet<Integer> feasibleCoalitions  )
		{
 			if( feasibleCoalitions == null ) return;
			int firstCoalitionInBitFormat  = Combinations.convertCombinationFromByteToBitFormat( firstCoalition );
			int secondCoalitionInBitFormat = Combinations.convertCombinationFromByteToBitFormat( secondCoalition );
			setDataMembers( numOfAgents,firstCoalitionInBitFormat,secondCoalitionInBitFormat,feasibleCoalitions );
		}		
		 
		public void setDataMembers( int numOfAgents,int firstCoalition,int secondCoalition,TreeSet<Integer> feasibleCoalitions )
		{

                    if( feasibleCoalitions == null ) return;
			
			firstCoalitionIsFeasible  = feasibleCoalitions.contains(new Integer(firstCoalition ));
			secondCoalitionIsFeasible = feasibleCoalitions.contains(new Integer(secondCoalition));

			firstCoalitionAsSingletonsIsFeasible = true;
			for(int i=0; i<numOfAgents; i++){
				if ((firstCoalition & (1<<i)) != 0){ 

                                    if( feasibleCoalitions.contains(new Integer((1<<i))) == false ){
						firstCoalitionAsSingletonsIsFeasible = false;
						break;
					}
				}
			}
			secondCoalitionAsSingletonsIsFeasible = true;
			for(int i=0; i<numOfAgents; i++){
				if ((secondCoalition & (1<<i)) != 0){ 

                                    if( feasibleCoalitions.contains(new Integer((1<<i))) == false ){
						secondCoalitionAsSingletonsIsFeasible = false;
						break;
					}
				}
			}
		}
	}
	
        
	public static void scanTheInputAndComputeAverage( Input input, Output output, Result result, double[] avgValueForEachSize)
	{    	

 		long startTime = System.currentTimeMillis();
		int numOfAgents = input.numOfAgents;
		int totalNumOfCoalitions = (int)Math.pow(2,numOfAgents);
		double bestValue=result.get_ipValueOfBestCSFound();
		int bestCoalition1=0; int bestCoalition2=0;
		double[] sumOfValues = new double[ numOfAgents ];
		for(int size=1; size<=numOfAgents; size++)
			sumOfValues[size-1]=0;

 		boolean coalition1IsFeasible = true;
		boolean coalition2IsFeasible = true;
		final boolean constraintsExist;
		if( input.feasibleCoalitions == null )
			constraintsExist = false;
		else
			constraintsExist = true;

		for(int coalition1=totalNumOfCoalitions/2; coalition1<totalNumOfCoalitions-1; coalition1++)
		{
			int sizeOfCoalition1 = Combinations.getSizeOfCombinationInBitFormat( coalition1, numOfAgents );
			
			int coalition2 = totalNumOfCoalitions - 1 - coalition1;
			int sizeOfCoalition2 = numOfAgents - sizeOfCoalition1;

			sumOfValues[ sizeOfCoalition1-1 ] += input.getCoalitionValue( coalition1 );
			sumOfValues[ sizeOfCoalition2-1 ] += input.getCoalitionValue( coalition2 );
			
 			if( constraintsExist ){
				coalition1IsFeasible = input.feasibleCoalitions.contains(new Integer(coalition1));
				coalition2IsFeasible = input.feasibleCoalitions.contains(new Integer(coalition2));
			}			
 			double value = 0;
			if(( constraintsExist == false )||( (coalition1IsFeasible)&&(coalition2IsFeasible) )){
				value = input.getCoalitionValue( coalition1 ) + input.getCoalitionValue( coalition2 );
			}						
			
 			if( bestValue < value ) { bestCoalition1=coalition1; bestCoalition2=coalition2; bestValue = value;}

 			// Each new CS is sent to be stored regardless of whether its value is better than current best or not
 			int[][] curBestCS = new int[2][];
 			curBestCS[0] = Combinations.convertCombinationFromBitToByteFormat(coalition1, numOfAgents);
			curBestCS[1] = Combinations.convertCombinationFromBitToByteFormat(coalition2, numOfAgents);
			//result.updateIPSolutionList(curBestCS, value);
			result.updateSolutionList(curBestCS, value);
		}

		int[][] bestCS = new int[2][];
		bestCS[0] = Combinations.convertCombinationFromBitToByteFormat(bestCoalition1, numOfAgents);
		bestCS[1] = Combinations.convertCombinationFromBitToByteFormat(bestCoalition2, numOfAgents);
		result.updateSolutionList( bestCS, bestValue );
 		if( bestValue > result.get_ipValueOfBestCSFound() )
		{

 			// LCC_Modification
			// Normally the solution is updated here if it is higher. I want to collect all CSs to get top ten. 
			// Therefore, I update solution list out of this if
			//int[][] bestCS = new int[2][];
			//bestCS[0] = Combinations.convertCombinationFromBitToByteFormat(bestCoalition1, numOfAgents);
			//bestCS[1] = Combinations.convertCombinationFromBitToByteFormat(bestCoalition2, numOfAgents);
			result.updateIPSolution( bestCS, bestValue );

		}
		output.printCurrentResultsOfIPToStringBuffer_ifPrevResultsAreDifferent( input, result );

 		for(int size=1; size<=numOfAgents; size++)
			avgValueForEachSize[ size-1 ] = (double) sumOfValues[ size-1 ] / Combinations.binomialCoefficient(numOfAgents,size); 

 		updateNumOfSearchedAndRemainingCoalitionsAndCoalitionStructures( input, result );
		
 		result.ipTimeForScanningTheInput = System.currentTimeMillis() - startTime;
	}
 
	private static void updateNumOfSearchedAndRemainingCoalitionsAndCoalitionStructures( Input input, Result result )
	{
		for(int size1=1; size1<=Math.floor(input.numOfAgents/(double)2); size1++)
		{
 			int size2=(int)(input.numOfAgents-size1);
			

                        int numOfCombinationsOfSize1 = (int) Combinations.binomialCoefficient( input.numOfAgents, size1 );

			
                        
			int temp; 
			if( size1 != size2 ) 
				temp = numOfCombinationsOfSize1;
			else
				temp =(numOfCombinationsOfSize1/2);

			result.ipNumOfExpansions  += 2*temp;;
		}
	}
}