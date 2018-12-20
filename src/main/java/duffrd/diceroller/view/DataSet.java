package duffrd.diceroller.view;

public enum DataSet
{
    EQUAL                 ( "Equal" ),
    LESS_THAN             ( "Less Than" ),
    LESS_THAN_OR_EQUAL    ( "Less Than or Equal" ),
    GREATER_THAN          ( "Greater Than" ),
    GREATER_THAN_OR_EQUAL ( "Greater Than or Equal" );
    
    private String label;
    
    private DataSet ( String label )
    {
        this.label = label;
    }
    
    public String label()
    {
        return label;
    }
}
