package duffrd.diceroller.model;

import java.util.List;

import org.luaj.vm2.Globals;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public abstract class Group
{
    private ObjectProperty<Globals> luaProperty = new SimpleObjectProperty<> ();
    protected StringProperty nameProperty = new SimpleStringProperty ();
    protected ListProperty<Roller> rollersProperty = new SimpleListProperty<> ( FXCollections.observableArrayList () );
    
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
    
    public ListProperty<Roller> rollersProperty()
    {
        return rollersProperty;
    }
    
    public abstract void addRoller ( Roller roller );    
    public abstract Roller newRoller();
}
