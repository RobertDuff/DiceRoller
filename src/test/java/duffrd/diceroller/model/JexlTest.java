package duffrd.diceroller.model;

import static org.junit.Assert.*;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.junit.Test;

public class JexlTest
{
	public static final JexlEngine jexlEngine = new JexlEngine();
	
	@Test
	public void testStatic ()
	{
		String text = "3*(4+5)";
		Expression expr = jexlEngine.createExpression ( text );
		
		JexlContext context = new MapContext();
		Object result = expr.evaluate ( context );
		
		assertEquals ( 27, result );
	}
	
	@Test
	public void testVariable ()
	{
		String text = "3*(A+5)";
		Expression expr = jexlEngine.createExpression ( text );
		System.out.println ( expr.toString () );
		System.out.println ( expr.getExpression () );
		System.out.println ( expr.dump () );
		
		JexlContext context = new MapContext();
		
		context.set ( "A", 2 );		
		assertEquals ( 21, expr.evaluate ( context ) );
		
		context.set ( "A", 3 );		
		assertEquals ( 24, expr.evaluate ( context ) );
	}
}
