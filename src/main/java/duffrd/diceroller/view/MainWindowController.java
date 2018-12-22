package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.RollerModel;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class MainWindowController implements Initializable
{	
	@FXML
	public Accordion chooser;

	@FXML
	public SplitPane detailPane;

	@FXML
	public SeparatorMenuItem recentBeginSeparator;

	@FXML
	public SeparatorMenuItem recentEndSeparator;

    @FXML
    public MenuItem newRollerItem;

    @FXML
    public MenuItem editRollerItem;

    @FXML
    public MenuItem deleteRollerItem;

    @FXML
    public MenuItem renameGroupItem;

    @FXML
    public MenuItem deleteGroupItem;
	
	private RollerModel model;
	
	private ObjectProperty<Roller> rollerProperty = new SimpleObjectProperty<> ();

	public MainWindowController ( RollerModel model )
	{
	    this.model = model;
	}
	
	@Override
	public void initialize ( URL location, ResourceBundle resources )
	{
	    deleteRollerItem.setOnAction ( event -> deleteRoller ( rollerProperty.get () ) );
	    
	    renameGroupItem.setOnAction ( e -> renameGroup ( chooser.getExpandedPane () ) );
	    deleteGroupItem.setOnAction ( event -> deleteGroup ( chooser.getExpandedPane () ) );
	    
		try
		{
			FXMLLoader outcomeLoader = new FXMLLoader ( getClass().getResource ( "OutcomePane.fxml" ) );
			AnchorPane outcomePane = outcomeLoader.load();
			final OutcomePaneController outcomePaneController = outcomeLoader.getController ();

			FXMLLoader historyLoader = new FXMLLoader ( getClass().getResource ( "HistoryPane.fxml" ) );
			AnchorPane historyPane = historyLoader.load ();

			detailPane.getItems ().addAll ( outcomePane, historyPane );
			detailPane.setDividerPosition ( 0, 0.3 );

			rollerProperty.addListener ( ( p, o, n ) ->
			{
			    if ( n == null )
			        outcomePaneController.setProperties ( null, null );
			    else
			        outcomePaneController.setProperties ( n.outcomeProperty (), n.triggersProperty () );
			} );
			
			for ( String groupName : model.groupNames () )
			{
	            RollerListPaneController rollerListController = new RollerListPaneController ( rollerProperty );
	            FXMLLoader rollerListLoader = new FXMLLoader ( getClass().getResource ( "RollerListPane.fxml" ) );
	            rollerListLoader.setController ( rollerListController );
	            final TitledPane groupPane = rollerListLoader.load ();
                groupPane.setText ( groupName );
                groupPane.setUserData ( rollerListController );
                
                groupPane.setOnContextMenuRequested ( event -> 
                {
                    ContextMenu menu = new ContextMenu();
                    
                    MenuItem rename = new MenuItem();
                    rename.setText ( "Rename" );
                    rename.setOnAction ( e -> renameGroup ( groupPane ) );
                    
                    MenuItem delete = new MenuItem();
                    delete.setText ( "Delete" );
                    delete.setOnAction ( e -> deleteGroup ( groupPane ) );
                    
                    menu.getItems ().addAll ( rename, delete );
                    
                    menu.show ( ( Node ) event.getSource (), event.getScreenX (), event.getScreenY () );
                } );
                
                chooser.getPanes ().add ( groupPane );

                rollerListController.rollerListProperty ().addAll ( model.rollers ( groupName ) );
			}
		}
		catch ( IOException e )
		{
			//TODO: Show Alert!
			e.printStackTrace();
		}
        catch ( DiceRollerException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	private void renameGroup ( TitledPane group )
	{
        if ( group == null )
            return;
        
        TextInputDialog dialog = new TextInputDialog ( group.getText () );
        dialog.setTitle ( "Group: " + group.getText () );
        dialog.setHeaderText ( "Please enter a new name for the Group" );
        dialog.setContentText ( "New Group Name:" );
        
        Optional<String> newName = dialog.showAndWait ();
        
        if ( !newName.isPresent () )
            return;
        
        try
        {
            model.renameGroup ( group.getText (), newName.get () );
        }
        catch ( DiceRollerException e )
        {
            e.printStackTrace();
            return;
        }
        
        group.setText ( newName.get () );
	}
	
	private void deleteGroup ( TitledPane group )
	{
	    if ( group == null )
	        return;
        
        Alert alert = new Alert ( AlertType.CONFIRMATION );
        alert.setTitle ( "Group: " + group.getText () );
        alert.setHeaderText ( "You are about to delete the " + group.getText () + " group. This cannot be undone!" );
        alert.setContentText ( "Are you sure?" );
        Optional<ButtonType> button = alert.showAndWait ();
        
        if ( !button.isPresent () || button.get () != ButtonType.OK )
            return;
        
	    
	    try
	    {
	        model.deleteGroup ( group.getText () );
	    }
	    catch ( DiceRollerException e )
	    {
	        e.printStackTrace();
	        return;
	    }
	    
	    chooser.getPanes ().remove ( group );
	}
	
	private void deleteRoller ( Roller roller )
	{
	    if ( roller == null )
	        return;
	    
	    Alert alert = new Alert ( AlertType.CONFIRMATION );
	    alert.setTitle ( "Roller: " + roller.name () );
	    alert.setHeaderText ( "You are about to delete the " + roller.name () + " roller. This cannot be undone!" );
	    alert.setContentText ( "Are you sure?" );
	    Optional<ButtonType> button = alert.showAndWait ();
	    
	    if ( !button.isPresent () || button.get () != ButtonType.OK )
	        return;
	    
	    try
        {
            model.deleteRoller ( roller );
        }
        catch ( DiceRollerException e )
        {
            e.printStackTrace();
            return;
        }
        
        for ( TitledPane pane : chooser.getPanes () )
        {
            @SuppressWarnings ( "unchecked" )
            ListView<Roller> lv = ( ListView<Roller> ) ( ( AnchorPane ) pane.getContent () ).getChildren ().get ( 0 );
            if ( lv.getItems ().remove ( roller ) )
                break;
        }
        
        rollerProperty.set ( null );
	}
	
    public void exit()
	{
		Platform.exit ();
	}
}
