package duffrd.diceroller.model;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Variable
{
    private ObjectProperty<Suite> suiteProperty = new SimpleObjectProperty<> ();
    private ObjectProperty<Globals> luaProperty = new SimpleObjectProperty<> ();
    protected StringProperty nameProperty = new SimpleStringProperty ();
    protected IntegerProperty valueProperty = new SimpleIntegerProperty ();
    @SuppressWarnings ( "unused" )
    private ObjectBinding<Void> updateProperty;
    
    public Variable ()
    {
        suiteProperty.addListener ( ( a, o, n ) -> luaProperty.bind ( n.luaProperty () ) );
        
        nameProperty.addListener ( ( object, oldValue, newValue ) -> 
        {
            if ( luaProperty.get () == null )
                return;
            
            if ( oldValue == null || oldValue.isEmpty () )
                return;
            
            luaProperty.get ().set ( oldValue, LuaValue.NIL );
        } );
        
        updateProperty = Bindings.createObjectBinding ( () -> 
        {
            if ( luaProperty.get () == null )
                return null;
            
            if ( nameProperty.get () == null || nameProperty.get ().isEmpty () )
                return null;
            
            luaProperty.get ().set ( nameProperty.get (), valueProperty.get () );
            
            return null;
        }, luaProperty, nameProperty, valueProperty );
    }
    
    public Suite suite()
    {
        return suiteProperty.get ();
    }
    
    public Variable suite ( Suite suite )
    {
        suiteProperty.set ( suite );
        return this;
    }
    
    public Globals lua()
    {
        return luaProperty.get ();
    }
    
    public Variable lua ( Globals lua )
    {
        luaProperty.set ( lua );
        updateProperty.get ();
        return this;
    }
    
    public String name()
    {
        return nameProperty.getValue ();
    }
    
    public Variable name ( String name )
    {
        this.nameProperty.set ( name );
        updateProperty.get ();
        return this;
    }
    
    public int value()
    {
        return valueProperty.getValue ();
    }
    
    public Variable value ( int value )
    {
        this.valueProperty.set ( value );
        updateProperty.get ();
        return this;
    }
    
    public ObjectProperty<Globals> luaProperty()
    {
        return luaProperty;
    }
    
    public StringProperty nameProperty()
    {
        return nameProperty;
    }
    
    public IntegerProperty valueProperty()
    {
        return valueProperty;
    }
}
