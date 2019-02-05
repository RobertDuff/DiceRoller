package duffrd.diceroller.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;

public abstract class Model
{
    protected SetProperty<Suite> suitesProperty = new SimpleSetProperty<> ( FXCollections.observableSet ( new HashSet<>() ) );
    protected ListProperty<Outcome> historyProperty = new SimpleListProperty<> ( new FilteredList<> ( FXCollections.observableArrayList () ) );
    
    public Set<Suite> suites()
    {
        return suitesProperty;
    }
    
    public List<Outcome> history()
    {
        return historyProperty;
    }
    
    public SetProperty<Suite> suitesProperty()
    {
        return suitesProperty;
    }
    
    public ListProperty<Outcome> historyProperty()
    {
        return historyProperty;
    }
    
    public abstract Suite createSuite ( String name ) throws DiceRollerException;
    public abstract void updateSuite ( Suite suite, String name ) throws DiceRollerException;
    public abstract void deleteSuite ( Suite suite ) throws DiceRollerException;

    public abstract Trigger createTrigger ( Suite suite, String name, String definition ) throws DiceRollerException;
    public abstract void updateTrigger ( Trigger trigger, String name, String definition ) throws DiceRollerException;
    public abstract void deleteTrigger ( Trigger trigger ) throws DiceRollerException;

    public abstract Variable createVariable ( Suite suite, String name, int value ) throws DiceRollerException;
    public abstract void updateVariables ( Suite suite, List<Variable> variables ) throws DiceRollerException;

    public abstract Group createGroup ( Suite suite, String name ) throws DiceRollerException;
    public abstract void updateGroup ( Group group, String name ) throws DiceRollerException;
    public abstract void moveGroup ( Group group, int pos ) throws DiceRollerException;
    public abstract void deleteGroup ( Group group ) throws DiceRollerException;

    public abstract Roller createRoller ( Group group, String name, String definition ) throws DiceRollerException;
    public abstract void updateRoller ( Roller roller, String name, String definition ) throws DiceRollerException;
    public abstract void moveRoller ( Roller roller, int pos ) throws DiceRollerException;
    public abstract void deleteRoller ( Roller roller ) throws DiceRollerException;

    public abstract void createLabel ( Roller roller, int value, String label ) throws DiceRollerException;
    public abstract void updateLabels ( Roller roller, Map<Integer,String> labels ) throws DiceRollerException;

    public abstract void createRollerTrigger ( Roller roller, Trigger trigger ) throws DiceRollerException;
    public abstract void updateRollerTriggers ( Roller roller, Set<Trigger> triggers ) throws DiceRollerException;
}
