package duffrd.diceroller.view;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import duffrd.diceroller.model.RollerModel;
import duffrd.diceroller.model.SqliteRollerModel;
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
	private static final String APPLICATION_PROPERTIES_PATH = "/DiceRoller.properties";

	private static final String SHOW_SPLASH_SCREEN_PROPERTY_KEY = "showSplashScreen";

	public static Properties applicationProperties;

	public RollerModel model;
	
	public static void main ( String[] args )
	{
		DiceRollerApplication.launch ( args );
	}

	public DiceRollerApplication ()
	{
	}

	@Override
	public void init () throws Exception
	{
		applicationProperties = new Properties ();
		
		InputStream propertyStream = getClass().getResourceAsStream ( APPLICATION_PROPERTIES_PATH );
		
		if ( propertyStream != null )
			applicationProperties.load ( propertyStream );
	}

	@Override
	public void start ( Stage splashStage ) throws Exception
	{
		if ( applicationProperties.getProperty ( SHOW_SPLASH_SCREEN_PROPERTY_KEY, "yes" ).matches ( "(?i:y|yes|t|true)" ) )
		{
		    FutureTask<RollerModel> modelTask = new FutureTask<RollerModel>(
		            new Callable<RollerModel>()
		            {
                        @Override
                        public RollerModel call () throws Exception
                        {
                            return new SqliteRollerModel();
                        }		                
		            }
		            );
		    
			URL splashImageURL = getClass().getResource ( "splash.bmp" );
			Image splashImage = new Image ( splashImageURL.toExternalForm () );
			ImageView splashImageView = new ImageView ( splashImage );

			AnchorPane splashPane = new AnchorPane ( splashImageView );

			Transition splashTransition = new PauseTransition ( Duration.seconds ( 2.5 ) );

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
		else
		{
		    model = new SqliteRollerModel ();
		    openMainStage ();
		}
	}
	
	public void openMainStage()
	{
		try
		{
			Stage mainStage = new Stage ( StageStyle.DECORATED );

			FXMLLoader mainWindowLoader = new FXMLLoader ( getClass().getResource ( "MainWindow.fxml" ) );
			mainWindowLoader.setController ( new MainWindowController ( model ) );
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
