package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sun.javafx.scene.control.skin.ListViewSkin;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.RollerModel;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utility.arrays.ListRearranger;

public class RollerListPaneController implements Initializable
{
    public class RefresherSkin extends ListViewSkin<Roller>
    {
        public RefresherSkin ( ListView<Roller> listView )
        {
            super ( listView );
            // TODO Auto-generated constructor stub
        }
        
        public void refresh()
        {
            super.flow.recreateCells ();
        }
    }
    
    public class RollerCell extends ListCell<Roller>
    {        
        public RollerCell ()
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
                    List<Roller> items = getListView ().getItems ();
                    
                    RollerCell source = ( RollerCell ) event.getGestureSource ();
                    RollerCell target = ( RollerCell ) event.getGestureTarget ();

                    int from = items.indexOf ( source.getItem () );
                    int to = items.indexOf ( target.getItem () );
                    
                    try
                    {
                        model.moveRoller ( groupName, source.getItem (), to+1 );
                        items.sort ( ListRearranger.move ( items, from, to ) );
                        success = true;
                    }
                    catch ( DiceRollerException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }                    
                }
                
                event.setDropCompleted ( success );
                event.consume ();
            } );
            
            setOnDragDone ( DragEvent::consume );

            setOnMouseClicked ( new EventHandler<MouseEvent>()
            {
                @Override
                public void handle ( MouseEvent event )
                {
                    switch ( event.getButton () )
                    {
                    case PRIMARY:
                        rollerProperty.set ( getItem() );

                        if ( event.getClickCount () == 2 )
                            getItem ().roll ();

                        break;

                    default:
                        break;
                    }
                }
            } );

            setOnContextMenuRequested ( new EventHandler<ContextMenuEvent>()
            {
                @Override
                public void handle ( ContextMenuEvent event )
                {                                
                    event.consume ();
                    
                    rollerProperty.set ( null );
                    
                    ContextMenu menu = new ContextMenu ();

                    MenuItem prob = new MenuItem ( "Probability Chart" );
                    prob.setOnAction ( e -> 
                    {
                        try
                        {
                            FXMLLoader probabilityLoader = new FXMLLoader ( getClass().getResource ( "ProbabilityWindow.fxml" ) );
                            probabilityLoader.setController ( new ProbabilityController ( getItem() ) );
                            Pane probabilityPane = probabilityLoader.load();

                            final Stage probabilityStage = new Stage();
                            probabilityStage.initModality ( Modality.APPLICATION_MODAL );
                            probabilityStage.initOwner ( DiceRollerApplication.instance ().mainStage () );
                            Scene probabilityScene = new Scene ( probabilityPane, 1000, 500 );
                            probabilityStage.setScene ( probabilityScene );
                            probabilityStage.show ();
                        }
                        catch ( IOException x )
                        {
                            // TODO Auto-generated catch block
                            x.printStackTrace();
                        }
                    } );
                    
                    MenuItem edit = new MenuItem ( "Edit" );

                    MenuItem delete = new MenuItem ( "Delete" );
                    delete.setOnAction ( e ->
                    {
                        Roller roller = getItem ();
                        
                        Alert alert = new Alert ( AlertType.CONFIRMATION );
                        alert.setTitle ( "Roller: " + roller.name () );
                        alert.setHeaderText ( "You are about to delete the " + roller.name () + " roller. This cannot be undone!" );
                        alert.setContentText ( "Are you sure?" );
                        Optional<ButtonType> button = alert.showAndWait ();
                        
                        if ( !button.isPresent () || button.get () != ButtonType.OK )
                            return;
                        
                        try
                        {
                            model.deleteRoller ( groupName, roller );
                        }
                        catch ( DiceRollerException e1 )
                        {
                            e1.printStackTrace();
                            return;
                        }
                        
                        rollerList.getItems ().remove ( roller );
                    } );

                    menu.getItems ().addAll ( prob, new SeparatorMenuItem (), edit, delete );

                    menu.show ( ( Node ) event.getSource (), event.getScreenX (), event.getScreenY () );
                }
            } );
        }

        @Override
        protected void updateItem ( Roller roller, boolean empty )
        {
            super.updateItem ( roller, empty );

            if ( empty || roller == null )
            {
                setText ( null );
                setGraphic ( null );
                return;
            }

            setText ( roller.name () );

            Tooltip tip = new Tooltip ();
            tip.setText ( roller.definition () );

            setTooltip ( tip );
        }
    }

    @FXML
    public ListView<Roller> rollerList;

    private RollerModel model;
    private ObjectProperty<Roller> rollerProperty;
    private String groupName;

    public RollerListPaneController ( RollerModel model, String groupName, ObjectProperty<Roller> rollerProperty )
    {
        this.model = model;
        this.groupName = groupName;
        this.rollerProperty = rollerProperty;
    }

    @Override
    public void initialize ( URL location, ResourceBundle resources )
    {
        RefresherSkin skin = new RefresherSkin ( rollerList );
        rollerList.setSkin ( skin );        
        rollerList.setCellFactory ( c -> new RollerCell() );
    }

    public ObservableList<Roller> rollerListProperty()
    {
        return rollerList.getItems ();
    }
}
