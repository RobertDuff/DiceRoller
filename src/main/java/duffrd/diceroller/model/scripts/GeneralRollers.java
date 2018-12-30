package duffrd.diceroller.model.scripts;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.RollerBuilder;
import duffrd.diceroller.model.RollerModel;

public class GeneralRollers implements DbScript
{
    @Override
    public void go ( RollerModel model ) throws DiceRollerException
    {
        model.createGroup ( "D4s" );
        model.createRoller ( "D4s", new RollerBuilder ( "D4s" ).name ( "1 D4" ).definition ( "1D4" ).build() );
        model.createRoller ( "D4s", new RollerBuilder ( "D4s" ).name ( "2 D4s" ).definition ( "2D4" ).build() );
        model.createRoller ( "D4s", new RollerBuilder ( "D4s" ).name ( "3 D4s" ).definition ( "3D4" ).build() );
        model.createRoller ( "D4s", new RollerBuilder ( "D4s" ).name ( "4 D4s" ).definition ( "4D4" ).build() );
        model.createRoller ( "D4s", new RollerBuilder ( "D4s" ).name ( "5 D4s" ).definition ( "5D4" ).build() );
        model.createRoller ( "D4s", new RollerBuilder ( "D4s" ).name ( "6 D4s" ).definition ( "6D4" ).build() );
        model.createRoller ( "D4s", new RollerBuilder ( "D4s" ).name ( "7 D4s" ).definition ( "7D4" ).build() );
        model.createRoller ( "D4s", new RollerBuilder ( "D4s" ).name ( "8 D4s" ).definition ( "8D4" ).build() );
        model.createRoller ( "D4s", new RollerBuilder ( "D4s" ).name ( "9 D4s" ).definition ( "9D4" ).build() );
        model.createRoller ( "D4s", new RollerBuilder ( "D4s" ).name ( "10 D4s" ).definition ( "10D4" ).build() );
        
        model.createGroup ( "D6s" );
        model.createRoller ( "D6s", new RollerBuilder ( "D6s" ).name ( "1 D6" ).definition ( "1D6" ).build() );
        model.createRoller ( "D6s", new RollerBuilder ( "D6s" ).name ( "2 D6s" ).definition ( "2D6" ).build() );
        model.createRoller ( "D6s", new RollerBuilder ( "D6s" ).name ( "3 D6s" ).definition ( "3D6" ).build() );
        model.createRoller ( "D6s", new RollerBuilder ( "D6s" ).name ( "4 D6s" ).definition ( "4D6" ).build() );
        model.createRoller ( "D6s", new RollerBuilder ( "D6s" ).name ( "5 D6s" ).definition ( "5D6" ).build() );
        model.createRoller ( "D6s", new RollerBuilder ( "D6s" ).name ( "6 D6s" ).definition ( "6D6" ).build() );
        model.createRoller ( "D6s", new RollerBuilder ( "D6s" ).name ( "7 D6s" ).definition ( "7D6" ).build() );
        model.createRoller ( "D6s", new RollerBuilder ( "D6s" ).name ( "8 D6s" ).definition ( "8D6" ).build() );
        model.createRoller ( "D6s", new RollerBuilder ( "D6s" ).name ( "9 D6s" ).definition ( "9D6" ).build() );
        model.createRoller ( "D6s", new RollerBuilder ( "D6s" ).name ( "10 D6s" ).definition ( "10D6" ).build() );
        
        model.createGroup ( "D8s" );
        model.createRoller ( "D8s", new RollerBuilder ( "D8s" ).name ( "1 D8" ).definition ( "1D8" ).build() );
        model.createRoller ( "D8s", new RollerBuilder ( "D8s" ).name ( "2 D8s" ).definition ( "2D8" ).build() );
        model.createRoller ( "D8s", new RollerBuilder ( "D8s" ).name ( "3 D8s" ).definition ( "3D8" ).build() );
        model.createRoller ( "D8s", new RollerBuilder ( "D8s" ).name ( "4 D8s" ).definition ( "4D8" ).build() );
        model.createRoller ( "D8s", new RollerBuilder ( "D8s" ).name ( "5 D8s" ).definition ( "5D8" ).build() );
        model.createRoller ( "D8s", new RollerBuilder ( "D8s" ).name ( "6 D8s" ).definition ( "6D8" ).build() );
        model.createRoller ( "D8s", new RollerBuilder ( "D8s" ).name ( "7 D8s" ).definition ( "7D8" ).build() );
        model.createRoller ( "D8s", new RollerBuilder ( "D8s" ).name ( "8 D8s" ).definition ( "8D8" ).build() );
        model.createRoller ( "D8s", new RollerBuilder ( "D8s" ).name ( "9 D8s" ).definition ( "9D8" ).build() );
        model.createRoller ( "D8s", new RollerBuilder ( "D8s" ).name ( "10 D8s" ).definition ( "10D8" ).build() );
        
        model.createGroup ( "D10s" );
        model.createRoller ( "D10s", new RollerBuilder ( "D10s" ).name ( "1 D10" ).definition ( "1D10" ).build() );
        model.createRoller ( "D10s", new RollerBuilder ( "D10s" ).name ( "2 D10s" ).definition ( "2D10" ).build() );
        model.createRoller ( "D10s", new RollerBuilder ( "D10s" ).name ( "3 D10s" ).definition ( "3D10" ).build() );
        model.createRoller ( "D10s", new RollerBuilder ( "D10s" ).name ( "4 D10s" ).definition ( "4D10" ).build() );
        model.createRoller ( "D10s", new RollerBuilder ( "D10s" ).name ( "5 D10s" ).definition ( "5D10" ).build() );
        model.createRoller ( "D10s", new RollerBuilder ( "D10s" ).name ( "6 D10s" ).definition ( "6D10" ).build() );
        model.createRoller ( "D10s", new RollerBuilder ( "D10s" ).name ( "7 D10s" ).definition ( "7D10" ).build() );
        model.createRoller ( "D10s", new RollerBuilder ( "D10s" ).name ( "8 D10s" ).definition ( "8D10" ).build() );
        model.createRoller ( "D10s", new RollerBuilder ( "D10s" ).name ( "9 D10s" ).definition ( "9D10" ).build() );
        model.createRoller ( "D10s", new RollerBuilder ( "D10s" ).name ( "10 D10s" ).definition ( "10D10" ).build() );
        
        model.createGroup ( "D12s" );
        model.createRoller ( "D12s", new RollerBuilder ( "D12s" ).name ( "1 D12" ).definition ( "1D12" ).build() );
        model.createRoller ( "D12s", new RollerBuilder ( "D12s" ).name ( "2 D12s" ).definition ( "2D12" ).build() );
        model.createRoller ( "D12s", new RollerBuilder ( "D12s" ).name ( "3 D12s" ).definition ( "3D12" ).build() );
        model.createRoller ( "D12s", new RollerBuilder ( "D12s" ).name ( "4 D12s" ).definition ( "4D12" ).build() );
        model.createRoller ( "D12s", new RollerBuilder ( "D12s" ).name ( "5 D12s" ).definition ( "5D12" ).build() );
        model.createRoller ( "D12s", new RollerBuilder ( "D12s" ).name ( "6 D12s" ).definition ( "6D12" ).build() );
        model.createRoller ( "D12s", new RollerBuilder ( "D12s" ).name ( "7 D12s" ).definition ( "7D12" ).build() );
        model.createRoller ( "D12s", new RollerBuilder ( "D12s" ).name ( "8 D12s" ).definition ( "8D12" ).build() );
        model.createRoller ( "D12s", new RollerBuilder ( "D12s" ).name ( "9 D12s" ).definition ( "9D12" ).build() );
        model.createRoller ( "D12s", new RollerBuilder ( "D12s" ).name ( "10 D12s" ).definition ( "10D12" ).build() );
        
        model.createGroup ( "D20s" );
        model.createRoller ( "D20s", 
                new RollerBuilder ( "D20s" )
                .name ( "1 D20" )
                .definition ( "1D20" )
                .addTrigger ( "Critical Fail", "A==1" )
                .addTrigger ( "Critical Success", "A==20" )
                .build() );
        
        model.createGroup ( "D100s" );
        model.createRoller ( "D100s", new RollerBuilder ( "D100s" ).name ( "D100 (0..99)" ).definition ( "1D100 - 1" ).build() );
        model.createRoller ( "D100s", new RollerBuilder ( "D100s" ).name ( "D100 (1.100)" ).definition ( "1D100" ).build() );
        
        model.createGroup ( "Miscellaneous" );
        model.createRoller ( "Miscellaneous",
                new RollerBuilder ( "Miscellaneous" )
                .name ( "Coin Flip" )
                .definition ( "D2" )
                .addLabel ( 1, "Heads" )
                .addLabel ( 2, "Tails" )
                .build() );
    }
}
