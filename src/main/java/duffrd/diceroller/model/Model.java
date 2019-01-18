package duffrd.diceroller.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;

public abstract class Model
{
    protected ObservableSet<Suite> suitesProperty = FXCollections.observableSet ( new HashSet<>() );
    protected ObservableList<Outcome> historyProperty = new FilteredList<> ( FXCollections.observableArrayList () );
    
    public Set<Suite> suites()
    {
        return suitesProperty;
    }
    
    public List<Outcome> history()
    {
        return historyProperty;
    }
    
    public ObservableSet<Suite> suitesProperty()
    {
        return suitesProperty;
    }
    
    public ObservableList<Outcome> historyProperty()
    {
        return historyProperty;
    }
    
    public abstract Suite addNewSuite();
}
