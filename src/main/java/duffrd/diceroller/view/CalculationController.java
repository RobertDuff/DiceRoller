package duffrd.diceroller.view;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import duffrd.diceroller.model.Roller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CalculationController implements Initializable
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

    private static Executor executor = Executors.newFixedThreadPool ( 1, new ThreadFactory()
    {   
        @Override
        public Thread newThread ( Runnable r )
        {
            Thread thread = new Thread ( r );
            thread.setDaemon ( true );
            return thread;
        }
    } );

    @FXML
    public Button cancelCalcButton;
    
    @FXML
    public ProgressBar calcProgress;
    
    private Roller roller;
    private ProbabilityCalculatorTask calcTask;
    private boolean recalculate;
    private Stage calcStage;
    
    public CalculationController ( Roller roller )
    {
        this.roller = roller;
    }
    
    @Override
    public void initialize ( URL location, ResourceBundle resources )
    {
        calcTask = new ProbabilityCalculatorTask ( roller, recalculate );
                
        calcTask.setOnSucceeded ( event -> calcStage.close () );
        calcTask.setOnFailed ( event -> calcStage.close () );
        calcTask.setOnCancelled ( event -> calcStage.close () );

        calcProgress.progressProperty().bind ( calcTask.progressProperty () );
        
        cancelCalcButton.setOnAction ( event -> calcTask.cancel () );
        
        logger.debug ( "Starting..." );
        executor.execute ( calcTask );
    }
    
    Map<DataSet,XYChart.Series<String,Long>> calculate() throws IOException
    {
        return calculate  ( false );
    }
    
    Map<DataSet,XYChart.Series<String,Long>> calculate ( boolean recalculate ) throws IOException
    {
        this.recalculate = recalculate;
        
        FXMLLoader calcLoader = new FXMLLoader ( getClass().getResource ( "CalculationProgress.fxml" ) );
        calcLoader.setController ( this );
        Pane calcPane = calcLoader.load();

        calcStage = new Stage();
        calcStage.initModality ( Modality.APPLICATION_MODAL );
        calcStage.initOwner ( DiceRollerApplication.instance ().mainStage () );
        Scene calcScene = new Scene ( calcPane, 600, 200 );
        calcStage.setScene ( calcScene );
        calcStage.showAndWait ();
        
        return calcTask.getValue ();
    }
}
