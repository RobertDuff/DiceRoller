package duffrd.diceroller.model.sqlite;

import duffrd.diceroller.model.Group;

public class SqliteGroup extends Group
{
    protected int id;

    public SqliteGroup ( int id )
    {
        super();
        this.id = id;
    }
    
    public int id()
    {
        return id;
    }
}