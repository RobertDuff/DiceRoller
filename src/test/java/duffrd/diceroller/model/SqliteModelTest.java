package duffrd.diceroller.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import duffrd.diceroller.model.sqlite.SqliteGroup;
import duffrd.diceroller.model.sqlite.SqliteModelLoader;
import duffrd.diceroller.model.sqlite.SqliteRoller;
import duffrd.diceroller.model.sqlite.SqliteSuite;
import duffrd.diceroller.model.sqlite.SqliteTrigger;
import utility.collections.SetOperations;
import utility.sql.Sql;

public class SqliteModelTest
{
    public static final String TEST_DATA = "modelTest.yaml";
    
    @Rule
    public ExpectedException thrown = ExpectedException.none ();
    
    public Connection db;
    public ModelLoader loader;
    public Model model;
    public SqliteSuite suite;
    
    @Before
    public void before() throws IOException, SQLException, DiceRollerException
    {
        db = SqliteDbProvider.provideInMemoryDB ();
        loader = new SqliteModelLoader ( db );
        model = loader.load ();
        
        Map<String,Object> suiteSpec = new Yaml ().load ( ClassLoader.getSystemResourceAsStream ( TEST_DATA ) );
        
        suite = ( SqliteSuite ) model.createSuite ( suiteSpec.get ( "suite" ).toString () );
        
        SuiteInitializer suiteInitializer = new SuiteInitializer ( model );
        suiteInitializer.apply ( suite, suiteSpec );  
        
        model = loader.load ();
        suite = ( SqliteSuite ) model.suites().iterator ().next ();
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
        
        assertEquals ( "Exactly 7 Groups", 7, n );
        
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
        assertEquals ( model, suite.model () );
        
        // Variables
        
        assertEquals ( 6, suite.variables ().size () );
        
        int n = 0;
        for ( Variable v : suite.variables () )
        {
            assertEquals ( "V" + n, v.name () );
            assertEquals ( n * 10, v.value () );
            assertEquals ( n * 10, v.lua ().get ( "V" + n ).toint () );
            assertEquals ( suite, v.suite () );
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
            assertEquals ( suite, trigger.suite () );
            n++;
        }
        
        //TODO: Figure out a way to verify triggers
        
        // Groups
        
        assertEquals ( 7, suite.groups ().size () );
        
        for ( int g=0; g < suite.groups ().size (); g++ )
        {
            Group group = suite.groups ().get ( g );
            
            assertEquals ( "G" + ( g+1 ), group.name () );
            assertEquals ( suite, group.suite () );
            
            if ( g == 0 )
            {
                assertEquals ( 7, group.rollers ().size () );
                
                for ( int r=0; r < group.rollers ().size (); r++ )
                {
                    Roller roller = group.rollers ().get ( r );
                    
                    assertEquals ( "R" + ( r+1 ), roller.name () );
                    assertEquals ( "d4", roller.definition () );
                    assertTrue ( roller.isValid () );
                    assertEquals ( group, roller.group () );
                    
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
    public void testCreateSuite() throws SQLException, DiceRollerException
    {
        Suite suite = model.createSuite ( "S2" );
        
        assertNotNull ( suite.lua () );        
        assertEquals ( "S2", new Sql ( db, "select name from suites where id = ( select max ( id ) from suites )" ).go ().single ().getString ( 1 ) );
    }
    
    @Test
    public void testUpdateSuite() throws SQLException, DiceRollerException
    {
        model.updateSuite ( suite, "S2" );
        
        assertEquals ( "S2", suite.name () );
        
        assertEquals ( "S2", new Sql ( db, "select name from suites" ).go ().single ().getString ( 1 ) );
    }
    
    @Test
    public void testDeleteSuite() throws SQLException, DiceRollerException
    {
        model.deleteSuite ( suite );
        
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
    public void testCreateVariable() throws SQLException, DiceRollerException
    {
        Variable v = model.createVariable ( suite, "VN", 67 );
        
        assertNotNull ( v.lua () );
        
        ResultSet row = new Sql ( db, "select suiteId,name,value,sequence from variables where id = ( select max ( id ) from variables )" ).go ().single ();
        
        assertEquals ( 1, row.getInt ( 1 ) );
        assertEquals ( "VN", row.getString ( 2 ) );
        assertEquals ( 67, row.getInt ( 3 ) );
        assertEquals ( 7, row.getInt ( 4 ) );
    }
    
    @Test
    public void testUpdateVariables() throws SQLException, DiceRollerException
    {
        List<Variable> variables = new ArrayList<>();
        
        variables.add ( new Variable().name ( "V10" ).value ( 10 ) );
        variables.add ( new Variable().name ( "V11" ).value ( 11 ) );
        variables.add ( new Variable().name ( "V12" ).value ( 12 ) );
        
        model.updateVariables ( suite, variables );
        
        assertEquals ( 3, suite.variables ().size () );
     
        int n = 10;
        
        for ( Variable variable : suite.variables () )
        {
            assertNotNull ( variable.lua () );
            assertEquals ( "V" + n, variable.name () );
            assertEquals ( n++, variable.value () );
        }
        
        n = 10;
        int s = 1;
        
        for ( ResultSet row : new Sql ( db, "select suiteId,name,value,sequence from variables where suiteId=?" ).go ( suite.id () ) )
        {
            assertEquals ( 1, row.getInt ( 1 ) );
            assertEquals ( "V" + n, row.getString ( 2 ) );
            assertEquals ( n++, row.getInt ( 3 ) );
            assertEquals ( s++, row.getInt ( 4 ) );
        }
    }
    
    @Test
    public void testDeleteAllVariables() throws SQLException, DiceRollerException
    {
        model.updateVariables ( suite, Collections.emptyList () );
        
        assertEquals ( 0, suite.variables ().size () );
        
        assertEquals ( 0, new Sql ( db, "select count (*) from variables" ).go ().single ().getInt ( 1 ) );
    }
    
    @Test
    public void testCreateTrigger() throws SQLException, DiceRollerException
    {
        SqliteTrigger trigger = ( SqliteTrigger ) model.createTrigger ( suite, "T3", "A==1" );
        
        assertTrue ( suite.triggersProperty.contains ( trigger ) );
        assertNotNull ( trigger.lua () );
        assertEquals ( "T3", trigger.name () );
        assertEquals ( "A==1", trigger.definition () );
        assertTrue ( trigger.isValid () );
        
        ResultSet row = new Sql ( db, "select suiteId,name,definition from triggers where id=?" ).go ( trigger.id() ).single ();
        
        assertEquals ( 1, row.getInt ( 1 ) );
        assertEquals ( "T3", row.getString ( 2 ) );
        assertEquals ( "A==1", row.getString ( 3 ) );
    }
    
    @Test
    public void testUpdateTrigger() throws SQLException, DiceRollerException
    {
        SqliteTrigger trigger = ( SqliteTrigger ) SetOperations.findAny ( suite.triggers (), t -> t.name ().equals ( "T1" ) );
        
        assertNotNull ( trigger );

        model.updateTrigger ( trigger, "TR", "OUTCOME==34" );
        
        assertEquals ( "TR", trigger.name () );
        assertEquals ( "OUTCOME==34", trigger.definition () );
        assertTrue ( trigger.isValid () );
        
        ResultSet row = new Sql ( db, "select name,definition from triggers where id=?" ).go ( trigger.id () ).single ();
        assertEquals ( "TR", row.getString ( 1 ) );
        assertEquals ( "OUTCOME==34", row.getString ( 2 ) );
    }
    
    @Test
    public void testDeleteTrigger() throws SQLException, DiceRollerException
    {
        SqliteTrigger trigger = ( SqliteTrigger ) SetOperations.findAny ( suite.triggers(), t -> t.name ().equals ( "T1" ) );

        model.deleteTrigger ( trigger );
        
        assertEquals ( 1, suite.triggers().size () );
        assertFalse ( suite.triggers ().contains ( trigger ) );
        
        assertEquals ( 1, new Sql ( db, "select count (*) from triggers" ).go ().single ().getInt ( 1 ) );
        assertEquals ( "T2", new Sql ( db, "select name from triggers" ).go ().single ().getString ( 1 ) );
        
        assertEquals ( 1, new Sql ( db, "select count (*) from rollerTriggers" ).go ().single ().getInt ( 1 ) );
        
        ResultSet row = new Sql ( db, "select rollerId,triggerId from rollerTriggers" ).go ().single ();
        
        assertEquals ( 1, row.getInt ( 1 ) );
        assertEquals ( 2, row.getInt ( 2 ) );
    }
    
    @Test
    public void testCreateGroup() throws SQLException, DiceRollerException
    {
        SqliteGroup group = ( SqliteGroup ) model.createGroup ( suite, "GN" );
        
        assertTrue ( suite.groups ().contains ( group ) );
        
        assertNotNull ( group.lua () );
        assertEquals ( "GN", group.name () );
        
        ResultSet row = new Sql ( db, "select suiteId,name,sequence from groups where id = ( select max ( id ) from groups )" ).go ().single ();
        
        assertEquals ( 1, row.getInt ( 1 ) );
        assertEquals ( "GN", row.getString ( 2 ) );
        assertEquals ( 8, row.getInt ( 3 ) );
    }
    
    @Test
    public void testUpdateGroup() throws SQLException, DiceRollerException
    {
        SqliteGroup group = ( SqliteGroup ) suite.groups ().get ( 0 );

        model.updateGroup ( group, "GN" );
        
        assertEquals ( "GN", group.name () );
        
        assertEquals ( "GN", new Sql ( db, "select name from groups where id=?" ).go ( group.id () ).single ().getString ( 1 ) );
    }
    
    @Test
    public void testMoveGroup() throws SQLException, DiceRollerException
    {        
        // Move First Down 1
        model.moveGroup ( suite.groups ().get ( 0 ), 2 );
        assertGroupOrder ( 2, 1, 3, 4, 5, 6, 7 );
        
        // Move First Down 2
        model.moveGroup ( suite.groups ().get ( 0 ), 3 );
        assertGroupOrder ( 1, 3, 2, 4, 5, 6, 7 );
        
        // Move First to Last
        model.moveGroup ( suite.groups ().get ( 0 ), 7 );
        assertGroupOrder ( 3, 2, 4, 5, 6, 7, 1 );
        
        // Move Last Up 1
        model.moveGroup ( suite.groups ().get ( 6 ), 6 );
        assertGroupOrder ( 3, 2, 4, 5, 6, 1, 7 );
        
        // Move Last Up 2
        model.moveGroup ( suite.groups ().get ( 6 ), 5 );
        assertGroupOrder ( 3, 2, 4, 5, 7, 6, 1 );
        
        // Move Last to First
        model.moveGroup ( suite.groups ().get ( 6 ), 1 );
        assertGroupOrder ( 1, 3, 2, 4, 5, 7, 6 );
        
        // Move Middle Up 1
        model.moveGroup ( suite.groups ().get ( 3 ), 3 );
        assertGroupOrder ( 1, 3, 4, 2, 5, 7, 6 );
        
        // Move Middle Up 2
        model.moveGroup ( suite.groups ().get ( 3 ), 2 );
        assertGroupOrder ( 1, 2, 3, 4, 5, 7, 6 );
        
        // Move Middle To First
        model.moveGroup ( suite.groups ().get ( 3 ), 1 );
        assertGroupOrder ( 4, 1, 2, 3, 5, 7, 6 );
        
        // Move Middle Down 1
        model.moveGroup ( suite.groups ().get ( 3 ), 5 );
        assertGroupOrder ( 4, 1, 2, 5, 3, 7, 6 );
        
        // Move Middle Down 2
        model.moveGroup ( suite.groups ().get ( 3 ), 6 );
        assertGroupOrder ( 4, 1, 2, 3, 7, 5, 6 );
        
        // Move Middle To Last
        model.moveGroup ( suite.groups ().get ( 3 ), 7 );
        assertGroupOrder ( 4, 1, 2, 7, 5, 6, 3 );
    }
    
    @Test
    public void testDeleteGroup() throws SQLException, DiceRollerException
    {
        SqliteGroup group = ( SqliteGroup ) suite.groups ().get ( 1 );

        model.deleteGroup ( group );
        
        assertEquals ( 6, suite.groups ().size () );
        assertFalse ( suite.groups ().contains ( group ) );

        assertGroupOrder ( 1, 3, 4, 5, 6, 7 );
    }
    
    private void assertGroupOrder ( int... order ) throws SQLException
    {
        assertEquals ( order.length, suite.groups ().size () );
        
        for ( int n=0; n<order.length; n++ )
            assertEquals ( "G" + order[ n ], suite.groups ().get ( n ).name() );
        
        int n = 0;
        
        for ( ResultSet row : new Sql ( db, "select name,sequence from groups order by sequence" ).go() )
        {
            assertEquals ( "G" + order[ n ], row.getString ( 1 ) );
            assertEquals ( ++n, row.getInt ( 2 ) );
        }
        
        assertEquals ( order.length, n );
    }
    
    @Test
    public void testCreateRoller() throws SQLException, DiceRollerException
    {
        SqliteGroup group = ( SqliteGroup ) suite.groups ().get ( 1 );
        SqliteRoller roller = ( SqliteRoller ) model.createRoller ( group, "RN", "d4" );
        
        assertEquals ( 1, group.rollers ().size () );
        assertTrue ( group.rollers ().contains ( roller ) );

        assertNotNull ( roller.lua () );
        assertEquals ( "RN", roller.name () );
        assertEquals ( "d4", roller.definition () );
        assertTrue ( roller.isValid () );
        
        ResultSet row = new Sql ( db, "select groupId,name,definition,sequence from rollers where id = ( select max ( id ) from rollers )" ).go ().single ();
        
        assertEquals ( 2, row.getInt ( 1 ) );
        assertEquals ( "RN", row.getString ( 2 ) );
        assertEquals ( "d4", row.getString ( 3 ) );
        assertEquals ( 1, row.getInt ( 4 ) );
    }
    
    @Test
    public void testUpdateRoller() throws SQLException, DiceRollerException
    {
        SqliteRoller roller = ( SqliteRoller ) suite.groups ().get ( 0 ).rollers ().get ( 0 );

        model.updateRoller ( roller, "RN", "d8" );
        
        assertEquals ( "RN", roller.name () );
        assertEquals ( "d8", roller.definition () );
        assertTrue ( roller.isValid () );
        
        ResultSet row = new Sql ( db, "select name,definition from rollers where id=?" ).go ( roller.id () ).single ();
        
        assertEquals ( "RN", row.getString ( 1 ) );
        assertEquals ( "d8", row.getString ( 2 ) );
    }
    
    @Test
    public void testRearrangeRollers() throws SQLException, DiceRollerException
    {
        SqliteGroup group = ( SqliteGroup ) suite.groups ().get ( 0 );
        
        // Move First Down 1
        model.moveRoller ( group.rollers ().get ( 0 ), 2 );
        assertRollerOrder ( group, 2, 1, 3, 4, 5, 6, 7 );
        
        // Move First Down 2
        model.moveRoller ( group.rollers ().get ( 0 ), 3 );
        assertRollerOrder ( group, 1, 3, 2, 4, 5, 6, 7 );
        
        // Move First to Last
        model.moveRoller ( group.rollers ().get ( 0 ), 7 );
        assertRollerOrder ( group, 3, 2, 4, 5, 6, 7, 1 );
        
        // Move Last Up 1
        model.moveRoller ( group.rollers ().get ( 6 ), 6 );
        assertRollerOrder ( group, 3, 2, 4, 5, 6, 1, 7 );
        
        // Move Last Up 2
        model.moveRoller ( group.rollers ().get ( 6 ), 5 );
        assertRollerOrder ( group, 3, 2, 4, 5, 7, 6, 1 );
        
        // Move Last to First
        model.moveRoller ( group.rollers ().get ( 6 ), 1 );
        assertRollerOrder ( group, 1, 3, 2, 4, 5, 7, 6 );
        
        // Move Middle Up 1
        model.moveRoller ( group.rollers ().get ( 3 ), 3 );
        assertRollerOrder ( group, 1, 3, 4, 2, 5, 7, 6 );
        
        // Move Middle Up 2
        model.moveRoller ( group.rollers ().get ( 3 ), 2 );
        assertRollerOrder ( group, 1, 2, 3, 4, 5, 7, 6 );
        
        // Move Middle To First
        model.moveRoller ( group.rollers ().get ( 3 ), 1 );
        assertRollerOrder ( group, 4, 1, 2, 3, 5, 7, 6 );
        
        // Move Middle Down 1
        model.moveRoller ( group.rollers ().get ( 3 ), 5 );
        assertRollerOrder ( group, 4, 1, 2, 5, 3, 7, 6 );
        
        // Move Middle Down 2
        model.moveRoller ( group.rollers ().get ( 3 ), 6 );
        assertRollerOrder ( group, 4, 1, 2, 3, 7, 5, 6 );
        
        // Move Middle To Last
        model.moveRoller ( group.rollers ().get ( 3 ), 7 );
        assertRollerOrder ( group, 4, 1, 2, 7, 5, 6, 3 );
    }
    
    @Test
    public void testDeleteRoller() throws SQLException, DiceRollerException
    {
        SqliteGroup group = ( SqliteGroup ) suite.groups ().get ( 0 );
        SqliteRoller roller = ( SqliteRoller ) group.rollers ().get ( 0 );
        
        model.deleteRoller ( roller );
        
        assertFalse ( group.rollers ().contains ( roller ) );
        
        assertRollerOrder ( group, 2, 3, 4, 5, 6, 7 );
        
        assertEquals ( 0, new Sql ( db, "select count (*) from labels" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( db, "select count (*) from rollerTriggers" ).go ().single ().getInt ( 1 ) );
    }
    
    private void assertRollerOrder ( SqliteGroup group, int... order ) throws SQLException
    {
        assertEquals ( order.length, group.rollers ().size () );
        
        for ( int n=0; n<order.length; n++ )
            assertEquals ( "R" + order[ n ], group.rollers ().get ( n ).name() );
        
        int n = 0;
        
        for ( ResultSet row : new Sql ( db, "select name,sequence from rollers where groupId=? order by sequence" ).go ( group.id() ) )
        {
            assertEquals ( "R" + order[ n ], row.getString ( 1 ) );
            assertEquals ( ++n, row.getInt ( 2 ) );
        }
        
        assertEquals ( order.length, n );
    }

    @Test
    public void testCreateLabel() throws SQLException, DiceRollerException
    {
        SqliteRoller roller = ( SqliteRoller ) suite.groups ().get ( 0 ).rollers ().get ( 0 );
        
        model.createLabel ( roller, 3, "L3" );
        
        int n = 0;
        
        for ( ResultSet row : new Sql ( db, "select value,label from labels where rollerId=? order by value" ).go ( roller.id () ) )
        {
            n++;
            
            assertEquals ( n, row.getInt ( 1 ) );
            assertEquals ( "L" + n, row.getString ( 2 ) );
        }
        
        assertEquals ( 3, n );
    }

    @Test
    public void testUpdateLabels() throws SQLException, DiceRollerException
    {
        SqliteRoller roller = ( SqliteRoller ) suite.groups ().get ( 0 ).rollers ().get ( 0 );

        Map<Integer,String> labels = new HashMap<> ();
        
        labels.put ( 10, "L10" );
        labels.put ( 11, "L11" );
        labels.put ( 12, "L12" );
        labels.put ( 13, "L13" );
        
        model.updateLabels ( roller, labels );
        
        assertEquals ( 4, roller.labels ().size () );
        
        for ( int n = 10; n < 14; n++ )
        {
            assertTrue ( roller.labels ().containsKey ( n ) );
            assertEquals ( "L" + n, roller.labels ().get ( n ) );
        }
        
        int n = 10;
        
        for ( ResultSet row : new Sql ( db, "select value,label from labels where rollerId=?" ).go ( roller.id () ) )
        {
            assertEquals ( n, row.getInt ( 1 ) );
            assertEquals ( "L" + n++, row.getString ( 2 ) );
        }
        
        assertEquals ( 14, n );
    }
}
