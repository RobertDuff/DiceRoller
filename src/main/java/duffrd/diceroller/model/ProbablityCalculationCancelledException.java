package duffrd.diceroller.model;

public class ProbablityCalculationCancelledException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ProbablityCalculationCancelledException ()
    {}

    public ProbablityCalculationCancelledException ( String message )
    {
        super ( message );
    }

    public ProbablityCalculationCancelledException ( Throwable cause )
    {
        super ( cause );
    }

    public ProbablityCalculationCancelledException ( String message, Throwable cause )
    {
        super ( message, cause );
    }

    public ProbablityCalculationCancelledException ( String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace )
    {
        super ( message, cause, enableSuppression, writableStackTrace );
    }
}
