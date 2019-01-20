package duffrd.diceroller.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Outcome
{
    private ObjectProperty<Date> timeProperty = new SimpleObjectProperty<> ();
    private StringProperty rollerNameProperty =  new SimpleStringProperty ();
    private StringProperty outcomeProperty = new SimpleStringProperty ();
    private StringProperty triggersProperty = new SimpleStringProperty ( "" );
    private StringProperty facesProperty = new SimpleStringProperty ( "" );

    public Outcome ()
    {
        timeProperty.set ( new Date ( System.currentTimeMillis() ) );    
    }

    public Date time()
    {
        return timeProperty.get ();
    }
    
    public String roller()
    {
        return rollerNameProperty.get ();
    }
    
    public Outcome roller ( String name )
    {
        rollerNameProperty.set ( name );
        return this;
    }
    
    public String outcome()
    {
        return outcomeProperty.get ();
    }
    
    public Outcome outcome ( String outcome )
    {
        outcomeProperty.set ( outcome );
        return this;
    }
    
    public String triggers()
    {
        return triggersProperty.get ();
    }
    
    public Outcome addTrigger ( Trigger trigger )
    {
        if ( !triggersProperty.get ().isEmpty () )
            triggersProperty.set ( triggersProperty.get () + ", " );
        
        triggersProperty.set ( triggersProperty.get () + trigger.name () );
        return this;
    }
    
    public String faces()
    {
        return facesProperty.get ();
    }
    
    public Outcome addFaces ( List<Integer> faces )
    {
        if ( !facesProperty.get ().isEmpty () )
            facesProperty.set ( facesProperty.get () + " " );
        
        facesProperty.set ( facesProperty.get () + faces.stream ().map ( i -> String.valueOf ( i ) ).collect ( Collectors.joining ( " ", "[ ", " ]" ) ) );
        return this;
    }
    
    public ReadOnlyObjectProperty<Date> timeProperty()
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

    @Override
    public boolean equals ( Object obj )
    {
        return this == obj;
    }

    @Override
    public String toString ()
    {
        return "Outcome [ " + roller() + " " + outcome() + " " + triggers() + " " + faces() + "]";
    }
}
