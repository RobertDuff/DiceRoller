package duffrd.diceroller.model.sqlite;

import duffrd.diceroller.model.Roller;

public class SqliteRoller extends Roller
{
    protected int id;
    
    public SqliteRoller ( int id )
    {
        super();
        this.id = id;
    }
    
    public int id()
    {
        return id;
    }
}
