package duffrd.diceroller.model.sqlite;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.Trigger;
import javafx.collections.MapChangeListener;
import javafx.collections.SetChangeListener;
import utility.sql.Sql;

public class SqliteRoller extends Roller
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

    protected Integer id = null;
    
    public SqliteRoller ()
    {
        super();
    }
    
    public SqliteRoller ( int id )
    {
        this();
        this.id = id;
    }

    SqliteRoller animate ( Connection db, int suiteId, int groupId )
    {
        logger.debug ( "Animating Roller" );
        
        nameProperty.addListener ( ( prop, oldName, newName ) -> 
        {
            try
            {
                boolean o = oldName != null && !oldName.isEmpty ();
                boolean n = newName != null && !newName.isEmpty ();
                
                if ( o && n )
                {
                    // Renaming ( Expect ID to be set )d
                    logger.debug ( "DB: Renaming Roller from " + oldName + " to " + newName );
                    new Sql ( db, "update rollers set name = ? where id = ?" ).go ( newName, id );
                }
                else if ( o )
                {
                    // Renaming to Blank, so use placeholder name ( Expect ID to be set )
                    logger.debug ( "DB: Renaming Roller " + oldName + " to blank value" );
                    new Sql ( db, "update rollers set name = ? where id = ?" ).go ( "<unnamed-roller>", id );                    
                }
                else if ( n )
                {
                    if ( id == null )
                    {
                        // Create New Roller
                        logger.debug ( "DB: Inserting new Roller " + newName );
                        new Sql ( db, "insert into rollers ( groupId, name ) values ( ?, ? )" ).go ( groupId, newName );
                        id = new Sql ( db, "select max ( id ) from rollers" ).go ().single ().getInt ( 1 );
                        logger.debug ( "DB: New Roller ID = " + id );
                    }
                    else
                    {
                        // Renaming 
                        logger.debug ( "DB: Renaming unnamed roller to " + newName );
                        new Sql ( db, "update rollers set name = ? where id = ?" ).go ( newName, id );
                    }
                }
            }
            catch ( SQLException e )
            {
                e.printStackTrace();
            }
        } );
        
        definitionProperty.addListener ( ( prop, oldValue, newValue ) -> 
        {
            try
            {
                if ( newValue == null )
                    newValue = "";
                
                logger.debug ( "DB: Changing Roller " + id + " Definition to" + newValue );
                new Sql ( db, "update rollers set definition = ? where id = ?" ).go ( newValue, id );
            }
            catch ( SQLException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } );
        
        labelsProperty.addListener ( ( MapChangeListener.Change<? extends Integer,? extends String> change ) -> 
        {
            try
            {
                int value = change.getKey ();
                String label = change.getValueAdded ();
                boolean isBlank = label == null || label.isEmpty ();
                
                boolean r = change.wasRemoved ();
                boolean a = change.wasAdded ();
                
                if ( r && a )
                {
                    if ( isBlank )
                    {
                        // Remove Label
                        logger.debug ( "DB: Deleting Label for " + value + " for Roller " + id );
                        new Sql ( db, "delete from labels where rollerId = ? and value = ?" ).go ( id, value );
                    }
                    else
                    {
                        // Change Label
                        logger.debug ( "DB: Changing " + value + " Label to " + label );
                        new Sql ( db, "update labels set label = ? where rollerId = ? and value = ?" ).go ( label, id, value );
                    }
                }
                else if ( a )
                {
                    if ( isBlank )
                    {
                        // Do Nothing.  We don't add empty labels.
                    }
                    else
                    {
                        // Create new Label
                        logger.debug ( "DB: Inserting Label " + label + " for Value " + value );
                        new Sql ( db, "insert into labels values ( ?, ?, ? )" ).go ( id, value, label );
                    }
                }
                else if ( r )
                {
                    // Remove Label
                    logger.debug ( "DB: Deleting Label for Value " + value );
                    new Sql ( db, "delete from labels where rollerId = ? and value = ?" ).go ( id, value );
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
                if ( change.wasRemoved () )
                {
                    Trigger o = change.getElementRemoved ();
                    int oldId = new Sql ( db, "select id from triggers where suiteId = ? and name = ?" ).go ( suiteId, o.name () ).single ().getInt ( 1 );

                    logger.debug ( "DB: Deleting Trigger " + oldId + " for Roller " + id );
                    new Sql ( db, "delete from rollerTriggers where rollerId = ? and triggerId = ?" ).go ( id, oldId );
                }
                
                if ( change.wasAdded () )
                {
                    Trigger n = change.getElementAdded ();
                    int newId = new Sql ( db, "select id from triggers where suiteId = ? and name = ?" ).go ( suiteId, n.name () ).single ().getInt ( 1 );

                    logger.debug ( "DB: Inserting Trigger " + newId + " for Roller " + id );
                    new Sql ( db, "insert into rollerTriggers values ( ?, ? )" ).go ( id, newId );
                }
            }
            catch ( SQLException e )
            {
                e.printStackTrace();
            }
        } );
        
        return this;
    }
}
