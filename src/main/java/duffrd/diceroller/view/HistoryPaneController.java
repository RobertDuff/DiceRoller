package duffrd.diceroller.view;

import java.net.URL;
import java.text.DateFormat;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import duffrd.diceroller.model.Outcome;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

public class HistoryPaneController implements Initializable, RollListener
{
    private static final DateFormat DATE_FORMAT = DateFormat.getTimeInstance ( DateFormat.MEDIUM );
    
	private ObjectProperty<Predicate<? super Outcome>> filterProperty;
	
	@FXML
	public TableView<Outcome> historyTable;
	
	@FXML
	public TableColumn<Outcome,String> historyTableTimeColumn;
	
	@FXML
	public TableColumn<Outcome,String> historyTableRollerNameColumn;
	
	@FXML
	public TableColumn<Outcome,String> historyTableOutcomeColumn;
	
	@FXML
	public TableColumn<Outcome,String> historyTableTriggersColumn;
	
	@FXML
	public TableColumn<Outcome,String> historyTableFacesColumn;
	
	private ListProperty<Outcome> history = new SimpleListProperty<> ( FXCollections.observableArrayList () );
	
	@Override
    public void roll ( Outcome outcome )
    {
        history.add ( outcome );
    }
    
	@Override
	public void initialize ( URL location, ResourceBundle resources )
	{	    
		FilteredList<Outcome> filteredList = new FilteredList<> ( history );
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
		
		SortedList<Outcome> sortedList = new SortedList<> ( filteredList );
		sortedList.comparatorProperty ().bind ( historyTable.comparatorProperty () );

		historyTable.setItems ( sortedList );
		
		//
		//
		//
		
		historyTableTimeColumn.setCellValueFactory ( cb -> 
		{
		    return new SimpleStringProperty ( DATE_FORMAT.format ( cb.getValue ().time () ) );
		} );
		
		historyTable.getSortOrder ().add ( historyTableTimeColumn );
		historyTableTimeColumn.setSortType ( SortType.DESCENDING );

		//
		//
		//
		
		historyTableRollerNameColumn.setCellFactory ( col -> 
        {
            TableCell<Outcome,String> cell = new TableCell<>();

            cell.textProperty ().bind ( cell.itemProperty () );

            cell.setOnMouseClicked ( event ->
            {
                if ( event.getButton () == MouseButton.SECONDARY )
                    if ( filterProperty.get () == null )
                    {
                        final String rollerName = cell.getText ();
                        filterProperty.set ( entry -> entry.roller ().equals ( rollerName ) );
                    }
                    else
                        filterProperty.set ( null );
            } );

            return cell;
        } );
		historyTableRollerNameColumn.setCellValueFactory ( new PropertyValueFactory<> ( "rollerName" ) );
		
		//
		//
		//
		
		historyTableOutcomeColumn.setCellFactory ( col -> 
        {
            TableCell<Outcome,String> cell = new TableCell<>();

            cell.textProperty ().bind ( cell.itemProperty () );

            cell.setOnMouseClicked ( event ->
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
            } );

            return cell;
        } );
		historyTableOutcomeColumn.setCellValueFactory ( new PropertyValueFactory<> ( "outcome" ) );
		
		//
		//
		//
		
		historyTableTriggersColumn.setCellFactory ( col -> 
        {
            TableCell<Outcome,String> cell = new TableCell<>();

            cell.textProperty ().bind ( cell.itemProperty () );

            cell.setOnMouseClicked ( event ->
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
                        filterProperty.set ( entry -> entry.triggers ().contains ( triggers[0].trim() ) );
                        return;
                    }
                    
                    ContextMenu menu = new ContextMenu();
                        
                    for ( String trigger : triggers )
                    {
                        MenuItem pickTrigger = new MenuItem ( trigger.trim() );
                        pickTrigger.setOnAction ( e -> filterProperty.set ( entry -> entry.triggers ().contains ( trigger.trim () ) ) );
                        
                        menu.getItems ().add ( pickTrigger );
                    }
                        
                    menu.show ( cell, event.getScreenX (), event.getScreenY () );
                }
            } );

            return cell;
        } );
		historyTableTriggersColumn.setCellValueFactory ( new PropertyValueFactory<> ( "triggers" ) );
		
		//
		//
		//
		
		historyTableFacesColumn.setCellValueFactory ( new PropertyValueFactory<> ( "faces" ) );
	}
	
	private enum Comparison
	{
		LT, LE, EQ, GE, GT;
	}
	
	private class OutcomePredicate implements Predicate<Outcome>
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
		public boolean test ( Outcome entry )
		{
			if ( targetString != null )
				return entry.outcome ().equals ( targetString );
						
			try
			{
				int outcome = Integer.valueOf ( entry.outcome () );
				
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
}
