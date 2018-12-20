package duffrd.diceroller.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import duffrd.diceroller.model.Roller;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import utility.arrays.ArrayConverter;

public class ProbabilityCalculatorTask extends Task<Map<DataSet,XYChart.Series<String,Long>>>
{
    private Roller roller;
    
	public ProbabilityCalculatorTask ( Roller roller )
	{
		this.roller = roller;
	}

	@Override
	protected Map<DataSet,XYChart.Series<String,Long>> call () throws Exception
	{
	    long[] eq = roller.probabilities ();
	            
        long totalOutcomes = Arrays.asList ( ArrayConverter.longArray ( eq ) ).stream ().mapToLong ( l -> l ).sum ();
        
        long[] lt = new long[ eq.length ];
        long[] le = new long[ eq.length ];
        long[] gt = new long[ eq.length ];
        long[] ge = new long[ eq.length ];
        
        Arrays.fill ( lt, 0L );
        Arrays.fill ( le, 0L );
        Arrays.fill ( gt, 0L );
        Arrays.fill ( ge, 0L );
        
        long soFar = 0;
        long remaining = totalOutcomes;
        
        for ( int i=0; i<eq.length; i++ )
        {
            if ( eq[ i ] == 0 )
                continue;
            
            lt[ i ] = soFar;
            ge[ i ] = remaining;
            
            soFar += eq[ i ];
            remaining -= eq[ i ];
            
            le[ i ] = soFar;
            gt[ i ] = remaining;
        }
        
        Map<DataSet,XYChart.Series<String,Long>> data = new HashMap<>();
        
        addSeries ( data, totalOutcomes,                 DataSet.EQUAL, eq );
        addSeries ( data, totalOutcomes,             DataSet.LESS_THAN, lt );
        addSeries ( data, totalOutcomes,    DataSet.LESS_THAN_OR_EQUAL, le );
        addSeries ( data, totalOutcomes,          DataSet.GREATER_THAN, gt );
        addSeries ( data, totalOutcomes, DataSet.GREATER_THAN_OR_EQUAL, ge );
        
        return data;
	}	
	
	private void addSeries ( Map<DataSet,XYChart.Series<String,Long>> data, long totalOutcomes, DataSet set, long[] prob )
	{
	   XYChart.Series<String,Long> series = new XYChart.Series<> ();
	   series.setName ( set.label () );
	   
	   for ( int outcome = 0; outcome < prob.length; outcome++ )
	   {
	       if ( prob[ outcome ] == 0 )
	           continue;
	       
	       String label = String.valueOf ( outcome );
	       
	       if ( roller.labels ().containsKey ( outcome ) )
	           label = roller.labels ().get ( outcome );
	       
	       double percentage = ( ( double ) prob[ outcome ] / ( double ) totalOutcomes ) * 100.0;
	       String toolTip = String.format ( "%d out of %d Outcomes\n%.1f%%", prob[ outcome ], totalOutcomes, percentage );
	       
	       series.getData ().add ( new XYChart.Data<> ( label, prob[ outcome ], toolTip ) );
	   }
	   
	   data.put ( set, series );
	}
}
