package duffrd.diceroller.view;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import duffrd.diceroller.model.Roller;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;

public class ProbabilityController implements Initializable
{
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
    public BarChart<String,Long> probabilityChart;
    
    @FXML
    public RadioButton eqButton;
    
    @FXML
    public RadioButton ltButton;
    
    @FXML
    public RadioButton leButton;
    
    @FXML
    public RadioButton gtButton;
    
    @FXML
    public RadioButton geButton;
    
    private Roller roller;
    
    Map<DataSet,XYChart.Series<String,Long>> data;
    
    public ProbabilityController ( Roller roller )
    {
        this.roller = roller;
    }
    
    @Override
    public void initialize ( URL location, ResourceBundle resources )
    {
        final Alert alert = new Alert ( AlertType.INFORMATION );
        alert.setTitle ( "Probability Distribution" );
        alert.setHeaderText ( "Calculation in Progress.  Please Wait..." );
        
        Task<Map<DataSet,XYChart.Series<String,Long>>> probabilityCalculationTask = new ProbabilityCalculatorTask ( roller );
        
        probabilityCalculationTask.setOnSucceeded ( new EventHandler<WorkerStateEvent>() 
        {
            @Override
            public void handle ( WorkerStateEvent event )
            {
              alert.close ();  
            }
        } );

        executor.execute ( probabilityCalculationTask );
        
        alert.showAndWait ();
                
        try
        {
            data = probabilityCalculationTask.get ();
        }
        catch ( InterruptedException | ExecutionException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        eqButton.setOnAction ( event -> loadDataSet ( DataSet.EQUAL ) );
        ltButton.setOnAction ( event -> loadDataSet ( DataSet.LESS_THAN ) );
        leButton.setOnAction ( event -> loadDataSet ( DataSet.LESS_THAN_OR_EQUAL ) );
        gtButton.setOnAction ( event -> loadDataSet ( DataSet.GREATER_THAN ) );
        geButton.setOnAction ( event -> loadDataSet ( DataSet.GREATER_THAN_OR_EQUAL ) );
        
        probabilityChart.setTitle ( roller.name () );
        
        probabilityChart.getXAxis ().setLabel ( "Outcomes" );
        probabilityChart.getYAxis ().setLabel ( "Probability" );
        
        loadDataSet ( DataSet.EQUAL );
    }
    
    private void loadDataSet ( DataSet set )
    {
        probabilityChart.getData ().clear ();
        probabilityChart.getData ().add ( data.get ( set ) );   
        
        for ( XYChart.Series<String,Long> series : probabilityChart.getData () )
            for ( XYChart.Data<String,Long> d : series.getData () )
            {
                Tooltip tip = new Tooltip();
                tip.setText ( d.getExtraValue ().toString () );
                Tooltip.install ( d.getNode (), tip );
            }
    }
}
