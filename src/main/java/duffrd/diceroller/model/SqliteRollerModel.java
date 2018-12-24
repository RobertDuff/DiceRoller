package duffrd.diceroller.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import utility.lua.LuaProvider;
import utility.sql.Sql;

public class SqliteRollerModel implements RollerModel
{    
    private static final Path DATABASE_DIR_PATH = Paths.get ( System.getenv ( "LOCALAPPDATA" ), "DiceRoller" ).toAbsolutePath ();
    private static final String DATABASE_NAME = "DiceRoller.db";
        
    private static final Pattern BEGIN_PATTERN = Pattern.compile ( "\\bbegin\\b", Pattern.CASE_INSENSITIVE );
    private static final Pattern END_PATTERN = Pattern.compile ( "\\bend\\b\\s*;", Pattern.CASE_INSENSITIVE );

    private static final String SCHEMA_SQL_FILE = "schema.sql";
    private static final String INIT_DATA_SQL_FILE = "init.sql";
    
    Connection sql;
    
    public SqliteRollerModel() throws IOException, SQLException
    {
        this ( initConnection () );
    }
    
    public SqliteRollerModel ( Connection conn ) throws IOException, SQLException
    {
        initModel ( conn, 
                ClassLoader.getSystemResourceAsStream ( SCHEMA_SQL_FILE ), 
                ClassLoader.getSystemResourceAsStream ( INIT_DATA_SQL_FILE ) );
    }
    
    public SqliteRollerModel ( Connection conn, InputStream dataSource ) throws IOException, SQLException
    {
        this ( conn, ClassLoader.getSystemResourceAsStream ( SCHEMA_SQL_FILE ), dataSource );
    }
    
    public SqliteRollerModel ( Connection conn, InputStream schema, InputStream dataSource ) throws IOException, SQLException
    {
        initModel ( conn, schema, dataSource );
    }
    
    private static Connection initConnection() throws SQLException, IOException
    {
        // Create DB Directory, if necessary
        Files.createDirectories ( DATABASE_DIR_PATH );
        
        // Open DB File
        Path dbPath = Paths.get ( DATABASE_DIR_PATH.toString(), DATABASE_NAME );
        
        return DriverManager.getConnection ( "jdbc:sqlite:" + dbPath.toString() ); 
    }
    
    private void initModel ( Connection conn, InputStream schemaSource, InputStream dataSource ) throws IOException, SQLException
    {
        sql = conn;
        sql.createStatement ().execute ( "pragma foreign_keys = on" );
        
        // If the DB file is newly created, then populate it.
        if ( !sql.createStatement().executeQuery ( "select name from sqlite_master where type = 'table' and name='rollers'" ).next() )
        {
            Statement stmt = sql.createStatement ();
            
            // Create Schema
            
            Scanner schema = new Scanner ( schemaSource );
            schema.useDelimiter ( ";" );
            
            while ( schema.hasNext () )
            {
                String s = schema.next ();
                
                // If the statement contains a BEGIN, then we need to find the END.
                if ( BEGIN_PATTERN.matcher ( s ).find () )
                {
                    schema.useDelimiter ( END_PATTERN );
                    s = s + schema.next () + "end";
                    
                    schema.useDelimiter ( ";" );
                    
                    // The delimiter "end;" was left by the last call, so we have to discard it.
                    schema.next ();
                }
                
                stmt.executeUpdate ( s );
            }
            
            schema.close ();
            
            // Populate Default Data
            
            Scanner init = new Scanner ( dataSource );
            init.useDelimiter ( ";" );
            
            while ( init.hasNext () )
                stmt.executeUpdate ( init.next () );
            
            init.close ();
        }
    }
    
    @Override
    public List<String> groupNames () throws DiceRollerException
    {
        try
        {
            List<String> groupNames = new ArrayList<>();
            
            for ( ResultSet row : new Sql ( sql, "select name from groups order by sequence" ).go() )
            {
                String name = row.getString ( 1 );
                
                groupNames.add ( name );
                
                // Initialize Group Variables in the Lua Context for each Group.
                for ( Variable variable : groupVariables ( name ) )
                    LuaProvider.lua ( name ).set ( variable.name, variable.value );
            }
            
            return groupNames;
        }
        catch ( Exception e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void createGroup ( String name ) throws DiceRollerException
    {
        try
        {
            new Sql ( sql, "insert into groups ( name ) values ( ? )" ).go ( name );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void renameGroup ( String origName, String newName ) throws DiceRollerException
    {
        try
        {
            new Sql ( sql, "update groups set name=? where name=?" ).go ( newName, origName );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void moveGroup ( String group, int position ) throws DiceRollerException
    {
        try
        {
            //
            // Get Original Position
            //
            
            ResultSet gRow = new Sql ( sql, "select id, sequence from groups where name=?" ).go ( group ).single ();
            
            int id = gRow.getInt ( 1 );
            int origPos = gRow.getInt ( 2 );
            
            if ( position == origPos )
                return;
            
            //
            // Shift Other Rollers
            //
            
            Sql shift;
            
            if ( position > origPos )
                // Moving Down
                shift = new Sql ( sql, "update groups set sequence = sequence - 1 where sequence > ? and sequence <= ?" );
            else
                // Moving Up
                shift = new Sql ( sql, "update groups set sequence = sequence + 1 where sequence < ? and sequence >= ?" );
            
            shift.go ( origPos, position );
            
            //
            // Change the Roller Position
            //
            
            new Sql ( sql, "update groups set sequence = ? where id = ?" ).go ( position, id );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void deleteGroup ( String group ) throws DiceRollerException
    {
        try
        {
            new Sql ( sql, "delete from groups where name=?" ).go ( group );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public List<Roller> rollers ( String group ) throws DiceRollerException
    {
        try
        {
            List<Roller> rollers = new ArrayList<>();
         
            for ( ResultSet rollerRow : new Sql ( sql, "select id, name, definition from rollers where groupId=( select id from groups where name=? ) order by sequence" ).go ( group ) )
            {
                RollerBuilder builder = new RollerBuilder();
                
                int rollerId = rollerRow.getInt ( 1 );
                
                builder.group ( group ).name ( rollerRow.getString ( 2 ) ).definition ( rollerRow.getString ( 3 ) );
                
                for ( ResultSet labelRow : new Sql ( sql, "select value, label from labels where rollerId=? order by value" ).go ( rollerId  ) )
                    builder.addLabel ( labelRow.getInt ( 1 ), labelRow.getString ( 2 ) );

                for ( ResultSet triggerRow : new Sql ( sql, "select name, definition from triggers where rollerId=?" ).go ( rollerId ) )
                    builder.addTrigger ( triggerRow.getString ( 1 ), triggerRow.getString ( 2 ) );
                
                rollers.add ( builder.build () );
            }
            
            return rollers;
        }
        catch ( Exception e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public void createRoller ( Roller roller ) throws DiceRollerException
    {
        try
        {           
            sql.setAutoCommit ( false );
                        
            // Get Group Id
            int groupId = new Sql ( sql, "select id from groups where name=?" ).go ( roller.groupName ).single ().getInt ( 1 );
            
            //
            // Create Roller
            //
            
            new Sql ( sql, "insert into rollers ( groupId, name, definition ) values ( ?, ?, ? )" ).go ( groupId, roller.rollerName, roller.definition );
            int rollerId = new Sql ( sql, "select id from rollers where groupId=? and name=?" ).go ( groupId, roller.name () ).single ().getInt ( 1 );
            
            Sql insertLabel = new Sql ( sql, "insert into labels values ( ?, ?, ? )" );

            for ( int value : roller.labels.keySet () )
                insertLabel.go ( rollerId, value, roller.labels.get ( value ) );
            
            Sql insertTrigger = new Sql ( sql, "insert into triggers values ( ?, ?, ? )" );
            
            for ( String trigger : roller.triggers.keySet () )
                insertTrigger.go ( rollerId, trigger, roller.triggers.get ( trigger ) );
        }
        catch ( SQLException e )
        {
            try
            {
                sql.rollback ();
            }
            catch ( SQLException e1 )
            {
                e = e1;
            }

            throw new DiceRollerException ( e );
        }
        finally
        {
            try
            {
                sql.setAutoCommit ( true );
            }
            catch ( SQLException e )
            {
                throw new DiceRollerException ( e );
            }
        }
    }

    @Override
    public void renameRoller ( String groupName, String origName, String newName ) throws DiceRollerException
    {
        try
        {
            new Sql ( sql, "update rollers set name=? where groupId = ( select id from groups where name=? ) and name=?" ).go ( newName, groupName, origName );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }
    
    @Override
    public void updateRoller ( Roller roller ) throws DiceRollerException
    {
        // Does not change Group Name, Roller Name or Sequence Number.
        
        try
        {           
            sql.setAutoCommit ( false );

            // Get Roller ID
            int rollerId = new Sql ( sql, "select id from rollers where groupId = ( select id from groups where name=? ) and name = ?" ).go ( roller.groupName, roller.rollerName ).single ().getInt ( 1 );
            
            // Update Roller Definition
            new Sql ( sql, "update rollers set definition=? where id=?" ).go ( roller.definition, rollerId );
                                
            // Replace All Labels
            
            new Sql ( sql, "delete from labels where rollerId=?" ).go ( rollerId );
            
            Sql insertLabel = new Sql ( sql, "insert into labels values ( ?, ?, ? )" );

            for ( int value : roller.labels.keySet () )
                insertLabel.go ( rollerId, value, roller.labels.get ( value ) );
            
            // Replace All Triggers
            
            new Sql ( sql, "delete from triggers where rollerId=?" ).go ( rollerId );
            
            Sql insertTrigger = new Sql ( sql, "insert into triggers values ( ?, ?, ? )" );
            
            for ( String trigger : roller.triggers.keySet () )
                insertTrigger.go ( rollerId, trigger, roller.triggers.get ( trigger ).definition );
        }
        catch ( SQLException e )
        {
            try
            {
                sql.rollback ();
            }
            catch ( SQLException e1 )
            {
                e = e1;
            }

            throw new DiceRollerException ( e );
        }
        finally
        {
            try
            {
                sql.setAutoCommit ( true );
            }
            catch ( SQLException e )
            {
                throw new DiceRollerException ( e );
            }
        }
    }

    @Override
    public void moveRoller ( Roller roller, int position ) throws DiceRollerException
    {
        try
        {
            //
            // Get Original Position
            //
            
            ResultSet rRow = new Sql ( sql, "select id, sequence from rollers where groupId = ( select id from groups where name=? ) and name=?" ).go ( roller.groupName, roller.rollerName ).single ();
    
            int id = rRow.getInt ( 1 );
            int origPos = rRow.getInt ( 2 );
            
            if ( position == origPos )
                return;
            
            //
            // Shift Other Rollers
            //
            
            Sql shift = null;
            
            if ( position > origPos )
                // Moving Down
                shift = new Sql ( sql, "update rollers set sequence = sequence - 1 where sequence > ? and sequence <= ?" );
            else
                // Moving Up
                shift = new Sql ( sql, "update rollers set sequence = sequence + 1 where sequence < ? and sequence >= ?" );
            
            shift.go ( origPos, position );
            
            //
            // Change the Roller Position
            //
            
            new Sql ( sql, "update rollers set sequence = ? where id = ?" ).go ( position, id );
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
            new Sql ( sql, "delete from rollers where groupId=( select id from groups where name=? ) and name=?" ).go ( roller.groupName, roller.rollerName );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public List<Variable> groupVariables ( String groupName ) throws DiceRollerException
    {
        List<Variable> variables = new ArrayList<>();

        try
        {    
            for ( ResultSet row : new Sql ( sql, "select name, value from variables where groupId=( select id from groups where name=? ) order by sequence" ).go ( groupName ) )
                variables.add ( new Variable ( row.getString ( 1 ), row.getInt ( 2 ) ) );
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
                        
        return variables;
    }

    @Override
    public void updateGroupVariables ( String groupName, List<Variable> variables ) throws DiceRollerException
    {
        try
        {
            // Get Group Id
            
            int groupId = new Sql ( sql, "select id from groups where name=?" ).go ( groupName ).single ().getInt ( 1 );
            
            // Replace All Variables
            
            new Sql ( sql, "delete from variables where groupId=?" ).go ( groupId );
            
            Sql insertVariable = new Sql ( sql, "insert into variables values ( ?, ?, ?, ? )" );
            
            int sequence = 1;
            
            for ( Variable variable : variables )
            {
                // Update SQL Database
                insertVariable.go ( groupId, variable.name, variable.value, sequence++ );
                
                // Update Lua Context for Group
                LuaProvider.lua ( groupName ).set ( variable.name, variable.value );
            }
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e ); 
        }
    }
}
