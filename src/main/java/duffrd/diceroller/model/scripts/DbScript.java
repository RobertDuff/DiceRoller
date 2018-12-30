package duffrd.diceroller.model.scripts;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.RollerModel;

@FunctionalInterface
public interface DbScript
{
    public void go ( RollerModel model ) throws DiceRollerException;
    
    public default DbScript andThen ( DbScript otherScript ) throws DiceRollerException
    {
        final DbScript thisScript = this;
        
        return new DbScript() 
        {
            @Override
            public void go ( RollerModel model ) throws DiceRollerException
            {
                thisScript.go ( model );
                otherScript.go ( model );
            }
        };
    }
}
