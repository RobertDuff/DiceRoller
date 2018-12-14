package duffrd.diceroller.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class RollerBuilderTest
{
	public Map<String,Object> def;
	public Map<String,String> labels;
	public Map<String,String> triggers;
	
	@Before
	public void before()
	{
		def = new HashMap<>();
		labels = new HashMap<>();
		triggers = new HashMap<> ();
	}
	
	@Test
	public void testSimpleStandard ()
	{
		def.put ( "name",       "Fred" );
		def.put ( "definition", "6 * ( -3,5D8 - 2d[6,7:3] ) + ADJUST" );
		
		Roller roller = RollerBuilder.build ( def );
		
		System.out.println ( roller.toString () );
	}
}
