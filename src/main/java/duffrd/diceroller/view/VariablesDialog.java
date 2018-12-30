package duffrd.diceroller.view;

import java.util.List;
import java.util.Optional;

import duffrd.diceroller.model.Variable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.converter.IntegerStringConverter;
import utility.arrays.Relocator;

public class VariablesDialog extends Dialog<ButtonType>
{    
    public class VariableNode extends HBox
    {
        private ObjectProperty<Variable> variable = new SimpleObjectProperty<> ();
        private Label nameLabel = new Label();
        private TextField valueField = new TextField();
        
        public VariableNode()
        {            
            variable.addListener ( ( object, oldValue, newValue ) -> 
            {
                nameLabel.textProperty ().unbind ();
                
                if ( newValue != null )
                {
                    nameLabel.textProperty ().bind ( newValue.nameProperty () );
                    valueField.setText ( String.valueOf ( newValue.value () ) );
                }
            } );
            
            valueField.setTextFormatter ( new TextFormatter<> ( new IntegerStringConverter() ) );
            valueField.textProperty ().addListener ( ( object, oldValue, newValue ) -> 
            {
                variable.getValue ().valueProperty ().setValue ( Integer.valueOf ( newValue ) );
            } );
            
            Region spacing = new Region();
            getChildren ().addAll ( nameLabel, spacing, valueField );
            setHgrow ( spacing, Priority.ALWAYS );

            setOnContextMenuRequested ( event ->
            {
                event.consume ();

                ContextMenu menu = new ContextMenu ();

                MenuItem delete = new MenuItem ();
                delete.setText ( "Delete Variable \"" + variable.getValue ().name () + "\"" );
                delete.setOnAction ( a -> varList.getItems ().remove ( ( ( VariableNode ) event.getSource () ).variable () ) );

                menu.getItems ().add ( delete );
                menu.show ( ( Node ) event.getSource (), event.getScreenX (), event.getScreenY () );
            } );
        }
        
        public String name()
        {
            return variable.getValue ().name ();
        }
        
        public int value()
        {
            return variable.getValue ().value ();
        }
        
        public Variable variable()
        {
            return variable.get ();
        }
        
        public VariableNode variable ( Variable var )
        {
            variableProperty ().set ( var );
            return this;
        }
        
        public ObjectProperty<Variable> variableProperty()
        {
            return variable;
        }
    }

    public class VarCell extends ListCell<Variable>
    {
        private VariableNode node = new VariableNode ();
        
        public VarCell ()
        {
            super ();

            setOnDragDetected ( event ->
            {                
                if ( getItem() == null )
                    return;

                event.consume ();
                
                Dragboard dragboard = startDragAndDrop ( TransferMode.MOVE );

                ClipboardContent content = new ClipboardContent ();

                content.putString ( getItem().name() );

                dragboard.setContent ( content );
            } );

            setOnDragOver ( event -> 
            {                
                if ( event.getGestureSource () != event.getGestureTarget () && event.getDragboard ().hasString () )
                    event.acceptTransferModes ( TransferMode.MOVE );

                event.consume ();
            } );

            setOnDragEntered ( event ->
            {                
                if ( event.getGestureSource () != event.getGestureTarget () && event.getDragboard ().hasString () )
                    setOpacity ( 0.3 );
            } );

            setOnDragExited ( event ->
            {                
                if ( event.getGestureSource () != event.getGestureTarget () && event.getDragboard ().hasString () )
                    setOpacity ( 1.0 );
            } );

            setOnDragDropped ( event -> 
            {                
                if ( getItem() == null )
                    return;

                Dragboard dragboard = event.getDragboard ();
                boolean success = false;

                if ( dragboard.hasString () )
                {                    
                    List<Variable> items = getListView ().getItems ();
                    
                    VarCell source = ( VarCell ) event.getGestureSource ();
                    VarCell target = ( VarCell ) event.getGestureTarget ();

                    Relocator.relocate ( items, items.indexOf ( source.getItem () ), items.indexOf ( target.getItem () ) );
                    
                    success = true;
                }
                
                event.setDropCompleted ( success );
                event.consume ();
            } );
            
            setOnDragDone ( DragEvent::consume );
        }

        @Override
        protected void updateItem ( Variable item, boolean empty )
        {
            super.updateItem ( item, empty );
            
            if ( item == null || empty )
            {
                setText ( "" );
                setGraphic ( null );
                return;
            }

            node.variable ( item );
            setGraphic ( node );
        }     
    }

    ListView<Variable> varList;

    public VariablesDialog ( String groupName, ObservableList<Variable> variables )
    {        
        setResizable ( true );
        getDialogPane().setPrefWidth ( 400 );

        varList = new ListView<>();
        varList.setCellFactory ( param -> new VarCell() );
        varList.itemsProperty ().set ( variables );

        setTitle ( "Edit Variables: " + groupName );
        setHeaderText ( "Add, Edit, or Delete Group Variables" );
        getDialogPane ().getButtonTypes ().addAll ( ButtonType.CANCEL, ButtonType.OK );

        varList.setPrefWidth ( 200 );

        varList.setOnContextMenuRequested ( event ->
        {
            event.consume ();

            ContextMenu menu = new ContextMenu();

            MenuItem newVar = new MenuItem();
            newVar.setText ( "Create New Variable" );
            newVar.setOnAction ( a -> 
            {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle ( "Create New Variable" );
                dialog.setHeaderText ( "Please enter a name for the new Variable" );
                dialog.setContentText ( "Variable Name:" );

                Optional<String> name = dialog.showAndWait ();

                if ( !name.isPresent () )
                    return;

                varList.getItems ().add ( new Variable ( name.get (), 0 ) );   
            } );

            menu.getItems ().addAll ( newVar );
            menu.show ( ( Node ) event.getSource (), event.getScreenX (), event.getScreenY () );
        } );

        getDialogPane ().setContent ( varList );
    }
}
