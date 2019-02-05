package duffrd.diceroller.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import duffrd.diceroller.model.sqlite.SqliteDbProvider;
import duffrd.diceroller.model.sqlite.SqliteModelLoader;
import utility.sql.Sql;

public class SuiteInitializationTest
{
    public static SuiteInitializer initializer;

    public Connection db;
    public Suite suite;
    
    @Before
    public void before() throws DiceRollerException, IOException, URISyntaxException, SQLException
    {
        db = SqliteDbProvider.provideInMemoryDB ();
        SqliteModelLoader loader = new SqliteModelLoader ( db );
        
        Model model = loader.load ();
        
        initializer = new SuiteInitializer ( model );        

        suite = model.createSuite ( "Test" );
    }
    
    @Test
    public void testTemplates()
    {
        assertEquals ( 5, initializer.suiteTemplates ().size () );
    }
    
    @Test
    public void testBasicSuite () throws SQLException, DiceRollerException
    {
        initializer.apply ( suite, "Basic Rolls" );
        
        assertEquals (  0, new Sql ( db, "select count(*) from variables").go ().single ().getInt ( 1 ) );
        assertEquals (  2, new Sql ( db, "select count(*) from triggers").go ().single ().getInt ( 1 ) );
        assertEquals (  7, new Sql ( db, "select count(*) from groups").go ().single ().getInt ( 1 ) );
        assertEquals ( 69, new Sql ( db, "select count(*) from rollers").go ().single ().getInt ( 1 ) );
        assertEquals (  0, new Sql ( db, "select count(*) from labels").go ().single ().getInt ( 1 ) );
        assertEquals ( 34, new Sql ( db, "select count(*) from rollerTriggers").go ().single ().getInt ( 1 ) );
    }
    
    @Test
    public void testDndSuite () throws SQLException, DiceRollerException
    {
        initializer.apply ( suite, "Dungeons & Dragons 3.5" );
        
        assertEquals ( 31, new Sql ( db, "select count(*) from variables").go ().single ().getInt ( 1 ) );
        assertEquals (  2, new Sql ( db, "select count(*) from triggers").go ().single ().getInt ( 1 ) );
        assertEquals (  5, new Sql ( db, "select count(*) from groups").go ().single ().getInt ( 1 ) );
        assertEquals ( 51, new Sql ( db, "select count(*) from rollers").go ().single ().getInt ( 1 ) );
        assertEquals (  4, new Sql ( db, "select count(*) from labels").go ().single ().getInt ( 1 ) );
        assertEquals ( 96, new Sql ( db, "select count(*) from rollerTriggers").go ().single ().getInt ( 1 ) );
    }
    
    @Test
    public void testHeroSuite () throws SQLException, DiceRollerException
    {
        initializer.apply ( suite, "Hero System 4.0" );
        
        assertEquals (  0, new Sql ( db, "select count(*) from variables").go ().single ().getInt ( 1 ) );
        assertEquals (  2, new Sql ( db, "select count(*) from triggers").go ().single ().getInt ( 1 ) );
        assertEquals (  3, new Sql ( db, "select count(*) from groups").go ().single ().getInt ( 1 ) );
        assertEquals ( 22, new Sql ( db, "select count(*) from rollers").go ().single ().getInt ( 1 ) );
        assertEquals ( 16, new Sql ( db, "select count(*) from labels").go ().single ().getInt ( 1 ) );
        assertEquals (  4, new Sql ( db, "select count(*) from rollerTriggers").go ().single ().getInt ( 1 ) );
    }
    
    @Test
    public void testMiscSuite () throws SQLException, DiceRollerException
    {
        initializer.apply ( suite, "Miscellaneous" );
        
        assertEquals (  0, new Sql ( db, "select count(*) from variables").go ().single ().getInt ( 1 ) );
        assertEquals (  0, new Sql ( db, "select count(*) from triggers").go ().single ().getInt ( 1 ) );
        assertEquals (  1, new Sql ( db, "select count(*) from groups").go ().single ().getInt ( 1 ) );
        assertEquals (  1, new Sql ( db, "select count(*) from rollers").go ().single ().getInt ( 1 ) );
        assertEquals (  2, new Sql ( db, "select count(*) from labels").go ().single ().getInt ( 1 ) );
        assertEquals (  0, new Sql ( db, "select count(*) from rollerTriggers").go ().single ().getInt ( 1 ) );
    }
    
    @Test
    public void testMunchkinSuite () throws SQLException, DiceRollerException
    {
        initializer.apply ( suite, "Munchkin" );
        
        assertEquals (  0, new Sql ( db, "select count(*) from variables").go ().single ().getInt ( 1 ) );
        assertEquals (  0, new Sql ( db, "select count(*) from triggers").go ().single ().getInt ( 1 ) );
        assertEquals (  1, new Sql ( db, "select count(*) from groups").go ().single ().getInt ( 1 ) );
        assertEquals (  5, new Sql ( db, "select count(*) from rollers").go ().single ().getInt ( 1 ) );
        assertEquals ( 10, new Sql ( db, "select count(*) from labels").go ().single ().getInt ( 1 ) );
        assertEquals (  0, new Sql ( db, "select count(*) from rollerTriggers").go ().single ().getInt ( 1 ) );
    }
}
