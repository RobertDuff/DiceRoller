package duffrd.diceroller.view;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import duffrd.diceroller.model.ProbablityCalculationCancelledException;
import duffrd.diceroller.model.Roller;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.chart.XYChart;

public class ProbabilityCalculatorTask extends Task<Map<DataSet,XYChart.Series<String,Double>>>
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );

    private Roller roller;
    private boolean recalculate;
    
	public ProbabilityCalculatorTask ( Roller roller, boolean recalculate )
	{
		this.roller = roller;
		this.recalculate = recalculate;
		roller.canceledProperty ().bind ( Bindings.equal ( stateProperty (), Worker.State.CANCELLED ) );
	}

	@Override
	protected void cancelled()
	{
	    //roller.canceledProperty ().set ( true );
	}
	
	@Override
	protected Map<DataSet,XYChart.Series<String,Double>> call () throws Exception
	{
	    logger.debug ( "Called..." );
        ChangeListener<Number> p = ( i, o, n ) -> updateProgress ( n.doubleValue (), 1.0 );
	    
        roller.progressProperty ().addListener ( p );
                
        logger.debug ( "Getting Probs..." );
        long[] eq = null;
        
        try
        {
            eq = roller.calculateProbabilities ( recalculate );
        }
        catch ( ProbablityCalculationCancelledException e )
        {
            return null;
        }
	                    
        roller.progressProperty ().removeListener ( p );
        
        updateProgress ( 1,  1 );
        
        long totalOutcomes = Arrays.stream ( eq ).sum ();
        
        long[] lt = new long[ eq.length ];
        long[] le = new long[ eq.length ];
        long[] gt = new long[ eq.length ];
        long[] ge = new long[ eq.length ];
        
        for ( int i=0; i<eq.length; i++ ) if ( eq[ i ] == 0 ) eq[ i ] = Long.MIN_VALUE;
        Arrays.fill ( lt, Long.MIN_VALUE );
        Arrays.fill ( le, Long.MIN_VALUE );
        Arrays.fill ( gt, Long.MIN_VALUE );
        Arrays.fill ( ge, Long.MIN_VALUE );
        
        long soFar = 0;
        long remaining = totalOutcomes;
        
        for ( int i=0; i<eq.length; i++ )
        {
            if ( eq[ i ] == Long.MIN_VALUE )
                continue;
            
            lt[ i ] = soFar;
            ge[ i ] = remaining;
            
            soFar += eq[ i ];
            remaining -= eq[ i ];
            
            le[ i ] = soFar;
            gt[ i ] = remaining;
        }
        
        Map<DataSet,XYChart.Series<String,Double>> data = new HashMap<>();
        
        addSeries ( data, totalOutcomes,                 DataSet.EQUAL, eq );
        addSeries ( data, totalOutcomes,             DataSet.LESS_THAN, lt );
        addSeries ( data, totalOutcomes,    DataSet.LESS_THAN_OR_EQUAL, le );
        addSeries ( data, totalOutcomes,          DataSet.GREATER_THAN, gt );
        addSeries ( data, totalOutcomes, DataSet.GREATER_THAN_OR_EQUAL, ge );
        
        return data;
	}	
	
	private void addSeries ( Map<DataSet,XYChart.Series<String,Double>> data, long totalOutcomes, DataSet set, long[] prob )
	{
	   XYChart.Series<String,Double> series = new XYChart.Series<> ();
	   series.setName ( set.label () );
	   
	   for ( int outcome = 0; outcome < prob.length; outcome++ )
	   {
	       if ( prob[ outcome ] == Long.MIN_VALUE )
	           continue;
	       
	       String label = String.valueOf ( outcome );
	       
	       if ( roller.labels ().containsKey ( outcome ) )
	           label = roller.labels ().get ( outcome );
	       
	       double percentage = ( double ) prob[ outcome ] / ( double ) totalOutcomes;
	       String toolTip = String.format ( "%d out of %d Outcomes\n%.1f%%", prob[ outcome ], totalOutcomes, percentage * 100.0 );
	       
	       series.getData ().add ( new XYChart.Data<> ( label, percentage, toolTip ) );
	   }
	   
	   data.put ( set, series );
	}
}
