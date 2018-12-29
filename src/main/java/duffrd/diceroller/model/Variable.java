package duffrd.diceroller.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Variable
{
    private StringProperty name = new SimpleStringProperty ();
    private IntegerProperty value = new SimpleIntegerProperty ();
    
    public Variable ()
    {
    }
    
    public Variable ( String name, int value )
    {
        this.name.setValue ( name );
        this.value.setValue ( value );
    }
    
    public String name()
    {
        return name.getValue ();
    }
    
    public Variable name ( String name )
    {
        this.name.set ( name );
        return this;
    }
    
    public int value()
    {
        return value.getValue ();
    }
    
    public Variable value ( int value )
    {
        this.value.set ( value );
        return this;
    }
    
    public ReadOnlyStringProperty nameProperty()
    {
        return name;
    }
    
    public IntegerProperty valueProperty()
    {
        return value;
    }
    
    public Variable nameProperty ( StringProperty prop )
    {
        name = prop;
        return this;
    }
    
    public Variable valueProperty ( IntegerProperty prop )
    {
        value = prop;
        return this;
    }
}
