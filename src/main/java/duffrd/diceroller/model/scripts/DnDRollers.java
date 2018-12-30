package duffrd.diceroller.model.scripts;

import java.util.Arrays;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.RollerBuilder;
import duffrd.diceroller.model.RollerModel;
import duffrd.diceroller.model.Variable;

public class DnDRollers implements DbScript
{
    @Override
    public void go ( RollerModel model ) throws DiceRollerException
    {
        String dnd = "Dungeons and Dragons 3.5";
        
        model.createGroup ( dnd );
        
        model.createRoller ( dnd, 
                new RollerBuilder ( dnd )
                .name ( "Karma D20" )
                .definition ( "d20" )
                .addTrigger ( "Good Karma", "A == 20" )
                .addTrigger ( "Bad Karma", "A == 1" )
                .build () );        
        
        model.createRoller ( dnd, 
                new RollerBuilder ( dnd )
                .name ( "Good Karma Point" )
                .definition ( "d20 >= 17" )
                .addLabel ( 1, "Bring On The Good Stuff" )
                .addLabel ( 0, "Too Bad" )
                .build () );        
        
        model.createRoller ( dnd, 
                new RollerBuilder ( dnd )
                .name ( "Bad Karma Point" )
                .definition ( "d20 <= 4" )
                .addLabel ( 1, "That's Gonna Sting" )
                .addLabel ( 0, "Wheew" )
                .build () );        
        
        model.createRoller ( dnd, 
                new RollerBuilder ( dnd )
                .name ( "Ability Roll" )
                .definition ( "3,4d6" )
                .build () );        
        
        String abilities = dnd + ": Abilities";
        
        //
        // Ability Checks
        //
        
        model.createGroup ( abilities );
        
        model.updateGroupVariables ( abilities,
                Arrays.asList (
                        new Variable ( "StrengthModifier",     0 ),
                        new Variable ( "ConstitutionModifier", 0 ),
                        new Variable ( "DexterityModifier",    0 ),
                        new Variable ( "IntelligenceModifier", 0 ),
                        new Variable ( "WisdomModifier",       0 ),
                        new Variable ( "CharismaModifier",     0 )
                        ) );
        
        model.createRoller ( abilities, new RollerBuilder ( abilities ).name ( "Strength"     ).definition ( "d20 + StrengthModifier"     ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build () );
        model.createRoller ( abilities, new RollerBuilder ( abilities ).name ( "Constitution" ).definition ( "d20 + ConstitutionModifier" ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build () );
        model.createRoller ( abilities, new RollerBuilder ( abilities ).name ( "Dexerity"     ).definition ( "d20 + DexterityModifier"    ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build () );
        model.createRoller ( abilities, new RollerBuilder ( abilities ).name ( "Intelligence" ).definition ( "d20 + IntelligenceModifier" ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build () );
        model.createRoller ( abilities, new RollerBuilder ( abilities ).name ( "Wisdom"       ).definition ( "d20 + WisdomModifier"       ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build () );
        model.createRoller ( abilities, new RollerBuilder ( abilities ).name ( "Charisma"     ).definition ( "d20 + CharismaModifier"     ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build () );
        
        //
        // Untrained Skill Checks
        //
        
        String skills = dnd + ": Untrained Skills";
        
        model.createGroup ( skills );
        
        model.updateGroupVariables ( skills, 
                Arrays.asList (
                        new Variable ( "AppraiseModifier",          0 ),
                        new Variable ( "BalanceModifier",           0 ),
                        new Variable ( "BluffModifier",             0 ),
                        new Variable ( "ClimbModifier",             0 ),
                        new Variable ( "ConcentrationModifier",     0 ),
                        new Variable ( "CraftModifier",             0 ),
                        new Variable ( "DiplomacyModifier",         0 ),
                        new Variable ( "DisguiseModifier",          0 ),
                        new Variable ( "EscapeArtistModifier",      0 ),
                        new Variable ( "ForgeryModifier",           0 ),
                        new Variable ( "GatherInformationModifier", 0 ),
                        new Variable ( "HealModifier",              0 ),
                        new Variable ( "HideModifier",              0 ),
                        new Variable ( "IntimidateModifier",        0 ),
                        new Variable ( "JumpModifier",              0 ),
                        new Variable ( "ListenModifier",            0 ),
                        new Variable ( "MoveSilentlyModifier",      0 ),
                        new Variable ( "PerformModifier",           0 ),
                        new Variable ( "RideModifier",              0 ),
                        new Variable ( "SearchModifier",            0 ),
                        new Variable ( "SenseMotiveModifier",       0 ),
                        new Variable ( "SpotModifier",              0 ),
                        new Variable ( "SurvivalModifier",          0 ),
                        new Variable ( "SwimModifier",              0 ),
                        new Variable ( "UseRopeModifier",           0 )
                        ) );
        
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Appraise"           ).definition ( "d20 + AppraiseModifier"          ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Balance"            ).definition ( "d20 + BalanceModifier"           ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Bluff"              ).definition ( "d20 + BluffModifier"             ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Climb"              ).definition ( "d20 + ClimbModifier"             ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Concentration"      ).definition ( "d20 + ConcentrationModifier"     ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Craft"              ).definition ( "d20 + CraftModifier"             ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Diplomacy"          ).definition ( "d20 + DiplomacyModifier"         ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Disguise"           ).definition ( "d20 + DisguiseModifier"          ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Escape"             ).definition ( "d20 + EscapeArtistModifier"      ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() ); 
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Forgery"            ).definition ( "d20 + ForgeryModifier"           ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Gather Information" ).definition ( "d20 + GatherInformationModifier" ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() ); 
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Heal"               ).definition ( "d20 + HealModifier"              ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Hide"               ).definition ( "d20 + HideModifier"              ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Intimidate"         ).definition ( "d20 + IntimidateModifier"        ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Jump"               ).definition ( "d20 + JumpModifier"              ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Listen"             ).definition ( "d20 + ListenModifier"            ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Move Silently"      ).definition ( "d20 + MoveSilentlyModifier"      ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() ); 
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Perform"            ).definition ( "d20 + PerformModifier"           ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Ride"               ).definition ( "d20 + RideModifier"              ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Search"             ).definition ( "d20 + SearchModifier"            ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Sense Motive"       ).definition ( "d20 + SenseMotiveModifier"       ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() ); 
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Spot"               ).definition ( "d20 + SpotModifier"              ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Survival"           ).definition ( "d20 + SurvivalModifier"          ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Swim"               ).definition ( "d20 + SwimModifier"              ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() );
        model.createRoller ( skills, new RollerBuilder ( skills ).name ( "Use Rope"           ).definition ( "d20 + UseRopeModifier"           ).addTrigger ( "Good Karma", "A == 20" ).addTrigger ( "Bad Karma", "A == 1" ).build() ); 
    }
}
