package duffrd.diceroller.model;

import static utility.arrays.ArrayConverter.intArray;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import utility.math.Polynomial;

public class Dice
{
    private static final Logger logger = LogManager.getLogger ( MethodHandles.lookup().lookupClass() );
    
    Die die;
    int count;
    int start;
    int end;

    long[] probabilities;
    
    private BooleanProperty canceledProperty = new SimpleBooleanProperty ();

    public Dice ( Die die, int count )
    {
        this ( die, count, 0, count );
    }

    public Dice ( Die die, int count, int start, int end )
    {
        this.die   = die;
        this.count = count;

        this.start = start;
        this.end   = end;
    }

    public BooleanProperty canceledProperty()
    {
        return canceledProperty;
    }
    
    public DiceOutcome roll()
    {
        return new DiceOutcome ( this );
    }
    
    public long[] probabilities() throws ProbablityCalculationCancelledException
    { 
        if ( probabilities == null )
        {
            //
            // If using all dice, then go ahead and calculate the probability distribution.
            //
            if ( start == 0 && end == count )
            {
                Polynomial poly = new Polynomial ( die.probabilities );
                Polynomial product = Polynomial.ONE;

                for ( int i=0; i<count; i++ )
                    product = product.multiply ( poly );

                probabilities = product.coefficients ();
            }
            else
            {
                logger.debug ( "Starting Probability Calculation" );
                int maxOutcome = die.range.get ( die.range.size ()-1 ) * ( end - start );
                probabilities = new long[ maxOutcome+1 ];
                
                int[] rolls = new int[ count ];
                
                try
                {
                    genProbProcessor ( rolls, count );
                }
                catch ( ProbablityCalculationCancelledException e )
                {
                    probabilities = null;
                    throw e;
                }                

                logger.debug ( "Probability Calculation Complete" );
            }
        }

        return probabilities;
    }

    private void genProbProcessor ( int[] rolls, int num ) throws ProbablityCalculationCancelledException
    {
        logger.debug ( "Enter Level " + num );
        
        if ( canceledProperty.getValue () )
            throw new ProbablityCalculationCancelledException ();
        
        if ( num == 0 )
        {
            logger.debug ( "Adding Counts" );
            
            long count = 1;
            
            for ( int roll : rolls )
                count *= die.probabilities[ roll ];
            
            Integer[] sorted = intArray ( Arrays.copyOf ( rolls, rolls.length ) );
            Arrays.sort ( sorted );
            int sum = Arrays.asList ( sorted ).subList ( start, end ).stream ().mapToInt ( i -> i.intValue () ).sum ();

            probabilities[ sum ] += count;                

            return;
        }
        
        for ( int face : die.range )
        {            
            if ( canceledProperty.getValue () )
                throw new ProbablityCalculationCancelledException ();
         
            logger.debug ( "Loop: " + num + "." + face );
                
            rolls[ num-1 ] = face;
            genProbProcessor ( rolls, num-1 );
        }
        
        logger.debug ( "Exit Level " + num );
    }
    
    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder ();

        b.append ( die.toString () );
        b.append ( ": Count=" );
        b.append ( count );
        b.append ( ", Start=" );
        b.append ( start );
        b.append ( ", End=" );
        b.append ( end );

        return b.toString ();
    }
}
