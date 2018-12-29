package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

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
        
        Callback<ListView<Roller>,ListCell<Roller>> cellFactory = new Callback<ListView<Roller>,ListCell<Roller>>()
        {
            @Override
            public ListCell<Roller> call ( ListView<Roller> param )
            {
                ListCell<Roller> cell = new ListCell<Roller>()
                {
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

                        setOnMouseClicked ( new EventHandler<MouseEvent>()
                        {
                            @Override
                            public void handle ( MouseEvent event )
                            {
                                switch ( event.getButton () )
                                {
                                case PRIMARY:
                                    rollerProperty.set ( roller );

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
                };

                return cell;
            }			
        };

        rollerList.setCellFactory ( cellFactory );
    }

    public ObservableList<Roller> rollerListProperty()
    {
        return rollerList.getItems ();
    }
}
