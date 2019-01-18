package duffrd.diceroller.model;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utility.lua.Function;

public class Trigger
{
    private ObjectProperty<Globals> luaProperty = new SimpleObjectProperty<> ();
    protected StringProperty nameProperty = new SimpleStringProperty ();
    protected StringProperty definitionProperty = new SimpleStringProperty ();
    private ObjectProperty<Function> functionProperty = new SimpleObjectProperty<> ();
    private BooleanProperty validProperty = new SimpleBooleanProperty ();

    public Trigger()
    {
        functionProperty.bind ( Bindings.createObjectBinding ( () -> 
        {
            if ( luaProperty.get () == null )
                return null;

            if ( definitionProperty.get () == null || definitionProperty.get ().isEmpty () )
                return null;

            try
            {
                return new Function ( luaProperty.get (), 
                        "local __ARGS__ = { ... }",
                        "local OUTCOME = __ARGS__[ 1 ]",
                        "local A = __ARGS__[ 2 ]",
                        "local B = __ARGS__[ 3 ]",
                        "local C = __ARGS__[ 4 ]",
                        "local D = __ARGS__[ 5 ]",
                        "local E = __ARGS__[ 6 ]",
                        "local F = __ARGS__[ 7 ]",
                        "local G = __ARGS__[ 8 ]",
                        "local H = __ARGS__[ 9 ]",
                        "local I = __ARGS__[ 10 ]",
                        "local J = __ARGS__[ 11 ]",
                        "return ", definitionProperty.get () );
            }
            catch ( LuaError e )
            {
                return null;
            }
        }, luaProperty, definitionProperty ) );

        validProperty.bind ( Bindings.isNotNull ( functionProperty ) );
    }

    public Globals lua()
    {
        return luaProperty.get ();
    }

    public Trigger lua ( Globals lua )
    {
        luaProperty.set ( lua );
        return this;
    }

    public String name()
    {
        return nameProperty.get ();
    }

    public Trigger name ( String name )
    {
        nameProperty.set ( name );
        return this;
    }

    public String definition()
    {
        return definitionProperty.get ();
    }

    public Trigger definition ( String definition )
    {
        definitionProperty.set ( definition );
        return this;
    }

    public boolean isValid()
    {
        return validProperty.get ();
    }

    public ObjectProperty<Globals> luaProperty()
    {
        return luaProperty;
    }

    public StringProperty nameProperty()
    {
        return nameProperty;
    }

    public StringProperty definitionProperty()
    {
        return definitionProperty;
    }

    public ReadOnlyBooleanProperty validProperty()
    {
        return validProperty;
    }

    public boolean fire ( Varargs args )
    {
        if ( functionProperty.get () == null )
            return false;

        return functionProperty.get ().call ( args ).checkboolean ();
    }

    @Override
    public String toString ()
    {
        return "Trigger [nameProperty=" + nameProperty.get () + ", definitionProperty=" + definitionProperty.get ()
                + ", functionProperty=" + functionProperty.get ().toString () + ", validProperty=" + validProperty.get () + "]";
    }
}
