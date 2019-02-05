package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import duffrd.diceroller.model.Roller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;

public class ProbabilityController implements Initializable
{
    private static final int NUM_TEST_ITERATIONS = 100_000;
    
    @FXML
    public BarChart<String,Double> probabilityChart;
    
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
    
    @FXML
    public Button recalculateButton;
    
    @FXML
    public Button rollerTestButton;
    
    private Roller roller;
    
    Map<DataSet,XYChart.Series<String,Double>> data;
    
    public ProbabilityController ( Roller roller )
    {
        this.roller = roller;
    }
    
    @Override
    public void initialize ( URL location, ResourceBundle resources )
    {
        calculate ( false );
        
        eqButton.setOnAction ( event -> loadDataSet ( DataSet.EQUAL ) );
        ltButton.setOnAction ( event -> loadDataSet ( DataSet.LESS_THAN ) );
        leButton.setOnAction ( event -> loadDataSet ( DataSet.LESS_THAN_OR_EQUAL ) );
        gtButton.setOnAction ( event -> loadDataSet ( DataSet.GREATER_THAN ) );
        geButton.setOnAction ( event -> loadDataSet ( DataSet.GREATER_THAN_OR_EQUAL ) );
        
        recalculateButton.setOnAction ( event -> calculate ( true ) );
        rollerTestButton.setOnAction ( event -> rollerTest() );
        
        probabilityChart.setTitle ( roller.name () );
        
        probabilityChart.getXAxis ().setLabel ( "Outcomes" );
        probabilityChart.getYAxis ().setLabel ( "Probability" );
        probabilityChart.setLegendVisible ( false );
        
        loadDataSet ( DataSet.EQUAL );
    }
    
    private void calculate ( boolean recalculate )
    {
        CalculationController calc = new CalculationController ( roller );
        try
        {
            data = calc.calculate ( recalculate );
            loadDataSet ( DataSet.EQUAL );
            eqButton.setSelected ( true );
        }
        catch ( IOException e1 )
        {
            new ExceptionAlert ( "Calculating Probabilities", e1 ).showAndWait ();
        }        
    }
    
    private void rollerTest()
    {
        if ( data == null )
            return;
        
        Map<String,Long> outcomeCounts = new HashMap<>();
        
        for ( XYChart.Data<String,Double> e : data.get ( DataSet.EQUAL ).getData () )
            outcomeCounts.put ( e.getXValue (), 0L );
        
        for ( int i = 0; i < NUM_TEST_ITERATIONS; i++ )
        {
            roller.roll();
            String name = roller.lastOutcome ().outcome ();
            
            outcomeCounts.put ( name, outcomeCounts.get ( name ) + 1 );
        }
        
        XYChart.Series<String,Double> testResults = new XYChart.Series<> ();
        
        for ( XYChart.Data<String,Double> e : data.get ( DataSet.EQUAL ).getData () )
        {
            String label = e.getXValue ();
            long count = outcomeCounts.get ( label );
            double percentage = ( double ) count / ( double ) NUM_TEST_ITERATIONS;
            String tip = String.format ( "%d out of %d Outcomes\n%.1f%%", count, NUM_TEST_ITERATIONS, percentage * 100.0 );
            
            testResults.getData().add ( new XYChart.Data<String,Double> ( label, ( double ) count / ( double ) NUM_TEST_ITERATIONS, tip ) );
        }
        
        probabilityChart.getData ().add ( testResults );
                
        for ( XYChart.Series<String,Double> series : probabilityChart.getData () )
            for ( XYChart.Data<String,Double> d : series.getData () )
            {
                Tooltip tip = new Tooltip();
                tip.setText ( d.getExtraValue ().toString () );
                Tooltip.install ( d.getNode (), tip );
            }
        
    }
    
    private void loadDataSet ( DataSet set )
    {
        probabilityChart.getData ().clear ();

        if ( data == null )
            return;
        
        probabilityChart.getData ().add ( data.get ( set ) );   
        
        for ( XYChart.Series<String,Double> series : probabilityChart.getData () )
            for ( XYChart.Data<String,Double> d : series.getData () )
            {
                Tooltip tip = new Tooltip();
                tip.setText ( d.getExtraValue ().toString () );
                Tooltip.install ( d.getNode (), tip );
                
                d.getNode ().setStyle ( set.style () );
            }
    }
}
