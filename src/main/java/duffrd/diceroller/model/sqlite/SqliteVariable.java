package duffrd.diceroller.model.sqlite;

import java.sql.Connection;
import java.sql.SQLException;

import duffrd.diceroller.model.Variable;
import utility.sql.Sql;

public class SqliteVariable extends Variable
{   
    protected Integer id = null;
    
    public SqliteVariable()
    {
        super();
    }
    
    public SqliteVariable ( int id )
    {
        this();
        this.id = id;
    }
    
    SqliteVariable animate ( Connection db, int suiteId )
    {
        nameProperty.addListener ( ( prop, oldName, newName ) -> 
        {
            try
            {
                boolean o = oldName != null && !oldName.isEmpty ();
                boolean n = newName != null && !newName.isEmpty ();
                
                if ( o && n )
                {
                    // Renaming ( Expect ID to be set )
                    new Sql ( db, "update variables set name = ? where id = ?" ).go ( newName, id );
                }
                else if ( o )
                {
                    // Renaming to Blank, so use placeholder name ( Expect ID to be set )
                    new Sql ( db, "update variables set name = ? where id = ?" ).go ( "<unnamed-variable>", id );                    
                }
                else if ( n )
                {
                    if ( id == null )
                    {
                        // Create New Variable
                        new Sql ( db, "insert into variables ( suiteId, name ) values ( ?, ? )" ).go ( suiteId, newName );
                        id = new Sql ( db, "select max ( id ) from variables" ).go ().single ().getInt ( 1 );
                    }
                    else
                    {
                        // Renaming 
                        new Sql ( db, "update variables set name = ? where id = ?" ).go ( newName, id );
                    }
                }
            }
            catch ( SQLException e )
            {
                e.printStackTrace();
            }
        } );
        
        valueProperty.addListener ( ( prop, oldValue, newValue ) -> 
        {
            try
            {
                if ( newValue == null )
                    newValue = 0;
                
                new Sql ( db, "update variables set value = ? where id = ?" ).go ( newValue, id );
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
