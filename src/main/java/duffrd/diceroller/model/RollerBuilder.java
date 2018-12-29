package duffrd.diceroller.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import utility.lua.Function;
import utility.lua.LuaProvider;

public class RollerBuilder
{
    protected static Globals validationLua;
    
    static final Pattern DICE_REGEX = Pattern.compile ( "(((-?\\d+),)?(\\d+))?[Dd](\\d+|\\[([^\\[]+)\\])" );
    static final Pattern COMPARISON_REGEX = Pattern.compile ( "[=<>!~]" );

    protected String groupName;
    protected Globals lua;
    
    protected StringProperty nameProperty = new SimpleStringProperty ();
    protected StringProperty definitionProperty = new SimpleStringProperty ();
    
    protected ObservableMap<Integer,String> labelMap = FXCollections.observableHashMap ();
    protected ObservableMap<String,String> triggerMap = FXCollections.observableHashMap ();
    
    public RollerBuilder ( String groupName )
    {
        this.groupName = groupName;
        lua = LuaProvider.lua ( groupName );
    }
    
    public StringProperty nameProperty()
    {
        return nameProperty;
    }
    
    public StringProperty definitionProperty()
    {
        return definitionProperty;
    }
    
    public ObservableMap<Integer,String> labelMap()
    {
        return labelMap;
    }
    
    public ObservableMap<String,String> triggerMap()
    {
        return triggerMap;
    }
    
    public RollerBuilder name ( String name )
    {
        nameProperty.set ( name );
        return this;
    }
    
    public RollerBuilder definition ( String def )
    {
        definitionProperty.set ( def );
        return this;
    }
    
    public RollerBuilder addLabel ( int value, String label )
    {
        labelMap.put ( value, label );
        return this;
    }
    
    public RollerBuilder addTrigger ( String name, String def )
    {
        triggerMap.put ( name, def );
        return this;
    }
    
    public Roller build() throws DiceRollerException
    {
        Roller roller = new Roller();
                
        roller.rollerName = nameProperty.get ();
        roller.definition = definitionProperty.get ();
        
        String luaCode = roller.definition;
        
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
                
                for ( String weight : weights.trim().split ( "\\s*,\\s*" ) )
                {
                    String[] valCount = weight.trim().split ( "\\s*:\\s*" );
                    
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
                        
            luaCode = luaCode.replaceFirst ( Pattern.quote ( matcher.group () ), "dice[" + argIndex++ + "]" );
            roller.dice.add ( dice );
            
            dice.canceledProperty ().bind ( roller.canceledProperty () );
        }
        
        try
        {
            roller.expression = new Function ( lua, "local dice = {...}", "return ", luaCode );
        }
        catch ( LuaError e )
        {
            throw new DiceRollerException ( e );
        }
        
        roller.labels.putAll ( labelMap.entrySet ().stream ().filter ( e -> !e.getValue ().isEmpty () ).collect ( Collectors.toMap ( Map.Entry::getKey, Map.Entry::getValue ) ) );
        
        roller.triggers = triggerMap.entrySet ().stream ().collect ( Collectors.toMap ( Map.Entry::getKey, e -> new Trigger ( lua, e.getValue() ) ) );
        
        return roller;
    }
    
    public boolean isDefinitionValid ( String definition )
    {
        if ( definition == null || definition.equals ( "" ) )
            return true;
        
        if ( validationLua == null )
            validationLua = LuaProvider.newLua ();
        
        String code = definition.replaceAll ( DICE_REGEX.pattern (), "X" );
        
        try
        {
            validationLua.load ( "return " + code );
        }
        catch ( Exception e )
        {
            return false;
        }
        
        return true;
    }
}
