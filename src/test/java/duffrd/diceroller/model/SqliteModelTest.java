package duffrd.diceroller.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yaml.snakeyaml.Yaml;

import duffrd.diceroller.model.sqlite.SqliteDbProvider;
import duffrd.diceroller.model.sqlite.SqliteModelLoader;
import utility.arrays.ListRearranger;
import utility.sql.Sql;

public class SqliteModelTest
{
    public static final String TEST_DATA = "modelTest.yaml";
    
    @Rule
    public ExpectedException thrown = ExpectedException.none ();
    
    public Connection db;
    public ModelLoader loader;
    public Model model;
    public Suite suite;
    
    @Before
    public void before() throws IOException, SQLException, DiceRollerException
    {
        db = SqliteDbProvider.provideInMemoryDB ();
        loader = new SqliteModelLoader ( db );
        model = loader.load ();
        
        Map<String,Object> suiteSpec = new Yaml ().load ( ClassLoader.getSystemResourceAsStream ( TEST_DATA ) );
        SuiteInitializer.createSuite ( model, suiteSpec );  
        
        suite = model.suites().iterator ().next ();
    }
    
    @After
    public void after() throws SQLException
    {
        db.close ();
    }
    
    @Ignore
    @Test
    public void testTypes() throws IllegalArgumentException, IllegalAccessException
    {
        for ( Field field : java.sql.Types.class.getFields () )
        {
            System.out.println ( field + " = " + field.getInt ( null ) );
        }
        
        for ( Field field : java.sql.ResultSetMetaData.class.getFields () )
        {
            System.out.println ( field + " = " + field.getInt ( null ) );
        }
    }
    
    @Test
    public void testInitializedDB() throws SQLException
    {
        int n;
        
        //
        // Suites
        //
        
        n = 0;
        for ( ResultSet row : new Sql ( db, "select * from suites" ).go () )
        {
            n++;
            
            ResultSetMetaData data = row.getMetaData ();
            
            assertEquals ( 2, data.getColumnCount () );
            
            assertEquals ( "id", data.getColumnName ( 1 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 1 ) );

            assertEquals ( "name", data.getColumnName ( 2 ) );
            assertEquals ( Types.VARCHAR, data.getColumnType ( 2 ) );
            
            assertEquals ( n, row.getInt ( 1 ) );
            assertEquals ( "S" + n, row.getString ( 2 ) );
        }
        
        assertEquals ( "Exactly One Suite", 1, n );
        
        //
        // Variables
        //
        
        n = 0;
        for ( ResultSet row : new Sql ( db, "select * from variables order by sequence" ).go() )
        {            
            n++;
            
            ResultSetMetaData data = row.getMetaData ();
            
            assertEquals ( 5, data.getColumnCount () );
            
            assertEquals ( "id", data.getColumnName ( 1 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 1 ) );
            
            assertEquals ( "suiteId", data.getColumnName ( 2 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 2 ) );
            
            assertEquals ( "name", data.getColumnName ( 3 ) );
            assertEquals ( Types.VARCHAR, data.getColumnType ( 3 ) );
            
            assertEquals ( "value", data.getColumnName ( 4 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 4 ) );
            
            assertEquals ( "sequence", data.getColumnName ( 5 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 5 ) );
            
            assertEquals ( n, row.getInt ( 1 ) );
            assertEquals ( 1, row.getInt ( 2 ) );
            assertEquals ( "V" + ( n-1 ), row.getString ( 3 ) );
            assertEquals ( ( n-1 ) * 10, row.getInt ( 4 ) );
            assertEquals ( n, row.getInt ( 5 ) );
        }
        
        assertEquals ( "Exactly Six Variables", 6, n );
        
        //
        // Triggers
        //
        
        n = 0;
        for ( ResultSet row : new Sql ( db, "select * from triggers" ).go() )
        {            
            n++;
            
            ResultSetMetaData data = row.getMetaData ();
            
            assertEquals ( 4, data.getColumnCount () );
            
            assertEquals ( "id", data.getColumnName ( 1 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 1 ) );
            
            assertEquals ( "suiteId", data.getColumnName ( 2 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 2 ) );
            
            assertEquals ( "name", data.getColumnName ( 3 ) );
            assertEquals ( Types.VARCHAR, data.getColumnType ( 3 ) );
            
            assertEquals ( "definition", data.getColumnName ( 4 ) );
            assertEquals ( Types.VARCHAR, data.getColumnType ( 4 ) );
            
            assertEquals ( n, row.getInt ( 1 ) );
            assertEquals ( 1, row.getInt ( 2 ) );
            assertEquals ( "T" + n, row.getString ( 3 ) );
            assertEquals ( "A == " + n, row.getString ( 4 ) );
        }
        
        assertEquals ( "Exactly Two Triggers", 2, n );
        
        //
        // Groups
        //
        
        n = 0;
        for ( ResultSet row : new Sql ( db, "select * from groups order by sequence" ).go() )
        {            
            n++;
            
            ResultSetMetaData data = row.getMetaData ();
            
            assertEquals ( 4, data.getColumnCount () );
            
            assertEquals ( "id", data.getColumnName ( 1 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 1 ) );
            
            assertEquals ( "suiteId", data.getColumnName ( 2 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 2 ) );
            
            assertEquals ( "name", data.getColumnName ( 3 ) );
            assertEquals ( Types.VARCHAR, data.getColumnType ( 3 ) );
            
            assertEquals ( "sequence", data.getColumnName ( 4 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 4 ) );
            
            assertEquals ( n, row.getInt ( 1 ) );
            assertEquals ( 1, row.getInt ( 2 ) );
            assertEquals ( "G" + n, row.getString ( 3 ) );
            assertEquals ( n, row.getInt ( 4 ) );
        }
        
        assertEquals ( "Exactly 3 Groups", 3, n );
        
        //
        // Rollers
        //
        
        n = 0;
        for ( ResultSet row : new Sql ( db, "select * from rollers order by sequence" ).go() )
        {            
            n++;
            
            ResultSetMetaData data = row.getMetaData ();
            
            assertEquals ( 5, data.getColumnCount () );
            
            assertEquals ( "id", data.getColumnName ( 1 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 1 ) );
            
            assertEquals ( "groupId", data.getColumnName ( 2 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 2 ) );
            
            assertEquals ( "name", data.getColumnName ( 3 ) );
            assertEquals ( Types.VARCHAR, data.getColumnType ( 3 ) );
            
            assertEquals ( "definition", data.getColumnName ( 4 ) );
            assertEquals ( Types.VARCHAR, data.getColumnType ( 4 ) );
            
            assertEquals ( "sequence", data.getColumnName ( 5 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 5 ) );
            
            assertEquals ( n, row.getInt ( 1 ) );
            assertEquals ( 1, row.getInt ( 2 ) );
            assertEquals ( "R" + n, row.getString ( 3 ) );
            assertEquals ( "d4", row.getString ( 4 ) );
            assertEquals ( n, row.getInt ( 5 ) );
        }
        
        assertEquals ( "Exactly Seven Rollers", 7, n );
        
        //
        // Labels
        //
        
        n = 0;
        for ( ResultSet row : new Sql ( db, "select * from labels order by value" ).go() )
        {            
            n++;
            
            ResultSetMetaData data = row.getMetaData ();
            
            assertEquals ( 3, data.getColumnCount () );
            
            assertEquals ( "rollerId", data.getColumnName ( 1 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 1 ) );
            
            assertEquals ( "value", data.getColumnName ( 2 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 2 ) );
            
            assertEquals ( "label", data.getColumnName ( 3 ) );
            assertEquals ( Types.VARCHAR, data.getColumnType ( 3 ) );
            
            assertEquals ( 1, row.getInt ( 1 ) );
            assertEquals ( n, row.getInt ( 2 ) );
            assertEquals ( "L" + n, row.getString ( 3 ) );
        }
        
        assertEquals ( "Exactly Two Labels", 2, n );
        
        //
        // Roller Triggers
        //
        
        n = 0;
        for ( ResultSet row : new Sql ( db, "select * from rollerTriggers order by triggerId" ).go() )
        {            
            n++;
            
            ResultSetMetaData data = row.getMetaData ();
            
            assertEquals ( 2, data.getColumnCount () );
            
            assertEquals ( "rollerId", data.getColumnName ( 1 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 1 ) );
            
            assertEquals ( "triggerId", data.getColumnName ( 2 ) );
            assertEquals ( Types.INTEGER, data.getColumnType ( 2 ) );
            
            assertEquals ( 1, row.getInt ( 1 ) );
            assertEquals ( n, row.getInt ( 2 ) );
        }
        
        assertEquals ( "Exactly Two Roller Triggers", 2, n );
    }
    
    @Test
    public void testLoadedModel()
    {
        assertEquals ( 1, model.suites ().size () );
                
        assertEquals ( "S1", suite.name() );
        
        // Variables
        
        assertEquals ( 6, suite.variables ().size () );
        
        int n = 0;
        for ( Variable v : suite.variables () )
        {
            assertEquals ( "V" + n, v.name () );
            assertEquals ( n * 10, v.value () );
            assertEquals ( n * 10, v.lua ().get ( "V" + n ).toint () );
            n++;
        }
        
        // Triggers
        
        assertEquals ( 2, suite.triggers ().size () );

        List<Trigger> triggers = new ArrayList<>();
        
        for ( Trigger t : suite.triggers () )
            triggers.add ( t );
        
        triggers.sort ( ( a, b ) -> a.name ().compareTo ( b.name () ) );
        
        n = 1;
        for ( Trigger trigger : triggers )
        {
            assertEquals ( "T" + n, trigger.name () );
            assertEquals ( "A == " + n, trigger.definition () );
            assertTrue ( trigger.isValid () );
            n++;
        }
        
        //TODO: Figure out a way to verify triggers
        
        // Groups
        
        assertEquals ( 3, suite.groups ().size () );
        
        for ( int g=0; g < suite.groups ().size (); g++ )
        {
            Group group = suite.groups ().get ( g );
            
            assertEquals ( "G" + ( g+1 ), group.name () );
            
            if ( g == 0 )
            {
                assertEquals ( 7, group.rollers ().size () );
                
                for ( int r=0; r < group.rollers ().size (); r++ )
                {
                    Roller roller = group.rollers ().get ( r );
                    
                    assertEquals ( "R" + ( r+1 ), roller.name () );
                    assertEquals ( "d4", roller.definition () );
                    assertTrue ( roller.isValid () );
                    
                    if ( r == 0 )
                    {
                        assertEquals ( 2, roller.labels ().size () );
                        
                        assertTrue ( roller.labels ().containsKey ( 1 ) );
                        assertEquals ( "L1", roller.labels ().get ( 1 ) );
                        
                        assertTrue ( roller.labels ().containsKey ( 2 ) );
                        assertEquals ( "L2", roller.labels ().get ( 2 ) );
                        
                        assertEquals ( 2, roller.triggers ().size () );
                        
                        //TODO: Figure out a way to verify triggers
                    }
                    else
                    {
                        assertEquals ( 0, roller.labels ().size () );
                        assertEquals ( 0, roller.triggers ().size () );
                    }
                }
            }
            else
            {
                assertEquals ( 0, group.rollers ().size () );
            }
        }
    }
    
    @Test
    public void testCreateSuite() throws SQLException
    {
        Suite suite = model.addNewSuite ();
        
        assertNotNull ( suite.lua () );
        
        suite.name ( "S2" );
        
        assertEquals ( "S2", new Sql ( db, "select name from suites where id = ( select max ( id ) from suites )" ).go ().single ().getString ( 1 ) );
    }
    
    @Test
    public void testRenameSuite() throws SQLException
    {
        // Rename
        
        suite.name ( "S2" );
        assertEquals ( "S2", new Sql ( db, "select name from suites" ).go ().single ().getString ( 1 ) );
        
        // Rename to Blank Value        
        
        suite.name ( "" );
        assertEquals ( "<unnamed-suite>", new Sql ( db, "select name from suites" ).go ().single ().getString ( 1 ) );
        
        // Rename 
        
        suite.name ( "S3" );
        assertEquals ( "S3", new Sql ( db, "select name from suites" ).go ().single ().getString ( 1 ) );
        
        // Rename to Null
        
        suite.name ( null );
        assertEquals ( "<unnamed-suite>", new Sql ( db, "select name from suites" ).go ().single ().getString ( 1 ) );
        
    }
    
    @Test
    public void testDeleteSuite() throws SQLException
    {
        model.suites ().remove ( suite );
        
        assertEquals ( 0, model.suites ().size () );

        assertEquals ( 0, new Sql ( db, "select count(*) from suites" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count(*) from variables" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count(*) from triggers" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count(*) from groups" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count(*) from rollers" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count(*) from labels" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count(*) from rollerTriggers" ).go ().single ().getInt ( 1 ) );
    }
    
    @Test
    public void testCreateVariable() throws SQLException
    {
        Variable v = suite.addNewVariable ();
        
        assertNotNull ( v.lua () );
        
        v.name ( "VN" );
        v.value ( 67 );
        
        ResultSet row = new Sql ( db, "select suiteId,name,value,sequence from variables where id = ( select max ( id ) from variables )" ).go ().single ();
        
        assertEquals ( 1, row.getInt ( 1 ) );
        assertEquals ( "VN", row.getString ( 2 ) );
        assertEquals ( 67, row.getInt ( 3 ) );
        assertEquals ( 7, row.getInt ( 4 ) );
    }
    
    @Test
    public void testRenameVariable() throws SQLException
    {
        Variable variable = suite.variables ().get ( 0 );
        variable.value ( 12 );
        
        // Rename
        
        variable.name ( "VR" );
        assertEquals ( "VR", new Sql ( db, "select name from variables where id = 1" ).go ().single ().getString ( 1 ) );
        assertTrue ( variable.lua ().get ( "V0" ).isnil () );
        assertEquals ( 12, variable.lua ().get ( "VR" ).checkint () );
        
        // Rename to Blank
        
        variable.name ( "" );
        assertEquals ( "<unnamed-variable>", new Sql ( db, "select name from variables where id = 1" ).go ().single ().getString ( 1 ) );
        assertTrue ( variable.lua ().get ( "VR" ).isnil () );

        // Rename 
        
        variable.name ( "VRR" );
        assertEquals ( "VRR", new Sql ( db, "select name from variables where id = 1" ).go ().single ().getString ( 1 ) );
        assertEquals ( 12, variable.lua ().get ( "VRR" ).checkint () );

        // Rename to Null
        
        variable.name ( null );
        assertEquals ( "<unnamed-variable>", new Sql ( db, "select name from variables where id = 1" ).go ().single ().getString ( 1 ) );
        assertTrue ( variable.lua ().get ( "VRR" ).isnil () );
    }
    
    @Test
    public void testChangeVariableValue() throws SQLException
    {
        Variable variable = suite.variables ().get ( 0 );
        
        variable.value ( 77 );
        assertEquals ( 77, new Sql ( db, "select value from variables where id = 1" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 77, variable.lua ().get ( "V0" ).toint () );
    }
    
    @Test
    public void testRearrangeVariables() throws SQLException
    {
        suite.variables ().sort ( ListRearranger.reverse ( suite.variables () ) );
        
        int n = 5;
        int s = 1;
        
        for ( ResultSet row : new Sql ( db, "select name,sequence from variables order by sequence" ).go () )
        {
            assertEquals ( "V" + n, row.getString ( 1 ) );
            assertEquals ( s, row.getInt ( 2 ) );
            
            n--;
            s++;
        }
    }
    
    @Test
    public void testDeleteVariable() throws SQLException
    {
        suite.variables ().remove ( 0 );
        
        assertEquals ( 5, new Sql ( db, "select count (*) from variables" ).go ().single ().getInt ( 1 ) );
        
        int n = 1;
        for ( ResultSet row : new Sql ( db, "select name,sequence from variables" ).go () )
        {
            assertEquals ( "V" + n, row.getString ( 1 ) );
            assertEquals ( n, row.getInt ( 2 ) );
            n++;
        }
    }
    
    @Test
    public void testDeleteAllVariables() throws SQLException
    {
        suite.variables ().clear();
        
        assertEquals ( 0, new Sql ( db, "select count (*) from variables" ).go ().single ().getInt ( 1 ) );
    }
    
    @Test
    public void testCreateTrigger() throws SQLException
    {
        Trigger trigger = suite.addNewTrigger ();
        
        trigger.name ( "T3" );
        
        ResultSet row = new Sql ( db, "select suiteId,name from triggers where id = ( select max ( id ) from triggers )" ).go ().single ();
        
        assertEquals ( 1, row.getInt ( 1 ) );
        assertEquals ( "T3", row.getString ( 2 ) );
    }
    
    @Test
    public void testRenameTrigger() throws SQLException
    {
        Trigger trigger = null;
        Iterator<Trigger> i = suite.triggers ().iterator ();
        
        while ( i.hasNext () )
        {
            trigger = i.next ();
            
            if ( trigger.name ().equals ( "T1" ) )
                break;
        }
        
        // Rename
        
        trigger.name ( "TR" );
        assertEquals ( "TR", new Sql ( db, "select name from triggers where id = 1" ).go ().single ().getString ( 1 ) );
        
        // Rename to Blank
        
        trigger.name ( "" );
        assertEquals ( "<unnamed-trigger>", new Sql ( db, "select name from triggers where id = 1" ).go ().single ().getString ( 1 ) );

        // Rename 
        
        trigger.name ( "TRR" );
        assertEquals ( "TRR", new Sql ( db, "select name from triggers where id = 1" ).go ().single ().getString ( 1 ) );

        // Rename to Null
        
        trigger.name ( null );
        assertEquals ( "<unnamed-trigger>", new Sql ( db, "select name from triggers where id = 1" ).go ().single ().getString ( 1 ) );
    }
    
    @Test
    public void testChangeTriggerDefinition() throws SQLException
    {
        Trigger trigger = null;
        Iterator<Trigger> i = suite.triggers ().iterator ();
        
        while ( i.hasNext () )
        {
            trigger = i.next ();
            
            if ( trigger.name ().equals ( "T1" ) )
                break;
        }
        
        trigger.definition ( "OUTCOME > 10" );
        assertEquals ( "OUTCOME > 10", new Sql ( db, "select definition from triggers where id = 1" ).go ().single ().getString ( 1 ) );
        assertTrue ( trigger.isValid () );
        
        trigger.definition ( "0" );
        assertEquals ( "0", new Sql ( db, "select definition from triggers where id = 1" ).go ().single ().getString ( 1 ) );
        assertTrue ( trigger.isValid () );
        
        trigger.definition ( "FRED==" );
        assertEquals ( "FRED==", new Sql ( db, "select definition from triggers where id = 1" ).go ().single ().getString ( 1 ) );
        assertFalse ( trigger.isValid () );
    }
    
    @Test
    public void testDeleteTrigger() throws SQLException
    {
        Trigger trigger = null;
        Iterator<Trigger> i = suite.triggers ().iterator ();
        
        while ( i.hasNext () )
        {
            trigger = i.next ();
            
            if ( trigger.name ().equals ( "T1" ) )
                break;
        }
                
        suite.triggers ().remove ( trigger );
        
        assertEquals ( 1, new Sql ( db, "select count (*) from triggers" ).go ().single ().getInt ( 1 ) );
        assertEquals ( "T2", new Sql ( db, "select name from triggers" ).go ().single ().getString ( 1 ) );
        
        assertEquals ( 1, new Sql ( db, "select count (*) from rollerTriggers" ).go ().single ().getInt ( 1 ) );
        
        ResultSet row = new Sql ( db, "select rollerId,triggerId from rollerTriggers" ).go ().single ();
        
        assertEquals ( 1, row.getInt ( 1 ) );
        assertEquals ( 2, row.getInt ( 2 ) );
    }
    
    @Test
    public void testDeleteAllTriggers() throws SQLException
    {
        suite.triggers ().clear();
        
        assertEquals ( 0, new Sql ( db, "select count (*) from triggers" ).go ().single ().getInt ( 1 ) );
    }
    
    @Test
    public void testCreateGroup() throws SQLException
    {
        Group group = suite.addNewGroup ();
        
        assertNotNull ( group.lua () );
        
        group.name ( "GN" );
        
        ResultSet row = new Sql ( db, "select suiteId,name,sequence from groups where id = ( select max ( id ) from groups )" ).go ().single ();
        
        assertEquals ( 1, row.getInt ( 1 ) );
        assertEquals ( "GN", row.getString ( 2 ) );
        assertEquals ( 4, row.getInt ( 3 ) );
    }
    
    @Test
    public void testRenameGroup() throws SQLException
    {
        Group group = suite.groups ().get ( 0 );
        
        // Rename
        
        group.name ( "GR" );
        assertEquals ( "GR", new Sql ( db, "select name from groups where id = 1" ).go ().single ().getString ( 1 ) );
        
        // Rename to Blank
        
        group.name ( "" );
        assertEquals ( "<unnamed-group>", new Sql ( db, "select name from groups where id = 1" ).go ().single ().getString ( 1 ) );

        // Rename 
        
        group.name ( "GRR" );
        assertEquals ( "GRR", new Sql ( db, "select name from groups where id = 1" ).go ().single ().getString ( 1 ) );

        // Rename to Null
        
        group.name ( null );
        assertEquals ( "<unnamed-group>", new Sql ( db, "select name from groups where id = 1" ).go ().single ().getString ( 1 ) );
    }
    
    @Test
    public void testRearrangeGroups() throws SQLException
    {
        suite.groups ().sort ( ListRearranger.reverse ( suite.groups () ) );
        
        int n = 3;
        int s = 1;
        
        for ( ResultSet row : new Sql ( db, "select name,sequence from groups order by sequence" ).go () )
        {
            assertEquals ( "G" + n, row.getString ( 1 ) );
            assertEquals ( s, row.getInt ( 2 ) );
            
            n--;
            s++;
        }
    }
    
    @Test
    public void testDeleteGroup() throws SQLException
    {
        suite.groups ().remove ( 1 );
        
        assertEquals ( 2, new Sql ( db, "select count (*) from groups" ).go ().single ().getInt ( 1 ) );
        
        int n = 1;
        int s = 1;
        
        for ( ResultSet row : new Sql ( db, "select name,sequence from groups order by sequence" ).go () )
        {
            assertEquals ( "G" + n, row.getString ( 1 ) );
            assertEquals ( s, row.getInt ( 2 ) );
            
            n += 2;
            s++;
        }
    }
    
    @Test
    public void testDeleteAllGroups() throws SQLException
    {
        suite.groups ().clear();
        
        assertEquals ( 0, new Sql ( db, "select count (*) from groups" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count (*) from rollers" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count (*) from labels" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count (*) from rollerTriggers" ).go ().single ().getInt ( 1 ) );
    }
    
    @Test
    public void testCreateRoller() throws SQLException
    {
        Roller roller = suite.groups ().get ( 1 ).addNewRoller ();
        
        assertNotNull ( roller.lua () );
        
        roller.name ( "RN" );
        
        ResultSet row = new Sql ( db, "select groupId,name,sequence from rollers where id = ( select max ( id ) from rollers )" ).go ().single ();
        
        assertEquals ( 2, row.getInt ( 1 ) );
        assertEquals ( "RN", row.getString ( 2 ) );
        assertEquals ( 1, row.getInt ( 3 ) );
    }
    
    @Test
    public void testRenameRoller() throws SQLException
    {
        Roller roller = suite.groups ().get ( 0 ).rollers ().get ( 0 );
        
        // Rename
        
        roller.name ( "RR" );
        assertEquals ( "RR", new Sql ( db, "select name from rollers where id = 1" ).go ().single ().getString ( 1 ) );
        
        // Rename to Blank
        
        roller.name ( "" );
        assertEquals ( "<unnamed-roller>", new Sql ( db, "select name from rollers where id = 1" ).go ().single ().getString ( 1 ) );

        // Rename 
        
        roller.name ( "RRR" );
        assertEquals ( "RRR", new Sql ( db, "select name from rollers where id = 1" ).go ().single ().getString ( 1 ) );

        // Rename to Null
        
        roller.name ( null );
        assertEquals ( "<unnamed-roller>", new Sql ( db, "select name from rollers where id = 1" ).go ().single ().getString ( 1 ) );
    }
    
    @Test
    public void testChangeRollerDefinition() throws SQLException
    {
        Roller roller = suite.groups ().get ( 0 ).rollers ().get ( 0 );
        
        roller.definition ( "d6 + 10" );
        assertEquals ( "d6 + 10", new Sql ( db, "select definition from rollers where id = 1" ).go ().single ().getString ( 1 ) );
        assertTrue ( roller.isValid () );
        
        roller.definition ( "3,6d10 + d5" );
        assertEquals ( "3,6d10 + d5", new Sql ( db, "select definition from rollers where id = 1" ).go ().single ().getString ( 1 ) );
        assertTrue ( roller.isValid () );
        
        roller.definition ( "d6 + " );
        assertEquals ( "d6 + ", new Sql ( db, "select definition from rollers where id = 1" ).go ().single ().getString ( 1 ) );
        assertFalse ( roller.isValid () );
    }
    
    @Test
    public void testRearrangeRollers() throws SQLException
    {
        suite.groups ().get ( 0 ).rollers ().sort ( ListRearranger.reverse ( suite.groups ().get ( 0 ).rollers() ) );
        
        int n = 7;
        int s = 1;
        
        for ( ResultSet row : new Sql ( db, "select name,sequence from rollers order by sequence" ).go () )
        {
            assertEquals ( "R" + n, row.getString ( 1 ) );
            assertEquals ( s, row.getInt ( 2 ) );
            
            n--;
            s++;
        }
    }
    
    @Test
    public void testDeleteRoller() throws SQLException
    {
        suite.groups ().get ( 0 ).rollers ().remove ( 0 );
        
        assertEquals ( 6, new Sql ( db, "select count (*) from rollers" ).go ().single ().getInt ( 1 ) );
        
        int n = 2;
        int s = 1;
        
        for ( ResultSet row : new Sql ( db, "select name,sequence from rollers order by sequence" ).go () )
        {
            assertEquals ( "R" + n, row.getString ( 1 ) );
            assertEquals ( s, row.getInt ( 2 ) );
            
            n++;
            s++;
        }
        
        assertEquals ( 0, new Sql ( db, "select count (*) from labels" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count (*) from rollerTriggers" ).go ().single ().getInt ( 1 ) );
    }
    
    @Test
    public void testDeleteAllRollers() throws SQLException
    {
        suite.groups ().clear();
        
        assertEquals ( 0, new Sql ( db, "select count (*) from groups" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count (*) from rollers" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count (*) from labels" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count (*) from rollerTriggers" ).go ().single ().getInt ( 1 ) );
    }

    @Test
    public void testAddLabel() throws SQLException
    {
        suite.groups ().get ( 0 ).rollers ().get ( 0 ).labels ().put ( 3, "L3" );
        
        int n = 1;
        
        for ( ResultSet row : new Sql ( db, "select value,label from labels where rollerId = 1 order by value" ).go () )
        {
            assertEquals ( n, row.getInt ( 1 ) );
            assertEquals ( "L" + n, row.getString ( 2 ) );
            
            n++;
        }
    }

    @Test
    public void testReplaceLabel() throws SQLException
    {
        suite.groups ().get ( 0 ).rollers ().get ( 0 ).labels ().put ( 2, "L3" );
        
        assertEquals ( "L3", new Sql ( db, "select label from labels where value = 2" ).go ().single ().getString ( 1 ) );
    }
    
    @Test
    public void testDeleteLabel() throws SQLException
    {
        suite.groups ().get ( 0 ).rollers ().get ( 0 ).labels ().remove ( 1 );

        assertEquals ( 1, new Sql ( db, "select count(*) from labels" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 2, new Sql ( db, "select value from labels" ).go ().single ().getInt ( 1 ) );
    }

    @Test
    public void testDeleteAllLabels() throws SQLException
    {
        suite.groups ().get ( 0 ).rollers ().get ( 0 ).labels ().clear();

        assertEquals ( 0, new Sql ( db, "select count(*) from labels" ).go ().single ().getInt ( 1 ) );
    }
}
