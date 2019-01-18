package duffrd.diceroller.model;

import java.util.Collections;
import java.util.List;

import org.luaj.vm2.Globals;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Group
{
    private ObjectProperty<Globals> luaProperty = new SimpleObjectProperty<> ();
    protected StringProperty nameProperty = new SimpleStringProperty ();
    protected ObservableList<Roller> rollersProperty = FXCollections.observableArrayList ();
    
    public Globals lua()
    {
        return luaProperty.get ();
    }
    
    public Group lua ( Globals lua )
    {
        luaProperty.set ( lua );
        return this;
    }
    
    public String name()
    {
        return nameProperty.get ();
    }
    
    public Group name ( String name )
    {
        nameProperty.set ( name );
        return this;
    }
    
    public List<Roller> rollers()
    {
        return rollersProperty;
    }
    
    public ObjectProperty<Globals> luaProperty()
    {
        return luaProperty;
    }
    
    public StringProperty nameProperty()
    {
        return nameProperty;
    }
    
    public ObservableList<Roller> rollersProperty()
    {
        return rollersProperty;
    }
    
    public abstract Roller addNewRoller();
}
