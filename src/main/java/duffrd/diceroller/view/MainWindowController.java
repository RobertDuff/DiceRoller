package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.Model;
import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.Suite;
import duffrd.diceroller.model.Variable;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import utility.arrays.ListRearranger;

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
    public MenuItem newGroupItem;

    @FXML
    public MenuItem renameGroupItem;

    @FXML
    public MenuItem deleteGroupItem;
    
    @FXML
    public MenuItem switchSuiteMenuItem;
    
    @FXML
    public MenuItem newSuiteMenuItem;
    
    @FXML
    public MenuItem deleteSuiteMenuItem;
    
    @FXML
    public MenuItem helpItem;
    
    @FXML
    public MenuItem aboutItem;
	
    private HostServices hostServices;
	private Model model;
	
	private ObjectProperty<Suite> suiteProperty = new SimpleObjectProperty<> ();
	private ObjectProperty<Roller> rollerProperty = new SimpleObjectProperty<> ();

	public MainWindowController ( HostServices hostServices, Model model )
	{
	    this.hostServices = hostServices;
	    this.model = model;
	}
	
	@Override
	public void initialize ( URL location, ResourceBundle resources )
	{	    
	    suiteProperty.addListener ( ( prop, oldValue, newValue ) -> setSuite() );
	    
	    switchSuiteMenuItem.setOnAction ( event -> chooseSuite() );
        newRollerItem.setOnAction ( event -> newRoller ( chooser.getExpandedPane () ) );
        newRollerItem.disableProperty ().bind ( Bindings.isNull ( chooser.expandedPaneProperty () ) );
        
        editRollerItem.setOnAction ( event -> editRoller ( chooser.getExpandedPane ().getText (), rollerProperty.get () ) );
        editRollerItem.disableProperty ().bind ( Bindings.isNull ( rollerProperty ) );
        
        deleteRollerItem.setOnAction ( event -> deleteRoller ( chooser.getExpandedPane ().getText (), rollerProperty.get () ) );
        deleteRollerItem.disableProperty ().bind ( Bindings.isNull ( rollerProperty ) );
	    
	    newGroupItem.setOnAction ( event -> newGroup() );
	    
	    renameGroupItem.setOnAction ( e -> renameGroup ( chooser.getExpandedPane () ) );
	    renameGroupItem.disableProperty ().bind ( Bindings.isNull ( chooser.expandedPaneProperty () ) );

        deleteGroupItem.setOnAction ( event -> deleteGroup ( chooser.getExpandedPane () ) );
        deleteGroupItem.disableProperty ().bind ( Bindings.isNull ( chooser.expandedPaneProperty () ) );
	    
		try
		{
			FXMLLoader outcomeLoader = new FXMLLoader ( getClass().getResource ( "OutcomePane.fxml" ) );
			AnchorPane outcomePane = outcomeLoader.load();
			final OutcomePaneController outcomePaneController = outcomeLoader.getController ();

			HistoryPaneController historyController = new HistoryPaneController ( model.historyProperty () );
			FXMLLoader historyLoader = new FXMLLoader ( getClass().getResource ( "HistoryPane.fxml" ) );
			historyLoader.setController ( historyController );
			AnchorPane historyPane = historyLoader.load ();

			detailPane.getItems ().addAll ( outcomePane, historyPane );
			detailPane.setDividerPosition ( 0, 0.3 );

			rollerProperty.addListener ( ( p, o, n ) -> outcomePaneController.bind ( n ) );
			
			for ( String groupName : model.groupNames () )
                chooser.getPanes ().add ( createGroupPane ( groupName ) );
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
		
		helpItem.setOnAction ( event -> 
		{ 
		    try
            {
                hostServices.showDocument ( ClassLoader.getSystemResource ( "help/help.html" ).toURI ().toString () );
            }
            catch ( URISyntaxException e1 )
            {
                e1.printStackTrace();
            } 
		} );
	}

	private void chooseSuite()
	{
	    ChoiceDialog<Suite> dialog = new ChoiceDialog<Suite> ( null, model.suites () );
	    
	    Optional<Suite> suite = dialog.showAndWait ();
	    
	    if ( suite.isPresent () )
	        suiteProperty.set ( suite.get () );
	}
	
	private void setSuite()
	{
	    
	}
	
	private void newRoller ( TitledPane groupPane )
    {        
	    if ( groupPane == null )
	        return;
	    
	    RollerWizard wizard = new RollerWizard ( groupPane.getText () );
	    Optional<ButtonType> result = wizard.showAndWait ();
	    
	    System.out.println ( result );
	    
        if ( !result.isPresent () || result.get () != ButtonType.FINISH )
            return;
        
        try
        {
            Roller roller = wizard.roller ();
            
            model.createRoller ( groupPane.getText (), roller );
            
            @SuppressWarnings ( "unchecked" )
            ListView<Roller> lv = ( ListView<Roller> ) ( ( AnchorPane ) groupPane.getContent () ).getChildren ().get ( 0 );
            lv.getItems ().add ( roller );
        }
        catch ( DiceRollerException e )
        {
            e.printStackTrace();
            return;
        }
    }

    private void editRoller ( String group, Roller roller )
    {
        if ( roller == null )
            return;
        
        TextInputDialog dialog = new TextInputDialog ();
        dialog.setTitle ( "Rename a New Roller" );
        dialog.setHeaderText ( "Please Enter the New Name for the Roller." );
        dialog.setContentText ( "New Roller Name: " );
        
        Optional<String> name = dialog.showAndWait ();
        
        if ( !name.isPresent () )
            return;
        
        try
        {            
            model.renameRoller ( group, roller.name (), name.get () );
            roller.name ( name.get () );
            
            @SuppressWarnings ( "unchecked" )
            ListView<Roller> lv = ( ListView<Roller> ) ( ( AnchorPane ) chooser.getExpandedPane ().getContent () ).getChildren ().get ( 0 );
            ( ( RollerListPaneController.RefresherSkin ) lv.getSkin () ).refresh();
        }
        catch ( DiceRollerException e )
        {
            e.printStackTrace();
            return;
        }
    }

    private void deleteRoller ( String group, Roller roller )
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
            model.deleteRoller ( group, roller );
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

    private void newGroup ()
    {
        TextInputDialog newGroupDialog = new TextInputDialog ();
        newGroupDialog.setTitle ( "Create a New Group" );
        newGroupDialog.setHeaderText ( "Please Enter the Name for the new Group." );
        newGroupDialog.setContentText ( "Group Name: " );
        
        Optional<String> name = newGroupDialog.showAndWait ();
        
        if ( !name.isPresent () )
            return;
        
        try
        {
            model.createGroup ( name.get () );
            chooser.getPanes ().add ( createGroupPane ( name.get () ) );
        }
        catch ( DiceRollerException | IOException e )
        {
            e.printStackTrace();
            return;
        }
    }

    private TitledPane createGroupPane ( String groupName ) throws IOException, DiceRollerException
    {
        RollerListPaneController rollerListController = new RollerListPaneController ( model, groupName, rollerProperty );
        FXMLLoader rollerListLoader = new FXMLLoader ( getClass().getResource ( "RollerListPane.fxml" ) );
        rollerListLoader.setController ( rollerListController );
        final TitledPane groupPane = rollerListLoader.load ();
        groupPane.setText ( groupName );
        groupPane.setUserData ( rollerListController );
        
        groupPane.setOnContextMenuRequested ( event -> 
        {
            event.consume ();
            
            ContextMenu menu = new ContextMenu();
            
            MenuItem vars = new MenuItem();
            vars.setText ( "Edit Variables" );
            vars.setOnAction ( e -> editGroupVariables ( groupPane.getText () ) );
            
            MenuItem rename = new MenuItem();
            rename.setText ( "Rename" );
            rename.setOnAction ( e -> renameGroup ( groupPane ) );
            
            MenuItem delete = new MenuItem();
            delete.setText ( "Delete" );
            delete.setOnAction ( e -> deleteGroup ( groupPane ) );
            
            menu.getItems ().addAll ( vars, new SeparatorMenuItem (), rename, delete );
            
            menu.show ( ( Node ) event.getSource (), event.getScreenX (), event.getScreenY () );
        } );
        
        groupPane.setOnDragDetected ( event ->
        {                
            event.consume ();
            TitledPane pane = ( TitledPane ) event.getSource ();
            
            Dragboard dragboard = pane.startDragAndDrop ( TransferMode.MOVE );

            ClipboardContent content = new ClipboardContent ();

            content.putString ( pane.getText () );

            dragboard.setContent ( content );
        } );

        groupPane.setOnDragOver ( event -> 
        {                
            if ( event.getGestureSource () != event.getGestureTarget () && event.getDragboard ().hasString () )
                event.acceptTransferModes ( TransferMode.MOVE );

            event.consume ();
        } );

        groupPane.setOnDragEntered ( event ->
        {                
            if ( event.getGestureSource () != event.getGestureTarget () && event.getDragboard ().hasString () )
            {
                TitledPane pane = ( TitledPane ) event.getSource ();
                pane.setOpacity ( 0.3 );
            }
        } );

        groupPane.setOnDragExited ( event ->
        {                
            if ( event.getGestureSource () != event.getGestureTarget () && event.getDragboard ().hasString () )
            {
                TitledPane pane = ( TitledPane ) event.getSource ();
                pane.setOpacity ( 1.0 );
            }
        } );

        groupPane.setOnDragDropped ( event -> 
        {                
            Dragboard dragboard = event.getDragboard ();
            boolean success = false;

            if ( dragboard.hasString () )
            {                    
                List<TitledPane> panes = chooser.getPanes ();
                
                TitledPane source = ( TitledPane ) event.getGestureSource ();
                TitledPane target = ( TitledPane ) event.getGestureTarget ();

                int from = panes.indexOf ( source );
                int to = panes.indexOf ( target );
                
                try
                {
                    model.moveGroup ( source.getText (), to+1 );
                    panes.sort ( ListRearranger.move ( panes, from, to ) );
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
        
        groupPane.setOnDragDone ( DragEvent::consume );
        
        rollerListController.rollerListProperty ().addAll ( model.rollers ( groupName ) );
        
        return groupPane;
    }
    
    private void renameGroup ( TitledPane group )
	{
        if ( group == null )
            return;
        
        TextInputDialog dialog = new TextInputDialog ( group.getText () );
        dialog.setTitle ( "Group: " + group.getText () );
        dialog.setHeaderText ( "Please enter a new name for the Group" );
        dialog.setContentText ( "Group Name:" );
        
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
	
	private void editGroupVariables ( String group )
	{	    
	    try
        {
	        ObservableList<Variable> variables = FXCollections.observableArrayList ();
	        variables.addAll ( model.groupVariables ( group ) );
	        
            VariablesDialog dialog = new VariablesDialog ( group,variables );
            
            Optional<ButtonType> button = dialog.showAndWait ();
            
            if ( !button.isPresent () || button.get () != ButtonType.OK )
                return;

            model.updateGroupVariables ( group, variables );
        }
        catch ( DiceRollerException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
    public void exit()
	{
		Platform.exit ();
	}
}
