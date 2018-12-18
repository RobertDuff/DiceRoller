package duffrd.diceroller.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utility.join.Join;
import utility.lua.Function;

public class Roller
{
    public static class Expression
    {
        public String definition;
        public Function function;
        
        public Expression ( String def, Function fn )
        {
            definition = def;
            function = fn;
        }
    }
    
	static Random random = new Random();
	
	String groupName;
	String rollerName;
	String definition;

	Globals lua;
	Function expression;
	boolean booleanOutcome;
	
	List<Dice> dice = new ArrayList<>();
	
	Map<String,Expression> triggers = new HashMap<>();
	Map<Object,String> labels = new HashMap<>();
	
	private StringProperty outcomeProperty = new SimpleStringProperty ();
	private StringProperty triggersProperty = new SimpleStringProperty ();
    
    public String group()
    {
        return groupName;
    }
    
    public String name()
    {
        return rollerName;
    }
	
	public String definition()
	{
		return definition;
	}
	
	public ReadOnlyStringProperty outcomeProperty()
	{
		return outcomeProperty;
	}
	
	public ReadOnlyStringProperty triggersProperty()
	{
		return triggersProperty;
	}
	
	public void roll()
	{
		StringBuilder historyFacesBuilder = new StringBuilder();
		// Roll Each Dice
				
		DieNameSequence seq = new DieNameSequence();
		
		for ( Dice die : dice )
		{
			DiceOutcome diceOutcome = die.roll ();
			historyFacesBuilder.append ( diceOutcome.faces.toString () );
		
			lua.set ( seq.next(), diceOutcome.outcome );
		}
		
		// Calculate Outcome
		LuaValue rawOutcome = expression.call();
		lua.set ( "OUTCOME", rawOutcome );
		
		// Get Outcome String
		String outcome = rawOutcome.tojstring();
		
		if ( booleanOutcome && labels.containsKey ( rawOutcome.toboolean() ) )
			outcome = labels.get ( rawOutcome.toboolean() );
		
		else if ( !booleanOutcome && labels.containsKey ( rawOutcome.toint() ) )
			outcome = labels.get ( rawOutcome.toint() );
		
		List<String> triggerList = new ArrayList<>();
		
		for ( String triggerName : triggers.keySet () )
			if ( ( Boolean ) triggers.get ( triggerName ).function.call().toboolean() == true )
				triggerList.add ( triggerName );
		
		String triggerString = Join.join ( ", ", triggerList );
		
		History.history ().record ( rollerName, outcome, triggerString, historyFacesBuilder.toString () );
		
		outcomeProperty.set ( outcome );
		triggersProperty.set ( triggerString );
	}
	
	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder ();
		b.append ( "Roller: '" + rollerName + "' -- {" + definition + "}\n" );
		
		b.append ( "\tExpression: " + expression.toString() + "\n" );
		
		b.append ( "\tDice:\n" );
		
		for ( Dice d : dice )
			b.append ( "\t\t'" + d + "': " + d.toString () + "\n" );
		
		b.append ( "\tVariables:\n" );
		
		b.append ( "\tLabels:\n" );
		
		for ( Object value : labels.keySet () )
			b.append ( "\t\t'" + value.toString () + "' -> '" + labels.get ( value ) + "'\n" );
		
		b.append ( "\tTriggers:\n" );
		
		for ( String trigger : triggers.keySet () )
			b.append ( "\t\t'" + trigger + "' -> '" + triggers.get ( trigger ).definition + "'" );
		
		return b.toString ();
	}
}
