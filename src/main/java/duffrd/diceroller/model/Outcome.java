package duffrd.diceroller.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Outcome
{
    private ObjectProperty<Date> timeProperty = new SimpleObjectProperty<> ();
    private StringProperty rollerNameProperty =  new SimpleStringProperty ();
    private StringProperty outcomeProperty = new SimpleStringProperty ();
    private ObservableList<String> triggersProperty = FXCollections.observableArrayList ();
    private ObservableList<List<Integer>> facesProperty = FXCollections.observableArrayList ();

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
        return triggersProperty.stream ().collect ( Collectors.joining ( ", " ) );
    }
    
    public Outcome addTrigger ( Trigger trigger )
    {
        triggersProperty.add ( trigger.name () );
        return this;
    }
    
    public String faces()
    {
        return facesProperty.stream ().map ( l -> 
            l.stream ().map ( i -> String.valueOf ( i ) ).collect ( Collectors.joining ( " ", "[ ", " ]" ) )
                ).collect ( Collectors.joining ( " " ) );
    }
    
    public Outcome addFaces ( List<Integer > faces )
    {
        facesProperty.add ( faces );
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
    
    public ObservableList<String> triggerProperty()
    {
        return triggersProperty;
    }
    
    public ObservableList<List<Integer>> facesProperty()
    {
        return facesProperty;
    }

    @Override
    public boolean equals ( Object obj )
    {
        return this == obj;
    }
}
