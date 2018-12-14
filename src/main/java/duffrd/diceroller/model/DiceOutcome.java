package duffrd.diceroller.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiceOutcome
{
	Dice dice;
	List<Integer> faces = new ArrayList<>();
	List<Integer> selectedFaces;
	int outcome;
	
	public DiceOutcome ( Dice dice )
	{
		this.dice = dice;

		for ( int i=0; i<dice.count; i++ )
			faces.add ( dice.die.roll () );
		
		Collections.sort ( faces );
		
		selectedFaces = faces.subList ( dice.start, dice.end );
		
		outcome = selectedFaces.stream ().mapToInt ( Integer::intValue ).sum ();
	}
}
