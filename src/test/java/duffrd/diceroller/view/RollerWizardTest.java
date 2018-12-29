package duffrd.diceroller.view;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import duffrd.diceroller.model.History;
import duffrd.diceroller.model.HistoryEntry;
import duffrd.diceroller.model.Roller;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class RollerWizardTest extends Application
{
    @Test
    public void test ()
    {
        assertTrue ( true );
    }

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
            RollerWizard wizard = new RollerWizard ( "Group" );
            
            Optional<ButtonType> result = wizard.showAndWait ();
            
            if ( result.isPresent () && result.get () == ButtonType.FINISH )
            {
                Roller roller = wizard.roller ();
                
                History.history ().historyProperty ().addListener ( ( ListChangeListener<? super HistoryEntry> ) change -> 
                {
                    while ( change.next () )
                        if ( change.wasAdded () )
                            for ( HistoryEntry e : change.getAddedSubList () )
                                System.out.println ( "Outcome=" + e.getOutcome () + " Triggers=" + e.getTriggers () );
                } );
                
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
}
