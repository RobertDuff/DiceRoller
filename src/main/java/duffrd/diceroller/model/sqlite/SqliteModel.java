package duffrd.diceroller.model.sqlite;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import duffrd.diceroller.model.Model;
import duffrd.diceroller.model.Suite;
import javafx.collections.SetChangeListener;
import utility.lua.LuaProvider;
import utility.sql.Sql;

public class SqliteModel extends Model
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

    protected Connection db = null;
    
    @Override
    public Suite newSuite ()
    {
        return new SqliteSuite().animate ( db ).lua ( LuaProvider.newLua () );
    }

    SqliteModel animate ( Connection conn )
    {
        logger.debug ( "Animating Model" );
        
        db = conn;
        
        suitesProperty.addListener ( ( SetChangeListener.Change<? extends Suite> change ) -> 
        {
            try
            {
                if ( change.wasAdded () )
                {
                    // Do Nothing, Let the new Suite handle it.
                }
                
                if ( change.wasRemoved () )
                {
                    logger.debug ( "DB: Deleting Suite " + change.getElementRemoved ().name () );
                    new Sql ( db, "delete from suites where name = ?" ).go ( change.getElementRemoved ().name () );
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
