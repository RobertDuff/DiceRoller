package duffrd.diceroller.model.sqlite;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.Group;
import duffrd.diceroller.model.Model;
import duffrd.diceroller.model.ModelLoader;
import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.Suite;
import duffrd.diceroller.model.Trigger;
import utility.lua.LuaProvider;
import utility.sql.Sql;

public class SqliteModelLoader implements ModelLoader
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

    Connection db;

    public SqliteModelLoader ( Connection conn ) throws DiceRollerException
    {
        try
        {
            db = conn;
            new Sql ( db, "pragma foreign_keys = on" ).go();
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }
    
    @Override
    public Model load () throws DiceRollerException
    {        
        logger.debug ( "Loading" );
        
        try
        {
            SqliteModel model = new SqliteModel();

            populateModel ( model );
            model.animate ( db );
            
            return model;
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }        
    }
    
    protected void populateModel ( Model model ) throws SQLException
    {
        for ( ResultSet rs : new Sql ( db, "select id,name from suites" ).go () )
        {
            int suiteId = rs.getInt ( 1 );
            String suiteName = rs.getString ( 2 );
            
            SqliteSuite suite = new SqliteSuite ( suiteId );
            
            suite.lua ( LuaProvider.newLua () );
            suite.name ( suiteName );
                        
            populateSuite ( suite, suiteId );
            suite.animate ( db );
            
            model.suitesProperty ().add ( suite );
        }
    }
    
    protected void populateSuite ( Suite suite, int suiteId ) throws SQLException
    {
        //
        // Variables
        //
        
        for ( ResultSet rs : new Sql ( db, "select id, name, value from variables where suiteId = ? order by sequence" ).go ( suiteId ) )
        {
            int varId = rs.getInt ( 1 );
            String varName = rs.getString ( 2 );
            int varVal = rs.getInt ( 3 );
            
            SqliteVariable var = new SqliteVariable ( varId );
            
            var.lua ( suite.lua () );
            var.name ( varName );
            var.value ( varVal );
            
            var.animate ( db, suiteId );
            
            suite.variablesProperty ().add ( var );
        }
        
        //
        // Triggers
        //

        for ( ResultSet rs : new Sql ( db, "select id, name, definition from triggers where suiteId = ?" ).go ( suiteId ) )
        {
            int trigId = rs.getInt ( 1 );
            String trigName = rs.getString ( 2 );
            String trigDef = rs.getString ( 3 );
            
            SqliteTrigger trig= new SqliteTrigger ( trigId );
            
            trig.lua ( suite.lua () );
            trig.name ( trigName );
            trig.definition ( trigDef );
            
            trig.animate ( db, suiteId );
            
            suite.triggersProperty ().add ( trig );        
        }

        //
        // Groups
        //
        
        for ( ResultSet rs : new Sql ( db, "select id, name from groups where suiteId = ? order by sequence" ).go ( suiteId ) )
        {
            int groupId = rs.getInt ( 1 );
            String groupName = rs.getString ( 2 );
            
            SqliteGroup group = new SqliteGroup ( groupId );
            
            group.lua ( suite.lua () );
            group.name ( groupName );

            populateGroup ( group, suiteId, groupId, suite.triggersProperty () );
            
            group.animate ( db, suiteId );
            
            suite.groupsProperty ().add ( group );
        }
    }

    protected void populateGroup ( Group group, int suiteId, int groupId, Set<Trigger> triggers ) throws SQLException
    {
        for ( ResultSet rs : new Sql ( db, "select id, name, definition from rollers where groupId = ? order by sequence" ).go ( groupId ) )
        {
            int rollerId = rs.getInt ( 1 );
            String rollerName = rs.getString ( 2 );
            String rollerDef = rs.getString ( 3 );
            
            SqliteRoller roller = new SqliteRoller ( rollerId );
            
            roller.lua ( group.lua () );
            roller.name ( rollerName );
            logger.debug ( "Def: " + rollerDef );
            roller.definition ( rollerDef );
                        
            populateRoller ( roller, rollerId, triggers );
            
            roller.animate ( db, suiteId, groupId );
            
            group.rollersProperty ().add ( roller );
        }
    }
    
    protected void populateRoller ( Roller roller, int rollerId, Set<Trigger> triggers ) throws SQLException
    {
        for ( ResultSet rs : new Sql ( db, "select value, label from labels where rollerId = ?" ).go ( rollerId ) )
        {
            int value = rs.getInt ( 1 );
            String label = rs.getString ( 2 );
            
            roller.labelsProperty ().put ( value, label );
        }

        for ( ResultSet rs : new Sql ( db, "select t.name from triggers as t join rollerTriggers as rt on rt.triggerId = t.id where rt.rollerId = ?" ).go ( rollerId ) )
        {
            String triggerName = rs.getString ( 1 );
            
            for ( Trigger trigger : triggers )
                if ( trigger.name ().equals ( triggerName ) )
                    roller.triggersProperty ().add ( trigger );
        }
    }
}
