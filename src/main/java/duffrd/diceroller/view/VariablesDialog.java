package duffrd.diceroller.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import duffrd.diceroller.model.Suite;
import duffrd.diceroller.model.Variable;
import javafx.collections.FXCollections;
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
import utility.collections.ListRearranger;

public class VariablesDialog extends Dialog<ButtonType>
{    
    public class VariableNode extends HBox
    {
        private Label nameLabel = new Label();
        private TextField valueField = new TextField();
        
        public VariableNode ( String name, int value )
        {                        
            UnaryOperator<TextFormatter.Change> filter = change ->
            {
                if ( change.isAdded () )
                {
                    String text = change.getText ();
                    String editedText = text.replaceAll ( "[^0-9-]", "" );
                    change.setText ( editedText );
                }
                
                return change;
            };
            
            valueField.setTextFormatter ( new TextFormatter<> ( new IntegerStringConverter (), 0, filter ) );
            
            Region spacing = new Region();
            getChildren ().addAll ( nameLabel, spacing, valueField );
            setHgrow ( spacing, Priority.ALWAYS );

            nameLabel.setText ( name );
            valueField.setText ( String.valueOf ( value ) );
        }
        
        public String name()
        {
            return nameLabel.getText ();
        }
        
        public void rename()
        {
            TextInputDialog nameDialog = new TextInputDialog ();
            nameDialog.setTitle ( "Rename Variable: " + nameLabel.getText () );
            nameDialog.setHeaderText ( "Enter the new name for the variable." );
            nameDialog.setContentText ( "Variable Name:" );
            
            Optional<String> result = nameDialog.showAndWait ();
            
            if ( result.isPresent () && !result.get ().isEmpty () )
                nameLabel.setText ( result.get () );
        }
        
        public int value()
        {
            return Integer.valueOf ( valueField.getText () );
        }        
    }

    public class VarCell extends ListCell<VariableNode>
    {        
        public VarCell ()
        {
            super ();

            setOnContextMenuRequested ( event ->
            {
                if ( getItem() == null )
                    return;
                
                event.consume ();

                ContextMenu menu = new ContextMenu ();

                MenuItem rename = new MenuItem ();
                rename.setText ( "Rename Variable \"" + getItem().name () + "\"" );
                rename.setOnAction ( a -> getItem().rename() );

                MenuItem delete = new MenuItem ();
                delete.setText ( "Delete Variable \"" + getItem().name () + "\"" );
                delete.setOnAction ( a -> getListView ().getItems ().remove ( getItem () ) );

                menu.getItems ().addAll ( rename, delete );
                menu.show ( ( Node ) event.getSource (), event.getScreenX (), event.getScreenY () );
            } );

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
                    List<VariableNode> items = getListView ().getItems ();
                    
                    VarCell source = ( VarCell ) event.getGestureSource ();
                    VarCell target = ( VarCell ) event.getGestureTarget ();

                    int from = items.indexOf ( source.getItem () );
                    int to = items.indexOf ( target.getItem () );
                    
                    FXCollections.sort ( getListView().itemsProperty ().getValue (), ListRearranger.move ( getListView().getItems (), from, to ) );
                    
                    success = true;
                }
                
                event.setDropCompleted ( success );
                event.consume ();
            } );
            
            setOnDragDone ( DragEvent::consume );
        }

        @Override
        protected void updateItem ( VariableNode item, boolean empty )
        {
            super.updateItem ( item, empty );
            
            if ( item == null || empty )
            {
                setText ( "" );
                setGraphic ( null );
                return;
            }

            setGraphic ( item );
        }     
    }

    ListView<VariableNode> varList;

    public VariablesDialog ( Suite suite )
    {                
        setResizable ( true );
        getDialogPane().setPrefWidth ( 400 );

        varList = new ListView<>();
        varList.setCellFactory ( param -> new VarCell() );
        varList.getItems ().addAll ( suite.variables ().stream ().map ( v -> new VariableNode ( v.name (), v.value () ) ).collect ( Collectors.toList () ) );

        setTitle ( "Edit Variables: " + suite.name () );
        setHeaderText ( "Add, Edit, or Delete Suite Variables" );
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

                if ( name.isPresent () && !name.get ().isEmpty () )
                    varList.getItems ().add ( new VariableNode ( name.get (), 0 ) );   
            } );

            menu.getItems ().addAll ( newVar );
            menu.show ( ( Node ) event.getSource (), event.getScreenX (), event.getScreenY () );
        } );

        getDialogPane ().setContent ( varList );
    }
    
    public List<Variable> variables()
    {        
        List<Variable> variables = new ArrayList<>();
        
        for ( VariableNode node : varList.getItems () )
            variables.add ( new Variable().name ( node.name () ).value ( node.value () ) );

        return variables;
    }
}
