package duffrd.diceroller.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class SuiteInitializer
{
    public static final String SUITES_DIRECTORY = "suites";
    
    private static Map<String,Map<String,Object>> templates;

    private Model model;

    public SuiteInitializer ( Model model ) throws DiceRollerException
    {
        this.model = model;
        
        loadTemplates ();
    }

    private static void loadTemplates() throws DiceRollerException
    {
        if ( templates == null )
        {
            templates = new HashMap<> ();

            try
            {

                Files.walk ( Paths.get ( ClassLoader.getSystemResource ( SUITES_DIRECTORY ).toURI () ) )
                .filter ( suitePath -> suitePath.getFileName ().toString ().endsWith ( ".yaml" ) )
                .forEach ( suitePath -> 
                {
                    try
                    {
                        Map<String,Object> suiteSpec = new Yaml ().load ( Files.newInputStream ( suitePath, StandardOpenOption.READ ) );
                        String suiteName = ( String ) suiteSpec.remove ( "suite" );
                        templates.put ( suiteName, suiteSpec );
                    }
                    catch ( IOException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } );
            }
            catch ( IOException | URISyntaxException e )
            {
                throw new DiceRollerException ( e );
            }
        }
    }
    
    public List<String> suiteTemplates()
    {
        List<String> suiteNames = new ArrayList<> ( templates.keySet () );
        suiteNames.sort ( Comparator.naturalOrder () );
        return suiteNames;
    }

    public void apply ( Suite suite, String templateName ) throws DiceRollerException
    {
        apply ( suite, templates.get ( templateName ) );
    }
    
    @SuppressWarnings ( "unchecked" )
    public void apply ( Suite suite, Map<String,Object> spec ) throws DiceRollerException
    {
        //
        // Variables
        //
        
        for ( Object o : ( List<Object> ) spec.getOrDefault ( "variables", Collections.emptyList () ) )
        {
            if ( o instanceof String )
                model.createVariable ( suite, o.toString (), 0 );
            else if ( o instanceof Map )
            {                
                Map<String,Integer> v = ( Map<String,Integer> ) o;
             
                // Using a Loop, even though there should be only one value.
                for ( String name : v.keySet () )
                    model.createVariable ( suite, name, v.get ( name ) );
            }
        }

        //
        // Triggers
        //

        Map<String,String> triggers = ( Map<String,String> ) spec.getOrDefault ( "triggers", Collections.emptyMap () );

        // This is an internal-only reference for finding triggers by name when building roller below.
        Map<String,Trigger> triggerRefs = new HashMap<> ();
        
        for ( String name : triggers.keySet () )
        {
            Trigger trigger = model.createTrigger ( suite, name, triggers.get ( name ) );
            triggerRefs.put ( name, trigger );
        }
        
        //
        // Groups
        //

        for ( Map<String,Object> g : ( List<Map<String,Object>> ) spec.getOrDefault ( "groups", Collections.emptyList () ) )
        {
            Group group = model.createGroup ( suite, ( String ) g.get ( "group" ) );
            
            for ( Map<String,Object> r : ( List<Map<String, Object>> ) g.getOrDefault ( "rollers", Collections.emptyList () ) )
            {
                Roller roller = model.createRoller ( group, ( String ) r.get ( "name" ), ( String ) r.get ( "definition" ) );

                Map<Integer,String> labels = ( Map<Integer,String> ) r.getOrDefault ( "labels", Collections.emptyMap () );
                
                for ( int value : labels.keySet () )
                    model.createLabel ( roller, value, labels.get ( value ) );

                for ( String t : ( List<String> ) r.getOrDefault ( "triggers", Collections.emptyList () ) )
                {
                    Trigger trigger = triggerRefs.get ( t );
                    
                    if ( trigger == null )
                        throw new DiceRollerException ( "Roller '" + roller.name () + "' references undefined Trigger '" + t + "'" );

                    model.createRollerTrigger ( roller, trigger );
                }
            }
        }
    }
}
