package duffrd.diceroller.view;

import java.sql.SQLException;

import org.junit.Test;

import duffrd.diceroller.model.DiceRollerException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ExceptionAlertTest extends Application
{
    Exception a = new IllegalArgumentException ( "Badness Here!" );
    Exception b = new SQLException ( "SQL Error!"  + a );
    Exception c = new DiceRollerException ( b );

    @Test
    public void test()
    {
        launch();
    }

    @Override
    public void start ( Stage stage ) throws Exception
    {
        Button button = new Button();
        button.setText ( "Test" );
        button.setOnAction ( event -> new ExceptionAlert ( "Creating Life", c ).showAndWait () );
        
        Scene scene = new Scene ( button, 400, 300 );
        stage.setScene ( scene );
        stage.show ();
    }
}

