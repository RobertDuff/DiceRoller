package duffrd.diceroller.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static utility.arrays.ArrayConverter.*;
import utility.math.Polynomial;

public class Dice
{
    Die die;
    int count;
    int start;
    int end;

    List<Integer> range;
    long[] probabilities;

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

        // Calculate Range

        Set<Integer> rangeSet = new HashSet<Integer>();			

        __genRange ( rangeSet, end - start, 0 );

        range = new ArrayList<> ( rangeSet );
        Collections.sort ( range );
    }

    private void __genRange ( Set<Integer> range, int count, int sum )
    {
        if ( count == 0 )
        {
            range.add ( sum );
            return;
        }

        for ( Integer face : die.range )
            __genRange ( range, count-1, sum+face );
    }

    public DiceOutcome roll()
    {
        return new DiceOutcome ( this );
    }

    public long[] probabilities()
    { 
        if ( probabilities == null )
        {
            //
            // If using all dice, then go ahead and calculate the probability distribution.
            //
            if ( start == 0 && end == count )
            {
                Polynomial probabilities = new Polynomial ( die.probabilities );
                Polynomial product = Polynomial.ONE;

                for ( int i=0; i<count; i++ )
                    product = product.multiply ( probabilities );

                this.probabilities = product.coefficients ();
            }
            else
            {
                int maxOutcome = die.range.get ( die.range.size ()-1 ) * ( end - start );
                long[] probabilityDistribution = new long[ maxOutcome+1 ];
                
                int[] rolls = new int[ count ];
                
                genProbProcessor ( probabilityDistribution, rolls, count );
                
                return probabilityDistribution;
            }
        }

        return probabilities;
    }

    private void genProbProcessor ( long[] probabilityDistribution, int[] rolls, int num )
    {
        if ( num == 0 )
        {
            long count = 1;
            
            for ( int roll : rolls )
                count *= die.probabilities[ roll ];
            
            Integer[] sorted = intArray ( Arrays.copyOf ( rolls, rolls.length ) );
            Arrays.sort ( sorted );
            int sum = Arrays.asList ( sorted ).subList ( start, end ).stream ().mapToInt ( i -> i.intValue () ).sum ();

            probabilityDistribution[ sum ] += count;                

            return;
        }
        
        for ( int face : die.range )
        {
            rolls[ num-1 ] = face;
            genProbProcessor ( probabilityDistribution, rolls, num-1 );
        }
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
