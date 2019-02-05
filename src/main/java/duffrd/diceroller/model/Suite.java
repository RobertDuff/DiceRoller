package duffrd.diceroller.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.luaj.vm2.Globals;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class Suite
{
    protected ObjectProperty<Model> modelProperty = new SimpleObjectProperty<> ();
    protected ObjectProperty<Globals> luaProperty = new SimpleObjectProperty<> ();
    protected StringProperty nameProperty = new SimpleStringProperty ();
    protected ListProperty<Variable> variablesProperty = new SimpleListProperty<> ( FXCollections.observableArrayList () );
    protected SetProperty<Trigger> triggersProperty = new SimpleSetProperty<> ( FXCollections.observableSet ( new HashSet<>() ) );
    protected ListProperty<Group> groupsProperty = new SimpleListProperty<> ( FXCollections.observableArrayList () );
    
    public Model model()
    {
        return modelProperty.get ();
    }
    
    public Suite model ( Model model )
    {
        modelProperty.set ( model );
        return this;
    }
    
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
    
    public ObjectProperty<Model> modelProperty()
    {
        return modelProperty;
    }
    
    public ObjectProperty<Globals> luaProperty()
    {
        return luaProperty;
    }
    
    public StringProperty nameProperty()
    {
        return nameProperty;
    }
    
    public ListProperty<Variable> variablesProperty()
    {
        return variablesProperty;
    }
    
    public SetProperty<Trigger> triggersProperty()
    {
        return triggersProperty;
    }
    
    public ListProperty<Group> groupsProperty()
    {
        return groupsProperty;
    }

    @Override
    public String toString ()
    {
        return name();
    }
}
