package duffrd.diceroller.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;

public abstract class Model
{
    protected SetProperty<Suite> suitesProperty = new SimpleSetProperty<> ( FXCollections.observableSet ( new HashSet<>() ) );
    protected ListProperty<Outcome> historyProperty = new SimpleListProperty<> ( new FilteredList<> ( FXCollections.observableArrayList () ) );
    
    public Set<Suite> suites()
    {
        return suitesProperty;
    }
    
    public List<Outcome> history()
    {
        return historyProperty;
    }
    
    public SetProperty<Suite> suitesProperty()
    {
        return suitesProperty;
    }
    
    public ListProperty<Outcome> historyProperty()
    {
        return historyProperty;
    }
    
    public abstract Suite newSuite();
}
