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
import utility.arrays.ArrayConverter;
import utility.join.Join;
import utility.lua.Function;
import utility.lua.VarargsBuilder;

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
	
	List<Dice> dice = new ArrayList<>();
	
	Map<String,Expression> triggers = new HashMap<>();
	Map<Integer,String> labels = new HashMap<>();
	
	private StringProperty outcomeProperty = new SimpleStringProperty ();
	private StringProperty triggersProperty = new SimpleStringProperty ();
    
	private long[] probabilities;
	
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
	
	public Map<Integer,String> labels()
	{
	    return labels;
	}
	
	public long[] probabilities ( boolean recalculate )
	{
        if ( probabilities == null || recalculate )
        {
            Map<Integer,Long> prob = new HashMap<>();
            
            int rolls[] = new int[ dice.size () ];
            
            genProbProcessor ( prob, rolls, 1L, 0 );
            
            int maxOutcome = prob.keySet ().stream ().max ( Integer::compare ).get ();
            
            probabilities = new long[ maxOutcome+1 ];
            
            for ( int outcome : prob.keySet () )
                probabilities[ outcome ] = prob.get ( outcome );
        }
        
        return probabilities;
	}
	
	public long[] probabilities()
	{
	    return probabilities ( false );
	}
	
	private void genProbProcessor ( Map<Integer,Long> prob, int[] rolls, long count, int num )
	{
	    if ( num == dice.size () )
	    {
	        LuaValue l = expression.call ( ArrayConverter.objectArray ( rolls ) );
	        int outcome = l.isboolean ()? ( l.toboolean ()? 1 : 0 ) : l.toint ();
	        
	        if ( !prob.containsKey ( outcome ) ) prob.put ( outcome, 0L );
	        prob.put ( outcome, prob.get ( outcome ) + count );
	        
	        return;
	    }
	    
	    long[] dp = dice.get ( num ).probabilities ();
	    for ( int o = 0; o < dp.length; o++ )
	    {
	        if ( dp[ o ] == 0 )
	            continue;
	        
	        rolls[ num ] = o;
	        genProbProcessor ( prob, rolls, count * dp[ o ], num+1 );
	    }
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
						
		VarargsBuilder outcomeArgs = new VarargsBuilder();
		
		for ( Dice die : dice )
		{
			DiceOutcome diceOutcome = die.roll ();
			historyFacesBuilder.append ( diceOutcome.faces.toString () );
		
			outcomeArgs.add ( diceOutcome.outcome );
		}
		
		// Calculate Outcome
		LuaValue luaOutcome = expression.call ( outcomeArgs.build () );
		int rawOutcome = luaOutcome.isboolean ()? ( luaOutcome.toboolean ()? 1 : 0 ) : luaOutcome.toint ();
		
		// Get Outcome String
		String outcome = String.valueOf ( rawOutcome );
		
		if ( labels.containsKey ( rawOutcome ) )
			outcome = labels.get ( rawOutcome );
		
		//
		// Process Triggers
		//
		
        VarargsBuilder triggerArgs = new VarargsBuilder ();
        triggerArgs.add ( luaOutcome ).add ( outcomeArgs.build () );

        List<String> triggerList = new ArrayList<>();
		
		for ( String triggerName : triggers.keySet () )
			if ( ( Boolean ) triggers.get ( triggerName ).function.call ( triggerArgs.build() ).toboolean() == true )
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
