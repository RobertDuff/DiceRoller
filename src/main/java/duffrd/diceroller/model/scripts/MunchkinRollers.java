package duffrd.diceroller.model.scripts;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.RollerBuilder;
import duffrd.diceroller.model.RollerModel;

public class MunchkinRollers implements DbScript
{
    @Override
    public void go ( RollerModel model ) throws DiceRollerException
    {        
        model.createGroup ( "Munchkin" );
        
        model.createRoller ( "Munchkin", 
                new RollerBuilder ( "Munchkin" )
                .name ( "Escape-1" )
                .definition ( "d6-1 >= 5" )
                .addLabel ( 1, "Escape" )
                .addLabel ( 0, "Bad Stuff" )
                .build () );
        
        model.createRoller ( "Munchkin", 
                new RollerBuilder ( "Munchkin" )
                .name ( "Escape" )
                .definition ( "d6 >= 5" )
                .addLabel ( 1, "Escape" )
                .addLabel ( 0, "Bad Stuff" )
                .build () );
        
        model.createRoller ( "Munchkin", 
                new RollerBuilder ( "Munchkin" )
                .name ( "Escape+1" )
                .definition ( "d6+1 >= 5" )
                .addLabel ( 1, "Escape" )
                .addLabel ( 0, "Bad Stuff" )
                .build () );
        
        model.createRoller ( "Munchkin", 
                new RollerBuilder ( "Munchkin" )
                .name ( "Escape+2" )
                .definition ( "d6+2 >= 5" )
                .addLabel ( 1, "Escape" )
                .addLabel ( 0, "Bad Stuff" )
                .build () );
        
        model.createRoller ( "Munchkin", 
                new RollerBuilder ( "Munchkin" )
                .name ( "Escape+3" )
                .definition ( "d6+3 >= 5" )
                .addLabel ( 1, "Escape" )
                .addLabel ( 0, "Bad Stuff" )
                .build () );
    }
}
