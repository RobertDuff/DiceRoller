package duffrd.diceroller.model;

import java.sql.Connection;
import java.util.List;

import javafx.collections.ObservableList;

public interface RollerModel
{
    public List<String> groupNames() throws DiceRollerException;
    
    public void createGroup ( String name ) throws DiceRollerException;
    public void renameGroup ( String origName, String newName ) throws DiceRollerException;
    public void moveGroup ( String group, int position ) throws DiceRollerException;
    public void deleteGroup ( String group ) throws DiceRollerException;

    public List<Roller> rollers ( String group ) throws DiceRollerException;

    public void createRoller ( String group, Roller roller ) throws DiceRollerException;
    public void renameRoller ( String groupName, String origName, String newName ) throws DiceRollerException;
    public void updateRoller ( String group, Roller roller ) throws DiceRollerException;
    public void moveRoller ( String group, Roller roller, int position ) throws DiceRollerException;
    public void deleteRoller ( String group, Roller roller ) throws DiceRollerException;
    
    public ObservableList<Variable> groupVariables ( String groupName ) throws DiceRollerException;
    
    public void updateGroupVariables ( String groupName, List<Variable> variables ) throws DiceRollerException;
    
    Connection connection();
}
