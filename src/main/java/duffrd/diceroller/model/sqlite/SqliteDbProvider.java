package duffrd.diceroller.model.sqlite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

import duffrd.diceroller.model.DiceRollerException;
import utility.sql.DbScript;
import utility.sql.Sql;

public class SqliteDbProvider
{
    private static final Path DATABASE_DIR_PATH = Paths.get ( System.getenv ( "LOCALAPPDATA" ), "DiceRoller" ).toAbsolutePath ();
    private static final String DATABASE_NAME = "DiceRoller.db";
    private static final String SCHEMA_FILE = "schema.sql";

    public static Connection provideStandardDB() throws DiceRollerException
    {
        try
        {
            // Create DB Directory, if necessary
            Files.createDirectories ( DATABASE_DIR_PATH );

            // Open DB File
            Path dbPath = Paths.get ( DATABASE_DIR_PATH.toString(), DATABASE_NAME );

            return provideDBFromSource ( dbPath );
        }
        catch ( IOException e )
        {
            throw new DiceRollerException ( e );
        }
    }
    
    public static Connection provideDBFromSource ( Path dbPath ) throws DiceRollerException
    {
        try
        {
            SQLiteConfig config = new SQLiteConfig ();
            config.enforceForeignKeys ( true );
            Connection db = config.createConnection ( "jdbc:sqlite:" + dbPath.toString() );             

            applySchemaIfNecessary ( db );
            
            return db;
        }
        catch ( SQLException | IOException e )
        {
            throw new DiceRollerException ( e );
        }
    }
    
    public static Connection provideInMemoryDB() throws DiceRollerException, IOException
    {
        try
        {
            Connection db = DriverManager.getConnection ( "jdbc:sqlite::memory:" );

            new Sql ( db, "pragma foreign_keys = on" ).go();

            applySchemaIfNecessary ( db );
            
            return db;
        }
        catch ( SQLException e )
        {
            throw new DiceRollerException ( e );
        }
    }

    protected static void applySchemaIfNecessary ( Connection db ) throws SQLException, IOException
    {
        if ( new Sql ( db, "select count ( name ) from sqlite_master where type = 'table' and name='rollers'" ).go ().single ().getInt ( 1 ) == 0 )
            new DbScript ( db).run ( ClassLoader.getSystemResource ( SCHEMA_FILE ) );
    }
}
