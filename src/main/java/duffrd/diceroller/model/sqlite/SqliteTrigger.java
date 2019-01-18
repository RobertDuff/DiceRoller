package duffrd.diceroller.model.sqlite;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import duffrd.diceroller.model.Trigger;
import utility.sql.Sql;

public class SqliteTrigger extends Trigger
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

    protected Connection db;
    protected Integer id = null;
    
    public SqliteTrigger()
    {
        super();
    }
    
    public SqliteTrigger ( int id )
    {
        this();
        this.id = id;
    }

    SqliteTrigger animate ( Connection conn, int suiteId )
    {
        logger.debug ( "Animating Trigger" );
        
        db = conn;
        
        nameProperty.addListener ( ( prop, oldName, newName ) -> 
        {
            try
            {
                boolean o = oldName != null && !oldName.isEmpty ();
                boolean n = newName != null && !newName.isEmpty ();
                
                if ( o && n )
                {
                    // Renaming ( Expect ID to be set )
                    logger.debug ( "DB: Renaming Trigger " + oldName + " to " + newName );
                    new Sql ( db, "update triggers set name = ? where id = ?" ).go ( newName, id );
                }
                else if ( o )
                {
                    // Renaming to Blank, so use placeholder name ( Expect ID to be set )
                    logger.debug ( "DB: Renaming Trigger " + oldName + " to blank value" ); 
                    new Sql ( db, "update triggers set name = ? where id = ?" ).go ( "<unnamed-trigger>", id );                    
                }
                else if ( n )
                {
                    if ( id == null )
                    {
                        // Create New Trigger
                        logger.debug ( "DB: Inserting Trigger " + newName );
                        new Sql ( db, "insert into triggers ( suiteId, name ) values ( ?, ? )" ).go ( suiteId, newName );
                        id = new Sql ( db, "select max ( id ) from triggers" ).go ().single ().getInt ( 1 );
                        logger.debug ( "DB: New Trigger ID = " + id );
                    }
                    else
                    {
                        // Renaming 
                        logger.debug ( "DB: Renaming unnamed trigger to " + newName );
                        new Sql ( db, "update triggers set name = ? where id = ?" ).go ( newName, id );
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
                
                logger.debug ( "DB: Setting Trigger " + id + " Definition: " + newValue );
                new Sql ( db, "update triggers set definition = ? where id = ?" ).go ( newValue, id );
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
