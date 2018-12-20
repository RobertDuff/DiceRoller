package duffrd.diceroller.model;

import static utility.arrays.ArrayConverter.intArray;

import java.util.Arrays;

import utility.math.Polynomial;

public class Dice
{
    Die die;
    int count;
    int start;
    int end;

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
                Polynomial poly = new Polynomial ( die.probabilities );
                Polynomial product = Polynomial.ONE;

                for ( int i=0; i<count; i++ )
                    product = product.multiply ( poly );

                probabilities = product.coefficients ();
            }
            else
            {
                int maxOutcome = die.range.get ( die.range.size ()-1 ) * ( end - start );
                probabilities = new long[ maxOutcome+1 ];
                
                int[] rolls = new int[ count ];
                
                genProbProcessor ( rolls, count );                
            }
        }

        return probabilities;
    }

    private void genProbProcessor ( int[] rolls, int num )
    {
        if ( num == 0 )
        {
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
            rolls[ num-1 ] = face;
            genProbProcessor ( rolls, num-1 );
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
