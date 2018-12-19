package duffrd.diceroller.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class DiceTest
{
	public static Die D6;
	public static Die D4;
	public static Die WEIGHTED;	
	
	@BeforeClass
	public static void beforeClass()
	{
		D6 = new Die ( 6 );
		D4 = new Die ( 4 );
		
		Map<Integer,Integer> weightedDistribution = new HashMap<>();
		
		weightedDistribution.put ( 2, 3 );
		weightedDistribution.put ( 5, 5 );
		weightedDistribution.put ( 7, 1 );
		
		WEIGHTED = new Die ( weightedDistribution );		
	}
	
	@Test
	public void test1d6 ()
	{
		Dice dice = new Dice ( D6, 1 );
		
		assertEquals ( "Range", Arrays.asList ( 1, 2, 3, 4, 5, 6 ), dice.range );
		assertArrayEquals ( "Prob", new long[] { 0, 1, 1, 1, 1, 1, 1 }, dice.probabilities() );
	}
	
	@Test
	public void test2d6 ()
	{
		Dice dice = new Dice ( D6, 2 );
		
		assertEquals ( "Range", Arrays.asList ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ), dice.range );
		assertArrayEquals ( "Prob", new long[] { 0, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1 }, dice.probabilities() );
	}
	
	@Test
	public void test3d6 ()
	{
		Dice dice = new Dice ( D6, 3 );
		
		assertEquals ( "Range", Arrays.asList ( 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 ), dice.range );
		assertArrayEquals ( "Prob", new long[] { 0, 0, 0, 1, 3, 6, 10, 15, 21, 25, 27, 27, 25, 21, 15, 10, 6, 3, 1 }, dice.probabilities() );
	}
	
	@Test
	public void test1d4 ()
	{
		Dice dice = new Dice ( D4, 1 );
		
		assertEquals ( "Range", Arrays.asList ( 1, 2, 3, 4 ), dice.range );
		assertArrayEquals ( "Prob", new long[] { 0, 1, 1, 1, 1 }, dice.probabilities() );
	}	
	
	@Test
	public void test2d4 ()
	{
		Dice dice = new Dice ( D4, 2 );
		
		assertEquals ( "Range", Arrays.asList ( 2, 3, 4, 5, 6, 7, 8 ), dice.range );
		assertArrayEquals ( "Prob", new long[] { 0, 0, 1, 2, 3, 4, 3, 2, 1 }, dice.probabilities() );
	}	
	
	@Test
	public void test3d4 ()
	{
		Dice dice = new Dice ( D4, 3 );
		
		assertEquals ( "Range", Arrays.asList ( 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ), dice.range );
		assertArrayEquals ( "Prob", new long[] { 0, 0, 0, 1, 3, 6, 10, 12, 12, 10, 6, 3, 1 }, dice.probabilities() );
	}	
	
	@Test
	public void test2of3d6()
	{
		Dice dice = new Dice ( D6, 3, 1, 3 );
		
		assertEquals ( "Range", Arrays.asList ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ), dice.range );		
		assertArrayEquals ( "Prob", new long[] { 0, 0, 1, 3, 7, 12, 19, 27, 34, 36, 34, 27, 16 }, dice.probabilities() );
	}
	
	@Test
	public void test2of4d6()
	{
		Dice dice = new Dice ( D6, 4, 2, 4 );
		
		assertEquals ( "Range", Arrays.asList ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ), dice.range );		
		assertArrayEquals ( "Prob", new long[] { 0, 0, 1, 4, 15, 32, 65, 108, 171, 224, 261, 244, 171 }, dice.probabilities() );
	}
	
	@Test
	public void testLow3of4d6()
	{
		Dice dice = new Dice ( D6, 4, 0, 3 );
		
		assertEquals ( "Range", Arrays.asList ( 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 ), dice.range );		
		assertArrayEquals ( "Prob", new long[] { 0, 0, 0, 21, 54, 94, 131, 160, 172, 167, 148, 122, 91, 62, 38, 21, 10, 4, 1 }, dice.probabilities() );
	}
	
	@Test
	public void test1DW()
	{
		Dice dice = new Dice ( WEIGHTED, 1 );
		
		assertEquals ( "Range", Arrays.asList ( 2, 5, 7 ), dice.range );
		assertArrayEquals ( "Prob", new long[] { 0, 0, 3, 0, 0, 5, 0, 1 }, dice.probabilities() );
	}
	
	@Test
	public void test2DW()
	{
		Dice dice = new Dice ( WEIGHTED, 2 );
		
		assertEquals ( "Range", Arrays.asList ( 4, 7, 9, 10, 12, 14 ), dice.range );
		assertArrayEquals ( "Prob", new long[] { 0, 0, 0, 0, 9, 0, 0, 30, 0, 6, 25, 0, 10, 0, 1 }, dice.probabilities() );
	}
    
    @Test
    public void test3DW()
    {
        Dice dice = new Dice ( WEIGHTED, 3 );
        
        assertEquals ( "Range", Arrays.asList ( 6, 9, 11, 12, 14, 15, 16, 17, 19, 21 ), dice.range );
        assertArrayEquals ( "Prob", new long[] { 
                  0, 
                  0,   0,   0,   0,   0, 
                 27,   0,   0, 135,   0, 
                 27, 225,   0,  90, 125, 
                  9,  75,   0,  15,   0,
                  1 
                }, dice.probabilities() );
    }
    
    @Test
    public void test2of3DW()
    {
        Dice dice = new Dice ( WEIGHTED, 3, 1, 3 );
        
        assertEquals ( "Range", Arrays.asList ( 4, 7, 9, 10, 12, 14 ), dice.range );
        assertArrayEquals ( "Prob", new long[] { 0, 0, 0, 0, 27, 0, 0, 135, 0, 27, 350, 0, 165, 0, 25 }, dice.probabilities () );
    }
}
