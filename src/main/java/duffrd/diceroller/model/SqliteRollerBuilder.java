package duffrd.diceroller.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqliteRollerBuilder extends RollerBuilder
{
    private Connection sql;

    public SqliteRollerBuilder ( Connection sql )
    {
        this.sql = sql;
    }
    
    /* (non-Javadoc)
     * @see duffrd.diceroller.model.IRollerBuilder#build()
     */
    @Override
    public Roller build() throws DiceRollerException
    {
        try
        {
            super.build ();
            
            //
            // Going to Replace all Roller data, so delete it if it already exists.
            //
            PreparedStatement deleteRoller = sql.prepareStatement ( "delete from rollers where groupName=? and rollerName=?" );
            
            deleteRoller.setString ( 1, roller.groupName );
            deleteRoller.setString ( 2, roller.rollerName );
            
            deleteRoller.executeUpdate ();
            
            //
            // Create Roller
            //
            
            PreparedStatement insertRoller = sql.prepareStatement ( "insert into rollers ( groupName, rollerName, definition, booleanOutcome ) values ( ?, ?, ?, ? )" );
            
            insertRoller.setString ( 1, roller.groupName );
            insertRoller.setString ( 2, roller.rollerName );
            insertRoller.setString ( 3, roller.definition );
            insertRoller.setBoolean ( 4, roller.booleanOutcome );
            
            insertRoller.executeUpdate();
            
            if ( roller.labels.size () > 0 )
            {
                PreparedStatement insertLabel = sql.prepareStatement ( "insert into rollerLabels values ( ( select id from rollers where groupName=? and rollerName=? ), ?, ? )" );
                
                insertLabel.setString ( 1, roller.groupName );
                insertLabel.setString ( 2, roller.rollerName );
                
                for ( Object value : roller.labels.keySet () )
                {
                   if ( value instanceof Boolean )
                       insertLabel.setBoolean ( 3, ( Boolean ) value );
                   else
                       insertLabel.setInt ( 3, ( Integer ) value );
                   
                   insertLabel.setString ( 4, roller.labels.get ( value ) );
                   
                   insertLabel.executeUpdate ();
                }
            }
            
            if ( roller.triggers.size () > 0 )
            {
                PreparedStatement insertTrigger = sql.prepareStatement ( "insert into rollerTriggers values ( ( select id from rollers where groupName=? and rollerName=? ), ?, ? )" );
                
                insertTrigger.setString ( 1, roller.groupName );
                insertTrigger.setString ( 2, roller.rollerName );
                
                for ( String trigger : roller.triggers.keySet () )
                {
                    insertTrigger.setString ( 3, trigger );
                    insertTrigger.setString ( 4, roller.triggers.get ( trigger ).definition );
                }
            }
            
            return roller;
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }
}
