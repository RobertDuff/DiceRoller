package duffrd.diceroller.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import utility.lua.LuaProvider;
import utility.sql.Sql;

public class SqliteRollerModelTest
{
    public static final String TEST_DATA = "modelTestData.sql";
    
    @Rule
    public ExpectedException thrown = ExpectedException.none ();
    
    public Connection sql;
    public SqliteRollerModel model;
    public List<Roller> rollers;
    
    @Before
    public void before() throws IOException, SQLException, DiceRollerException
    {
        sql = DriverManager.getConnection ( "jdbc:sqlite::memory:" );
        model = new SqliteRollerModel ( sql, ClassLoader.getSystemResourceAsStream ( TEST_DATA ) );
        rollers = model.rollers ( "G1" );
    }
    
    @After
    public void after() throws SQLException
    {
        sql.close ();
    }
    
    @Test
    public void testGroupNames () throws DiceRollerException
    {
        List<String> groupNames = model.groupNames ();
        //for ( String name : groupNames ) System.out.println ( name );

        assertEquals ( 7, groupNames.size () );
                
        int n = 1;
        
        for ( String name : groupNames )
            assertEquals ( "G" + n++, name );
        
        // Check Lua Context
        
        assertEquals ( 3, LuaProvider.lua ( "G1" ).get ( "V1" ).toint () );
        assertEquals ( 6, LuaProvider.lua ( "G1" ).get ( "V2" ).toint () );
    }
    
    @Test
    public void testCreateGroup() throws DiceRollerException, SQLException
    {
        model.createGroup ( "Test" );
                
        assertEquals ( "Test", model.groupNames ().get ( 7 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testCreateDuplicateGroup() throws DiceRollerException, SQLException
    {
        thrown.expect ( DiceRollerException.class );
        model.createGroup ( "G1" );
    }
    
    @Test
    public void testRenameGroup() throws DiceRollerException, SQLException
    {
        assertEquals ( "G3", model.groupNames ().get ( 2 ) );

        model.renameGroup ( "G3", "Test" );
        
        assertEquals ( "Test", model.groupNames ().get ( 2 ) );
    }
    
    @Test
    public void testRenameGroupDuplicate() throws DiceRollerException, SQLException
    {
        thrown.expect ( DiceRollerException.class );
        model.renameGroup ( "G1", "G2" );
    }
    
    @Test
    public void testDeleteFirstGroup() throws DiceRollerException, SQLException
    {
        assertEquals ( 7, model.rollers ( "G1" ).size () );
        
        assertEquals ( 2, new Sql ( sql, "select count(*) from labels where rollerId=1" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 2, new Sql ( sql, "select count(*) from triggers where rollerId=1" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 2, new Sql ( sql, "select count(*) from variables where groupId=1" ).go ().single ().getInt ( 1 ) );
        
        model.deleteGroup ( "G1" );
        
        assertEquals ( 6, model.groupNames ().size() );
        assertEquals ( 0, model.rollers ( "G1" ).size () );
        
        assertEquals ( 0, new Sql ( sql, "select count(*) from labels where rollerId=1" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( sql, "select count(*) from triggers where rollerId=1" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( sql, "select count(*) from variables where groupId=1" ).go ().single ().getInt ( 1 ) );

        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testDeleteMiddleGroup() throws DiceRollerException, SQLException
    {        
        model.deleteGroup ( "G4" );
        
        assertEquals ( 6, model.groupNames ().size() );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testDeleteLastGroup() throws DiceRollerException, SQLException
    {
        model.deleteGroup ( "G7" );
        
        assertEquals ( 6, model.groupNames ().size() );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_First_To_Last() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G1", 7 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G2", groups.get ( 0 ) );
        assertEquals ( "G3", groups.get ( 1 ) );
        assertEquals ( "G4", groups.get ( 2 ) );
        assertEquals ( "G5", groups.get ( 3 ) );
        assertEquals ( "G6", groups.get ( 4 ) );
        assertEquals ( "G7", groups.get ( 5 ) );
        assertEquals ( "G1", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_First_Down_One() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G1", 2 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G2", groups.get ( 0 ) );
        assertEquals ( "G1", groups.get ( 1 ) );
        assertEquals ( "G3", groups.get ( 2 ) );
        assertEquals ( "G4", groups.get ( 3 ) );
        assertEquals ( "G5", groups.get ( 4 ) );
        assertEquals ( "G6", groups.get ( 5 ) );
        assertEquals ( "G7", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_First_Down_Two() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G1", 3 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G2", groups.get ( 0 ) );
        assertEquals ( "G3", groups.get ( 1 ) );
        assertEquals ( "G1", groups.get ( 2 ) );
        assertEquals ( "G4", groups.get ( 3 ) );
        assertEquals ( "G5", groups.get ( 4 ) );
        assertEquals ( "G6", groups.get ( 5 ) );
        assertEquals ( "G7", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_First_In_Place() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G1", 1 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G1", groups.get ( 0 ) );
        assertEquals ( "G2", groups.get ( 1 ) );
        assertEquals ( "G3", groups.get ( 2 ) );
        assertEquals ( "G4", groups.get ( 3 ) );
        assertEquals ( "G5", groups.get ( 4 ) );
        assertEquals ( "G6", groups.get ( 5 ) );
        assertEquals ( "G7", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_Last_To_First() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G7", 1 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G7", groups.get ( 0 ) );
        assertEquals ( "G1", groups.get ( 1 ) );
        assertEquals ( "G2", groups.get ( 2 ) );
        assertEquals ( "G3", groups.get ( 3 ) );
        assertEquals ( "G4", groups.get ( 4 ) );
        assertEquals ( "G5", groups.get ( 5 ) );
        assertEquals ( "G6", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_Last_Up_One() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G7", 6 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G1", groups.get ( 0 ) );
        assertEquals ( "G2", groups.get ( 1 ) );
        assertEquals ( "G3", groups.get ( 2 ) );
        assertEquals ( "G4", groups.get ( 3 ) );
        assertEquals ( "G5", groups.get ( 4 ) );
        assertEquals ( "G7", groups.get ( 5 ) );
        assertEquals ( "G6", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_Last_Up_Two() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G7", 5 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G1", groups.get ( 0 ) );
        assertEquals ( "G2", groups.get ( 1 ) );
        assertEquals ( "G3", groups.get ( 2 ) );
        assertEquals ( "G4", groups.get ( 3 ) );
        assertEquals ( "G7", groups.get ( 4 ) );
        assertEquals ( "G5", groups.get ( 5 ) );
        assertEquals ( "G6", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_Last_In_Place() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G7", 7 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G1", groups.get ( 0 ) );
        assertEquals ( "G2", groups.get ( 1 ) );
        assertEquals ( "G3", groups.get ( 2 ) );
        assertEquals ( "G4", groups.get ( 3 ) );
        assertEquals ( "G5", groups.get ( 4 ) );
        assertEquals ( "G6", groups.get ( 5 ) );
        assertEquals ( "G7", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_Middle_To_First() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G4", 1 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G4", groups.get ( 0 ) );
        assertEquals ( "G1", groups.get ( 1 ) );
        assertEquals ( "G2", groups.get ( 2 ) );
        assertEquals ( "G3", groups.get ( 3 ) );
        assertEquals ( "G5", groups.get ( 4 ) );
        assertEquals ( "G6", groups.get ( 5 ) );
        assertEquals ( "G7", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_Middle_To_Last() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G4", 7 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G1", groups.get ( 0 ) );
        assertEquals ( "G2", groups.get ( 1 ) );
        assertEquals ( "G3", groups.get ( 2 ) );
        assertEquals ( "G5", groups.get ( 3 ) );
        assertEquals ( "G6", groups.get ( 4 ) );
        assertEquals ( "G7", groups.get ( 5 ) );
        assertEquals ( "G4", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveGroup_Middle_Up_One() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G4", 3 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G1", groups.get ( 0 ) );
        assertEquals ( "G2", groups.get ( 1 ) );
        assertEquals ( "G4", groups.get ( 2 ) );
        assertEquals ( "G3", groups.get ( 3 ) );
        assertEquals ( "G5", groups.get ( 4 ) );
        assertEquals ( "G6", groups.get ( 5 ) );
        assertEquals ( "G7", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }    
    
    @Test
    public void testMoveGroup_Middle_Up_Two() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G4", 2 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G1", groups.get ( 0 ) );
        assertEquals ( "G4", groups.get ( 1 ) );
        assertEquals ( "G2", groups.get ( 2 ) );
        assertEquals ( "G3", groups.get ( 3 ) );
        assertEquals ( "G5", groups.get ( 4 ) );
        assertEquals ( "G6", groups.get ( 5 ) );
        assertEquals ( "G7", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }    
    
    @Test
    public void testMoveGroup_Middle_Down_One() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G4", 5 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G1", groups.get ( 0 ) );
        assertEquals ( "G2", groups.get ( 1 ) );
        assertEquals ( "G3", groups.get ( 2 ) );
        assertEquals ( "G5", groups.get ( 3 ) );
        assertEquals ( "G4", groups.get ( 4 ) );
        assertEquals ( "G6", groups.get ( 5 ) );
        assertEquals ( "G7", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }    
    
    @Test
    public void testMoveGroup_Middle_Down_Two() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G4", 6 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G1", groups.get ( 0 ) );
        assertEquals ( "G2", groups.get ( 1 ) );
        assertEquals ( "G3", groups.get ( 2 ) );
        assertEquals ( "G5", groups.get ( 3 ) );
        assertEquals ( "G6", groups.get ( 4 ) );
        assertEquals ( "G4", groups.get ( 5 ) );
        assertEquals ( "G7", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }    
    
    @Test
    public void testMoveGroup_Middle_In_Place() throws DiceRollerException, SQLException
    {
        model.moveGroup ( "G4", 4 );
        
        List<String> groups = model.groupNames ();
        
        //for ( String name : groups ) System.out.println ( name );
        
        assertEquals ( "G1", groups.get ( 0 ) );
        assertEquals ( "G2", groups.get ( 1 ) );
        assertEquals ( "G3", groups.get ( 2 ) );
        assertEquals ( "G4", groups.get ( 3 ) );
        assertEquals ( "G5", groups.get ( 4 ) );
        assertEquals ( "G6", groups.get ( 5 ) );
        assertEquals ( "G7", groups.get ( 6 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from groups order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testRollers() throws DiceRollerException
    {        
        assertEquals ( 7, rollers.size () );
        
        int expect = 1;
        
        for ( Roller roller : rollers )
            assertEquals ( "R" + expect++, roller.rollerName );
    }
    
    @Test
    public void testCreateRoller() throws DiceRollerException, SQLException
    {
        Roller roller = new RollerBuilder ().group ( "G1" ).name ( "Test" ).definition ( "1d4" ).build ();
        
        model.createRoller ( roller );
                
        assertEquals ( "Test", model.rollers ( "G1" ).get ( 7 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testCreateDuplicateRoller() throws DiceRollerException, SQLException
    {
        Roller roller = new RollerBuilder ().group ( "G1" ).name ( "R1" ).definition ( "1d4" ).build ();
        
        thrown.expect ( DiceRollerException.class );
        model.createRoller ( roller );
    }
    
    @Test
    public void testRenameRoller() throws DiceRollerException, SQLException
    {
        assertEquals ( "R3", rollers.get ( 2 ).name () );

        model.renameRoller ( "G1", "R3", "Test" );
        
        assertEquals ( "Test", model.rollers ( "G1" ).get ( 2 ).name () );
    }
    
    @Test
    public void testRenameRollerDuplicate() throws DiceRollerException, SQLException
    {
        thrown.expect ( DiceRollerException.class );
        model.renameRoller ( "G1", "R1", "R2" );
    }
    
    @Test
    public void testUpdateRoller() throws DiceRollerException, SQLException
    {
        assertEquals ( "G1", rollers.get ( 0 ).groupName );
        assertEquals ( "R1", rollers.get ( 0 ).rollerName );
        assertEquals ( "1D4", rollers.get ( 0 ).definition );
        
        assertEquals ( 2, new Sql ( sql, "select count(*) from labels where rollerId=1" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 2, new Sql ( sql, "select count(*) from triggers where rollerId=1" ).go ().single ().getInt ( 1 ) );

        model.updateRoller ( new RollerBuilder().group ( "G1" ).name ( "R1" ).definition ( "3d6" ).addLabel ( 7, "Jackie" ).addTrigger ( "Fred", "A==10" ).build () );
        
        rollers = model.rollers ( "G1" );

        assertEquals ( "G1", rollers.get ( 0 ).groupName );
        assertEquals ( "R1", rollers.get ( 0 ).rollerName );
        assertEquals ( "3d6", rollers.get ( 0 ).definition );
        
        assertEquals ( 1, new Sql ( sql, "select count(*) from labels where rollerId=1" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 1, new Sql ( sql, "select count(*) from triggers where rollerId=1" ).go ().single ().getInt ( 1 ) );
        
        ResultSet label = new Sql ( sql, "select value,label from labels where rollerId=1" ).go().single ();
        
        assertEquals ( 7, label.getInt ( 1 ) );
        assertEquals ( "Jackie", label.getString ( 2 ) );
        
        ResultSet trigger = new Sql ( sql, "select name,definition from triggers where rollerId=1" ).go().single ();
        
        assertEquals ( "Fred", trigger.getString ( 1 ) );
        assertEquals ( "A==10", trigger.getString ( 2 ) );        
   }
    
    @Test
    public void testDeleteFirstRoller() throws DiceRollerException, SQLException
    {
        assertEquals ( 2, new Sql ( sql, "select count(*) from labels where rollerId=1" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 2, new Sql ( sql, "select count(*) from triggers where rollerId=1" ).go ().single ().getInt ( 1 ) );
        
        model.deleteRoller ( rollers.get ( 0 ) );
                
        assertEquals ( 6, model.rollers ( "G1" ).size() );
        
        assertEquals ( 0, new Sql ( sql, "select count(*) from labels where rollerId=1" ).go ().single ().getInt ( 1 ) );
        assertEquals ( 0, new Sql ( sql, "select count(*) from triggers where rollerId=1" ).go ().single ().getInt ( 1 ) );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testDeleteMiddleRoller() throws DiceRollerException, SQLException
    {        
        model.deleteRoller ( rollers.get ( 3 ) );
        
        assertEquals ( 6, model.rollers ( "G1" ).size() );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testDeleteLastRoller() throws DiceRollerException, SQLException
    {
        model.deleteRoller ( rollers.get ( 6 ) );
        
        assertEquals ( 6, model.rollers ( "G1" ).size() );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_First_To_Last() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 0 ), 7 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R2", rollers.get ( 0 ).name () );
        assertEquals ( "R3", rollers.get ( 1 ).name () );
        assertEquals ( "R4", rollers.get ( 2 ).name () );
        assertEquals ( "R5", rollers.get ( 3 ).name () );
        assertEquals ( "R6", rollers.get ( 4 ).name () );
        assertEquals ( "R7", rollers.get ( 5 ).name () );
        assertEquals ( "R1", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_First_Down_One() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 0 ), 2 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R2", rollers.get ( 0 ).name () );
        assertEquals ( "R1", rollers.get ( 1 ).name () );
        assertEquals ( "R3", rollers.get ( 2 ).name () );
        assertEquals ( "R4", rollers.get ( 3 ).name () );
        assertEquals ( "R5", rollers.get ( 4 ).name () );
        assertEquals ( "R6", rollers.get ( 5 ).name () );
        assertEquals ( "R7", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_First_Down_Two() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 0 ), 3 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R2", rollers.get ( 0 ).name () );
        assertEquals ( "R3", rollers.get ( 1 ).name () );
        assertEquals ( "R1", rollers.get ( 2 ).name () );
        assertEquals ( "R4", rollers.get ( 3 ).name () );
        assertEquals ( "R5", rollers.get ( 4 ).name () );
        assertEquals ( "R6", rollers.get ( 5 ).name () );
        assertEquals ( "R7", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_First_In_Place() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 0 ), 1 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R1", rollers.get ( 0 ).name () );
        assertEquals ( "R2", rollers.get ( 1 ).name () );
        assertEquals ( "R3", rollers.get ( 2 ).name () );
        assertEquals ( "R4", rollers.get ( 3 ).name () );
        assertEquals ( "R5", rollers.get ( 4 ).name () );
        assertEquals ( "R6", rollers.get ( 5 ).name () );
        assertEquals ( "R7", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_Last_To_First() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 6 ), 1 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R7", rollers.get ( 0 ).name () );
        assertEquals ( "R1", rollers.get ( 1 ).name () );
        assertEquals ( "R2", rollers.get ( 2 ).name () );
        assertEquals ( "R3", rollers.get ( 3 ).name () );
        assertEquals ( "R4", rollers.get ( 4 ).name () );
        assertEquals ( "R5", rollers.get ( 5 ).name () );
        assertEquals ( "R6", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_Last_Up_One() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 6 ), 6 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R1", rollers.get ( 0 ).name () );
        assertEquals ( "R2", rollers.get ( 1 ).name () );
        assertEquals ( "R3", rollers.get ( 2 ).name () );
        assertEquals ( "R4", rollers.get ( 3 ).name () );
        assertEquals ( "R5", rollers.get ( 4 ).name () );
        assertEquals ( "R7", rollers.get ( 5 ).name () );
        assertEquals ( "R6", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_Last_Up_Two() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 6 ), 5 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R1", rollers.get ( 0 ).name () );
        assertEquals ( "R2", rollers.get ( 1 ).name () );
        assertEquals ( "R3", rollers.get ( 2 ).name () );
        assertEquals ( "R4", rollers.get ( 3 ).name () );
        assertEquals ( "R7", rollers.get ( 4 ).name () );
        assertEquals ( "R5", rollers.get ( 5 ).name () );
        assertEquals ( "R6", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_Last_In_Place() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 6 ), 7 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R1", rollers.get ( 0 ).name () );
        assertEquals ( "R2", rollers.get ( 1 ).name () );
        assertEquals ( "R3", rollers.get ( 2 ).name () );
        assertEquals ( "R4", rollers.get ( 3 ).name () );
        assertEquals ( "R5", rollers.get ( 4 ).name () );
        assertEquals ( "R6", rollers.get ( 5 ).name () );
        assertEquals ( "R7", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_Middle_To_First() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 3 ), 1 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R4", rollers.get ( 0 ).name () );
        assertEquals ( "R1", rollers.get ( 1 ).name () );
        assertEquals ( "R2", rollers.get ( 2 ).name () );
        assertEquals ( "R3", rollers.get ( 3 ).name () );
        assertEquals ( "R5", rollers.get ( 4 ).name () );
        assertEquals ( "R6", rollers.get ( 5 ).name () );
        assertEquals ( "R7", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_Middle_To_Last() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 3 ), 7 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R1", rollers.get ( 0 ).name () );
        assertEquals ( "R2", rollers.get ( 1 ).name () );
        assertEquals ( "R3", rollers.get ( 2 ).name () );
        assertEquals ( "R5", rollers.get ( 3 ).name () );
        assertEquals ( "R6", rollers.get ( 4 ).name () );
        assertEquals ( "R7", rollers.get ( 5 ).name () );
        assertEquals ( "R4", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testMoveRoller_Middle_Up_One() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 3 ), 3 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R1", rollers.get ( 0 ).name () );
        assertEquals ( "R2", rollers.get ( 1 ).name () );
        assertEquals ( "R4", rollers.get ( 2 ).name () );
        assertEquals ( "R3", rollers.get ( 3 ).name () );
        assertEquals ( "R5", rollers.get ( 4 ).name () );
        assertEquals ( "R6", rollers.get ( 5 ).name () );
        assertEquals ( "R7", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }    
    
    @Test
    public void testMoveRoller_Middle_Up_Two() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 3 ), 2 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R1", rollers.get ( 0 ).name () );
        assertEquals ( "R4", rollers.get ( 1 ).name () );
        assertEquals ( "R2", rollers.get ( 2 ).name () );
        assertEquals ( "R3", rollers.get ( 3 ).name () );
        assertEquals ( "R5", rollers.get ( 4 ).name () );
        assertEquals ( "R6", rollers.get ( 5 ).name () );
        assertEquals ( "R7", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }    
    
    @Test
    public void testMoveRoller_Middle_Down_One() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 3 ), 5 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R1", rollers.get ( 0 ).name () );
        assertEquals ( "R2", rollers.get ( 1 ).name () );
        assertEquals ( "R3", rollers.get ( 2 ).name () );
        assertEquals ( "R5", rollers.get ( 3 ).name () );
        assertEquals ( "R4", rollers.get ( 4 ).name () );
        assertEquals ( "R6", rollers.get ( 5 ).name () );
        assertEquals ( "R7", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }    
    
    @Test
    public void testMoveRoller_Middle_Down_Two() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 3 ), 6 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R1", rollers.get ( 0 ).name () );
        assertEquals ( "R2", rollers.get ( 1 ).name () );
        assertEquals ( "R3", rollers.get ( 2 ).name () );
        assertEquals ( "R5", rollers.get ( 3 ).name () );
        assertEquals ( "R6", rollers.get ( 4 ).name () );
        assertEquals ( "R4", rollers.get ( 5 ).name () );
        assertEquals ( "R7", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }    
    
    @Test
    public void testMoveRoller_Middle_In_Place() throws DiceRollerException, SQLException
    {
        model.moveRoller ( rollers.get ( 3 ), 4 );
        
        rollers = model.rollers ( "G1" );
                
        assertEquals ( "R1", rollers.get ( 0 ).name () );
        assertEquals ( "R2", rollers.get ( 1 ).name () );
        assertEquals ( "R3", rollers.get ( 2 ).name () );
        assertEquals ( "R4", rollers.get ( 3 ).name () );
        assertEquals ( "R5", rollers.get ( 4 ).name () );
        assertEquals ( "R6", rollers.get ( 5 ).name () );
        assertEquals ( "R7", rollers.get ( 6 ).name () );
        
        int expect = 1;
        
        for ( ResultSet row : new Sql ( sql, "select sequence from rollers where groupId = 1 order by sequence" ).go() )
            assertEquals ( expect++, row.getInt ( 1 ) );
    }
    
    @Test
    public void testVariables() throws DiceRollerException
    {
        List<Variable> variables = model.groupVariables ( "G1" );
        
        assertEquals ( 2, variables.size () );
        
        assertEquals ( "V1", variables.get ( 0 ).name );
        assertEquals ( 3, variables.get ( 0 ).value );
        
        assertEquals ( "V2", variables.get ( 1 ).name );
        assertEquals ( 6, variables.get ( 1 ).value );
                
        variables = new ArrayList<> ();
        
        variables.add ( new Variable ( "Jackie", 10 ) );
        
        model.updateGroupVariables ( "G1", variables );

        variables = model.groupVariables ( "G1" );
        
        assertEquals ( 1, variables.size () );

        assertEquals ( "Jackie", variables.get ( 0 ).name );
        assertEquals ( 10, variables.get ( 0 ).value );
        
        // Test Lua Context
        
        assertEquals ( 10, LuaProvider.lua ( "G1" ).get ( "Jackie" ).toint () );
    }
}
