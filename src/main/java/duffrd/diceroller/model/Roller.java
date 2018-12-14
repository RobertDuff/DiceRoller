package duffrd.diceroller.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import utility.join.Join;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Roller
{
	static final JexlEngine jexlEngine = new JexlEngine();
	
	static Random random = new Random();
	
	String rollerName;
	String definition;
	
	Expression expression;
	JexlContext context = new MapContext();
	
	Map<String,Dice> dice = new HashMap<>();
	Map<String,IntegerProperty> variables = new HashMap<>();
	
	Map<String,Expression> triggers = new HashMap<>();
	Map<Object,String> labels = new HashMap<>();
	
	private StringProperty outcomeProperty = new SimpleStringProperty ();
	private StringProperty triggersProperty = new SimpleStringProperty ();
	
	public Roller()
	{
		//expression = jexlEngine.createExpression ( expr );
	}

	public String name()
	{
		return rollerName;
	}
	
	public String definition()
	{
		return definition;
	}
	
	public Set<String> variables()
	{
		return variables.keySet ();
	}

	public IntegerProperty variableProperty ( String variable )
	{
		return variables.get ( variable );
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
		for ( String dieName : dice.keySet () )
		{
			DiceOutcome diceOutcome = dice.get ( dieName ).roll ();
			historyFacesBuilder.append ( diceOutcome.faces.toString () );
			
			context.set ( dieName, diceOutcome.outcome );
		}
		
		// Update Variables
		for ( String variable : variables.keySet () )
			context.set ( variable, variables.get ( variable ).get () );
		
		// Calculate Outcome
		Object rawOutcome = expression.evaluate ( context );
		
		// Get Outcome String
		String outcome;
		
		if ( labels.containsKey ( rawOutcome ) )
			outcome = labels.get ( rawOutcome );
		else
			outcome = rawOutcome.toString ();
		
		// Insert Outcome into Context for Trigger Evaluation
		context.set ( "OUTCOME", rawOutcome );
		
		List<String> triggerList = new ArrayList<>();
		
		for ( String triggerName : triggers.keySet () )
			if ( ( Boolean ) triggers.get ( triggerName ).evaluate ( context ) == true )
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
		
		b.append ( "\tExpression: " + expression.dump () + "\n" );
		
		b.append ( "\tDice:\n" );
		
		for ( String d : dice.keySet () )
			b.append ( "\t\t'" + d + "': " + dice.get ( d ).toString () + "\n" );
		
		b.append ( "\tVariables:\n" );
		
		for ( String var : variables.keySet () )
			b.append ( "\t\t'" + var + "'\n" );
		
		b.append ( "\tLabels:\n" );
		
		for ( Object value : labels.keySet () )
			b.append ( "\t\t'" + value.toString () + "' -> '" + labels.get ( value ) + "'\n" );
		
		b.append ( "\tTriggers:\n" );
		
		for ( String trigger : triggers.keySet () )
			b.append ( "\t\t'" + trigger + "' -> '" + triggers.get ( trigger ) + "'" );
		
		return b.toString ();
	}
}
