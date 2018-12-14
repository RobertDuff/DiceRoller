package duffrd.diceroller.model;

import javafx.concurrent.Task;

public class ProbabilityCalculatorTask extends Task<Void>
{
	public ProbabilityCalculatorTask ( )
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Void call () throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
//	private int[] countProbabilities()
//	{	
//		//TODO: Find An Algorithm.
//		
//		int[] countedProbabilities = new int[ ( range().get ( range().size () - 1 ) + 1 ) ];
//		int[] roll = new int[ count ];
//		
//		__genProbabilities ( countedProbabilities, roll, count-1 );
//		
//		return countedProbabilities;
//	}
//	
//	private void __genProbabilities ( int[] countedProbabilties, int[] roll, int level )
//	{
//		if ( level == -1 )
//		{
//			List<Integer> sortedRoll = Arrays.asList ( ArrayConverter.intArray ( roll ) );
//			Collections.sort ( sortedRoll );
//			
//			int face = sortedRoll.subList ( start, end ).stream ().mapToInt ( i -> i.intValue() ).sum ();
//			
//			countedProbabilties[ face ]++;
//			return;
//		}
//		
//		for ( Integer face : die.range )
//		{
//			roll[ level ] = face;
//			__genProbabilities ( countedProbabilties, roll, level-1 );
//		}
//	}
}
