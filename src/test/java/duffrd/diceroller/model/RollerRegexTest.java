package duffrd.diceroller.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith ( Parameterized.class )
public class RollerRegexTest
{
	@Rule
	public ExpectedException exc = ExpectedException.none ();

	public String text;
	public String[] groups;

	public RollerRegexTest ( String text, String[] groups )
	{
		this.text = text;
		this.groups = groups;
	}

	@Test
	public void testMatch ()
	{		
		Matcher matcher;

		matcher = RollerBuilder.DICE_REGEX.matcher ( text );

		if ( groups.length == 0 )
		{
			assertFalse ( matcher.find() );
			exc.expect ( IllegalStateException.class );
			matcher.group ();
		}
		else
			assertTrue ( matcher.find() );
		
		for ( int i=0; i<groups.length; i++ )
			assertEquals ( "Group " + i, groups[ i ], matcher.group ( i ) );
	}

	@Parameters ( name = "{0}" )
	public static Object[] parameters()
	{
		List<Object[]> params = new ArrayList<>();

		params.add ( new Object[] { "", new String[0] } );
		
		params.addAll ( variations ( "Fred", new String[0] ) );
		params.addAll ( variations ( "34d",  new String[0] ) );
		params.addAll ( variations ( "2D",   new String[0] ) );
		params.addAll ( variations ( "D-3",  new String[0] ) );
		params.addAll ( variations ( "2d 5", new String[0] ) );
		params.addAll ( variations ( "D[]",  new String[0] ) );
		
		params.addAll ( variations ( "d1",     new String[] { "d1",     null, null, null, null, "1",     null  } ) );
		params.addAll ( variations ( "D1",     new String[] { "D1",     null, null, null, null, "1",     null  } ) );
		params.addAll ( variations ( "D[3,4]", new String[] { "D[3,4]", null, null, null, null, "[3,4]", "3,4" } ) );
		
		params.addAll ( variations ( "3D6",    new String[] { "3D6", "3", null, null, "3", "6", null } ) );
		params.addAll ( variations ( "-3D6",   new String[] { "3D6", "3", null, null, "3", "6", null } ) );
		params.addAll ( variations ( ",3D6",   new String[] { "3D6", "3", null, null, "3", "6", null } ) );
		params.addAll ( variations ( "2 ,3D6", new String[] { "3D6", "3", null, null, "3", "6", null } ) );
		params.addAll ( variations ( "2, 3D6", new String[] { "3D6", "3", null, null, "3", "6", null } ) );
		
		params.addAll ( variations (  "2,3D6", new String[] {  "2,3D6",  "2,3",  "2,",  "2", "3", "6", null } ) );
		params.addAll ( variations ( "-2,3D6", new String[] { "-2,3D6", "-2,3", "-2,", "-2", "3", "6", null } ) );
		
		params.addAll ( variations (  "4,5D[ 5:3, 6 ]", new String[] {  "4,5D[ 5:3, 6 ]",  "4,5",  "4,",  "4", "5", "[ 5:3, 6 ]", " 5:3, 6 " } ) );
		params.addAll ( variations ( "-4,5D[ 5:3, 6 ]", new String[] { "-4,5D[ 5:3, 6 ]", "-4,5", "-4,", "-4", "5", "[ 5:3, 6 ]", " 5:3, 6 " } ) );

		return params.toArray ();
	}
	
	private static List<Object[]> variations ( String text, String[] groups )
	{
		List<Object[]> variations = new ArrayList<>();
		
		variations.add ( new Object[] {       text,        groups } );
		variations.add ( new Object[] { " " + text,        groups } );
		variations.add ( new Object[] {       text + " ",  groups } );
		variations.add ( new Object[] { " " + text + " ",  groups } );
		variations.add ( new Object[] { "(" + text + ")",  groups } );
		variations.add ( new Object[] {       text + "+3", groups } );
		
		return variations;
	}
}
