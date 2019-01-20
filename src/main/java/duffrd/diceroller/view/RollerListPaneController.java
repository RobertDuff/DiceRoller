package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.fxmisc.easybind.EasyBind;

import com.sun.javafx.scene.control.skin.ListViewSkin;

import duffrd.diceroller.model.Group;
import duffrd.diceroller.model.Outcome;
import duffrd.diceroller.model.Roller;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
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
                    RollerCell source = ( RollerCell ) event.getGestureSource ();
                    RollerCell target = ( RollerCell ) event.getGestureTarget ();

                    int from = getListView ().getItems ().indexOf ( source.getItem () );
                    int to = getListView ().getItems ().indexOf ( target.getItem () );
                    
                    FXCollections.sort ( group.rollersProperty ().getValue (), ListRearranger.move ( group.rollers (), from, to ) );
                    FXCollections.sort ( rollerList.itemsProperty ().getValue (), ListRearranger.move ( getListView().getItems (), from, to ) );
                    
                    success = true;
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
                        {
                            Outcome outcome = getItem ().roll ();
                            rollListeners.stream ().forEach ( l -> l.roll ( outcome ) );
                        }

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
                        
                        group.rollers ().remove ( roller );
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

    private Group group;
    private ObjectProperty<Roller> rollerProperty;
    private Set<RollListener> rollListeners = new HashSet<>();

    public RollerListPaneController ( Group group, ObjectProperty<Roller> rollerProperty, RollListener... listeners )
    {
        this.group = group;
        this.rollerProperty = rollerProperty;
        rollListeners.addAll ( Arrays.asList ( listeners ) );
    }

    @Override
    public void initialize ( URL location, ResourceBundle resources )
    {
        RefresherSkin skin = new RefresherSkin ( rollerList );
        rollerList.setSkin ( skin );        
        rollerList.setCellFactory ( c -> new RollerCell() );
        EasyBind.listBind ( rollerList.getItems (), group.rollersProperty () );
        
        rollerList.prefHeightProperty ().bind ( Bindings.size ( rollerList.getItems () ).multiply ( 24 ) );
    }
}
