package duffrd.diceroller.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class History
{
	private static History singletonInstance;
	
	public static History history()
	{
		if ( singletonInstance == null )
			singletonInstance = new History();
		
		return singletonInstance;
	}
	
	private ObservableList<HistoryEntry> history = FXCollections.observableArrayList ();
	
	public void record ( String rollerName, String outcome, String triggers, String faces )
	{
		history.add ( new HistoryEntry ( rollerName, outcome, triggers, faces ) );
	}
	
	public void clear()
	{
		history.clear ();
	}
	
	public ObservableList<HistoryEntry> historyProperty()
	{
		return history;
	}
}
