package duffrd.diceroller.model;

public class DieNameSequence
{
	private char variable = 'A';
	
	public String next()
	{
		if ( variable > 'Z' )
			throw new IndexOutOfBoundsException ( "Too Many Dice Have Been Defined for a single Roller" );
		
		return String.valueOf ( variable++ );
	}
}