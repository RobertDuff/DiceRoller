package duffrd.diceroller.model.scripts;

import java.sql.SQLException;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.RollerModel;
import utility.sql.Sql;

public class RollerModelSchema implements DbScript
{
    @Override
    public void go ( RollerModel model ) throws DiceRollerException
    {
        try
        {
            //
            // Set Pragma so that Sqlite3 Foreign Keys will work properly.
            //
            
            new Sql ( model.connection(), "pragma foreign_keys = on" ).go();
            
            //
            // Create Groups Table
            //
            
            new Sql ( model.connection(), "create table groups ( id integer primary key, name text not null unique, sequence integer )" ).go ();
            new Sql ( model.connection(), "create trigger applySequenceToNewGroup after insert on groups when new.sequence is null begin update groups set sequence = ( select ifnull ( max ( sequence ), 0 ) + 1 from groups ) where id=new.id; end" ).go ();
            new Sql ( model.connection(), "create trigger resequenceGroupsOnDelete after delete on groups begin update groups set sequence = sequence - 1 where sequence > old.sequence; end" ).go ();
            
            //
            // Create Rollers Table
            //
            new Sql ( model.connection(), "create table rollers ( id integer primary key, groupId integer references groups ( id ) on delete cascade, Name text not null, definition text not null, sequence integer, unique ( groupId, name ) )" ).go ();
            new Sql ( model.connection(), "create trigger applySquenceToNewRoller after insert on rollers when new.sequence is null begin update rollers set sequence = ( select ifnull ( max ( sequence ), 0 ) + 1 from rollers where groupId=new.groupId ) where id=new.id; end" ).go ();
            new Sql ( model.connection(), "create trigger resequenceRollersOnDelete after delete on rollers begin update rollers set sequence = sequence - 1 where groupId = old.groupId and sequence > old.sequence; end" ).go ();
            
            //
            // Create Labels and Triggers Tables
            //
            
            new Sql ( model.connection(), "create table labels ( rollerId integer references rollers ( id ) on delete cascade, value integer not null, label text not null, unique ( rollerId, value ) )" ).go ();
            new Sql ( model.connection(), "create table triggers ( rollerId integer references rollers ( id ) on delete cascade, name text not null, definition text not null, unique ( rollerId, name ) )" ).go ();
            
            //
            // Create Variables Table
            //
            
            new Sql ( model.connection(), "create table variables ( groupId  references groups ( id ) on delete cascade, name text not null, value integer not null, sequence integer, unique ( groupId, name ) )" ).go ();
            new Sql ( model.connection(), "create trigger applySquenceToNewVariable after insert on variables when new.sequence is null begin update variables set sequence = ( select ifnull ( max ( sequence ), 0 ) + 1 from variables where groupId=new.groupId ) where groupId=new.groupId and name=new.name; end" ).go ();
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }
}
