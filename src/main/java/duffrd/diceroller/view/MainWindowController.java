package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.Group;
import duffrd.diceroller.model.Model;
import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.Suite;
import duffrd.diceroller.model.Trigger;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import utility.collections.ListRearranger;
import utility.lua.LuaProvider;

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
    public MenuItem editVariablesMenuItem;
    
    @FXML
    public MenuItem newTriggerMenuItem;
    
    @FXML
    public MenuItem editTriggerMenuItem;
    
    @FXML
    public MenuItem deleteTriggerMenuItem;
    
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
	
	public MainWindowController ( HostServices hostServices, StringProperty titleProperty, Model model )
	{
	    this.hostServices = hostServices;
	    this.titleProperty = titleProperty;
	    this.model = model;
	}
	
	@Override
	public void initialize ( URL location, ResourceBundle resources )
	{	    
	    titleProperty.set ( "Dice Roller" );
	    
	    suiteProperty.addListener ( ( prop, oldValue, newValue ) -> setSuite ( oldValue, newValue ) );
	    	    
	    switchSuiteMenuItem.setOnAction ( event -> chooseSuite() );
	    switchSuiteMenuItem.disableProperty ().bind ( Bindings.greaterThan ( 2, model.suitesProperty ().sizeProperty () ) );
	    
        newSuiteMenuItem.setOnAction ( event -> newSuite() );
        
        renameSuiteMenuItem.setOnAction ( event -> renameSuite ( suiteProperty.get () ) );
        renameSuiteMenuItem.disableProperty ().bind ( Bindings.isNull ( suiteProperty ) );
        
        deleteSuiteMenuItem.setOnAction ( event -> deleteSuite ( suiteProperty.get () ) );
	    deleteSuiteMenuItem.disableProperty ().bind ( Bindings.isNull ( suiteProperty ) );
	    
	    editVariablesMenuItem.setOnAction ( event -> editVariables ( suiteProperty.get () ) );
	    
        newTriggerMenuItem.setOnAction ( event -> newTrigger ( suiteProperty.get () ) );
        deleteTriggerMenuItem.setOnAction ( event -> deleteTrigger ( suiteProperty.get () ) );
	    
        newRollerItem.setOnAction ( event -> newRoller ( suiteProperty.get (), ( Group ) chooser.getExpandedPane ().getProperties().get ( "group" ) ) );
        newRollerItem.disableProperty ().bind ( Bindings.isNull ( chooser.expandedPaneProperty () ) );
        
//        editRollerItem.setOnAction ( event -> editRoller ( rollerProperty.get () ) );
//        editRollerItem.disableProperty ().bind ( Bindings.isNull ( rollerProperty ) );
//        
//        deleteRollerItem.setOnAction ( event -> deleteRoller ( ( Group ) chooser.getExpandedPane ().getProperties ().get ( "group" ), rollerProperty.get () ) );
//        deleteRollerItem.disableProperty ().bind ( Bindings.isNull ( rollerProperty ) );
	    
	    newGroupItem.setOnAction ( event -> newGroup ( suiteProperty.get () ) );
	    
	    renameGroupItem.setOnAction ( e -> renameGroup ( ( Group ) chooser.getExpandedPane ().getProperties ().get ( "group" ) ) );
	    renameGroupItem.disableProperty ().bind ( Bindings.isNull ( chooser.expandedPaneProperty () ) );

        deleteGroupItem.setOnAction ( event -> deleteGroup ( ( Group ) chooser.getExpandedPane ().getProperties ().get ( "group" ) ) );
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
            new ExceptionAlert ( "Loading Panels", e ).showAndWait ();
        }

		groupsListener = ( ListChangeListener.Change<? extends Group> change ) -> 
		{
		    while ( change.next () )
		    {
		        if ( change.wasPermutated () )
		        {
		            System.out.println ( "Re-Ordering Group Panes" );
		            int[] order = IntStream.range ( change.getFrom (), change.getTo () ).map ( i -> change.getPermutation ( i ) ).toArray ();
		            chooser.getPanes ().sort ( new ListRearranger<> ( chooser.getPanes (), order ) );
		        }
		        
		        if ( change.wasRemoved () )
		            chooser.getPanes ().remove ( change.getFrom (), change.getFrom () + change.getRemovedSize () );
		        
		        if ( change.wasAdded () )
		            chooser.getPanes ().addAll ( change.getFrom (), change.getAddedSubList ().stream ().map ( g -> createGroupPane ( g ) ).collect ( Collectors.toList () ) );
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
                new ExceptionAlert ( "Showing Help", e1 ).showAndWait ();
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
	        chooser.getPanes ().stream ().map ( p -> ( RollerListPaneController ) p.getProperties ().get ( "controller" ) ).forEach ( c -> c.close() );
	        oldSuite.groupsProperty ().removeListener ( groupsListener );
	        chooser.getPanes ().clear ();
	        titleProperty.unbind ();
	        titleProperty.set ( "Dice Roller" );
	    }
	    
	    //
	    // Create New Group Panes
	    //
	    
	    if ( newSuite != null )
	    {
	        newSuite.groupsProperty ().addListener ( groupsListener );

	        for ( Group group : newSuite.groups () )
	            chooser.getPanes ().add ( createGroupPane ( group ) );

	        titleProperty.bind ( Bindings.createStringBinding ( () -> "Dice Roller: " + newSuite.name (), newSuite.nameProperty () ) );
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
            new ExceptionAlert ( "Creating Suite", e ).showAndWait ();
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
        
        try
        {
            model.updateSuite ( suite, newName.get () );
        }
        catch ( DiceRollerException e )
        {
            new ExceptionAlert ( "Renaming Suite", e );
        }
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
        
        try
        {
            model.deleteSuite ( suite );
            suiteProperty.set ( null );
            selectSuite ();
        }
        catch ( DiceRollerException e )
        {
            new ExceptionAlert ( "Deleting Suite", e );
        }
    }
	
	private void newTrigger ( Suite suite )
    {
        try
        {
            Dialog<ButtonType> triggerDialog = new Dialog<>();
            
            Trigger temporaryTrigger = new Trigger();   
            temporaryTrigger.lua ( LuaProvider.newLua () );
            
            triggerDialog.setTitle ( "Define New Trigger" );
            triggerDialog.setHeaderText ( "Enter the definition of the new Trigger" );
            triggerDialog.setWidth ( 600 );
            triggerDialog.setHeight ( 300 );
            triggerDialog.setResizable ( true );
            
            DialogPane dialogPane = new DialogPane();
            dialogPane.getButtonTypes ().addAll ( ButtonType.CANCEL, ButtonType.OK );
            
            AnchorPane pane = new AnchorPane();
            
            Label namePrompt = new Label();
            namePrompt.setText ( "Name:" );
            
            AnchorPane.setLeftAnchor ( namePrompt, 20.0 );
            AnchorPane.setTopAnchor ( namePrompt, 20.0 );
            
            Label defPrompt = new Label();
            defPrompt.setText ( "Definition:" );
            
            AnchorPane.setLeftAnchor ( defPrompt, 20.0 );
            AnchorPane.setTopAnchor ( defPrompt, 50.0 );
            
            TextField nameField = new TextField();
            temporaryTrigger.nameProperty ().bind ( nameField.textProperty () );
            
            AnchorPane.setTopAnchor ( nameField, 20.0 );
            AnchorPane.setRightAnchor ( nameField, 20.0 );
            
            TextField defField = new TextField();
            defField.styleProperty ().bind ( Bindings.when ( temporaryTrigger.validProperty () ).then ( "" ).otherwise ( "-fx-control-inner-background: pink" ) );
            temporaryTrigger.definitionProperty ().bind ( defField.textProperty () );
            
            AnchorPane.setTopAnchor ( defField, 50.0 );
            AnchorPane.setRightAnchor ( defField, 20.0 );
            
            Button okButton = ( Button ) dialogPane.lookupButton ( ButtonType.OK );
            
            okButton.disableProperty ().bind ( temporaryTrigger.validProperty ().not () );
            
            pane.getChildren ().addAll ( namePrompt, nameField, defPrompt, defField );
            
            dialogPane.setContent ( pane );
            
            triggerDialog.setDialogPane ( dialogPane );
            
            Optional<ButtonType> result = triggerDialog.showAndWait ();
            
            if ( result.isPresent () && result.get () == ButtonType.OK )
                model.createTrigger ( suite, temporaryTrigger.name (), temporaryTrigger.definition () );
        }
        catch ( DiceRollerException e )
        {
            new ExceptionAlert ( "Creating Trigger", e ).showAndWait ();
        }
    }

    private void deleteTrigger ( Suite suite )
    {
        if ( suite == null )
            return;
        
        try
        {
            ChoiceDialog<Trigger> dialog = new ChoiceDialog<Trigger> ( null, suite.triggers () );
            
            Optional<Trigger> trigger = dialog.showAndWait ();
            
            if ( trigger.isPresent () )
                model.deleteTrigger ( trigger.get () );
        }
        catch ( DiceRollerException e )
        {
            new ExceptionAlert ( "Delete Trigger", e ).showAndWait ();
        }
    }

    private void editVariables ( Suite suite )
    {       
        try
        {
            VariablesDialog dialog = new VariablesDialog ( suite );
            
            Optional<ButtonType> result = dialog.showAndWait ();
            
            if ( !result.isPresent () || result.get () != ButtonType.OK )
                return;
            
            model.updateVariables ( suite, dialog.variables () );
        }
        catch ( DiceRollerException e )
        {
            new ExceptionAlert ( "Editing Variables", e ).showAndWait ();
        }
    }

    private void newGroup ( Suite suite )
    {
        if ( suite == null )
            return;
        
        try
        {
            TextInputDialog newGroupDialog = new TextInputDialog ();
            newGroupDialog.setTitle ( "Create a New Group" );
            newGroupDialog.setHeaderText ( "Please Enter the Name for the new Group." );
            newGroupDialog.setContentText ( "Group Name: " );
            
            Optional<String> name = newGroupDialog.showAndWait ();
            
            if ( !name.isPresent () )
                return;
            
            model.createGroup ( suite, name.get () );
        }
        catch ( DiceRollerException e )
        {
            new ExceptionAlert ( "Creating Group", e ).showAndWait ();
        }
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
        
        try
        {
            model.updateGroup ( group, newName.get () );
        }
        catch ( DiceRollerException e )
        {
            new ExceptionAlert ( "Rename Group", e ).showAndWait ();
        }
    }

    private void deleteGroup ( Group group )
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
        
        try
        {
            model.deleteGroup ( group );
        }
        catch ( DiceRollerException e )
        {
            new ExceptionAlert ( "Delete Group", e ).showAndWait ();
        }
    }

    private void newRoller ( Suite suite, Group group )
    {        
	    try
        {
            RollerWizard wizard = new RollerWizard ( suite );
            Optional<ButtonType> result = wizard.cast ();
            	    
            if ( !result.isPresent () || result.get () != ButtonType.FINISH )
                return;
            
            Roller product = wizard.product ();
                        
            Roller roller = model.createRoller ( group, product.name (), product.definition() );
            
            model.updateLabels ( roller, product.labels () );
            model.updateRollerTriggers ( roller, product.triggers () );
        }
        catch ( DiceRollerException e )
        {
            new ExceptionAlert ( "Creating Roller", e ).showAndWait ();
        }
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
            new ExceptionAlert ( "Delete Roller", e );
        }       
    }

    private TitledPane createGroupPane ( Group group )
    {
        try
        {
            RollerListPaneController rollerListController = new RollerListPaneController ( group, outcomeController, historyController );
            FXMLLoader rollerListLoader = new FXMLLoader ( getClass().getResource ( "RollerListPane.fxml" ) );
            rollerListLoader.setController ( rollerListController );
            final TitledPane groupPane = rollerListLoader.load ();
            groupPane.getProperties ().put ( "group", group );
            groupPane.getProperties ().put ( "controller", rollerListController );
            groupPane.textProperty ().bind ( group.nameProperty () );
            
            groupPane.setOnContextMenuRequested ( event -> 
            {
                event.consume ();
                
                ContextMenu menu = new ContextMenu();
                
                MenuItem rename = new MenuItem();
                rename.setText ( "Rename" );
                rename.setOnAction ( e -> renameGroup ( group ) );
                
                MenuItem delete = new MenuItem();
                delete.setText ( "Delete" );
                delete.setOnAction ( e -> deleteGroup ( group ) );
                
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

                    int to = panes.indexOf ( target );

                    try
                    {
                        model.moveGroup ( ( Group ) source.getProperties ().get ( "group" ), to+1 );
                    }
                    catch ( DiceRollerException e )
                    {
                        new ExceptionAlert ( "Moving Group", e ).showAndWait ();
                    }
                    
                    success = true;
                }
                
                event.setDropCompleted ( success );
                event.consume ();
            } );
            
            groupPane.setOnDragDone ( DragEvent::consume );
            
            return groupPane;
        }
        catch ( IOException e )
        {
            new ExceptionAlert ( "Creating Group Pane", e ).showAndWait ();
            return null;
        }
    }
    
    public void exit()
	{
		Platform.exit ();
	}
}
