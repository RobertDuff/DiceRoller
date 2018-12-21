package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import duffrd.diceroller.model.Roller;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class RollerListPaneController implements Initializable
{
    @FXML
    public ListView<Roller> rollerList;

    private ObjectProperty<Roller> rollerProperty;

    public RollerListPaneController ( ObjectProperty<Roller> rollerProperty )
    {
        this.rollerProperty = rollerProperty;
    }

    @Override
    public void initialize ( URL location, ResourceBundle resources )
    {
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
                                rollerProperty.set ( null );
                                
                                ContextMenu menu = new ContextMenu ();

                                MenuItem prob = new MenuItem ( "Probability Chart" );
                                prob.setOnAction ( new EventHandler<ActionEvent>()
                                {
                                    @Override
                                    public void handle ( ActionEvent event )
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
                                        catch ( IOException e )
                                        {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                MenuItem edit = new MenuItem ( "Edit" );

                                MenuItem delete = new MenuItem ( "Delete" );

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

    public void addRollers ( Roller... rollers )
    {
        rollerList.getItems ().addAll ( rollers );
    }
}
