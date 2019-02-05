package duffrd.diceroller.model.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.Group;
import duffrd.diceroller.model.Model;
import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.Suite;
import duffrd.diceroller.model.Trigger;
import duffrd.diceroller.model.Variable;
import utility.collections.ListRearranger;
import utility.lua.LuaProvider;
import utility.sql.Sql;

public class SqliteModel extends Model
{
    protected Connection db = null;
    
    public SqliteModel ( Connection conn )
    {
        db = conn;
    }

    @Override
    public Suite createSuite ( String name ) throws DiceRollerException
    {
        try
        {
            new Sql ( db, "insert into suites ( name ) values ( ? )" ).go ( name );
            int id = new Sql ( db, "select max ( id ) from suites" ).go ().single ().getInt ( 1 );
            
            Suite suite = new SqliteSuite ( id );
            
            suitesProperty.add ( suite );
            
            suite.model ( this );
            suite.lua ( LuaProvider.newLua () );
            suite.name ( name );
            
            return suite;
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void updateSuite ( Suite suite, String name ) throws DiceRollerException
    {        
        try
        {
            int suiteId = ( ( SqliteSuite ) suite ).id ();
            
            new Sql ( db, "update suites set name=? where id=?" ).go ( name, suiteId );
            
            suite.name ( name );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void deleteSuite ( Suite suite ) throws DiceRollerException
    {
        try
        {
            int suiteId = ( ( SqliteSuite ) suite ).id ();
            
            new Sql ( db, "delete from suites where id=?" ).go ( suiteId );
            
            suitesProperty.remove ( suite );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public Trigger createTrigger ( Suite suite, String name, String definition ) throws DiceRollerException
    {
        try
        {
            int suiteId = ( ( SqliteSuite ) suite ).id ();
            
            new Sql ( db, "insert into triggers ( suiteId, name, definition ) values ( ?, ?, ? )" ).go ( suiteId, name, definition );
            int id = new Sql ( db, "select max ( id ) from triggers" ).go ().single ().getInt ( 1 );
            
            Trigger trigger = new SqliteTrigger ( id );
            
            suite.triggers ().add ( trigger );
            
            trigger.suite ( suite );
            trigger.name ( name );
            trigger.definition ( definition );
            
            return trigger;
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void updateTrigger ( Trigger trigger, String name, String definition ) throws DiceRollerException
    {
        try
        {
            int triggerId = ( ( SqliteTrigger ) trigger ).id ();
            
            new Sql ( db, "update triggers set name=?, definition=? where id=?" ).go ( name, definition, triggerId );
            
            trigger.name ( name );
            trigger.definition ( definition );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void deleteTrigger ( Trigger trigger ) throws DiceRollerException
    {
        try
        {
            int triggerId = ( ( SqliteTrigger ) trigger ).id ();
            
            new Sql ( db, "delete from triggers where id=?" ).go ( triggerId );
            
            trigger.suite().triggers ().remove ( trigger );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }
    
    @Override
    public Variable createVariable ( Suite suite, String name, int value ) throws DiceRollerException
    {
        try
        {
            int suiteId = ( ( SqliteSuite ) suite ).id ();
            
            new Sql ( db, "insert into variables ( suiteId, name, value ) values ( ?, ?, ? )" ).go ( suiteId, name, value );
            int varId = new Sql ( db, "select max ( id ) from variables" ).go ().single ().getInt ( 1 );
            
            SqliteVariable variable = new SqliteVariable ( varId );
            
            suite.variables ().add ( variable );
            
            variable.suite ( suite );
            variable.name ( name );
            variable.value ( value );
            
            return variable;
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void updateVariables ( Suite suite, List<Variable> variables ) throws DiceRollerException
    {
        try
        {
            int suiteId = ( ( SqliteSuite ) suite ).id ();
            
            db.setAutoCommit ( false );
            
            new Sql ( db, "delete from variables where suiteId=?" ).go ( suiteId );
            
            Sql insertVariable = new Sql ( db, "insert into variables ( suiteId, name, value ) values ( ?, ?, ? )" );
            Sql varId = new Sql ( db, "select max ( id ) from variables" );
            
            List<Integer> ids = new ArrayList<>();
            
            for ( Variable variable : variables )
            {
                insertVariable.go ( suiteId, variable.name (), variable.value () );
                ids.add ( varId.go ().single ().getInt ( 1 ) );
            }
            
            db.commit ();

            suite.variables ().clear ();

            Iterator<Integer> i = ids.iterator ();

            for ( Variable var : variables )
            {
                SqliteVariable variable = new SqliteVariable ( i.next () );
                
                suite.variables ().add ( variable );
                
                variable.suite ( suite );
                variable.name ( var.name () );
                variable.value ( var.value () );
            }
        }
        catch ( SQLException e )
        {
            try { db.rollback (); } catch ( SQLException e1 ) {}
            throw new DiceRollerException ( e );
        }
        finally
        {
            try
            {
                db.setAutoCommit ( true );
            }
            catch ( SQLException e )
            {
                throw new DiceRollerException ( e );
            }
        }
    }

    @Override
    public Group createGroup ( Suite suite, String name ) throws DiceRollerException
    {
        try
        {
            int suiteId = ( ( SqliteSuite ) suite ).id ();
            
            new Sql ( db, "insert into groups ( suiteId, name ) values ( ?, ? )" ).go ( suiteId, name );
            int id = new Sql ( db, "select max ( id ) from groups" ).go ().single ().getInt ( 1 );
            
            SqliteGroup group = new SqliteGroup ( id );
            
            suite.groups ().add ( group );
            
            group.suite ( suite );
            group.name ( name );
            
            return group;
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void updateGroup ( Group group, String name ) throws DiceRollerException
    {
        try
        {
            int groupId = ( ( SqliteGroup ) group ).id();
            
            new Sql ( db, "update groups set name=? where id=?" ).go ( name, groupId );

            group.name ( name );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void moveGroup ( Group group, int pos ) throws DiceRollerException
    {
        try
        {
            int groupId = ( ( SqliteGroup ) group ).id ();

            int origPos = new Sql ( db, "select sequence from groups where id=?" ).go ( groupId ).single ().getInt ( 1 );

            if ( pos == origPos )
                return;

            //
            // Shift Other Groups
            //

            Sql shift = null;

            if ( pos > origPos )
                // Moving Down
                shift = new Sql ( db, "update groups set sequence = sequence - 1 where sequence > ? and sequence <= ?" );
            else
                // Moving Up
                shift = new Sql ( db, "update groups set sequence = sequence + 1 where sequence < ? and sequence >= ?" );

            shift.go ( origPos, pos );

            //
            // Change the Group Position
            //

            new Sql ( db, "update groups set sequence = ? where id = ?" ).go ( pos, groupId );
            
            group.suite ().groups ().sort ( ListRearranger.move ( group.suite ().groups (), origPos-1, pos-1 ) );
        }
        catch ( Throwable e )
        {
            e.printStackTrace();
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void deleteGroup ( Group group ) throws DiceRollerException
    {
        try
        {
            int groupId = ( ( SqliteGroup ) group ).id ();
            
            new Sql ( db, "delete from groups where id=?" ).go ( groupId );
            
            group.suite().groups ().remove ( group );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public Roller createRoller ( Group group, String name, String definition ) throws DiceRollerException
    {
        try
        {
            int groupId = ( ( SqliteGroup ) group ).id ();
            
            new Sql ( db, "insert into rollers ( groupId, name, definition ) values ( ?, ?, ? )" ).go ( groupId, name, definition );
            int id = new Sql ( db, "select max ( id ) from rollers" ).go ().single ().getInt ( 1 );
            
            Roller roller = new SqliteRoller ( id );
            
            group.rollers ().add ( roller );
            
            roller.group ( group );
            roller.name ( name );
            roller.definition ( definition );
            
            return roller;
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void updateRoller ( Roller roller, String name, String definition ) throws DiceRollerException
    {
        try
        {
            int rollerId = ( ( SqliteRoller ) roller ).id ();
            
            new Sql ( db, "update rollers set name=?, definition=? where id=?" ).go ( name, definition, rollerId );
            
            roller.name ( name );
            roller.definition ( definition );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void moveRoller ( Roller roller, int pos ) throws DiceRollerException
    {
        try
        {
            int rollerId = ( ( SqliteRoller )  roller ).id ();

            int origPos = new Sql ( db, "select sequence from rollers where id=?" ).go ( rollerId ).single ().getInt ( 1 );

            if ( pos == origPos )
                return;

            //
            // Shift Other Rollers
            //

            Sql shift = null;

            if ( pos > origPos )
                // Moving Down
                shift = new Sql ( db, "update rollers set sequence = sequence - 1 where sequence > ? and sequence <= ?" );
            else
                // Moving Up
                shift = new Sql ( db, "update rollers set sequence = sequence + 1 where sequence < ? and sequence >= ?" );

            shift.go ( origPos, pos );

            //
            // Change the Roller Position
            //

            new Sql ( db, "update rollers set sequence = ? where id = ?" ).go ( pos, rollerId );
            
            roller.group().rollers ().sort ( ListRearranger.move ( roller.group ().rollers (), origPos-1, pos-1 ) );            
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void deleteRoller ( Roller roller ) throws DiceRollerException
    {
        try
        {
            int rollerId = ( ( SqliteRoller ) roller ).id ();
            
            new Sql ( db, "delete from rollers where id=?" ).go ( rollerId );
            
            roller.group().rollers ().remove ( roller );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void createLabel ( Roller roller, int value, String label ) throws DiceRollerException
    {
        try
        {
            int rollerId = ( ( SqliteRoller ) roller ).id ();

            new Sql ( db, "insert into labels ( rollerId, value, label ) values ( ?, ?, ? )" ).go ( rollerId, value, label );
            
            roller.labels ().put ( value, label );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }
    
    @Override
    public void updateLabels ( Roller roller, Map<Integer, String> labels ) throws DiceRollerException
    {
        try
        {
            int rollerId = ( ( SqliteRoller ) roller ).id ();
            
            db.setAutoCommit ( false );
            
            new Sql ( db, "delete from labels where rollerId=?" ).go ( rollerId );
            
            Sql insertLabel = new Sql ( db, "insert into labels ( rollerId, value, label ) values ( ?, ?, ? )" );
                        
            for ( Integer value : labels.keySet () )
                insertLabel.go ( rollerId, value, labels.get ( value ) );
            
            db.commit ();

            roller.labels().clear ();
            roller.labels().putAll ( labels );
        }
        catch ( SQLException e )
        {
            try { db.rollback (); } catch ( SQLException e1 ) {}
            throw new DiceRollerException ( e );
        }
        finally
        {
            try
            {
                db.setAutoCommit ( true );
            }
            catch ( SQLException e )
            {
                throw new DiceRollerException ( e );
            }
        }
    }

    @Override
    public void createRollerTrigger ( Roller roller, Trigger trigger ) throws DiceRollerException
    {
        try
        {
            int rollerId = ( ( SqliteRoller ) roller ).id ();
            int triggerId = ( ( SqliteTrigger ) trigger ).id ();

            new Sql ( db, "insert into rollerTriggers ( rollerId, triggerId ) values ( ?, ? )" ).go ( rollerId, triggerId );
            
            roller.triggers ().add ( trigger );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void updateRollerTriggers ( Roller roller, Set<Trigger> triggers ) throws DiceRollerException
    {
        try
        {
            int rollerId = ( ( SqliteRoller ) roller ).id ();
            
            db.setAutoCommit ( false );
            
            new Sql ( db, "delete from rollerTriggers where rollerId=?" ).go ( rollerId );
            
            Sql insertLabel = new Sql ( db, "insert into rollerTriggers ( rollerId, triggerId ) values ( ?, ? )" );
                        
            for ( Trigger trigger : triggers )
            {
                int triggerId = ( ( SqliteTrigger ) trigger ).id ();
                
                insertLabel.go ( rollerId, triggerId );
            }
            
            db.commit ();

            roller.triggers().clear ();
            roller.triggers().addAll ( triggers );
        }
        catch ( SQLException e )
        {
            try { db.rollback (); } catch ( SQLException e1 ) {}
            throw new DiceRollerException ( e );
        }
        finally
        {
            try
            {
                db.setAutoCommit ( true );
            }
            catch ( SQLException e )
            {
                throw new DiceRollerException ( e );
            }
        }
    }
}
