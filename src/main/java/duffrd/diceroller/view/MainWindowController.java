package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Accordion;
import javafx.scene.control.Menu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class MainWindowController implements Initializable
{	
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

	private RollerModel model;
	
	private ObjectProperty<Roller> rollerProperty = new SimpleObjectProperty<> ();

	public MainWindowController ( RollerModel model )
	{
	    this.model = model;
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
			
			for ( String groupName : model.groupNames () )
			{
	            RollerListPaneController rollerListController = new RollerListPaneController ( rollerProperty );
	            FXMLLoader rollerListLoader = new FXMLLoader ( getClass().getResource ( "RollerListPane.fxml" ) );
	            rollerListLoader.setController ( rollerListController );
	            TitledPane groupPane = rollerListLoader.load ();
                groupPane.setText ( groupName );
                groupPane.setUserData ( rollerListController );
                chooser.getPanes ().add ( groupPane );

                for ( Roller roller : model.rollers ( groupName ) )
                    rollerListController.addRollers ( roller );
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

	public void exit()
	{
		Platform.exit ();
	}
}
