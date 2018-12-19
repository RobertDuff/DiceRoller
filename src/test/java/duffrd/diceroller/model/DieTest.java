package duffrd.diceroller.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith ( Parameterized.class )
public class DieTest
{
	public Integer faces;
	public Map<Integer,Integer> distribution;
	public int expectedFaces;
	public List<Integer> expectedRange;
	public long[] expectedPropbabilities;
	
	public DieTest ( Integer f, Map<Integer,Integer> d, int ef, List<Integer> er, long[] ep )
	{
		faces                  = f;
		distribution           = d;
		expectedFaces          = ef;
		expectedRange          = er;
		expectedPropbabilities = ep;
	}
	
	@Test
	public void test ()
	{
		Die die;
		
		if ( faces != null )
			die = new Die ( faces );
		else
			die = new Die ( distribution );
		
		assertEquals ( "Faces", expectedFaces, die.faces );
		assertEquals ( "Range", expectedRange, die.range );
		assertArrayEquals ( "Probs", expectedPropbabilities, die.probabilities );
	}
	
	@Parameters()
	public static Object[] parameters()
	{
		List<Object[]> parameters = new ArrayList<>();
		
		// d4
		parameters.add ( new Object[]
				{
					4, 
					null, 
					4, 
					Arrays.asList ( 1, 2, 3, 4 ), 
					new long[] { 0, 1, 1, 1, 1 }	
				} );
		
		// d6
		parameters.add ( new Object[]
				{
					6, 
					null, 
					6, 
					Arrays.asList ( 1, 2, 3, 4, 5, 6 ), 
					new long[] { 0, 1, 1, 1, 1, 1, 1 }	
				} );
		
		// d10
		parameters.add ( new Object[]
				{
					10, 
					null, 
					10, 
					Arrays.asList ( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ), 
					new long[] { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }	
				} );
		
		// Weighted
		parameters.add ( new Object[]
				{
					null, 
					distMap ( 3, 8 ), 
					8, 
					Arrays.asList ( 3 ), 
					new long[] { 0, 0, 0, 8 }	
				} );
		
		// Weighted
		parameters.add ( new Object[]
				{
					null, 
					distMap ( 1, 3, 2, 2, 3, 1 ), 
					6, 
					Arrays.asList ( 1, 2, 3 ), 
					new long[] { 0, 3, 2, 1 }	
				} );
		
		// Weighted
		parameters.add ( new Object[]
				{
					null, 
					distMap ( 0, 123, 15, 76, 33, 2009 ), 
					123+76+2009, 
					Arrays.asList ( 0, 15, 33 ), 
					new long[] { 123, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 76, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2009 }	
				} );
		
		return parameters.toArray ();
	}
	
	private static Map<Integer,Integer> distMap ( int...dist )
	{
		Map<Integer,Integer> map = new HashMap<>();
		
		for ( int i=0; i<dist.length; i+=2 )
			map.put ( dist[ i ], dist[ i+1 ] );
		
		return map;
	}
}
