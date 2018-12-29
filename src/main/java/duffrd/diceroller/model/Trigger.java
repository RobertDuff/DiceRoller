package duffrd.diceroller.model;

import org.luaj.vm2.Globals;

import utility.lua.Function;

public class Trigger
{
    String definition;
    Function function;
    
    public Trigger ( Globals lua, String def )
    {
        definition = def;
        
        function = new Function ( lua, 
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
                "return ", def );
    }
    
    public String definition()
    {
        return definition;
    }
}
