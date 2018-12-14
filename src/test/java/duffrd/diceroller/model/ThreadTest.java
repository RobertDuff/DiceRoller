package duffrd.diceroller.model;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ThreadTest extends Application
{
	private static ThreadFactory threadFactory = new ThreadFactory()
	{	
		@Override
		public Thread newThread ( Runnable r )
		{
			Thread thread = new Thread ( r );
			thread.setDaemon ( true );
			return thread;
		}
	};
	
	private static Executor executor = Executors.newFixedThreadPool ( 1, threadFactory );

	@Override
	public void start ( Stage stage ) throws Exception
	{
		Task<Integer> task = new Task<Integer>()
		{
			@Override
			protected Integer call () throws Exception
			{				
				updateValue ( -1 );
				
				int max = 1000000000;
				for ( int i = 0; i < max; i++ )
				{
					if ( isCancelled () )
						return 0;

					if ( i % 100000 == 0 )
					{
						updateMessage ( "Iteration: " + i );
						updateProgress ( i, max );
					}
				}

				updateMessage ( "Complete" );
				updateProgress ( 1,  1 );

				return 777;
			}
		};

		ProgressIndicator progress = new ProgressIndicator ();
		progress.setProgress ( 0 );

		ProgressIndicator bar = new ProgressBar ();
		bar.setProgress ( 0 );

		Label result = new Label();
		result.textProperty ().bind ( Bindings.convert ( task.valueProperty () ) );

		Label state = new Label();
		state.textProperty ().bind ( Bindings.convert ( task.stateProperty () ) );

		Label message = new Label();
		message.textProperty ().bind ( task.messageProperty () );

		Button go = new Button();
		go.setText ( "Go" );
		go.setOnAction ( e ->
		{
			progress.progressProperty ().bind ( task.progressProperty () );
			bar.progressProperty ().bind ( task.progressProperty () );
			executor.execute ( task );			
		} );

		Button cancel = new Button ( "Cancel" );
		cancel.setOnAction ( e -> 
		{
			task.cancel ();
		} );

		Pane pane = new VBox();
		pane.getChildren ().addAll ( go, cancel, state, progress, bar, message, result );

		Scene scene = new Scene ( pane );

		stage.setScene ( scene );
		stage.show ();
	}

	public static void main ( String[] args )
	{
		ThreadTest.launch();
	}
}
