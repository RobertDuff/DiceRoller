package duffrd.diceroller.view;

public enum DataSet
{
    EQUAL                 ( "Equal",                 "-fx-bar-fill: blue"   ),
    LESS_THAN             ( "Less Than",             "-fx-bar-fill: orange" ),
    LESS_THAN_OR_EQUAL    ( "Less Than or Equal",    "-fx-bar-fill: green"  ),
    GREATER_THAN          ( "Greater Than",          "-fx-bar-fill: violet" ),
    GREATER_THAN_OR_EQUAL ( "Greater Than or Equal", "-fx-bar-fill: red"    );
    
    private String label;
    private String style;
    
    private DataSet ( String label, String style )
    {
        this.label = label;
        this.style = style;
    }
    
    public String label()
    {
        return label;
    }
    
    public String style()
    {
        return style;
    }
}
