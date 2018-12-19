package duffrd.diceroller.model;

import static org.junit.Assert.assertEquals;

import java.util.Random;

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
		History.history ().historyProperty ().clear ();
	}
	
	@Test
	public void testSimpleInteger () throws DiceRollerException
	{
		Roller roller = new RollerBuilder().group ( "x" ).name ( "Simple" ).definition ( "d6" ).build ();		
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
	public void testSimpleBoolean () throws DiceRollerException
	{
		Roller roller = new RollerBuilder().group ( "x" ).name ( "Bool" ).definition ( "d20 > 10" ).build();		
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
	public void testSuccess () throws DiceRollerException
	{
		Roller roller = new RollerBuilder().group ( "x" ).name ( "Test" ).definition ( "d20 > 10" ).addLabel ( 0, "Failure" ).addLabel ( 1, "Success" ).build();
		
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
	public void testNamed () throws DiceRollerException
	{
		Roller roller = new RollerBuilder().group ( "x" ).name ( "Named" ).definition ( "d3+5" ).addLabel ( 6, "Red" ).addLabel ( 7, "Green" ).addLabel ( 8, "Blue" ).build ();
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
	public void testTriggered () throws DiceRollerException
	{
		Roller roller = new RollerBuilder().group ( "x" ).name ( "Trig" ).definition ( "( d3 + 1 ) * 2" ).addTrigger ( "Big", "OUTCOME >= 6" ).addTrigger ( "Three", "A == 3" ).build ();		
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
	public void testTwo () throws DiceRollerException
	{
		Roller roller = new RollerBuilder().group ( "x" ).name ( "Two" ).definition ( "d4 + 3d6" ).build();
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
	public void testVar() throws DiceRollerException
	{
		Roller roller = new RollerBuilder ().group ( "x" ).name ( "Var" ).definition ( "V1 * d4 + V2" ).build();

		lua.set ( "V1", 1 );
		lua.set ( "V2", 0 );
		
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
		
		lua.set ( "V1", 2 );
		
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
		
		lua.set ( "V2", 17 );
		
		roller.roll ();

		assertEquals ( "25",          roller.outcomeProperty ().get () );
		assertEquals ( "", roller.triggersProperty ().get () );
		assertEquals ( "Var",       History.history ().historyProperty ().get ( 5 ).rollerNameProperty ().get () );
		assertEquals ( "25",          History.history ().historyProperty ().get ( 5 ).outcomeProperty ().get () );
		assertEquals ( "", History.history ().historyProperty ().get ( 5 ).triggersProperty ().get () );
		assertEquals ( "[4]",        History.history ().historyProperty ().get ( 5 ).facesProperty ().get () );
	}
}
