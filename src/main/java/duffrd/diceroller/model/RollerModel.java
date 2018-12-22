package duffrd.diceroller.model;

import java.util.List;

public interface RollerModel
{
    public List<String> groupNames() throws DiceRollerException;
    public List<Roller> rollers ( String group ) throws DiceRollerException;
    
    public void renameGroup ( String origName, String newName ) throws DiceRollerException;
    public void deleteGroup ( String group ) throws DiceRollerException;
    
    public RollerBuilder rollerBuilder();
    public void deleteRoller ( Roller roller ) throws DiceRollerException;
}
