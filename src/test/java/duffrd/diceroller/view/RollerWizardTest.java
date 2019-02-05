package duffrd.diceroller.view;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;

import org.luaj.vm2.Globals;

import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.Suite;
import duffrd.diceroller.model.Trigger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import utility.lua.LuaProvider;

public class RollerWizardTest extends Application
{
    public static void main ( String[] args )
    {
        launch ( args );
    }
    
    @Override
    public void start ( Stage primaryStage ) throws Exception
    {
        Button button = new Button();
        button.setText ( "Test Wizard" );
        button.setOnAction ( event -> 
        {            
            RollerWizard wizard = new RollerWizard ( testSuite() );
            
            Optional<ButtonType> result = wizard.cast ();
            
            if ( result.isPresent () && result.get () == ButtonType.FINISH )
            {
                Roller roller = wizard.product ();
                
                Iterator<Entry<Integer,String>> x = roller.labels ().entrySet ().iterator ();
                
                while ( x.hasNext () )
                {
                    Entry<Integer,String> entry = x.next ();
                    
                    if ( entry.getValue () == null || entry.getValue ().isEmpty () )
                        x.remove ();
                }
                
                roller.outcomeProperty ().addListener ( ( a, o, n ) -> System.out.println ( n ) );
                
                System.out.println ( "---" );
                roller.roll ();
                roller.roll ();
                roller.roll ();
                roller.roll ();
                roller.roll ();
                roller.roll ();
                roller.roll ();
                roller.roll ();
                roller.roll ();
                roller.roll ();
            }
        } );
        
        primaryStage.setScene ( new Scene ( button, 400, 250 ) );
        primaryStage.show ();        
    }
    
    private Suite testSuite()
    {
        Globals lua = LuaProvider.newLua ();
        
        Suite suite = new Suite();
        suite.lua ( lua );
        
        suite.triggers ().add ( new Trigger().lua ( lua ).name ( ">1" ).definition ( "A>1" ) );
        suite.triggers ().add ( new Trigger().lua ( lua ).name ( ">2" ).definition ( "A>2" ) );
        suite.triggers ().add ( new Trigger().lua ( lua ).name ( ">3" ).definition ( "A>3" ) );
        suite.triggers ().add ( new Trigger().lua ( lua ).name ( ">4" ).definition ( "A>4" ) );
        suite.triggers ().add ( new Trigger().lua ( lua ).name ( ">5" ).definition ( "A>5" ) );
        
        return suite;
    }
}
