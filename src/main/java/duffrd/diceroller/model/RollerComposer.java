package duffrd.diceroller.model;

import java.util.HashMap;
import java.util.Map;

public class RollerComposer
{
	private RollerComposer () {}
	
	public static Map<String,Object> compose ( Roller roller )
	{
		Map<String,Object> composition = new HashMap<>();
		
		composition.put ( RollerBuilder.NAME_KEY, roller.rollerName );
		composition.put ( RollerBuilder.DEFINITION_KEY, roller.definition );
		
		if ( roller.labels.size () > 0 )
			composition.put ( RollerBuilder.LABELS_KEY, roller.labels );
		
		if( roller.triggers.size () > 0 )
		{
			Map<String,String> triggers = new HashMap<>();
			
			for ( String trigger : roller.triggers.keySet () )
				triggers.put ( trigger, roller.triggers.get ( trigger ).getExpression () );
			
			composition.put ( RollerBuilder.TRIGGERS_KEY, triggers );
		}
		
		return composition;
	}
}
