package duffrd.diceroller.view;

import duffrd.diceroller.model.ProbablityCalculationCancelledException;
import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.Suite;
import duffrd.diceroller.model.Trigger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

public class RollerWizard extends Dialog<ButtonType>
{
    protected class DefinitionPane extends DialogPane
    {
        DefinitionPane()
        {
            super();
            
            setHeaderText ( "Enter Roller Name and Definition" );
            getButtonTypes ().addAll ( ButtonType.NEXT, ButtonType.CANCEL, ButtonType.FINISH );
            
            AnchorPane pane = new AnchorPane ();
            
            Label nameLabel = new Label();
            nameLabel.setText ( "Name:" );
            AnchorPane.setLeftAnchor ( nameLabel, 30.0 );
            AnchorPane.setTopAnchor ( nameLabel, 30.0 );
            
            Label definitionLabel = new Label ();
            definitionLabel.setText ( "Definition:" );
            AnchorPane.setTopAnchor ( definitionLabel, 70.0 );
            AnchorPane.setLeftAnchor ( definitionLabel, 30.0 );
            
            TextField nameField = new TextField();
            roller.nameProperty ().bind ( nameField.textProperty () );
            nameField.requestFocus ();
            AnchorPane.setTopAnchor ( nameField, 30.0 );
            AnchorPane.setLeftAnchor ( nameField, 150.0 );
            AnchorPane.setRightAnchor ( nameField, 30.0 );
            
            TextField definitionField = new TextField();
         
            roller.definitionProperty().bind ( definitionField.textProperty () );
            AnchorPane.setTopAnchor ( definitionField, 70.0 );
            AnchorPane.setLeftAnchor ( definitionField, 150.0 );
            AnchorPane.setRightAnchor ( definitionField, 30.0 );
            
            definitionField.styleProperty ().bind ( Bindings.when ( roller.validProperty () ).then ( "" ).otherwise ( "-fx-control-inner-background: pink" ) );
            
            BooleanBinding completeBinding = Bindings.createBooleanBinding ( () -> !roller.isValid () || roller.name ().isEmpty (), 
                    roller.validProperty(), roller.nameProperty(), roller.definitionProperty() );
            
            Button nextButton = ( Button ) lookupButton ( ButtonType.NEXT );
            nextButton.disableProperty ().bind ( completeBinding );
            
            Button finishButton = ( Button ) lookupButton ( ButtonType.FINISH );
            finishButton.disableProperty ().bind ( completeBinding );
            
            pane.getChildren ().addAll ( nameLabel, definitionLabel, nameField, definitionField );
            
            setContent ( pane );
        }
    }
    
    protected class LabelsPane extends DialogPane
    {        
        protected class Entry
        {
            public Integer outcome;
            public String label;
            
            public Entry ( int outcome, String label )
            {
                this.outcome = outcome;
                this.label = label;
            }

            @Override
            public int hashCode ()
            {
                final int prime = 31;
                int result = 1;
                result = prime * result + getOuterType ().hashCode ();
                result = prime * result + ( ( outcome == null )? 0 : outcome.hashCode () );
                return result;
            }

            @Override
            public boolean equals ( Object obj )
            {
                if ( this == obj ) return true;
                if ( obj == null ) return false;
                if ( getClass () != obj.getClass () ) return false;
                Entry other = ( Entry ) obj;
                if ( !getOuterType ().equals ( other.getOuterType () ) ) return false;
                if ( outcome == null )
                {
                    if ( other.outcome != null ) return false;
                }
                else if ( !outcome.equals ( other.outcome ) ) return false;
                return true;
            }

            private LabelsPane getOuterType ()
            {
                return LabelsPane.this;
            }
        }
        
        LabelsPane()
        {            
            setHeaderText ( "Add Labels to Roller Outcomes" );
            getButtonTypes ().addAll ( ButtonType.PREVIOUS, ButtonType.NEXT, ButtonType.CANCEL, ButtonType.FINISH );

            AnchorPane pane = new AnchorPane();
            
            TableView<Entry> labelTable = new TableView<>();     
            labelTable.setEditable ( true );
            
            TableColumn<Entry,Number> outcomeColumn = new TableColumn<>();
            outcomeColumn.setText ( "Outcome" );
            outcomeColumn.setPrefWidth ( 90 );
            outcomeColumn.setCellValueFactory ( cd -> Bindings.createIntegerBinding ( () -> cd.getValue ().outcome ) );
            
            TableColumn<Entry,String> labelColumn = new TableColumn<>();
            labelColumn.setText ( "Label" );
            labelColumn.setPrefWidth ( 200 );
            labelColumn.setCellValueFactory ( cd -> Bindings.createStringBinding ( () -> cd.getValue ().label ) );
            labelColumn.setCellFactory ( TextFieldTableCell.forTableColumn() );
            labelColumn.setOnEditCommit ( event -> 
            {
                roller.labels().put ( event.getRowValue ().outcome, event.getNewValue () );
            });

            labelTable.getColumns ().add ( outcomeColumn );
            labelTable.getColumns ().add ( labelColumn );
            
            AnchorPane.setLeftAnchor ( labelTable, 30.0 );
            AnchorPane.setRightAnchor ( labelTable, 30.0 );
            AnchorPane.setTopAnchor ( labelTable, 30.0 );
            AnchorPane.setBottomAnchor ( labelTable, 30.0 );
            
            pane.getChildren ().add ( labelTable );
            
            setContent ( pane );

            roller.labelsProperty().addListener ( ( MapChangeListener<Integer,String> ) change ->
            {
                boolean removed = change.wasRemoved ();
                boolean added = change.wasAdded ();

                if ( removed && added )
                {
                    Entry entry = new Entry ( change.getKey (), change.getValueAdded () );
                    labelTable.getItems ().set ( labelTable.getItems ().indexOf ( entry ), entry );
                }
                else if ( added )
                {
                    labelTable.getItems ().add ( new Entry ( change.getKey (), change.getValueAdded () ) );
                }
                else if ( removed )
                {
                    labelTable.getItems ().remove ( new Entry ( change.getKey (), change.getValueRemoved () ) );
                }
            } );
        }
    }
    
    protected class TriggersPane extends DialogPane
    {                
        TriggersPane()
        {            
            setHeaderText ( "Create Triggers" );
            getButtonTypes ().addAll ( ButtonType.PREVIOUS, ButtonType.CANCEL, ButtonType.FINISH );

            AnchorPane pane = new AnchorPane();
            
            ListView<Trigger> triggerList = new ListView<>();
            
            triggerList.setCellFactory ( cb -> 
            {
              return new ListCell<Trigger> ()
              {

                @Override
                protected void updateItem ( Trigger trigger, boolean empty )
                {
                    super.updateItem ( trigger, empty );
                    
                    if ( empty )
                    {
                        setText ( null );
                        setGraphic ( null );
                        return;
                    }
                    
                    setText ( trigger.name () );
                }
                  
              };
            } );
            
            AnchorPane.setLeftAnchor ( triggerList, 30.0 );
            AnchorPane.setRightAnchor ( triggerList, 30.0 );
            AnchorPane.setTopAnchor ( triggerList, 30.0 );
            AnchorPane.setBottomAnchor ( triggerList, 30.0 );
            
            pane.getChildren ().add ( triggerList );

            triggerList.getItems ().addAll ( suite.triggers () );
            triggerList.getSelectionModel ().setSelectionMode ( SelectionMode.MULTIPLE );
            
            triggerList.getSelectionModel ().getSelectedItems ().addListener ( ( ListChangeListener.Change<? extends Trigger> change ) -> 
            {
                while ( change.next () )
                {
                    if ( change.wasRemoved () )
                        roller.triggers ().removeAll ( change.getRemoved () );
                    
                    if ( change.wasAdded () )
                        roller.triggers ().addAll ( change.getAddedSubList () );
                }
            } );
            
            setContent ( pane );
        }
    }

    private Suite suite;
    private Roller roller;
    private DefinitionPane definitionPane;
    private LabelsPane labelsPane;
    private TriggersPane triggersPane;
    
    public RollerWizard ( Suite suite )
    {
        setTitle ( "Create New Roller" );
        
        this.suite = suite;
        roller = new Roller().lua ( suite.lua () );
        
        definitionPane = new DefinitionPane();
        labelsPane = new LabelsPane();
        triggersPane = new TriggersPane();
        
        definitionPane.lookupButton ( ButtonType.NEXT ).addEventFilter ( ActionEvent.ACTION, event -> 
        { 
            event.consume (); 
            dialogPaneProperty ().set ( labelsPane );
            
            // Fill Builder's Label Map with the Full Range of the Roller.
            
            long[] probabilities = null;
            
            try
            {
                roller.calculateProbabilities ();
                probabilities = roller.rawProbabilities ();
            }
            catch ( ProbablityCalculationCancelledException e )
            {
                e.printStackTrace();
            }
            
            roller.labels ().clear ();
            
            for ( int outcome = 0; outcome < probabilities.length; outcome ++ )
                if ( probabilities[ outcome ] > 0 )
                    roller.labels().put ( outcome, "" );
        } );
        
        labelsPane.lookupButton ( ButtonType.PREVIOUS ).addEventFilter ( ActionEvent.ACTION, event -> 
        { 
            event.consume (); 
            dialogPaneProperty ().set ( definitionPane );
            
            roller.labels().clear ();
        } );
        
        labelsPane.lookupButton ( ButtonType.NEXT ).addEventFilter ( ActionEvent.ACTION, event -> { event.consume (); dialogPaneProperty ().set ( triggersPane ); } );

        triggersPane.lookupButton ( ButtonType.PREVIOUS ).addEventFilter ( ActionEvent.ACTION, event -> { event.consume (); dialogPaneProperty ().set ( labelsPane ); } );

        dialogPaneProperty ().set ( definitionPane );
        initModality ( Modality.WINDOW_MODAL );
        
        setWidth ( 600 );
        setHeight ( 500 );
        setResizable ( true );
    }
    
    public Roller roller()
    {
        return roller;
    }
}
