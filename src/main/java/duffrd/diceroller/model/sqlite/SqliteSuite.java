package duffrd.diceroller.model.sqlite;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import duffrd.diceroller.model.Group;
import duffrd.diceroller.model.Suite;
import duffrd.diceroller.model.Trigger;
import duffrd.diceroller.model.Variable;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import utility.sql.Sql;

public class SqliteSuite extends Suite
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

    protected Integer id = null;
    protected Connection db = null;
    
    public SqliteSuite()
    {
        super();
    }
    
    public SqliteSuite ( int id )
    {
        this();
        this.id = id;
    }
    
    @Override
    public Variable newVariable ()
    {
        return new SqliteVariable ().animate ( db, id ).lua ( lua () );
    }

    @Override
    public Trigger newTrigger ()
    {
        return new SqliteTrigger ().animate ( db, id ).lua ( lua () );
    }

    @Override
    public Group newGroup ()
    {
        return new SqliteGroup().animate ( db, id ).lua ( lua () );
    }

    SqliteSuite animate ( Connection conn )
    {
        logger.debug ( "Animating Suite" );
        
        db = conn;
        
        nameProperty.addListener ( ( prop, oldName, newName ) -> 
        {
            try
            {
                boolean o = oldName != null && !oldName.isEmpty ();
                boolean n = newName != null && !newName.isEmpty ();
                
                if ( n )
                {
                    if ( id == null )
                    {
                        // Create New Suite
                        logger.debug ( "DB: Inserting Suite " + newName );
                        new Sql ( db, "insert into suites ( name ) values ( ? )" ).go ( newName );
                        id = new Sql ( db, "select max ( id ) from suites" ).go ().single ().getInt ( 1 );
                        logger.debug ( "DB: New Suite ID = " + id );
                    }
                    else
                    {
                        // Renaming ( Expect ID to be set )
                        logger.debug ( "DB: Renaming Suite with no name to " + newName );
                        new Sql ( db, "update suites set name = ? where id = ?" ).go ( newName, id );
                    }
                }
                else if ( o )
                {
                    // Renaming to Blank, so use placeholder name ( Expect ID to be set )
                    logger.debug ( "DB: Renaming Suite " + oldName + " to blank value" );
                    new Sql ( db, "update suites set name = ? where id = ?" ).go ( "<unnamed-suite>", id );                    
                }
            }
            catch ( SQLException e )
            {
                e.printStackTrace();
            }
        } );
        
        variablesProperty.addListener ( ( ListChangeListener.Change<? extends Variable> change ) -> 
        {
            try
            {
                while ( change.next () )
                {
                    if ( change.wasPermutated () )
                    {
                        Map<String,Integer> order = new HashMap<>();
                        
                        for ( int oldPos = 0; oldPos < change.getList ().size (); oldPos++ )
                        {
                            int newPos = change.getPermutation ( oldPos );
                            order.put ( change.getList ().get ( newPos ).name (), newPos+1 );
                        }
                        
                        for ( String name : order.keySet () )
                        {
                            logger.debug ( "DB: Permuting Variable from " + name + " to " + order.get ( name ) );
                            new Sql ( db, "update variables set sequence = ? where suiteId = ? and name = ?" ).go ( order.get ( name ), id, name );
                        }
                    }
                    else if ( change.wasReplaced () )
                    {
                        logger.error ( "DB: Variable Should not be replaced" );
                        // Should never happen
                    }
                    else if ( change.wasAdded () )
                    {
                        // Do Nothing, Let the new Variable handle it.
                    }
                    else if ( change.wasRemoved () )
                    {
                        for ( Variable v : change.getRemoved () )
                        {
                            logger.debug ( "Deleting Variable " + v.name () );
                            new Sql ( db, "delete from variables where suiteId = ? and name = ?" ).go ( id, v.name () );
                        }
                    }
                }
            }
            catch ( SQLException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } );
        
        triggersProperty.addListener ( ( SetChangeListener.Change<? extends Trigger> change ) -> 
        {
            try
            {
                if ( change.wasAdded () )
                {
                    // Do Nothing, Let the Trigger handle it.
                }
                
                if ( change.wasRemoved () )
                {
                    logger.debug ( "DB: Deleting Trigger " + change.getElementRemoved ().name () );
                    new Sql ( db, "delete from triggers where suiteId = ? and name = ?" ).go ( id, change.getElementRemoved ().name () );
                }
            }
            catch ( SQLException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } );
        
        groupsProperty.addListener ( ( ListChangeListener.Change<? extends Group> change ) -> 
        {
            try
            {
                while ( change.next () )
                {
                    if ( change.wasPermutated () )
                    {
                        Map<String,Integer> order = new HashMap<>();
                        
                        for ( int oldPos = 0; oldPos < change.getList ().size (); oldPos++ )
                        {
                            int newPos = change.getPermutation ( oldPos );
                            order.put ( change.getList ().get ( newPos ).name (), newPos+1 );
                        }
                        
                        for ( String name : order.keySet () )
                        {
                            logger.debug ( "DB: Permuting Group from " + name + " to " + order.get ( name ) );
                            new Sql ( db, "update groups set sequence = ? where suiteId = ? and name = ?" ).go ( order.get ( name ), id, name );
                        }
                    }
                    else if ( change.wasReplaced () )
                    {
                        // Should never happen
                        logger.error ( "DB: Should not replace Group" );
                    }
                    else if ( change.wasAdded () )
                    {
                        // Do Nothing, Let the new Group handle it.
                    }
                    else if ( change.wasRemoved () )
                    {
                        for ( Group g : change.getRemoved () )
                        {
                            logger.debug ( "DB: Deleting Group " + g.name () );
                            new Sql ( db, "delete from groups where suiteId = ? and name = ?" ).go ( id, g.name () );
                        }
                    }
                }
            }
            catch ( SQLException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } );
        
        return this;
    }
}
