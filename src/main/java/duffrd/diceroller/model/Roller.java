package duffrd.diceroller.model;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.luaj.vm2.LuaValue;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utility.arrays.ArrayConverter;
import utility.join.Join;
import utility.lua.Function;
import utility.lua.VarargsBuilder;

public class Roller
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

	static Random random = new Random();
	
	String rollerName;
	String definition;

	Function expression;
	
	List<Dice> dice = new ArrayList<>();
	
	Map<String,Trigger> triggers = new HashMap<>();
	Map<Integer,String> labels = new HashMap<>();
	
	private StringProperty outcomeProperty = new SimpleStringProperty ();
	private StringProperty triggersProperty = new SimpleStringProperty ();
    private DoubleProperty progressProperty = new SimpleDoubleProperty();
    private BooleanProperty canceledProperty = new SimpleBooleanProperty ();
    
	private long[] probabilities;
	
    public String name()
    {
        return rollerName;
    }
	
	public void name ( String name )
    {
        rollerName = name; 
    }

    public String definition()
	{
		return definition;
	}
	
	public Map<Integer,String> labels()
	{
	    return labels;
	}
	
	public long[] probabilities ( boolean recalculate ) throws ProbablityCalculationCancelledException
	{
	    if ( recalculate )
	    {
	        logger.info ( "Recalculating Probabilities" );
	        probabilities = null;
	        for ( Dice d : dice ) d.probabilities = null;
	    }
	    
        if ( probabilities == null )
        {
            logger.debug ( "Starting Probability Calculation" );
            
            progressProperty.setValue ( 0.3 );
                        
            Map<Integer,Long> prob = new HashMap<>();
            
            int rolls[] = new int[ dice.size () ];
            
            genProbProcessor ( prob, rolls, 1L, 0 );
                        
            int maxOutcome = prob.keySet ().stream ().max ( Integer::compare ).get ();
            
            probabilities = new long[ maxOutcome+1 ];
            
            for ( int outcome : prob.keySet () )
                probabilities[ outcome ] = prob.get ( outcome );
            
            progressProperty.set ( 1.0 );
            
            logger.debug ( "Probablity Calculation Complete" );
        }
        
        return probabilities;
	}
	
	public long[] probabilities() throws ProbablityCalculationCancelledException
	{
	    return probabilities ( false );
	}
	
	private void genProbProcessor ( Map<Integer,Long> prob, int[] rolls, long count, int num ) throws ProbablityCalculationCancelledException
	{
	    logger.debug ( "Entered Level " + num );

	    if ( canceledProperty.getValue () )
	        throw new ProbablityCalculationCancelledException ();
	        
	    if ( num == dice.size () )
	    {
	        logger.debug ( "Adding Counts" );
	        
	        LuaValue l = expression.call ( ArrayConverter.objectArray ( rolls ) );
	        int outcome = l.isboolean ()? ( l.toboolean ()? 1 : 0 ) : l.toint ();
	        
	        if ( !prob.containsKey ( outcome ) ) prob.put ( outcome, 0L );
	        prob.put ( outcome, prob.get ( outcome ) + count );
	        
	        return;
	    }
	    
	    logger.debug ( "Get DICE Probs Level " + num );
	    
	    long[] dp = dice.get ( num ).probabilities ();
	    
	    for ( int o = 0; o < dp.length; o++ )
	    {            
            if ( canceledProperty.getValue () )
                throw new ProbablityCalculationCancelledException ();
	        	            
	        if ( dp[ o ] == 0 )
	            continue;

	        logger.debug ( "Loop: " + num + "." + o );

	        rolls[ num ] = o;
	        genProbProcessor ( prob, rolls, count * dp[ o ], num+1 );
	    }
	    
	    logger.debug ( "Exit Level " + num );
	}
	
	public ReadOnlyStringProperty outcomeProperty()
	{
		return outcomeProperty;
	}
	
	public ReadOnlyStringProperty triggersProperty()
	{
		return triggersProperty;
	}
		
    public DoubleProperty progressProperty()
    {
        return progressProperty;
    }
    
	public BooleanProperty canceledProperty()
	{
	    return canceledProperty;
	}
	
	public void roll()
	{
	    roll ( false );
	}
	
	public void roll ( boolean testMode )
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
		
		if ( !testMode )
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

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass () != obj.getClass () ) return false;
        Roller other = ( Roller ) obj;
        if ( rollerName == null )
        {
            if ( other.rollerName != null ) return false;
        }
        else if ( !rollerName.equals ( other.rollerName ) ) return false;
        return true;
    }
}
