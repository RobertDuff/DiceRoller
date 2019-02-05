package duffrd.diceroller.model.sqlite;

import duffrd.diceroller.model.Trigger;

public class SqliteTrigger extends Trigger
{
    protected int id;
    
    public SqliteTrigger ( int id )
    {
        super();
        this.id = id;
    }
    
    public int id()
    {
        return id;
    }
}
