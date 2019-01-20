package duffrd.diceroller.model.sqlite;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import duffrd.diceroller.model.Group;
import duffrd.diceroller.model.Roller;
import javafx.collections.ListChangeListener;
import utility.sql.Sql;

public class SqliteGroup extends Group
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

    protected Integer id = null;
    protected int suiteId;
    protected Connection db = null;
    
    public SqliteGroup()
    {
        super();
    }
    
    public SqliteGroup ( int id )
    {
        this();
        this.id = id;
    }
    
    @Override
    public Roller newRoller ()
    {
        return new SqliteRoller().animate ( db, suiteId, id ).lua ( luaProperty ().get () );
    }
    
    @Override
    public void addRoller ( Roller src )
    {
        Roller roller = newRoller ();

        roller.name ( src.name () );
        roller.definition ( src.definition () );
        roller.labels ().putAll ( src.labels ().entrySet ().stream ().filter ( entry -> entry.getValue () == null || !entry.getValue ().isEmpty () ).collect ( Collectors.toMap ( Map.Entry::getKey, Map.Entry::getValue ) ) );
        
        roller.triggers ().addAll ( src.triggers () );
    }

    SqliteGroup animate ( Connection conn, int suiteId )
    {
        logger.debug ( "Animating Group" );
        db = conn;
        this.suiteId = suiteId;
        
        nameProperty.addListener ( ( prop, oldName, newName ) -> 
        {
            try
            {
                boolean o = oldName != null && !oldName.isEmpty ();
                boolean n = newName != null && !newName.isEmpty ();
                
                if ( o && n )
                {
                    // Renaming ( Expect ID to be set )
                    logger.debug ( "DB: updating group " + id + " name from <" + oldName + "> to <" + newName + ">" );
                    new Sql ( db, "update groups set name = ? where id = ?" ).go ( newName, id );
                }
                else if ( o )
                {
                    // Renaming to Blank, so use placeholder name ( Expect ID to be set )
                    logger.debug ( "DB: updating group " + id + " name from <" + oldName + "> to blank value" );
                    new Sql ( db, "update groups set name = ? where id = ?" ).go ( "<unnamed-group>", id );                    
                }
                else if ( n )
                {
                    if ( id == null )
                    {
                        // Create New Group
                        logger.debug ( "DB: Inserting New Group <" + newName + ">"  );
                        new Sql ( db, "insert into groups ( suiteId, name ) values ( ?, ? )" ).go ( suiteId, newName );
                        id = new Sql ( db, "select max ( id ) from groups" ).go ().single ().getInt ( 1 );
                        logger.debug ( "DB: New Group <" + newName + "> created with ID=" + id );
                    }
                    else
                    {
                        // Renaming 
                        logger.debug ( "DB: Updating group " + id + " name from blank value to <" + newName + ">" );
                        new Sql ( db, "update groups set name = ? where id = ?" ).go ( newName, id );
                    }
                }
            }
            catch ( SQLException e )
            {
                e.printStackTrace();
            }
        } );

        rollersProperty.addListener ( ( ListChangeListener.Change<? extends Roller> change ) -> 
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
                            logger.debug ( "DB: Permuting Roller from " + name + " to " + order.get ( name ) );
                            new Sql ( db, "update rollers set sequence = ? where groupId = ? and name = ?" ).go ( order.get ( name ), id, name );
                        }
                    }
                    else if ( change.wasReplaced () )
                    {
                        // Should never happen
                        logger.error ( "DB: Roller Replacement Should Not Happen" );
                        logger.error ( "DB:     Range:   " + change.getFrom () + ".." + change.getTo () );
                        
                        for ( Roller roller : change.getRemoved () )
                            logger.error ( "DB:    Removed " + roller );
                        
                        for ( Roller roller : change.getAddedSubList () )
                            logger.error ( "DB:    Added " + roller );
                    }
                    else if ( change.wasAdded () )
                    {
                        // Do Nothing, Let the new Variable handle it.
                    }
                    else if ( change.wasRemoved () )
                    {
                        for ( Roller r : change.getRemoved () )
                        {
                            logger.debug ( "DB: Deleting Roller " + r.name() );
                            new Sql ( db, "delete from rollers where groupId = ? and name = ?" ).go ( id, r.name () );
                        }
                    }
                }
            }
            catch ( Throwable e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } );

        return this;
    }
}
