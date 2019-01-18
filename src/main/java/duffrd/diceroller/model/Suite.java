package duffrd.diceroller.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.luaj.vm2.Globals;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public abstract class Suite
{
    protected ObjectProperty<Globals> luaProperty = new SimpleObjectProperty<> ();
    protected StringProperty nameProperty = new SimpleStringProperty ();
    protected ObservableList<Variable> variablesProperty = FXCollections.observableArrayList ();
    protected ObservableSet<Trigger> triggersProperty = FXCollections.observableSet ( new HashSet<>() );
    protected ObservableList<Group> groupsProperty = FXCollections.observableArrayList ();
    
    public Globals lua()
    {
        return luaProperty.get ();
    }
    
    public Suite lua ( Globals lua )
    {
        luaProperty.set ( lua );
        return this;
    }
    
    public String name()
    {
        return nameProperty.get ();
    }
    
    public Suite name ( String name )
    {
        nameProperty.set ( name );
        return this;
    }
    
    public List<Variable> variables()
    {
        return variablesProperty;
    }
    
    public Set<Trigger> triggers()
    {
        return triggersProperty;
    }
    
    public List<Group> groups()
    {
        return groupsProperty;
    }
    
    public StringProperty nameProperty()
    {
        return nameProperty;
    }
    
    public ObservableList<Variable> variablesProperty()
    {
        return variablesProperty;
    }
    
    public ObservableSet<Trigger> triggersProperty()
    {
        return triggersProperty;
    }
    
    public ObservableList<Group> groupsProperty()
    {
        return groupsProperty;
    }
    
    public abstract Variable addNewVariable();
    public abstract Trigger addNewTrigger();
    public abstract Group addNewGroup();
}
