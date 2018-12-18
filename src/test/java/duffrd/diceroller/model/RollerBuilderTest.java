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
	}
}
