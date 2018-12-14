package duffrd.diceroller.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class DieRollTest
{
	public static final int NUM_ROLLS = 100_000;
	
	@Before
	public void before()
	{
		Roller.random = new Random ( 47473889938L );
	}
	
	@Test
	public void testStandard()
	{
		Die die = new Die ( 6 );
		
		int[] rolls = new int[ 7 ];
		Arrays.fill ( rolls, 0 );
		
		for ( int n=0; n<NUM_ROLLS; n++ )
		{
			int roll = die.roll ();
			rolls[ roll ]++;
		}
		
		assertEquals (     0, rolls[ 0 ] );
		assertEquals ( 16772, rolls[ 1 ] );
		assertEquals ( 16759, rolls[ 2 ] );
		assertEquals ( 16701, rolls[ 3 ] );
		assertEquals ( 16532, rolls[ 4 ] );
		assertEquals ( 16543, rolls[ 5 ] );
		assertEquals ( 16693, rolls[ 6 ] );
	}
	
	@Test
	public void testWeighted ()
	{
		Map<Integer,Integer> dist = new HashMap<>();
		
		dist.put ( 1, 3 );
		dist.put ( 3, 1 );
		dist.put ( 4, 6 );
		
		Die die = new Die ( dist );
		
		int[] rolls = new int[ 5 ];
		Arrays.fill ( rolls, 0 );
		
		for ( int n=0; n<NUM_ROLLS; n++ )
		{
			int roll = die.roll ();
			rolls[ roll ]++;
		}
				
		
		assertEquals (     0, rolls[ 0 ] );
		assertEquals ( 30163, rolls[ 1 ] );
		assertEquals (     0, rolls[ 2 ] );
		assertEquals (  9988, rolls[ 3 ] );
		assertEquals ( 59849, rolls[ 4 ] );
	}
}
