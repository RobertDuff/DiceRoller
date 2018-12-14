package duffrd.diceroller.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import com.google.common.eventbus.Subscribe;

import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.RollerBuilder;
import duffrd.diceroller.model.RollerComposer;
import duffrd.diceroller.view.ObserverEventBus.Event;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainWindowController implements Initializable
{
	private static final Path UNTITLED_FILE_PATH = Paths.get ( "Untitled.yaml" );
	
	@FXML
	public Menu fileMenu;

	@FXML
	public Accordion chooser;

	@FXML
	public SplitPane detailPane;

	@FXML
	public SeparatorMenuItem recentBeginSeparator;

	@FXML
	public SeparatorMenuItem recentEndSeparator;

	private ObjectProperty<Roller> rollerProperty = new SimpleObjectProperty<> ();

	private ObjectProperty<Path> rollerFileProperty = new SimpleObjectProperty<> ();
	private FileChooser fileChooser;

	private final Path rollerDirectory;
	private final Path recentFilesFile;
	private ObservableList<Path> recentFiles;

	public MainWindowController() throws IOException
	{
		rollerDirectory = Paths.get ( System.getenv ( "PUBLIC" ), "Documents", "Rollers" );
		Files.createDirectories ( rollerDirectory );

		recentFilesFile = rollerDirectory.resolve ( "rollersRecentFiles.txt" );

		ObserverEventBus.register ( this );

		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters ().add ( new ExtensionFilter ( "YAML", "*.yaml" ) );
		fileChooser.getExtensionFilters ().add ( new ExtensionFilter ( "XML", "*.xml" ) );
		fileChooser.setInitialDirectory ( rollerDirectory.toFile () );
	}

	@Subscribe
	public void bindMainStageTitleTextProperty ( StringProperty titleProperty )
	{
		StringBinding titleBinding = new StringBinding()
		{
			{
				super.bind ( rollerFileProperty );
			}

			@Override
			protected String computeValue ()
			{
				StringBuilder builder = new StringBuilder();

				if ( rollerFileProperty.get () != null )
				{
					builder.append ( rollerFileProperty.get () );
					builder.append ( " - " );
				}

				builder.append ( "Dice Roller" );

				return builder.toString ();
			}
		};

		titleProperty.bind ( titleBinding );
	}

	@Subscribe
	public void handleEvent ( Event event )
	{
		switch ( event )
		{
			default:
				break;
		}
	}

	@Override
	public void initialize ( URL location, ResourceBundle resources )
	{
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
				outcomePaneController.setProperties ( n.outcomeProperty (), n.triggersProperty () );
			} );

			recentFiles = FXCollections.observableArrayList ();

			if ( Files.exists ( recentFilesFile ) )
				recentFiles.addAll ( Files.readAllLines ( recentFilesFile ).stream ().map ( Paths::get ).filter ( Files::exists ).collect ( Collectors.toList () ) );

			updateRecentFilesInMenu ();
			
			newFile();
		}
		catch ( IOException e )
		{
			//TODO: Show Alert!
			e.printStackTrace();
		}
	}

	public void newFile()
	{
		closeFile();
		
		rollerFileProperty.setValue ( UNTITLED_FILE_PATH );
	}

	public void openFile()
	{
		File file = fileChooser.showOpenDialog ( detailPane.getScene ().getWindow () );

		if ( file != null )
			loadFile ( file.toPath () );
	}

	public void saveFile()
	{
		if ( rollerFileProperty.get ().equals ( UNTITLED_FILE_PATH ) )
			saveFileAs();
		else
			writeFile ( rollerFileProperty.get () );
	}

	public void saveFileAs()
	{
		File file = fileChooser.showSaveDialog ( detailPane.getScene ().getWindow () );
		
		if ( file != null )
			writeFile ( file.toPath () );
	}

	public void closeFile()
	{
		rollerFileProperty.set ( null );
		chooser.getPanes ().clear ();
		ObserverEventBus.post ( Event.CLOSE_FILE );
	}

	private void writeFile ( Path path )
	{
		try
		{
			Map<String,java.util.List<Map<String,Object>>> groups = new HashMap<>();
			
			for ( TitledPane pane : chooser.getPanes () )
			{
				RollerListPaneController controller = ( RollerListPaneController ) pane.getUserData ();
				
				java.util.List<Map<String,Object>> rollerList = new ArrayList<>();
				
				for ( Roller roller : controller.rollerList.getItems () )
					rollerList.add ( RollerComposer.compose ( roller ) );
				
				groups.put ( pane.getText (), rollerList );
			}
			
			Yaml yaml = new Yaml();
			
			Writer writer = new FileWriter ( path.toFile () );
			
			yaml.dump ( groups, writer );
			
			writer.close ();
		}
		catch ( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadFile ( Path path )
	{
		closeFile();
		
		try
		{
			InputStream fs = Files.newInputStream ( path );

			Yaml yaml = new Yaml();

			@SuppressWarnings ( "unchecked" )
			Map<String,java.util.List<Map<String,Object>>> groups = ( Map<String,java.util.List<Map<String,Object>>> ) yaml.load ( fs );

			fs.close ();	

			for ( String groupName : groups.keySet () )
			{
				TitledPane groupPane = createGroup ( groups.get ( groupName ) );
				groupPane.setText ( groupName );

				chooser.getPanes ().add ( groupPane );
			}

			addToRecentFiles ( path );
			rollerFileProperty.setValue ( path );
		}
		catch ( FileNotFoundException e )
		{
			Alert alert = new Alert ( AlertType.ERROR );

			alert.setTitle ( "File Not Found" );
			alert.setHeaderText ( "The file could not be opened" );
			alert.setContentText ( e.getMessage () );

			alert.showAndWait ();
		}
		catch ( IOException e )
		{
			Alert alert = new Alert ( AlertType.ERROR );

			alert.setTitle ( "Error Opening File" );
			alert.setHeaderText ( "The file could not be opened" );
			alert.setContentText ( e.getMessage () );

			alert.showAndWait ();
		}
	}

	private TitledPane createGroup ( java.util.List<Map<String,Object>> def )
	{
		try
		{
			RollerListPaneController rollerListController = new RollerListPaneController ( rollerProperty );
			FXMLLoader rollerListLoader = new FXMLLoader ( getClass().getResource ( "RollerListPane.fxml" ) );
			rollerListLoader.setController ( rollerListController );
			TitledPane groupPane = rollerListLoader.load ();

			for ( Map<String,Object> rollerDef : def )
				rollerListController.addRollers ( RollerBuilder.build ( rollerDef ) );

			groupPane.setUserData ( rollerListController );
			
			return groupPane;
		}
		catch ( IOException e )
		{
			// TODO Show Alert!!
			e.printStackTrace();
		}

		return null;
	}

	private void addToRecentFiles ( Path path )
	{
		recentFiles.remove ( path );
		recentFiles.add ( 0, path );

		while ( recentFiles.size () > 5 )
			recentFiles.remove ( recentFiles.size ()-1 );

		try
		{
			Files.write ( recentFilesFile, recentFiles.stream ().map ( p -> p.toAbsolutePath ().toString () ).collect ( Collectors.toList () ), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING );
		}
		catch ( IOException e )
		{
			// TODO Alert!
			e.printStackTrace();
		}

		updateRecentFilesInMenu ();
	}

	private void updateRecentFilesInMenu()
	{
		int begin = -1;
		int end = -1;

		for ( int i=0; i<fileMenu.getItems ().size (); i++ )
		{
			if ( fileMenu.getItems ().get ( i ) == recentBeginSeparator )
				begin = i+1;

			if ( fileMenu.getItems ().get ( i ) == recentEndSeparator )
				end = i;
		}

		// Clear Recent Files Items
		fileMenu.getItems ().remove ( begin, end );

		fileMenu.getItems ().addAll ( begin, recentFiles.stream ().map ( this::buildRecentFileMenuItem ).collect ( Collectors.toList () ) );

	}

	private MenuItem buildRecentFileMenuItem ( Path path )
	{
		MenuItem item = new MenuItem();
		item.setText ( path.getFileName ().toString () );
		item.setUserData ( path );

		item.setOnAction ( e ->
		{
			loadFile ( ( ( Path ) ( ( MenuItem ) e.getTarget () ).getUserData () ) );
		} );

		return item;
	}

	public void exit()
	{
		Platform.exit ();
	}
}
