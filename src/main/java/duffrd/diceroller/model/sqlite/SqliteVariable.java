package duffrd.diceroller.model.sqlite;

import duffrd.diceroller.model.Variable;

public class SqliteVariable extends Variable
{   
    protected int id;
    
    public SqliteVariable ( int id )
    {
        super();
        this.id = id;
    }
    
    public int id()
    {
        return id;
    }
}
