package duffrd.diceroller.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HistoryEntry
{
	public ObjectProperty<HistoryTime>	timeProperty		= new SimpleObjectProperty<> ( this, "timeProperty" );
	public StringProperty				rollerNameProperty	= new SimpleStringProperty   ( this, "rollerNameProperty" );
	public StringProperty				outcomeProperty		= new SimpleStringProperty   ( this, "outcomeProperty" );
	public StringProperty				triggersProperty	= new SimpleStringProperty   ( this, "triggersProperty" );
	public StringProperty				facesProperty		= new SimpleStringProperty   ( this, "facesProperty" );
	
	public HistoryEntry ()
	{
		// TODO Auto-generated constructor stub
	}
	
	public HistoryEntry ( String rollerName, String outcome, String triggers, String faces )
	{
		this ( new HistoryTime (), rollerName, outcome, triggers, faces );
	}
	
	public HistoryEntry ( HistoryTime time, String rollerName, String outcome, String triggers, String faces )
	{
		setTime       ( time     );
		setRollerName ( rollerName );
		setOutcome    ( outcome  );
		setTriggers   ( triggers );
		setFaces      ( faces    );
	}
	
	public ObjectProperty<HistoryTime> timeProperty()
	{
		return timeProperty;
	}
	
	public StringProperty rollerNameProperty()
	{
		return rollerNameProperty;
	}
	
	public StringProperty outcomeProperty()
	{
		return outcomeProperty;
	}
	
	public StringProperty triggersProperty()
	{
		return triggersProperty;
	}
	
	public StringProperty facesProperty()
	{
		return facesProperty;
	}
	
	public void setTime ( HistoryTime time )
	{
		timeProperty.set ( time );
	}
	
	public void setRollerName ( String rollerName )
	{
		rollerNameProperty.set ( rollerName );
	}
	
	public void setOutcome ( String outcome )
	{
		outcomeProperty.set ( outcome );
	}
	
	public void setTriggers ( String triggers )
	{
		triggersProperty.set ( triggers );
	}
	
	public void setFaces ( String faces )
	{
		facesProperty.set ( faces );
	}
	
	public HistoryTime getTime()
	{
		return timeProperty.get ();
	}
	
	public String getRollerName()
	{
		return rollerNameProperty.get ();
	}
	
	public String getOutcome()
	{
		return outcomeProperty.get ();
	}
	
	public String getTriggers()
	{
		return triggersProperty.get ();
	}
	
	public String getFaces()
	{
		return facesProperty.get ();
	}
}
