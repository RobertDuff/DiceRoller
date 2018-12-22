package duffrd.diceroller.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SqliteRollerModel implements RollerModel
{
    private static final Pattern BEGIN_PATTERN = Pattern.compile ( "\\bbegin\\b", Pattern.CASE_INSENSITIVE );
    private static final Pattern END_PATTERN = Pattern.compile ( "\\bend\\b\\s*;", Pattern.CASE_INSENSITIVE );
    
    private static final Path DATABASE_DIR_PATH = Paths.get ( System.getenv ( "LOCALAPPDATA" ), "DiceRoller" ).toAbsolutePath ();
    private static final String DATABASE_NAME = "DiceRoller.db";
    private static final String SCHEMA_SQL_FILE = "schema.sql";
    private static final String INIT_DATA_SQL_FILE = "init.sql";
    
    private Connection sql;
    
    public SqliteRollerModel() throws IOException, SQLException
    {
        // Create DB Directory, if necessary
        Files.createDirectories ( DATABASE_DIR_PATH );
        
        // Open DB File
        Path dbPath = Paths.get ( DATABASE_DIR_PATH.toString(), DATABASE_NAME );
        sql = DriverManager.getConnection ( "jdbc:sqlite:" + dbPath.toString() );
        
        // If the DB file is newly created, then populate it.
        if ( !sql.createStatement().executeQuery ( "select name from sqlite_master where type = 'table' and name='rollers'" ).next() )
        {
            Statement stmt = sql.createStatement ();
            
            // Create Schema
            
            Scanner schema = new Scanner ( ClassLoader.getSystemResourceAsStream ( SCHEMA_SQL_FILE ) );
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
            
            Scanner init = new Scanner ( ClassLoader.getSystemResourceAsStream ( INIT_DATA_SQL_FILE ) );
            init.useDelimiter ( ";" );
            
            while ( init.hasNext () )
            {
                stmt.executeUpdate ( init.next () );
            }
            
            init.close ();
        }
    }
    
    @Override
    public List<String> groupNames () throws DiceRollerException
    {
        try
        {
            ResultSet rs = sql.createStatement ().executeQuery ( "select distinct groupName from rollers order by groupName" );
            
            List<String> groupNames = new ArrayList<>();
            
            while ( rs.next () )
                groupNames.add ( rs.getString ( 1 ) );
            
            return groupNames;
        }
        catch ( Exception e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public List<Roller> rollers ( String group ) throws DiceRollerException
    {
        try
        {
            PreparedStatement r = sql.prepareStatement ( "select id, rollerName, definition from rollers where groupName=? order by sequence" );
            
            r.setString ( 1, group );
            
            ResultSet rs = r.executeQuery ();
            
            List<Roller> rollers = new ArrayList<>();
            
            while ( rs.next () )
            {
                RollerBuilder builder = new RollerBuilder();
                
                builder.group ( group ).name ( rs.getString ( 2 ) ).definition ( rs.getString ( 3 ) );
                
                int rollerId = rs.getInt ( 1 );
                
                PreparedStatement lbl = sql.prepareStatement ( "select value, label from rollerLabels where rollerId=? order by value" );
                
                lbl.setInt ( 1, rollerId );
                
                ResultSet ls = lbl.executeQuery ();
                
                while ( ls.next () )
                    builder.addLabel ( ls.getInt ( 1 ), ls.getString ( 2 ) );
                
                PreparedStatement trig = sql.prepareStatement ( "select triggerName, definition from rollerTriggers where rollerId=?" );
                
                trig.setInt ( 1, rollerId );
                
                trig.executeQuery ();
                
                ResultSet ts = trig.executeQuery ();
                
                while ( ts.next () )
                    builder.addTrigger ( ts.getString ( 1 ), ts.getString ( 2 ) );
                
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
    public void renameGroup ( String origName, String newName ) throws DiceRollerException
    {
        try
        {
            PreparedStatement deleteRoller = sql.prepareStatement ( "update rollers set groupName=? where groupName=?" );
            
            deleteRoller.setString ( 1, newName );
            deleteRoller.setString ( 2, origName );
            
            deleteRoller.executeUpdate ();
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
            PreparedStatement deleteRoller = sql.prepareStatement ( "delete from rollers where groupName=?" );
            
            deleteRoller.setString ( 1, group );
            
            deleteRoller.executeUpdate ();
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    @Override
    public RollerBuilder rollerBuilder ()
    {
        return new SqliteRollerBuilder ( sql );
    }

    @Override
    public void deleteRoller ( Roller roller ) throws DiceRollerException
    {
        try
        {
            PreparedStatement deleteRoller = sql.prepareStatement ( "delete from rollers where groupName=? and rollerName=?" );
            
            deleteRoller.setString ( 1, roller.groupName );
            deleteRoller.setString ( 2, roller.rollerName );
            
            deleteRoller.executeUpdate ();
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }
}
