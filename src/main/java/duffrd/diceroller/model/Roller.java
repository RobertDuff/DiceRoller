package duffrd.diceroller.model;

import java.lang.invoke.MethodHandles;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import utility.lua.Function;
import utility.lua.VarargsBuilder;

public class Roller
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

    static Random random = new Random();

    private ObjectProperty<Globals> luaProperty = new SimpleObjectProperty<> ();
    protected StringProperty nameProperty = new SimpleStringProperty ();
    protected StringProperty definitionProperty = new SimpleStringProperty ();
    private ObjectProperty<Function> functionProperty = new SimpleObjectProperty<> ();
    private BooleanProperty validProperty = new SimpleBooleanProperty ();

    private ObservableList<Dice> diceProperty = FXCollections.observableArrayList ();

    protected ObservableSet<Trigger> triggersProperty = FXCollections.observableSet ( new HashSet<>() );
    protected ObservableMap<Integer,String> labelsProperty = FXCollections.observableHashMap ();
    
    private ObjectProperty<Outcome> outcomeProperty = new SimpleObjectProperty<> ();

    private DoubleProperty progressProperty = new SimpleDoubleProperty();
    private BooleanProperty canceledProperty = new SimpleBooleanProperty ();

    private long[] rawProbabilities;
    private ObservableMap<String,Long> probabilitiesProperty = FXCollections.observableHashMap ();

    public Roller()
    {
        functionProperty.bind ( Bindings.createObjectBinding ( () -> parseDefinition (), luaProperty, definitionProperty ) );
        validProperty.bind ( Bindings.isNotNull ( functionProperty ) );
    }
    
    public Globals lua()
    {
        return luaProperty.get ();
    }

    public Roller lua ( Globals lua )
    {
        luaProperty.set ( lua );
        return this;
    }

    public String name()
    {
        return nameProperty.get ();
    }

    public Roller name ( String name )
    {
        nameProperty.set ( name );
        return this;
    }

    public String definition()
    {
        return definitionProperty.get ();
    }

    public Roller definition ( String definition )
    {
        definitionProperty.set ( definition );
        return this;
    }

    public Map<Integer,String> labels()
    {
        return labelsProperty;
    }

    public Set<Trigger> triggers()
    {
        return triggersProperty;
    }

    public boolean isValid()
    {
        return validProperty.get ();
    }

    public Outcome lastOutcome()
    {
        return outcomeProperty.get ();
    }
    
    public long[] rawProbabilities() throws ProbablityCalculationCancelledException
    {
        calculateProbabilities ();
        return rawProbabilities;
    }

    public Map<String,Long> probabilities()
    {
        return Collections.unmodifiableMap ( probabilitiesProperty );
    }

    public ObjectProperty<Globals> luaProperty()
    {
        return luaProperty;
    }

    public StringProperty nameProperty()
    {
        return nameProperty;
    }

    public StringProperty definitionProperty()
    {
        return definitionProperty;
    }

    public ReadOnlyBooleanProperty validProperty()
    {
        return validProperty;
    }

    public ObservableSet<Trigger> triggersProperty()
    {
        return triggersProperty;
    }

    public ObservableMap<Integer,String> labelsProperty()
    {
        return labelsProperty;
    }
    
    public ReadOnlyObjectProperty<Outcome> outcomeProperty()
    {
        return outcomeProperty;
    }

    public DoubleProperty progressProperty()
    {
        return progressProperty;
    }

    public BooleanProperty canceledProperty()
    {
        return canceledProperty;
    }

    public ObservableMap<String,Long> probabilitiesProperty()
    {
        return FXCollections.unmodifiableObservableMap ( probabilitiesProperty );
    }

    public Outcome roll()
    {
        Outcome outcome = new Outcome();
        outcome.roller ( nameProperty.get () );

        // Roll Each Dice

        VarargsBuilder outcomeArgs = new VarargsBuilder();

        for ( Dice die : diceProperty )
        {
            DiceOutcome diceOutcome = die.roll ();
            outcomeArgs.add ( diceOutcome.outcome );
            outcome.addFaces ( diceOutcome.faces );
        }

        // Calculate Outcome
        LuaValue luaOutcome = functionProperty.get ().call ( outcomeArgs.build () );
        int rawOutcome = luaOutcome.isboolean ()? ( luaOutcome.toboolean ()? 1 : 0 ) : luaOutcome.toint ();

        // Get Outcome String

        if ( labelsProperty.containsKey ( rawOutcome ) )
            outcome.outcome ( labelsProperty.get ( rawOutcome ) );
        else
            outcome.outcome ( String.valueOf ( rawOutcome ) );

        //
        // Process Triggers
        //

        VarargsBuilder triggerArgsBuilder = new VarargsBuilder ();
        triggerArgsBuilder.add ( luaOutcome ).add ( outcomeArgs.build () );
        Varargs triggerArgs = triggerArgsBuilder.build ();

        for ( Trigger trigger : triggersProperty )
            if ( trigger.fire ( triggerArgs ) )
                outcome.addTrigger ( trigger );

        outcomeProperty.set ( outcome );
        
        return outcome;
    }

    public void calculateProbabilities() throws ProbablityCalculationCancelledException
    {
        calculateProbabilities ( false );
    }

    public void calculateProbabilities ( boolean recalculate ) throws ProbablityCalculationCancelledException
    {
        if ( recalculate )
        {
            logger.debug ( "Recalculating Probabilities" );
            rawProbabilities = null;
            probabilitiesProperty.clear ();
            for ( Dice d : diceProperty ) d.probabilities = null;
        }

        if ( probabilitiesProperty.isEmpty () )
        {
            if ( !validProperty.get () )
                return;
            
            logger.debug ( "Starting Probability Calculation" );
            
            progressProperty.setValue ( 0.3 );

            Map<Integer,Long> prob = new HashMap<>();

            int rolls[] = new int[ diceProperty.size () ];

            genProbProcessor ( prob, rolls, 1L, 0 );

            int maxOutcome = prob.keySet ().stream ().max ( Integer::compare ).get ();

            rawProbabilities = new long[ maxOutcome+1 ];

            for ( int outcome : prob.keySet () )
                rawProbabilities[ outcome ] = prob.get ( outcome );

            logger.debug ( "RAW: " + Arrays.stream ( rawProbabilities ).boxed ().map ( l -> String.valueOf ( l ) ).collect ( Collectors.joining ( " ", "[", "]" ) ) );
            
            progressProperty.set ( 1.0 );

            logger.debug ( "Probablity Calculation Complete" );

            probabilitiesProperty.putAll ( 
                    prob.entrySet ()
                    .stream ()
                    .map ( e -> new AbstractMap.SimpleEntry<String,Long> ( labelsProperty.getOrDefault ( e.getKey(), String.valueOf ( e.getKey () ) ), e.getValue () ) )
                    .collect ( Collectors.toMap ( Map.Entry::getKey, Map.Entry::getValue ) ) );
        }
    }

    private void genProbProcessor ( Map<Integer,Long> prob, int[] rolls, long count, int num ) throws ProbablityCalculationCancelledException
    {
        logger.debug ( "Entered Level " + num );

        if ( canceledProperty.getValue () )
            throw new ProbablityCalculationCancelledException ();

        if ( num == diceProperty.size () )
        {
            logger.debug ( "Adding Counts" );

            LuaValue l = functionProperty.get ().call ( new VarargsBuilder().addAll ( Arrays.stream ( rolls ).boxed ().collect ( Collectors.toList () ) ).build () );
            int outcome = l.isboolean ()? ( l.toboolean ()? 1 : 0 ) : l.toint ();

            if ( !prob.containsKey ( outcome ) ) prob.put ( outcome, 0L );
            prob.put ( outcome, prob.get ( outcome ) + count );

            return;
        }

        logger.debug ( "Get DICE Probs Level " + num );

        long[] dp = diceProperty.get ( num ).probabilities ();

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

    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder ();

        b.append ( "Roller: '" + nameProperty.get () + "' -- {" + definitionProperty.get () + "}\n" );

        b.append ( "\tExpression: " + functionProperty.get() + "\n" );

        b.append ( "\tDice:\n" );

        for ( Dice d : diceProperty )
            b.append ( "\t\t" + d + "\n" );

        if ( !labelsProperty.isEmpty () )
        {
            b.append ( "\tLabels:\n" );

            for ( int value : labelsProperty.keySet () )
                b.append ( "\t\t" + value + " -> '" + labelsProperty.get ( value ) + "'\n" );
        }

        if ( !triggersProperty.isEmpty () )
        {
            b.append ( "\tTriggers:\n" );

            for ( Trigger trigger : triggersProperty )
                b.append ( "\t\t" + trigger.toString () );
        }
        
        return b.toString ();
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass () != obj.getClass () ) return false;
        Roller other = ( Roller ) obj;
        if ( nameProperty == null )
        {
            if ( other.nameProperty != null ) return false;
        }
        else if ( !nameProperty.equals ( other.nameProperty ) ) return false;
        return true;
    }

    static final Pattern DICE_REGEX = Pattern.compile ( "(((-?\\d+),)?(\\d+))?[Dd](\\d+|\\[([^\\[]+)\\])" );

    private Function parseDefinition()
    {        
        if ( luaProperty.get () == null )
            return null;

        if ( definitionProperty.get () == null || definitionProperty.get ().isEmpty () )
            return null;

        diceProperty.clear ();

        String functionCode = definitionProperty.get ();

        Matcher matcher;
        char diceVar = 'A';

        while ( ( matcher = DICE_REGEX.matcher ( functionCode ) ).find () )
        {           
            String adjustment = matcher.group ( 3 );
            String numDice = matcher.group ( 4 );
            String faces = matcher.group ( 5 );
            String weights = matcher.group ( 6 );

            Die die;

            if ( weights != null )
            {
                Map<Integer,Integer> distribution = new HashMap<>();

                for ( String weight : weights.trim().split ( "\\s*,\\s*" ) )
                {
                    String[] valCount = weight.trim().split ( "\\s*:\\s*" );

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
            dice.canceledProperty ().bind ( canceledProperty () );

            functionCode = matcher.replaceFirst ( String.valueOf ( diceVar++ ) );
            diceProperty.add ( dice );
        }

        try
        {
            return new Function ( luaProperty.get (), 
                    "local __ARGS__ = { ... }",
                    "local A = __ARGS__[  1 ]",
                    "local B = __ARGS__[  2 ]",
                    "local C = __ARGS__[  3 ]",
                    "local D = __ARGS__[  4 ]",
                    "local E = __ARGS__[  5 ]",
                    "local F = __ARGS__[  6 ]",
                    "local G = __ARGS__[  7 ]",
                    "local H = __ARGS__[  8 ]",
                    "local I = __ARGS__[  9 ]",
                    "local J = __ARGS__[ 10 ]",
                    "return ", functionCode );
        }
        catch ( LuaError e )
        {
            return null;
        }
    }
}
