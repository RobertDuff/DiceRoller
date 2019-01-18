package duffrd.diceroller.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Random;
import java.util.concurrent.atomic.LongAccumulator;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.luaj.vm2.Globals;

import utility.lua.LuaProvider;

public class RollerTest
{
    public static final Globals lua = LuaProvider.lua ( "x" );
    
    @Before
    public void before()
    {
        Roller.random = new Random ( 47473889938L );
    }

    @Test
    public void testIdenticalOutcomes()
    {
        LongAccumulator acc = new LongAccumulator ( ( a, b ) -> a+b, 0 );
        
        Roller roller = new Roller().lua ( lua ).name ( "Trivial" ).definition ( "1" );
        roller.outcomeProperty ().addListener ( ( a, o, n ) -> acc.accumulate ( 1 ) );
        
        Outcome lastOutcome = null;
        
        for ( int n = 0; n < 100; n++ )
        {
            Outcome outcome = roller.roll ();
            
            if ( lastOutcome == null )
                lastOutcome = outcome;
            else
            {
                if ( !lastOutcome.outcome ().equals ( outcome.outcome () ) )
                    fail ( "Outcomes Differ: " + lastOutcome.outcome () + " != " + outcome.outcome () );
                
                if ( !lastOutcome.triggers ().equals ( outcome.triggers () ) )
                    fail ( "Triggers Differ: " + lastOutcome.triggers () + " != " + outcome.triggers () );
                
                if ( !lastOutcome.faces ().equals ( outcome.faces () ) )
                    fail ( "Faces Differ: " + lastOutcome.faces () + " != " + outcome.faces () );
            }
        }
        
        assertEquals ( 100, acc.get () );
    }
    
    @Test
    public void testSimpleInteger () throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua  ).name ( "Simple" ).definition ( "d6" );
        
        assertArrayEquals ( new long[] { 0, 1, 1, 1, 1, 1, 1 }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "2",     outcome.outcome() );
        assertEquals ( "",      outcome.triggers() );
        assertEquals ( "[ 2 ]", outcome.faces() );

        roller.roll ();
        outcome = roller.roll ();

        assertEquals ( "4",     outcome.outcome() );
        assertEquals ( "",      outcome.triggers() );
        assertEquals ( "[ 4 ]", outcome.faces() );

        outcome = roller.roll ();

        assertEquals ( "3",     outcome.outcome() );
        assertEquals ( "",      outcome.triggers() );
        assertEquals ( "[ 3 ]", outcome.faces() );
    }

    @Test
    public void testSimpleBoolean () throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "Bool" ).definition ( "d20 > 10" );		

        assertArrayEquals ( new long[] { 10, 10 }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "0",     outcome.outcome() );
        assertEquals ( "",      outcome.triggers() );
        assertEquals ( "[ 4 ]", outcome.faces() );

        roller.roll ();
        roller.roll ();
        outcome = roller.roll ();

        assertEquals ( "1",     outcome.outcome() );
        assertEquals ( "",      outcome.triggers() );
        assertEquals ( "[ 13 ]", outcome.faces() );
    }

    @Test
    public void testSuccess () throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "Test" ).definition ( "d20 > 10" );
        roller.labelsProperty().put ( 0, "Failure" );
        roller.labelsProperty().put ( 1,  "Success" );

        assertArrayEquals ( new long[] { 10, 10 }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "Failure", outcome.outcome() );
        assertEquals ( "",        outcome.triggers() );
        assertEquals ( "[ 4 ]", outcome.faces() );

        roller.roll ();
        roller.roll ();
        outcome = roller.roll ();

        assertEquals ( "Success", outcome.outcome() );
        assertEquals ( "",        outcome.triggers() );
        assertEquals ( "[ 13 ]", outcome.faces() );
    }

    @Test
    public void testNamed () throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "Named" ).definition ( "d3+5" );
        roller.labelsProperty ().put ( 6, "Red" );
        roller.labelsProperty ().put ( 7, "Green" );
        roller.labelsProperty ().put ( 8, "Blue" );

        assertArrayEquals ( new long[] { 0, 0, 0, 0, 0, 0, 1, 1, 1 }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "Green",  outcome.outcome() );
        assertEquals ( "",       outcome.triggers() );
        assertEquals ( "[ 2 ]", outcome.faces() );

        roller.roll ();
        outcome = roller.roll ();

        assertEquals ( "Red",    outcome.outcome() );
        assertEquals ( "",       outcome.triggers() );
        assertEquals ( "[ 1 ]", outcome.faces() );

        outcome = roller.roll ();

        assertEquals ( "Blue",   outcome.outcome() );
        assertEquals ( "",       outcome.triggers() );
        assertEquals ( "[ 3 ]", outcome.faces() );
    }

    @Test
    public void testTriggered () throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "Trig" ).definition ( "( d3 + 1 ) * 2" );
        roller.triggersProperty ().add ( new Trigger().lua ( lua ).name ( "Big" ).definition ( "OUTCOME >= 6" ) );
        roller.triggersProperty ().add ( new Trigger().lua ( lua ).name ( "Three" ).definition ( "A == 3" ) );

        assertArrayEquals ( new long[] { 0, 0, 0, 0, 1, 0, 1, 0, 1 }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "6",      outcome.outcome() );
        assertEquals ( "Big",    outcome.triggers() );
        assertEquals ( "[ 2 ]", outcome.faces() );

        roller.roll ();
        outcome = roller.roll ();

        assertEquals ( "4",      outcome.outcome() );
        assertEquals ( "",       outcome.triggers() );
        assertEquals ( "[ 1 ]", outcome.faces() );

        outcome = roller.roll ();

        assertEquals ( "8",          outcome.outcome() );
        
        Matcher<String> triggerMatcher = new BaseMatcher<String>()
        {
            @Override
            public boolean matches ( Object str )
            {
                if ( str.toString ().equals ( "Big, Three" ) ) return true;
                if ( str.toString ().equals ( "Three, Big" ) ) return true;
                return false;
            }

            @Override
            public void describeTo ( Description desc )
            {
                desc.appendText ( "Only Triggers 'Big' and 'Three'" );
            }
        };
        
        assertThat ( outcome.triggers(), triggerMatcher );
        assertEquals ( "[ 3 ]", outcome.faces() );
    }

    @Test
    public void testTwo () throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "Two" ).definition ( "d4 + 3d6" );

        assertArrayEquals ( new long[] { 0, 0, 0, 0, 1, 4, 10, 20, 34, 52, 71, 88, 100, 104, 100, 88, 71, 52, 34, 20, 10, 4, 1 }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "11",  outcome.outcome() );
        assertEquals ( "",    outcome.triggers() );
        assertEquals ( "[ 2 ] [ 2 3 4 ]", outcome.faces() );

        outcome = roller.roll ();

        assertEquals ( "13",      outcome.outcome() );
        assertEquals ( "",       outcome.triggers() );
        assertEquals ( "[ 2 ] [ 1 5 5 ]", outcome.faces() );

        roller.roll ();
        roller.roll ();
        outcome = roller.roll (); 

        assertEquals ( "16",           outcome.outcome() );
        assertEquals ( "",             outcome.triggers() );
        assertEquals ( "[ 4 ] [ 3 4 5 ]", outcome.faces() );

        roller.roll ();
        roller.roll ();
        outcome = roller.roll ();

        assertEquals ( "7",          outcome.outcome() );
        assertEquals ( "", outcome.triggers() );
        assertEquals ( "[ 2 ] [ 1 2 2 ]", outcome.faces() );
    }

    @Test
    public void testVar() throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "Var" ).definition ( "V1 * d4 + V2" );

        lua.set ( "V1", 1 );
        lua.set ( "V2", 0 );

        assertArrayEquals ( new long[] { 0, 1, 1, 1, 1 }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "2",  outcome.outcome() );
        assertEquals ( "",    outcome.triggers() );
        assertEquals ( "[ 2 ]", outcome.faces() );

        outcome = roller.roll ();

        assertEquals ( "3",      outcome.outcome() );
        assertEquals ( "",       outcome.triggers() );
        assertEquals ( "[ 3 ]", outcome.faces() );

        lua.set ( "V1", 2 );

        roller.calculateProbabilities ( true );
        assertArrayEquals ( new long[] { 0, 0, 1, 0, 1, 0, 1, 0, 1 }, roller.rawProbabilities() );

        outcome = roller.roll (); 

        assertEquals ( "6",           outcome.outcome() );
        assertEquals ( "",             outcome.triggers() );
        assertEquals ( "[ 3 ]", outcome.faces() );

        roller.roll ();
        outcome = roller.roll ();

        assertEquals ( "4",          outcome.outcome() );
        assertEquals ( "", outcome.triggers() );
        assertEquals ( "[ 2 ]", outcome.faces() );

        lua.set ( "V2", 17 );

        roller.calculateProbabilities ( true );
        assertArrayEquals ( new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1 }, roller.rawProbabilities() );

        outcome = roller.roll ();

        assertEquals ( "25",          outcome.outcome() );
        assertEquals ( "", outcome.triggers() );
        assertEquals ( "[ 4 ]", outcome.faces() );
    }

    @Test
    public void testSimpleWeighted() throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "Weighted" ).definition ( "d[ 3, 7, 11 ]" );
        roller.triggersProperty ().add ( new Trigger().lua ( lua ).name ( "Flag" ).definition ( "A <= 6" ) );

        assertArrayEquals ( new long[] { 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1 }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "7",        outcome.outcome() );
        assertEquals ( "",         outcome.triggers() );
        assertEquals ( "[ 7 ]", outcome.faces() );

        roller.roll ();
        outcome = roller.roll ();

        assertEquals ( "3",        outcome.outcome() );
        assertEquals ( "Flag",     outcome.triggers() );
        assertEquals ( "[ 3 ]", outcome.faces() );
    }

    @Test
    public void testMultipleWeighted() throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "Weighted" ).definition ( "3d[ 3, 7, 11 ]" );
        roller.labelsProperty ().put ( 21, "Twenty-One" );

        assertArrayEquals ( new long[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 6, 0, 0, 0, 7, 0, 0, 0, 6, 0, 0, 0, 3, 0, 0, 0, 1 }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "17",        outcome.outcome() );
        assertEquals ( "",          outcome.triggers() );
        assertEquals ( "[ 3 7 7 ]", outcome.faces() );

        outcome = roller.roll ();

        assertEquals ( "Twenty-One", outcome.outcome() );
        assertEquals ( "",           outcome.triggers() );
        assertEquals ( "[ 3 7 11 ]", outcome.faces() );
    }

    @Test
    public void testDuplicate () throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "Duplicate" ).definition ( "10 * ( d10 - 1 ) + d10" );
        
        assertArrayEquals ( new long[] { 
                0, 
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "36",        outcome.outcome() );
        assertEquals ( "",          outcome.triggers() );
        assertEquals ( "[ 4 ] [ 6 ]", outcome.faces() );
    }
    
    @Test
    public void testBestOf() throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "BestOf" ).definition ( "1,2d2" );
        
        assertArrayEquals ( new long[] { 0, 1, 3 }, roller.rawProbabilities () );
        
        Outcome outcome = roller.roll ();

        assertEquals ( "2",         outcome.outcome() );
        assertEquals ( "[ 1 2 ]", outcome.faces() );
        
        outcome = roller.roll ();

        assertEquals ( "2",         outcome.outcome() );
        assertEquals ( "[ 2 2 ]", outcome.faces() );
        
        //
        // It's ridiculous that it takes this long reach a 1 1 result
        //
        for ( int n = 0; n < 15; n++ )
            outcome = roller.roll ();

        assertEquals ( "1",         outcome.outcome() );
        assertEquals ( "[ 1 1 ]", outcome.faces() );
    }
    
    @Test
    public void testDegenerate() throws DiceRollerException, ProbablityCalculationCancelledException
    {
        Roller roller = new Roller().lua ( lua ).name ( "Degenerate" ).definition ( "5" );

        assertArrayEquals ( new long[] { 0, 0, 0, 0, 0, 1 }, roller.rawProbabilities () );

        Outcome outcome = roller.roll ();

        assertEquals ( "5",          outcome.outcome() );
        assertEquals ( "",           outcome.triggers() );
        assertEquals ( "", outcome.faces() );
    }
}
