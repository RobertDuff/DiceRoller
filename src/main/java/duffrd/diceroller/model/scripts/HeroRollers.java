package duffrd.diceroller.model.scripts;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.RollerBuilder;
import duffrd.diceroller.model.RollerModel;

public class HeroRollers implements DbScript
{
    @Override
    public void go ( RollerModel model ) throws DiceRollerException
    {
        String hero = "Hero System 4.0";
        
        model.createGroup ( hero );
        
        model.createRoller ( hero, 
                new RollerBuilder ( hero )
                .name ( "Hero 3D6" )
                .definition ( "3D6" )
                .addTrigger ( "Brownie Point", "A <= 4" )
                .addTrigger ( "Sympathy Point", "A == 18" )
                .build () );        
        
        model.createRoller ( hero, 
                new RollerBuilder ( hero )
                .name ( "Location" )
                .definition ( "3D6" )
                .addLabel ( 3, "Eyes" )
                .addLabel ( 4, "Face" )
                .addLabel ( 5, "Head" )
                .addLabel ( 6, "Hands" )
                .addLabel ( 7, "Arms" )
                .addLabel ( 8, "Arms" )
                .addLabel ( 9, "Shoulders" )
                .addLabel ( 10, "Chest" )
                .addLabel ( 11, "Core" )
                .addLabel ( 12, "Stomach" )
                .addLabel ( 13, "Vitals" )
                .addLabel ( 14, "Thighs" )
                .addLabel ( 15, "Legs" )
                .addLabel ( 16, "Legs" )
                .addLabel ( 17, "Feet" )
                .addLabel ( 18, "Feet" )
                .build () );        
        
        String atk = hero + " Normal Attacks";
        
        model.createGroup ( atk );
        
        model.createRoller ( atk, new RollerBuilder ( atk ).name ( "Normal Attack DC-1" ).definition ( "d6" ).build () );
        model.createRoller ( atk, new RollerBuilder ( atk ).name ( "Normal Attack DC-2" ).definition ( "2d6" ).build () );
        model.createRoller ( atk, new RollerBuilder ( atk ).name ( "Normal Attack DC-3" ).definition ( "3d6" ).build () );
        model.createRoller ( atk, new RollerBuilder ( atk ).name ( "Normal Attack DC-4" ).definition ( "4d6" ).build () );
        model.createRoller ( atk, new RollerBuilder ( atk ).name ( "Normal Attack DC-5" ).definition ( "5d6" ).build () );
        model.createRoller ( atk, new RollerBuilder ( atk ).name ( "Normal Attack DC-6" ).definition ( "6d6" ).build () );
        model.createRoller ( atk, new RollerBuilder ( atk ).name ( "Normal Attack DC-7" ).definition ( "7d6" ).build () );
        model.createRoller ( atk, new RollerBuilder ( atk ).name ( "Normal Attack DC-8" ).definition ( "8d6" ).build () );
        model.createRoller ( atk, new RollerBuilder ( atk ).name ( "Normal Attack DC-9" ).definition ( "9d6" ).build () );
        model.createRoller ( atk, new RollerBuilder ( atk ).name ( "Normal Attack DC-10" ).definition ( "10d6" ).build () );
        
        String kill = hero + " Killing Attacks";

        model.createGroup ( kill );
        
        model.createRoller ( kill, new RollerBuilder ( kill ).name ( "Killing Attack DC-1" ).definition ( "d2-1" ).build () );
        model.createRoller ( kill, new RollerBuilder ( kill ).name ( "Killing Attack DC-2" ).definition ( "d3" ).build () );
        model.createRoller ( kill, new RollerBuilder ( kill ).name ( "Killing Attack DC-3" ).definition ( "d6" ).build () );
        model.createRoller ( kill, new RollerBuilder ( kill ).name ( "Killing Attack DC-4" ).definition ( "d6 + 1" ).build () );
        model.createRoller ( kill, new RollerBuilder ( kill ).name ( "Killing Attack DC-5" ).definition ( "d6 + d3" ).build () );
        model.createRoller ( kill, new RollerBuilder ( kill ).name ( "Killing Attack DC-6" ).definition ( "2d6" ).build () );
        model.createRoller ( kill, new RollerBuilder ( kill ).name ( "Killing Attack DC-7" ).definition ( "2d6 + 1" ).build () );
        model.createRoller ( kill, new RollerBuilder ( kill ).name ( "Killing Attack DC-8" ).definition ( "2d6 + d3" ).build () );
        model.createRoller ( kill, new RollerBuilder ( kill ).name ( "Killing Attack DC-9" ).definition ( "3d6" ).build () );
        model.createRoller ( kill, new RollerBuilder ( kill ).name ( "Killing Attack DC-10" ).definition ( "3d6 + 1" ).build () );

    }
}
