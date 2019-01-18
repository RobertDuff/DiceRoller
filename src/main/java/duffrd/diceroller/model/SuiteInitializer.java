package duffrd.diceroller.model;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
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
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

public class SuiteInitializer
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

    public static final String SUITES_DIRECTORY = "suites";

    private Map<String,Map<String,Object>> templates;

    public SuiteInitializer () throws IOException, URISyntaxException
    {
        templates = new HashMap<> ();

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
                e.printStackTrace();
            }
        } );
    }

    public List<String> suiteTemplates()
    {
        List<String> suiteNames = new ArrayList<> ( templates.keySet () );
        suiteNames.sort ( Comparator.naturalOrder () );
        return suiteNames;
    }

    public void createSuite ( Model model, String suiteName, String templateName )
    {
        Map<String,Object> spec = new HashMap<> ( templates.get ( templateName ) );
        spec.put ( "suite", suiteName );
        
        createSuite ( model, spec );
    }
    
    @SuppressWarnings ( "unchecked" )
    public static void createSuite ( Model model, Map<String,Object> spec )
    {
        Suite suite = model.addNewSuite ();

        suite.name ( ( String ) spec.get ( "suite" ) );

        //
        // Variables
        //

        for ( Object o : ( List<Object> ) spec.getOrDefault ( "variables", Collections.emptyList () ) )
        {
            Variable variable = suite.addNewVariable ();

            if ( o instanceof String )
            {
                variable.name ( o.toString () );
                variable.value ( 0 );
            }
            else if ( o instanceof Map )
            {                
                Map<String,Integer> v = ( Map<String,Integer> ) o;
             
                // Using a Loop, even though there should be only one value.
                for ( String name : v.keySet () )
                {
                    variable.name ( name );
                    variable.value ( v.get ( name ) );
                }
            }
            
            suite.variablesProperty ().add ( variable );
        }

        //
        // Triggers
        //

        Map<String,String> triggers = ( Map<String,String> ) spec.getOrDefault ( "triggers", Collections.emptyMap () );
        
        for ( String name : triggers.keySet () )
        {
            Trigger trigger = suite.addNewTrigger ();

            trigger.name ( name );
            trigger.definition ( triggers.get ( name ) );
            
            logger.debug ( "Adding Trigger " + name );
            suite.triggersProperty ().add ( trigger );
        }
        
        logger.debug ( "Suite Triggers: " + suite.triggersProperty ().stream ().map ( t -> t.name () ).collect ( Collectors.joining ( " " ) ) );

        //
        // Groups
        //

        for ( Map<String,Object> g : ( List<Map<String,Object>> ) spec.getOrDefault ( "groups", Collections.emptyList () ) )
        {
            Group group = suite.addNewGroup ();

            group.name ( ( String ) g.get ( "group" ) );
            logger.debug ( "Assigning new Group Name: " + group.name () );
            
            for ( Map<String,Object> r : ( List<Map<String, Object>> ) g.getOrDefault ( "rollers", Collections.emptyList () ) )
            {
                Roller roller = group.addNewRoller ();

                roller.name ( ( String ) r.get ( "name" ) );
                roller.definition ( ( String ) r.get ( "definition" ) );

                roller.labelsProperty ().putAll ( ( Map<Integer, String> ) r.getOrDefault ( "labels", Collections.emptyMap () ) );

                for ( String t : ( List<String> ) r.getOrDefault ( "triggers", Collections.emptyList () ) )
                {
                    logger.debug ( "Looking for Trigger named: " + t );
                    for ( Trigger trigger : suite.triggersProperty () )
                    {
                        logger.debug ( "Comparing " + t + " with " + trigger.name () );
                        if ( trigger.name ().equals ( t ) )
                        {
                            logger.debug ( "Associating Roller " + roller.name () + " with Trigger " + trigger.name () );
                            roller.triggersProperty ().add ( trigger );
                            break;
                        }
                    }
                }
                
                group.rollersProperty ().add ( roller );
            }
            
            suite.groupsProperty ().add ( group );
        }
        
        model.suitesProperty ().add ( suite );
    }
}
