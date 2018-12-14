package duffrd.diceroller.view;

import com.google.common.eventbus.EventBus;

public class ObserverEventBus
{
	public static enum Event
	{
		CLOSE_FILE,
	}
	
	private static EventBus bus = new EventBus();
	
	private ObserverEventBus () {}

	public static void register ( Object obj )
	{
		bus.register ( obj );
	}
	
	public static void post ( Object obj )
	{
		bus.post ( obj );
	}
}
