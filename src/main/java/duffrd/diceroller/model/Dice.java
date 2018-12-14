package duffrd.diceroller.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utility.math.Polynomial;

public class Dice
{
	Die die;
	int count;
	int start;
	int end;
	
	int faces;
	List<Integer> range;
	int[] probabilities;
	
	public Dice ( Die die, int count )
	{
		this ( die, count, 0, count );
	}
	
	public Dice ( Die die, int count, int start, int end )
	{
		this.die   = die;
		this.count = count;

		this.start = start;
		this.end   = end;
		
		faces = ( int ) Math.pow ( die.faces, count );
		
		// Calculate Range
		
		Set<Integer> rangeSet = new HashSet<Integer>();			
		
		__genRange ( rangeSet, end - start, 0 );
		
		range = new ArrayList<> ( rangeSet );
		Collections.sort ( range );
		
		//
		// If using all dice, then go ahead and calculate the probability distribution.
		// Otherwise, leave it null, and it can be calculated later on demand.
		//
		if ( start == 0 && end == count )
		{
			Polynomial probabilities = new Polynomial ( die.probabilities );
			Polynomial product = Polynomial.ONE;
			
			for ( int i=0; i<count; i++ )
				product = product.multiply ( probabilities );
			
			this.probabilities = product.coefficients ();
		}
	}
	
	private void __genRange ( Set<Integer> range, int count, int sum )
	{
		if ( count == 0 )
		{
			range.add ( sum );
			return;
		}
		
		for ( Integer face : die.range )
			__genRange ( range, count-1, sum+face );
	}

	public DiceOutcome roll()
	{
		return new DiceOutcome ( this );
	}
	
	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder ();
		
		b.append ( die.toString () );
		b.append ( ": Count=" );
		b.append ( count );
		b.append ( ", Start=" );
		b.append ( start );
		b.append ( ", End=" );
		b.append ( end );
		
		return b.toString ();
	}
}
