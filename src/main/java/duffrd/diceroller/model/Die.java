package duffrd.diceroller.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Die
{
	int faces;
	List<Integer> range;
	long[] probabilities;
	
	/**
	 * Creates a standard die.
	 * @param faces The number of faces on the die.  The faces of the die will be the values 1 through {@code "faces"}.
	 */
	public Die ( int faces )
	{	
		this.faces = faces;
		
		range = IntStream.rangeClosed ( 1, faces ).boxed ().collect ( Collectors.toList () );
		
		probabilities = new long[ faces+1 ];
		probabilities[ 0 ] = 0;
		
		Arrays.fill ( probabilities, 1, probabilities.length, 1 );
	}
	
	/**
	 * Creates a weighted die.
	 * @param distribution A {@code Map} of face values to the number of times that face value appears on the die.
	 */
	public Die ( Map<Integer,Integer> distribution )
	{			
		faces = distribution.values ().stream ().mapToInt ( Integer::intValue ).sum();
		range = distribution.keySet ().stream ().sorted ().collect ( Collectors.toList () );
		
		probabilities = new long[ range.get ( range.size ()-1 ) + 1 ];
		Arrays.fill ( probabilities, 0 );
		
		for ( Integer face : distribution.keySet () )
			probabilities[ face ] = distribution.get ( face );		
	}

	/**
	 * Rolls the die.
	 * @return The face value rolled.
	 */
	public int roll()
	{
		long target = Roller.random.nextInt ( faces );
		int threshold = 0;
		
		for ( int face=0; face<probabilities.length; face++ )
		{
			threshold += probabilities[ face ];
			if ( target < threshold )
				return face;
		}
		
		return -1;
	}
	
	@Override
	public String toString()
	{
	    return Arrays.stream ( probabilities ).boxed ().map ( l -> String.valueOf ( l ) ).collect ( Collectors.joining ( " ", "[ ", " ]" ) );
	}
}
