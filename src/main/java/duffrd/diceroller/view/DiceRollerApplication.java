package duffrd.diceroller.view;

import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import duffrd.diceroller.model.Model;
import duffrd.diceroller.model.ModelLoader;
import duffrd.diceroller.model.sqlite.SqliteDbProvider;
import duffrd.diceroller.model.sqlite.SqliteModelLoader;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DiceRollerApplication extends Application
{	
	private static DiceRollerApplication instance;
	
	private Stage mainStage;
	private Model model;
	
	public static void main ( String[] args )
	{
		DiceRollerApplication.launch ( args );
	}

	public DiceRollerApplication () throws URISyntaxException
	{
	    instance = this;
	}

	public static DiceRollerApplication instance()
	{
	    return instance;
	}
	
	public Stage mainStage()
	{
	    return mainStage;
	}

	@Override
	public void start ( Stage splashStage ) throws Exception
	{
	    URL splashImageURL = getClass().getResource ( "splash.bmp" );
	    Image splashImage = new Image ( splashImageURL.toExternalForm () );
	    ImageView splashImageView = new ImageView ( splashImage );

	    AnchorPane splashPane = new AnchorPane ( splashImageView );

	    Transition splashTransition = new PauseTransition ( Duration.seconds ( 2.5 ) );
        
	    FutureTask<Model> modelTask = new FutureTask<Model>( () -> 
	    {
	        Connection db = SqliteDbProvider.provideStandardDB ();
	        ModelLoader loader = new SqliteModelLoader ( db );	        
	        return loader.load ();
	    } );

	    splashTransition.setOnFinished ( e -> 
	    { 
	        try
	        {
	            model = modelTask.get ();
	        }
	        catch ( InterruptedException e1 )
	        {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	        }
	        catch ( ExecutionException e1 )
	        {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	        }
	        splashStage.close (); 
	        openMainStage();
	    } );

	    splashTransition.play ();

	    Scene splashScene = new Scene ( splashPane );

	    splashStage.setScene ( splashScene );
	    splashStage.initStyle ( StageStyle.TRANSPARENT );
	    splashStage.show();

	    Thread modelThread = new Thread ( modelTask );
	    modelThread.setDaemon ( true );
	    modelThread.start ();
	}
	
	public void openMainStage()
	{
		try
		{
			mainStage = new Stage ( StageStyle.DECORATED );

			FXMLLoader mainWindowLoader = new FXMLLoader ( getClass().getResource ( "MainWindow.fxml" ) );
			mainWindowLoader.setController ( new MainWindowController ( getHostServices (), model ) );
			Pane mainWindowPane = mainWindowLoader.load();
			
			Scene mainScene = new Scene ( mainWindowPane );
			mainStage.setScene ( mainScene );
			
			mainStage.show ();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			
			Platform.runLater ( () ->
			{
				new Alert ( AlertType.ERROR, "Could not initialize Main Window" ).showAndWait ();
				Platform.exit ();
			} );
		}
	}

	@Override
	public void stop () throws Exception
	{
		super.stop ();
	}
}
