package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import duffrd.diceroller.model.Group;
import duffrd.diceroller.model.Model;
import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.Suite;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
    public MenuItem renameSuiteMenuItem;
    
    @FXML
    public MenuItem deleteSuiteMenuItem;
    
    @FXML
    public MenuItem helpItem;
    
    @FXML
    public MenuItem aboutItem;
	
    private HostServices hostServices;
    private StringProperty titleProperty;
	private Model model;
	private OutcomePaneController outcomeController;
	private HistoryPaneController historyController;
	
	private ObjectProperty<Suite> suiteProperty = new SimpleObjectProperty<> ();
	private ListChangeListener<Group> groupsListener;
	
	private ObjectProperty<Roller> rollerProperty = new SimpleObjectProperty<> ();

	public MainWindowController ( HostServices hostServices, StringProperty titleProperty, Model model )
	{
	    this.hostServices = hostServices;
	    this.titleProperty = titleProperty;
	    this.model = model;
	}
	
	@Override
	public void initialize ( URL location, ResourceBundle resources )
	{	    
	    suiteProperty.addListener ( ( prop, oldValue, newValue ) -> setSuite ( oldValue, newValue ) );
	    
	    titleProperty.set ( "Dice Roller" );
	    
	    switchSuiteMenuItem.setOnAction ( event -> chooseSuite() );
	    switchSuiteMenuItem.disableProperty ().bind ( Bindings.lessThan ( 2, model.suitesProperty ().sizeProperty () ) );
	    
        newSuiteMenuItem.setOnAction ( event -> newSuite() );
        
        renameSuiteMenuItem.setOnAction ( event -> renameSuite ( suiteProperty.get () ) );
        renameSuiteMenuItem.disableProperty ().bind ( Bindings.isNull ( suiteProperty ) );
        
        deleteSuiteMenuItem.setOnAction ( event -> deleteSuite ( suiteProperty.get () ) );
	    deleteSuiteMenuItem.disableProperty ().bind ( Bindings.isNull ( suiteProperty ) );
	    
        newRollerItem.setOnAction ( event -> newRoller ( suiteProperty.get (), ( Group ) chooser.getExpandedPane ().getUserData () ) );
        newRollerItem.disableProperty ().bind ( Bindings.isNull ( chooser.expandedPaneProperty () ) );
        
        editRollerItem.setOnAction ( event -> editRoller ( rollerProperty.get () ) );
        editRollerItem.disableProperty ().bind ( Bindings.isNull ( rollerProperty ) );
        
        deleteRollerItem.setOnAction ( event -> deleteRoller ( ( Group ) chooser.getExpandedPane ().getUserData(), rollerProperty.get () ) );
        deleteRollerItem.disableProperty ().bind ( Bindings.isNull ( rollerProperty ) );
	    
	    newGroupItem.setOnAction ( event -> newGroup ( suiteProperty.get () ) );
	    
	    renameGroupItem.setOnAction ( e -> renameGroup ( ( Group ) chooser.getExpandedPane ().getUserData () ) );
	    renameGroupItem.disableProperty ().bind ( Bindings.isNull ( chooser.expandedPaneProperty () ) );

        deleteGroupItem.setOnAction ( event -> deleteGroup ( suiteProperty.get (), ( Group ) chooser.getExpandedPane ().getUserData () ) );
        deleteGroupItem.disableProperty ().bind ( Bindings.isNull ( chooser.expandedPaneProperty () ) );
	    
		try
		{
			FXMLLoader outcomeLoader = new FXMLLoader ( getClass().getResource ( "OutcomePane.fxml" ) );
			AnchorPane outcomePane = outcomeLoader.load();
			outcomeController = outcomeLoader.getController ();

			FXMLLoader historyLoader = new FXMLLoader ( getClass().getResource ( "HistoryPane.fxml" ) );
			AnchorPane historyPane = historyLoader.load ();
			historyController = historyLoader.getController ();

			detailPane.getItems ().addAll ( outcomePane, historyPane );
            detailPane.setDividerPosition ( 0, 0.3 );
        }
        catch ( IOException e )
        {
            //TODO: Show Alert!
            e.printStackTrace();
        }

		groupsListener = ( ListChangeListener.Change<? extends Group> change ) -> 
		{
		    while ( change.next () )
		    {
		        if ( change.wasRemoved () )
		        {
		            for ( Group group : change.getRemoved () )
		            {
		                Iterator<TitledPane> i = chooser.getPanes ().iterator ();
                    
		                while ( i.hasNext () )
		                {
		                    if ( i.next ().getUserData () == group )
		                        i.remove ();
		                }
		            }
		        }
		        
		        if ( change.wasAdded () )
		        {
		            int pos = change.getFrom ();
		            
		            for ( Group group : change.getAddedSubList () )
                        try
                        {
                            chooser.getPanes ().add ( pos++, createGroupPane ( suiteProperty.get (), group ) );
                        }
                        catch ( IOException e1 )
                        {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
		        }
		        
		        if ( change.wasReplaced () || change.wasUpdated () )
		        {
		            //TODO: Should never happen
		        }
		    }
		};
		
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
		
		selectSuite ();
	}

	private void selectSuite()
	{
	    Platform.runLater ( () -> 
	    {
	        switch ( model.suites ().size () )
	        {
	        case 0:
	            newSuite();
	            break;

	        case 1:
	            suiteProperty.set ( model.suites ().iterator ().next () );
	            break;

	        default:
	            chooseSuite ();	  
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
	
	private void setSuite ( Suite oldSuite, Suite newSuite )
	{
	    //
	    // Remove old Group Panes
	    //
	    
	    if ( oldSuite != null )
	    {
	        oldSuite.groupsProperty ().removeListener ( groupsListener );
	        chooser.getPanes ().clear ();
	        titleProperty.set ( "Dice Roller" );
	    }
	    
	    //
	    // Create New Group Panes
	    //
	    
	    try
        {
            if ( newSuite != null )
            {
                newSuite.groupsProperty ().addListener ( groupsListener );
                
                for ( Group group : newSuite.groups () )
                    chooser.getPanes ().add ( createGroupPane ( newSuite, group ) );
                
                titleProperty.set ( "Dice Roller: " + newSuite.name () );
            }
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
    
    private void newSuite()
	{
        try
        {
            NewSuiteDialog newSuiteDialog = new NewSuiteDialog ( model );
            
            Optional<ButtonType> button = newSuiteDialog.showAndWait ();
            
            if ( button.isPresent () && button.get () == ButtonType.OK )
                suiteProperty.set ( newSuiteDialog.suite() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
	}
    
    private void renameSuite ( Suite suite )
    {
        System.out.println ( "Renaming: " + suite );
        if ( suite == null )
            return;
        
        TextInputDialog dialog = new TextInputDialog ( suite.name () );
        dialog.setTitle ( "Suite: " + suite.name () );
        dialog.setHeaderText ( "Please enter a new name for the Suite" );
        dialog.setContentText ( "Suite Name:" );
        
        Optional<String> newName = dialog.showAndWait ();
        
        if ( !newName.isPresent () )
            return;
        
        suite.name ( newName.get() );
        titleProperty.set ( "Dice Roller: " + suite.name () );
    }

    private void deleteSuite ( Suite suite )
    {
        if ( suite == null )
            return;
        
        Alert alert = new Alert ( AlertType.CONFIRMATION );
        alert.setTitle ( "Suite: " + suite.name() );
        alert.setHeaderText ( "You are about to delete the " + suite.name () + " suite. This cannot be undone!" );
        alert.setContentText ( "Are you sure?" );
        Optional<ButtonType> button = alert.showAndWait ();
        
        if ( !button.isPresent () || button.get () != ButtonType.OK )
            return;
        
        model.suites ().remove ( suite );
        suiteProperty.set ( null );
        selectSuite ();
    }
	
	private void newRoller ( Suite suite, Group group )
    {        
	    RollerWizard wizard = new RollerWizard ( suite );
	    Optional<ButtonType> result = wizard.showAndWait ();
	    
	    System.out.println ( result );
	    
        if ( !result.isPresent () || result.get () != ButtonType.FINISH )
            return;
        
        group.addRoller ( wizard.roller () );
    }

    private void editRoller ( Roller roller )
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
        
        roller.name ( name.get () );
    }

    private void deleteRoller ( Group group, Roller roller )
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
        
        group.rollers ().remove ( roller );        
        rollerProperty.set ( null );
    }

    private void newGroup ( Suite suite )
    {
        TextInputDialog newGroupDialog = new TextInputDialog ();
        newGroupDialog.setTitle ( "Create a New Group" );
        newGroupDialog.setHeaderText ( "Please Enter the Name for the new Group." );
        newGroupDialog.setContentText ( "Group Name: " );
        
        Optional<String> name = newGroupDialog.showAndWait ();
        
        if ( !name.isPresent () )
            return;
        
        Group group = suite.newGroup ();
        group.name ( name.get () );
    }

    private TitledPane createGroupPane ( Suite suite, Group group ) throws IOException
    {
        RollerListPaneController rollerListController = new RollerListPaneController ( group, rollerProperty, outcomeController, historyController );
        FXMLLoader rollerListLoader = new FXMLLoader ( getClass().getResource ( "RollerListPane.fxml" ) );
        rollerListLoader.setController ( rollerListController );
        final TitledPane groupPane = rollerListLoader.load ();
        groupPane.setUserData ( group );
        groupPane.setText ( group.name () );
        groupPane.setUserData ( rollerListController );
        
        groupPane.setOnContextMenuRequested ( event -> 
        {
            event.consume ();
            
            ContextMenu menu = new ContextMenu();
            
            MenuItem rename = new MenuItem();
            rename.setText ( "Rename" );
            rename.setOnAction ( e -> renameGroup ( group ) );
            
            MenuItem delete = new MenuItem();
            delete.setText ( "Delete" );
            delete.setOnAction ( e -> deleteGroup ( suite, group ) );
            
            menu.getItems ().addAll ( rename, delete );
            
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

                FXCollections.sort ( suite.groupsProperty ().getValue (), ListRearranger.move ( suite.groups (), from, to ) );
                chooser.getPanes ().sort ( ListRearranger.move ( chooser.getPanes (), from, to ) );
                
                success = true;
            }
            
            event.setDropCompleted ( success );
            event.consume ();
        } );
        
        groupPane.setOnDragDone ( DragEvent::consume );
        
        return groupPane;
    }
    
    private void renameGroup ( Group group )
	{
        if ( group == null )
            return;
        
        TextInputDialog dialog = new TextInputDialog ( group.name () );
        dialog.setTitle ( "Group: " + group.name () );
        dialog.setHeaderText ( "Please enter a new name for the Group" );
        dialog.setContentText ( "Group Name:" );
        
        Optional<String> newName = dialog.showAndWait ();
        
        if ( !newName.isPresent () )
            return;
        
        group.name ( newName.get() );
	}
	
	private void deleteGroup ( Suite suite, Group group )
	{
	    if ( group == null )
	        return;
        
        Alert alert = new Alert ( AlertType.CONFIRMATION );
        alert.setTitle ( "Group: " + group.name() );
        alert.setHeaderText ( "You are about to delete the " + group.name () + " group. This cannot be undone!" );
        alert.setContentText ( "Are you sure?" );
        Optional<ButtonType> button = alert.showAndWait ();
        
        if ( !button.isPresent () || button.get () != ButtonType.OK )
            return;
	    
        suite.groups ().remove ( group );
	}
	
	private void editVariables ( Suite suite )
	{	    
	    new VariablesDialog ( suite ).showAndWait ();
	}
	
    public void exit()
	{
		Platform.exit ();
	}
}
