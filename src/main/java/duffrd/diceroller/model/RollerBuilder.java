package duffrd.diceroller.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleIntegerProperty;

public class RollerBuilder
{
	static final Pattern DICE_REGEX     = Pattern.compile ( "(((-?\\d+),)?(\\d+))?[Dd](\\d+|\\[([^\\[]+)\\])" );
	static final Pattern VARIABLE_REGEX = Pattern.compile ( "[A-Za-z]{2,}" );

	static final String NAME_KEY       = "name";
	static final String DEFINITION_KEY = "definition";
	static final String LABELS_KEY     = "labels";
	static final String TRIGGERS_KEY   = "triggers";
	
	private static class DieNameGenerator
	{
		private char variable = 'A';
		
		public String generate()
		{
			if ( variable > 'Z' )
				throw new IndexOutOfBoundsException ( "Too Many Dice Have Been Defined for a single Roller" );
			
			return String.valueOf ( variable++ );
		}
	}
	
	@SuppressWarnings ( "unchecked" )
	public static Roller build ( Map<String,Object> def )
	{
		Roller roller = new Roller();
		
		//
		// Name
		//
		
		if ( !def.containsKey ( NAME_KEY ) )
			return null;
		
		roller.rollerName = ( String ) def.get ( NAME_KEY );
		
		//
		// Definition
		//
		
		if ( !def.containsKey ( DEFINITION_KEY ) )
			return null;
		
		buildDice ( roller, ( String ) def.get ( DEFINITION_KEY ) );
		
		//
		// Labels
		//
		
		if ( def.containsKey ( LABELS_KEY ) )
			buildLabels ( roller, ( Map<Object,String> ) def.get ( LABELS_KEY ) );
		
		//
		// Triggers
		//
		
		if ( def.containsKey ( TRIGGERS_KEY ) )
			buildTriggers ( roller, ( Map<String,String> ) def.get ( TRIGGERS_KEY ) );
		
		return roller;
	}
	
	private static void buildDice ( Roller roller, String definition )
	{
		Matcher matcher;

		roller.definition = definition;

		//
		// Extract Variables (Prior to Dice Name Substitution Below)
		//
		matcher = VARIABLE_REGEX.matcher ( definition );
		
		while ( matcher.find() )
			roller.variables.put ( matcher.group (), new SimpleIntegerProperty ( 0 ) );

		//
		// Extract Dice
		//
		
		DieNameGenerator dieNameGenerator = new DieNameGenerator ();
		
		while ( ( matcher = DICE_REGEX.matcher ( definition ) ).find () )
		{			
			String adjustment = matcher.group ( 3 );
			String numDice = matcher.group ( 4 );
			String faces = matcher.group ( 5 );
			String weights = matcher.group ( 6 );
			
			Die die;
			
			if ( weights != null )
			{
				Map<Integer,Integer> distribution = new HashMap<>();
				
				for ( String weight : weights.split ( "\\s*,\\s*" ) )
				{
					String[] valCount = weight.split ( "\\s*:\\s*" );
					
					if ( valCount.length == 2 )
					{
						int face = Integer.valueOf ( valCount[ 0 ] );
						int count = Integer.valueOf ( valCount[ 1 ] );
						
						distribution.put ( face, count );
					}
					else
					{
						int face = Integer.valueOf ( weight );
						distribution.put ( face, 1 );
					}
				}
				
				die = new Die ( distribution );
			}
			else
				die = new Die ( Integer.valueOf ( faces ) );
			
			int num = 0;
			
			if ( numDice != null )
				num = Integer.valueOf ( numDice );
			else
				num = 1;
			
			int start = 0;
			int end = num;
			
			if ( adjustment != null )
			{
				int adjust = Integer.valueOf ( adjustment );
				
				if ( adjust > 0 )
					start += num - adjust;
				else
					end = -adjust;
			}
			
			Dice dice = new Dice ( die, num, start, end );
			
			String dieName = dieNameGenerator.generate ();
			
			definition = definition.replace ( matcher.group (), dieName );
			roller.dice.put ( dieName, dice );
		}
		
		roller.expression = Roller.jexlEngine.createExpression ( definition );
	}

	private static void buildLabels ( Roller roller, Map<Object,String> labels )
	{
		roller.labels.putAll ( labels );
	}
	
	private static void buildTriggers ( Roller roller, Map<String,String> triggers )
	{
		for ( String trigger : triggers.keySet () )
			roller.triggers.put ( trigger, Roller.jexlEngine.createExpression ( triggers.get ( trigger ) ) );
	}
	
	private RollerBuilder () {}
}
