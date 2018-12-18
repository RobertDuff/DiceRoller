package duffrd.diceroller.view;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import duffrd.diceroller.model.History;
import duffrd.diceroller.model.HistoryEntry;
import duffrd.diceroller.model.HistoryTime;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class HistoryPaneController implements Initializable
{
	private ObjectProperty<Predicate<? super HistoryEntry>> filterProperty;
	
	@FXML
	public TableView<HistoryEntry> historyTable;
	
	@FXML
	public TableColumn<HistoryEntry,HistoryTime> historyTableTimeColumn;
	
	@FXML
	public TableColumn<HistoryEntry,String> historyTableRollerNameColumn;
	
	@FXML
	public TableColumn<HistoryEntry,String> historyTableOutcomeColumn;
	
	@FXML
	public TableColumn<HistoryEntry,String> historyTableTriggersColumn;
	
	@FXML
	public TableColumn<HistoryEntry,String> historyTableFacesColumn;
	
	@Override
	public void initialize ( URL location, ResourceBundle resources )
	{
		FilteredList<HistoryEntry> filteredList = new FilteredList<> ( History.history ().historyProperty () );
		filterProperty = filteredList.predicateProperty ();
		
		filterProperty.addListener ( ( p, o, n ) ->
		{
			if ( historyTable.getItems ().isEmpty () )
			{
				Platform.runLater ( () ->
				{
					Alert alert = new Alert ( AlertType.WARNING );
					alert.setTitle ( "Warning" );
					alert.setHeaderText ( "Applying the filter resulted in no entries being selected" );
					alert.showAndWait ();
					
					filterProperty.set ( o );
				} );
			}
		} );
		
		SortedList<HistoryEntry> sortedList = new SortedList<> ( filteredList );
		sortedList.comparatorProperty ().bind ( historyTable.comparatorProperty () );

		historyTable.setItems ( sortedList );
		
		//
		//
		//
		
		historyTableTimeColumn.setCellValueFactory ( new PropertyValueFactory<> ( "time" ) );
		
		historyTable.getSortOrder ().add ( historyTableTimeColumn );
		historyTableTimeColumn.setSortType ( SortType.DESCENDING );

		//
		//
		//
		
		historyTableRollerNameColumn.setCellFactory ( rollerNameCellFactory () );
		historyTableRollerNameColumn.setCellValueFactory ( new PropertyValueFactory<> ( "rollerName" ) );
		
		//
		//
		//
		
		historyTableOutcomeColumn.setCellFactory ( outcomeCellFactory () );
		historyTableOutcomeColumn.setCellValueFactory ( new PropertyValueFactory<> ( "outcome" ) );
		
		//
		//
		//
		
		historyTableTriggersColumn.setCellFactory ( triggerCellFactory () );
		historyTableTriggersColumn.setCellValueFactory ( new PropertyValueFactory<> ( "triggers" ) );
		
		//
		//
		//
		
		historyTableFacesColumn.setCellValueFactory ( new PropertyValueFactory<> ( "faces" ) );
	}
	
	private Callback<TableColumn<HistoryEntry,String>,TableCell<HistoryEntry,String>> rollerNameCellFactory()
	{
		return col -> 
		{
			TableCell<HistoryEntry,String> cell = new TableCell<>();

			cell.textProperty ().bind ( cell.itemProperty () );

			cell.setOnMouseClicked ( rollerNameMouseClickEventHandler ( cell ) );

			return cell;
		};
	}
	
	private EventHandler<? super MouseEvent> rollerNameMouseClickEventHandler ( final TableCell<HistoryEntry,String> cell )
	{
		return event ->
		{
			if ( event.getButton () == MouseButton.SECONDARY )
				if ( filterProperty.get () == null )
				{
					final String rollerName = cell.getText ();
					filterProperty.set ( entry -> entry.getRollerName ().equals ( rollerName ) );
				}
				else
					filterProperty.set ( null );
		};
	}
	
	private Callback<TableColumn<HistoryEntry,String>,TableCell<HistoryEntry,String>> outcomeCellFactory()
	{
		return col -> 
		{
			TableCell<HistoryEntry,String> cell = new TableCell<>();

			cell.textProperty ().bind ( cell.itemProperty () );

			cell.setOnMouseClicked ( outcomeMouseClickEventHandler ( cell ) );

			return cell;
		};
	}
	
	private EventHandler<? super MouseEvent> outcomeMouseClickEventHandler ( final TableCell<HistoryEntry,String> cell )
	{
		return event ->
		{
			if ( event.getButton () == MouseButton.SECONDARY )
			{
				if ( filterProperty.get () != null )
				{
					filterProperty.set ( null );
					return;
				}
								
				// Determine if the outcome is an integer, or not
				try 
				{ 
					final int value = Integer.valueOf ( cell.getText () ); 
					ContextMenu menu = new ContextMenu();
					
					MenuItem lt = new MenuItem ( "Less Than " + value );
					lt.setOnAction ( e -> filterProperty.set ( new OutcomePredicate ( Comparison.LT, value ) ) );
					
					MenuItem le = new MenuItem ( "Less Than or Equal To " + value );
					le.setOnAction ( e -> filterProperty.set ( new OutcomePredicate ( Comparison.LE, value ) ) );
					
					MenuItem eq = new MenuItem ( "Equal To " + value );
					eq.setOnAction ( e -> filterProperty.set ( new OutcomePredicate ( Comparison.EQ, value ) ) );
					
					MenuItem gt = new MenuItem ( "Greater Than " + value );
					gt.setOnAction ( e -> filterProperty.set ( new OutcomePredicate ( Comparison.GT, value ) ) );
					
					MenuItem ge = new MenuItem ( "Greater Than or Equal To " + value );
					ge.setOnAction ( e -> filterProperty.set ( new OutcomePredicate ( Comparison.GE, value ) ) );
					
					menu.getItems ().addAll ( lt, le, eq, ge, gt );
					
					menu.show ( cell, event.getScreenX (), event.getScreenY () );

				} 
				catch ( NumberFormatException e )
				{
					// If Outcome is not an Integer, then create a String comparison Predicate
					filterProperty.set (  new OutcomePredicate ( cell.getText () ) );
				}
			}
		};
	}
	
	private enum Comparison
	{
		LT, LE, EQ, GE, GT;
	}
	
	private class OutcomePredicate implements Predicate<HistoryEntry>
	{
		private String targetString;
		private Comparison comparison;
		private int targetValue;
		
		public OutcomePredicate ( String outcomeString )
		{
			targetString = outcomeString;
		}
		
		public OutcomePredicate ( Comparison comparison, int outcomeValue )
		{
			this.comparison = comparison;
			targetValue = outcomeValue;
		}

		@Override
		public boolean test ( HistoryEntry entry )
		{
			if ( targetString != null )
				return entry.getOutcome ().equals ( targetString );
						
			try
			{
				int outcome = Integer.valueOf ( entry.getOutcome () );
				
				switch ( comparison )
				{
					case EQ: return outcome == targetValue;
					case GE: return outcome >= targetValue;
					case GT: return outcome >  targetValue;
					case LE: return outcome <= targetValue;
					case LT: return outcome <  targetValue;
				}
			}
			catch ( NumberFormatException e ) 
			{}
			
			return false;
		}
	}
	
	private Callback<TableColumn<HistoryEntry,String>,TableCell<HistoryEntry,String>> triggerCellFactory()
	{
		return col -> 
		{
			TableCell<HistoryEntry,String> cell = new TableCell<>();

			cell.textProperty ().bind ( cell.itemProperty () );

			cell.setOnMouseClicked ( triggerMouseClickEventHandler ( cell ) );

			return cell;
		};
	}
	
	private EventHandler<? super MouseEvent> triggerMouseClickEventHandler ( final TableCell<HistoryEntry,String> cell )
	{
		return event ->
		{
			if ( event.getButton () == MouseButton.SECONDARY )
			{
				if ( filterProperty.get () != null )
				{
					filterProperty.set ( null );
					return;
				}
								
				if ( cell.getText ().isEmpty () )
					return;
				
				String[] triggers = cell.getText ().split ( "\\s*,\\s*" );
				
				if ( triggers.length == 1 )
				{
					filterProperty.set ( entry -> entry.getTriggers ().contains ( triggers[0].trim() ) );
					return;
				}
				
				ContextMenu menu = new ContextMenu();
					
				for ( String trigger : triggers )
				{
					MenuItem pickTrigger = new MenuItem ( trigger.trim() );
					pickTrigger.setOnAction ( e -> filterProperty.set ( entry -> entry.getTriggers ().contains ( trigger.trim () ) ) );
					
					menu.getItems ().add ( pickTrigger );
				}
					
				menu.show ( cell, event.getScreenX (), event.getScreenY () );
			}
		};
	}
}
