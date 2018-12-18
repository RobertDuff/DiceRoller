package duffrd.diceroller.model;

public class DiceRollerException extends Exception
{
    private static final long serialVersionUID = 1L;

    public DiceRollerException ()
    {
        super ();
    }

    public DiceRollerException ( String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace )
    {
        super ( message, cause, enableSuppression, writableStackTrace );
    }

    public DiceRollerException ( String message, Throwable cause )
    {
        super ( message, cause );
    }

    public DiceRollerException ( String message )
    {
        super ( message );
    }

    public DiceRollerException ( Throwable cause )
    {
        super ( cause );
    }

}
