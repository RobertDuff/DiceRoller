package duffrd.diceroller.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utility.lua.Function;
import utility.lua.LuaProvider;

public class RollerBuilder
{
    static final Pattern DICE_REGEX = Pattern.compile ( "(((-?\\d+),)?(\\d+))?[Dd](\\d+|\\[([^\\[]+)\\])" );
    static final Pattern COMPARISON_REGEX = Pattern.compile ( "[=<>!~]" );

    protected Roller roller;
    
    public RollerBuilder()
    {
        roller = new Roller();
    }
    
    public RollerBuilder group ( String group )
    {
        roller.groupName = group;
        roller.lua = LuaProvider.lua ( group );
        
        return this;
    }
    
    public RollerBuilder name ( String name )
    {
        roller.rollerName = name;
        return this;
    }
    
    public RollerBuilder definition ( String def ) throws DiceRollerException
    {
        roller.definition = def;
        roller.booleanOutcome = COMPARISON_REGEX.matcher ( def ).find ();
        
        String luaCode = def;
        
        int argIndex = 1;
        Matcher matcher;
        
        while ( ( matcher = DICE_REGEX.matcher ( luaCode ) ).find () )
        {           
            String adjustment = matcher.group ( 3 );
            String numDice = matcher.group ( 4 );
            String faces = matcher.group ( 5 );
            String weights = matcher.group ( 6 );
            
            Die die;
            
            if ( weights != null )
            {
                Map<Integer,Integer> distribution = new HashMap<>();
                
                for ( String weight : weights.split ( "\\s*,\\s*" ) )
                {
                    String[] valCount = weight.split ( "\\s*:\\s*" );
                    
                    if ( valCount.length == 2 )
                    {
                        int face = Integer.valueOf ( valCount[ 0 ] );
                        int count = Integer.valueOf ( valCount[ 1 ] );
                        
                        distribution.put ( face, count );
                    }
                    else
                    {
                        int face = Integer.valueOf ( weight );
                        distribution.put ( face, 1 );
                    }
                }
                
                die = new Die ( distribution );
            }
            else
                die = new Die ( Integer.valueOf ( faces ) );
            
            int num = 0;
            
            if ( numDice != null )
                num = Integer.valueOf ( numDice );
            else
                num = 1;
            
            int start = 0;
            int end = num;
            
            if ( adjustment != null )
            {
                int adjust = Integer.valueOf ( adjustment );
                
                if ( adjust > 0 )
                    start += num - adjust;
                else
                    end = -adjust;
            }
            
            Dice dice = new Dice ( die, num, start, end );
                        
            luaCode = luaCode.replace ( matcher.group (), "dice[" + argIndex++ + "]" );
            roller.dice.add ( dice );
        }
        
        roller.expression = new Function ( roller.lua, "local dice = {...}", "return ", luaCode );
        
        return this;
    }
    
    public RollerBuilder outcomeIsBoolean ( boolean state )
    {
        roller.booleanOutcome = state;
        return this;
    }
    
    public RollerBuilder addLabel ( int value, String label )
    {
        if ( roller.booleanOutcome )
            roller.labels.put ( value==1, label );
        else
            roller.labels.put ( value, label );
        
        return this;
    }
    
    public RollerBuilder addTrigger ( String name, String def )
    {
        roller.triggers.put ( name, new Roller.Expression ( def, new Function ( roller.lua, 
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
                "local H = __ARGS__[ 11 ]",
                "return ", def ) ) );
        return this;
    }
    
    public Roller build() throws DiceRollerException
    {
        return roller;
    }
}
