package duffrd.diceroller.model.sqlite;

import duffrd.diceroller.model.Suite;

public class SqliteSuite extends Suite
{
    protected int id;
    
    public SqliteSuite ( int id )
    {
        super();
        this.id = id;
    }

    public int id()
    {
        return id;
    }
}
