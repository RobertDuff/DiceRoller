package duffrd.diceroller.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import utility.arrays.ArrayConverter;

public class DiceRollTest
{
	public static final int NUM_ROLLS = 100_000;

	@Before
	public void before()
	{
		Roller.random = new Random ( 47473889938L );
	}

	@Test
	public void testSimple()
	{
		Dice dice = new Dice ( new Die ( 6 ), 1 );
		
		DiceOutcome outcome;
		
		outcome = dice.roll ();
		
		assertEquals ( 2, outcome.outcome );
		assertArrayEquals ( new int[] { 2 }, ArrayConverter.intArray ( outcome.faces ) );
		assertArrayEquals ( new int[] { 2 }, ArrayConverter.intArray ( outcome.selectedFaces ) );
		
		outcome = dice.roll ();
		outcome = dice.roll ();
		
		assertEquals ( 4, outcome.outcome );
		assertArrayEquals ( new int[] { 4 }, ArrayConverter.intArray ( outcome.faces ) );
		assertArrayEquals ( new int[] { 4 }, ArrayConverter.intArray ( outcome.selectedFaces ) );
		
		outcome = dice.roll ();
		
		assertEquals ( 3, outcome.outcome );
		assertArrayEquals ( new int[] { 3 }, ArrayConverter.intArray ( outcome.faces ) );
		assertArrayEquals ( new int[] { 3 }, ArrayConverter.intArray ( outcome.selectedFaces ) );
	}
	
	@Test
	public void testStandard()
	{
		Dice dice = new Dice ( new Die ( 8 ), 5, 1, 4 );

		DiceOutcome outcome;

		outcome = dice.roll ();

		assertEquals ( 15, outcome.outcome );
		assertArrayEquals ( new int[] { 4, 4, 5, 6, 6 }, ArrayConverter.intArray ( outcome.faces ) );
		assertArrayEquals ( new int[] { 4, 5, 6 }, ArrayConverter.intArray ( outcome.selectedFaces ) );

		outcome = dice.roll ();
		outcome = dice.roll ();

		assertEquals ( 15, outcome.outcome );
		assertArrayEquals ( new int[] { 2, 4, 5, 6, 7 }, ArrayConverter.intArray ( outcome.faces ) );
		assertArrayEquals ( new int[] { 4, 5, 6 }, ArrayConverter.intArray ( outcome.selectedFaces ) );

		outcome = dice.roll ();
		outcome = dice.roll ();
		outcome = dice.roll ();

		assertEquals ( 10, outcome.outcome );
		assertArrayEquals ( new int[] { 1, 1, 3, 6, 7 }, ArrayConverter.intArray ( outcome.faces ) );
		assertArrayEquals ( new int[] { 1, 3, 6 }, ArrayConverter.intArray ( outcome.selectedFaces ) );
	}

	@Test
	public void test3d6()
	{
		Dice dice = new Dice ( new Die ( 6 ), 3 );

		int[] rolls = new int[ 19 ];
		Arrays.fill ( rolls, 0 );

		for ( int n=0; n<NUM_ROLLS; n++ )
		{
			int roll = dice.roll ().outcome;
			rolls[ roll ]++;
		}

		assertEquals (     0, rolls[  0 ] );
		assertEquals (     0, rolls[  1 ] );
		assertEquals (     0, rolls[  2 ] );
		assertEquals (   470, rolls[  3 ] );
		assertEquals (  1408, rolls[  4 ] );
		assertEquals (  2780, rolls[  5 ] );
		assertEquals (  4593, rolls[  6 ] );
		assertEquals (  6910, rolls[  7 ] );
		assertEquals (  9706, rolls[  8 ] );
		assertEquals ( 11714, rolls[  9 ] );
		assertEquals ( 12343, rolls[ 10 ] );
		assertEquals ( 12666, rolls[ 11 ] );
		assertEquals ( 11464, rolls[ 12 ] );
		assertEquals (  9684, rolls[ 13 ] );
		assertEquals (  6977, rolls[ 14 ] );
		assertEquals (  4587, rolls[ 15 ] );
		assertEquals (  2814, rolls[ 16 ] );
		assertEquals (  1416, rolls[ 17 ] );
		assertEquals (   468, rolls[ 18 ] );
	}

	@Test
	public void testWeighted ()
	{
		Map<Integer,Integer> dist = new HashMap<>();
		
		dist.put ( 3, 1 );
		dist.put ( 8, 1 );
		dist.put ( 1, 2 );

		Dice dice = new Dice ( new Die ( dist ), 5, 1, 4 );

		DiceOutcome outcome;

		outcome = dice.roll ();

		assertEquals ( 7, outcome.outcome );
		assertArrayEquals ( new int[] { 1, 1, 3, 3, 3 }, ArrayConverter.intArray ( outcome.faces ) );
		assertArrayEquals ( new int[] { 1, 3, 3 }, ArrayConverter.intArray ( outcome.selectedFaces ) );

		outcome = dice.roll ();

		assertEquals ( 12, outcome.outcome );
		assertArrayEquals ( new int[] { 1, 1, 3, 8, 8 }, ArrayConverter.intArray ( outcome.faces ) );
		assertArrayEquals ( new int[] { 1, 3, 8 }, ArrayConverter.intArray ( outcome.selectedFaces ) );

		outcome = dice.roll ();
		outcome = dice.roll ();

		assertEquals ( 19, outcome.outcome );
		assertArrayEquals ( new int[] { 1, 3, 8, 8, 8 }, ArrayConverter.intArray ( outcome.faces ) );
		assertArrayEquals ( new int[] { 3, 8, 8 }, ArrayConverter.intArray ( outcome.selectedFaces ) );
	}
}
