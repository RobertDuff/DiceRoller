package duffrd.diceroller.model;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.luaj.vm2.Globals;

import javafx.beans.property.SimpleIntegerProperty;
import utility.lua.Function;
import utility.lua.LuaProvider;

public class RollerTest
{
	public static final Globals lua = LuaProvider.lua();
	
	@Before
	public void before()
	{
		Roller.random = new Random ( 47473889938L );
		History.history ().historyProperty ().clear ();
	}
	
	@Test
	public void testSimpleInteger ()
	{
		Roller roller = new Roller();
		
		roller.rollerName = "Simple";
		roller.expression = new Function ( lua, "return A" );
		roller.dice.add ( new Dice ( new Die ( 6 ), 1 ) );
		
		roller.roll ();
		
		assertEquals ( "2",      roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Simple", History.history ().historyProperty ().get ( 0 ).rollerNameProperty ().get () );
		assertEquals ( "2",      History.history ().historyProperty ().get ( 0 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 0 ).triggersProperty ().get () );
		assertEquals ( "[2]",    History.history ().historyProperty ().get ( 0 ).facesProperty ().get () );
		
		roller.roll ();
		roller.roll ();
		
		assertEquals ( "4",      roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Simple", History.history ().historyProperty ().get ( 2 ).rollerNameProperty ().get () );
		assertEquals ( "4",      History.history ().historyProperty ().get ( 2 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 2 ).triggersProperty ().get () );
		assertEquals ( "[4]",    History.history ().historyProperty ().get ( 2 ).facesProperty ().get () );
		
		roller.roll ();
		
		assertEquals ( "3",      roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Simple", History.history ().historyProperty ().get ( 3 ).rollerNameProperty ().get () );
		assertEquals ( "3",      History.history ().historyProperty ().get ( 3 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 3 ).triggersProperty ().get () );
		assertEquals ( "[3]",    History.history ().historyProperty ().get ( 3 ).facesProperty ().get () );
	}
	
	@Test
	public void testSimpleBoolean ()
	{
		Roller roller = new Roller();
		
		roller.rollerName = "Bool";
		roller.expression = new Function ( lua, "return A > 10" );
		roller.dice.add ( new Dice ( new Die ( 20 ), 1 ) );
		
		roller.roll ();
		
		assertEquals ( "false",  roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Bool",   History.history ().historyProperty ().get ( 0 ).rollerNameProperty ().get () );
		assertEquals ( "false",  History.history ().historyProperty ().get ( 0 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 0 ).triggersProperty ().get () );
		assertEquals ( "[4]",    History.history ().historyProperty ().get ( 0 ).facesProperty ().get () );
		
		roller.roll ();
		roller.roll ();
		roller.roll ();
		
		assertEquals ( "true",      roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Bool", History.history ().historyProperty ().get ( 3 ).rollerNameProperty ().get () );
		assertEquals ( "true",      History.history ().historyProperty ().get ( 3 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 3 ).triggersProperty ().get () );
		assertEquals ( "[13]",    History.history ().historyProperty ().get ( 3 ).facesProperty ().get () );
	}
	
	@Test
	public void testSuccess ()
	{
		Roller roller = new Roller ();
		
		roller.rollerName = "Test";
		roller.expression = new Function ( lua, "return A > 10" );
		roller.dice.add ( new Dice ( new Die ( 20 ), 1 ) );
		
		roller.labels.put ( false, "Failure" );
		roller.labels.put ( true,  "Success" );
		
		roller.roll ();
		
		assertEquals ( "Failure", roller.outcomeProperty ().get () );
		assertEquals ( "",        roller.triggersProperty ().get () );
		assertEquals ( "Test",    History.history ().historyProperty ().get ( 0 ).rollerNameProperty ().get () );
		assertEquals ( "Failure", History.history ().historyProperty ().get ( 0 ).outcomeProperty ().get () );
		assertEquals ( "",        History.history ().historyProperty ().get ( 0 ).triggersProperty ().get () );
		assertEquals ( "[4]",     History.history ().historyProperty ().get ( 0 ).facesProperty ().get () );
		
		roller.roll ();
		roller.roll ();
		roller.roll ();
		
		assertEquals ( "Success", roller.outcomeProperty ().get () );
		assertEquals ( "",        roller.triggersProperty ().get () );
		assertEquals ( "Test",    History.history ().historyProperty ().get ( 3 ).rollerNameProperty ().get () );
		assertEquals ( "Success", History.history ().historyProperty ().get ( 3 ).outcomeProperty ().get () );
		assertEquals ( "",        History.history ().historyProperty ().get ( 3 ).triggersProperty ().get () );
		assertEquals ( "[13]",    History.history ().historyProperty ().get ( 3 ).facesProperty ().get () );
	}
	
	@Test
	public void testNamed ()
	{
		Roller roller = new Roller();
		
		roller.rollerName = "Named";
		roller.expression = new Function ( lua, "return A+5" );
		roller.dice.add ( new Dice ( new Die ( 3 ), 1 ) );
		roller.labels.put ( 6, "Red" );
		roller.labels.put ( 7, "Green" );
		roller.labels.put ( 8, "Blue" );
		
		roller.roll ();
		
		assertEquals ( "Green",  roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Named",  History.history ().historyProperty ().get ( 0 ).rollerNameProperty ().get () );
		assertEquals ( "Green",  History.history ().historyProperty ().get ( 0 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 0 ).triggersProperty ().get () );
		assertEquals ( "[2]",    History.history ().historyProperty ().get ( 0 ).facesProperty ().get () );
		
		roller.roll ();
		roller.roll ();
		
		assertEquals ( "Red",    roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Named",  History.history ().historyProperty ().get ( 2 ).rollerNameProperty ().get () );
		assertEquals ( "Red",    History.history ().historyProperty ().get ( 2 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 2 ).triggersProperty ().get () );
		assertEquals ( "[1]",    History.history ().historyProperty ().get ( 2 ).facesProperty ().get () );
		
		roller.roll ();
		
		assertEquals ( "Blue",   roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Named",  History.history ().historyProperty ().get ( 3 ).rollerNameProperty ().get () );
		assertEquals ( "Blue",   History.history ().historyProperty ().get ( 3 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 3 ).triggersProperty ().get () );
		assertEquals ( "[3]",    History.history ().historyProperty ().get ( 3 ).facesProperty ().get () );
	}
	
	@Test
	public void testTriggered ()
	{
		Roller roller = new Roller();
		
		roller.rollerName = "Trig";
		roller.expression = new Function ( lua, "return ( A + 1 ) * 2" );
		roller.dice.add ( new Dice ( new Die ( 3 ), 1 ) );

		roller.triggers.put ( "Big", new Function ( lua, "return OUTCOME >= 6" ) );
		roller.triggers.put ( "Three", new Function ( lua, "return A == 3" ) );
		
		roller.roll ();
		
		assertEquals ( "6",      roller.outcomeProperty ().get () );
		assertEquals ( "Big",    roller.triggersProperty ().get () );
		assertEquals ( "Trig",   History.history ().historyProperty ().get ( 0 ).rollerNameProperty ().get () );
		assertEquals ( "6",      History.history ().historyProperty ().get ( 0 ).outcomeProperty ().get () );
		assertEquals ( "Big",    History.history ().historyProperty ().get ( 0 ).triggersProperty ().get () );
		assertEquals ( "[2]",    History.history ().historyProperty ().get ( 0 ).facesProperty ().get () );
		
		roller.roll ();
		roller.roll ();
		
		assertEquals ( "4",      roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Trig",   History.history ().historyProperty ().get ( 2 ).rollerNameProperty ().get () );
		assertEquals ( "4",      History.history ().historyProperty ().get ( 2 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 2 ).triggersProperty ().get () );
		assertEquals ( "[1]",    History.history ().historyProperty ().get ( 2 ).facesProperty ().get () );
		
		roller.roll ();
		
		assertEquals ( "8",          roller.outcomeProperty ().get () );
		assertEquals ( "Big, Three", roller.triggersProperty ().get () );
		assertEquals ( "Trig",       History.history ().historyProperty ().get ( 3 ).rollerNameProperty ().get () );
		assertEquals ( "8",          History.history ().historyProperty ().get ( 3 ).outcomeProperty ().get () );
		assertEquals ( "Big, Three", History.history ().historyProperty ().get ( 3 ).triggersProperty ().get () );
		assertEquals ( "[3]",        History.history ().historyProperty ().get ( 3 ).facesProperty ().get () );
	}
	
	@Test
	public void testTwo ()
	{
		Roller roller = new Roller();
		
		roller.rollerName = "Two";
		roller.expression = new Function ( lua, "return A+B" );
		
		roller.dice.add ( new Dice ( new Die ( 4 ), 1 ) );
		roller.dice.add ( new Dice ( new Die ( 6 ), 3 ) );
		
		roller.roll ();
		
		assertEquals ( "11",  roller.outcomeProperty ().get () );
		assertEquals ( "",    roller.triggersProperty ().get () );
		assertEquals ( "Two", History.history ().historyProperty ().get ( 0 ).rollerNameProperty ().get () );
		assertEquals ( "11",  History.history ().historyProperty ().get ( 0 ).outcomeProperty ().get () );
		assertEquals ( "",    History.history ().historyProperty ().get ( 0 ).triggersProperty ().get () );
		assertEquals ( "[2][2, 3, 4]", History.history ().historyProperty ().get ( 0 ).facesProperty ().get () );
		
		roller.roll ();
		
		assertEquals ( "13",      roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Two",   History.history ().historyProperty ().get ( 1 ).rollerNameProperty ().get () );
		assertEquals ( "13",      History.history ().historyProperty ().get ( 1 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 1 ).triggersProperty ().get () );
		assertEquals ( "[2][1, 5, 5]",    History.history ().historyProperty ().get ( 1 ).facesProperty ().get () );
		
		roller.roll ();
		roller.roll ();
		roller.roll (); 

		assertEquals ( "16",           roller.outcomeProperty ().get () );
		assertEquals ( "",             roller.triggersProperty ().get () );
		assertEquals ( "Two",          History.history ().historyProperty ().get ( 4 ).rollerNameProperty ().get () );
		assertEquals ( "16",           History.history ().historyProperty ().get ( 4 ).outcomeProperty ().get () );
		assertEquals ( "",             History.history ().historyProperty ().get ( 4 ).triggersProperty ().get () );
		assertEquals ( "[4][3, 4, 5]", History.history ().historyProperty ().get ( 4 ).facesProperty ().get () );
		
		roller.roll ();
		roller.roll ();
		roller.roll ();

		assertEquals ( "7",          roller.outcomeProperty ().get () );
		assertEquals ( "", roller.triggersProperty ().get () );
		assertEquals ( "Two",       History.history ().historyProperty ().get ( 7 ).rollerNameProperty ().get () );
		assertEquals ( "7",          History.history ().historyProperty ().get ( 7 ).outcomeProperty ().get () );
		assertEquals ( "", History.history ().historyProperty ().get ( 7 ).triggersProperty ().get () );
		assertEquals ( "[2][1, 2, 2]",        History.history ().historyProperty ().get ( 7 ).facesProperty ().get () );
	}
	
	@Test
	public void testVar()
	{
		Roller roller = new Roller();
		
		roller.rollerName = "Var";
		roller.expression = new Function ( lua, "return V1 * A + V2" );
		
		roller.dice.add ( new Dice ( new Die ( 4 ), 1 ) );

		roller.variables.put ( "V1", new SimpleIntegerProperty ( 1 ) );
		roller.variables.put ( "V2", new SimpleIntegerProperty ( 0 ) );
		
		roller.roll ();
		
		assertEquals ( "2",  roller.outcomeProperty ().get () );
		assertEquals ( "",    roller.triggersProperty ().get () );
		assertEquals ( "Var", History.history ().historyProperty ().get ( 0 ).rollerNameProperty ().get () );
		assertEquals ( "2",  History.history ().historyProperty ().get ( 0 ).outcomeProperty ().get () );
		assertEquals ( "",    History.history ().historyProperty ().get ( 0 ).triggersProperty ().get () );
		assertEquals ( "[2]", History.history ().historyProperty ().get ( 0 ).facesProperty ().get () );
		
		roller.roll ();
		
		assertEquals ( "3",      roller.outcomeProperty ().get () );
		assertEquals ( "",       roller.triggersProperty ().get () );
		assertEquals ( "Var",   History.history ().historyProperty ().get ( 1 ).rollerNameProperty ().get () );
		assertEquals ( "3",      History.history ().historyProperty ().get ( 1 ).outcomeProperty ().get () );
		assertEquals ( "",       History.history ().historyProperty ().get ( 1 ).triggersProperty ().get () );
		assertEquals ( "[3]",    History.history ().historyProperty ().get ( 1 ).facesProperty ().get () );
		
		roller.variableProperty ( "V1" ).set ( 2 );
		roller.roll (); 

		assertEquals ( "6",           roller.outcomeProperty ().get () );
		assertEquals ( "",             roller.triggersProperty ().get () );
		assertEquals ( "Var",          History.history ().historyProperty ().get ( 2 ).rollerNameProperty ().get () );
		assertEquals ( "6",           History.history ().historyProperty ().get ( 2 ).outcomeProperty ().get () );
		assertEquals ( "",             History.history ().historyProperty ().get ( 2 ).triggersProperty ().get () );
		assertEquals ( "[3]", History.history ().historyProperty ().get ( 2 ).facesProperty ().get () );
		
		roller.roll ();
		roller.roll ();

		assertEquals ( "4",          roller.outcomeProperty ().get () );
		assertEquals ( "", roller.triggersProperty ().get () );
		assertEquals ( "Var",       History.history ().historyProperty ().get ( 4 ).rollerNameProperty ().get () );
		assertEquals ( "4",          History.history ().historyProperty ().get ( 4 ).outcomeProperty ().get () );
		assertEquals ( "", History.history ().historyProperty ().get ( 4 ).triggersProperty ().get () );
		assertEquals ( "[2]",        History.history ().historyProperty ().get ( 4 ).facesProperty ().get () );
		
		roller.variableProperty ( "V2" ).set ( 17 );
		roller.roll ();

		assertEquals ( "25",          roller.outcomeProperty ().get () );
		assertEquals ( "", roller.triggersProperty ().get () );
		assertEquals ( "Var",       History.history ().historyProperty ().get ( 5 ).rollerNameProperty ().get () );
		assertEquals ( "25",          History.history ().historyProperty ().get ( 5 ).outcomeProperty ().get () );
		assertEquals ( "", History.history ().historyProperty ().get ( 5 ).triggersProperty ().get () );
		assertEquals ( "[4]",        History.history ().historyProperty ().get ( 5 ).facesProperty ().get () );
	}
}
