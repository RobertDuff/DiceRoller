package duffrd.diceroller.view;

import java.net.URL;
import java.util.ResourceBundle;

import duffrd.diceroller.model.ProbablityCalculationCancelledException;
import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.Suite;
import duffrd.diceroller.model.Trigger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import utility.javafx.Wizard;
import utility.javafx.WizardPage;

public class RollerWizard extends Wizard<Roller>
{
    protected class DefinitionPane extends WizardPage<Roller>
    {
        public DefinitionPane ()
        {
            super();
            initialize ( null, null );
        }
        
        @Override
        public void initialize ( URL location, ResourceBundle resources )
        {
            setHeaderText ( "Enter Roller Name and Definition" );
            getButtonTypes ().addAll ( ButtonType.NEXT, ButtonType.CANCEL, ButtonType.FINISH );
            
            GridPane pane = new GridPane();
            
            pane.setHgap ( 30 );
            pane.setVgap ( 10 );
            
            Label nameLabel = new Label();
            nameLabel.setText ( "Name:" );
            GridPane.setConstraints ( nameLabel, 0, 0 );
            
            Label definitionLabel = new Label ();
            definitionLabel.setText ( "Definition:" );
            GridPane.setConstraints ( definitionLabel, 0, 1 );
            
            TextField nameField = new TextField();
            nameField.requestFocus ();
            GridPane.setConstraints ( nameField, 1, 0 );
            GridPane.setHgrow ( nameField, Priority.ALWAYS );
            
            TextField definitionField = new TextField();
            GridPane.setConstraints ( definitionField, 1, 1 );
            GridPane.setHgrow ( definitionField, Priority.ALWAYS );
            
            BooleanProperty completeProperty = new SimpleBooleanProperty ();
            
            canMoveForwardProperty.bind ( completeProperty );
            canFinishProperty.bind ( completeProperty );
            
            pane.getChildren ().addAll ( nameLabel, definitionLabel, nameField, definitionField );
            
            setContent ( pane );
            
            productProperty.addListener ( ( a, o, roller ) ->
            {
                roller.nameProperty ().bind ( nameField.textProperty () );
                roller.definitionProperty ().bind ( definitionField.textProperty () );

                definitionField.styleProperty ().bind ( Bindings.when ( roller.validProperty () ).then ( "" ).otherwise ( "-fx-control-inner-background: pink" ) );
                
                completeProperty.bind ( Bindings.createBooleanBinding ( () -> roller.isValid () && !roller.name ().isEmpty (), 
                        roller.validProperty(), roller.nameProperty(), roller.definitionProperty() ) );
            } );
        }
    }
    
    protected class LabelsPane extends WizardPage<Roller>
    {        
        protected class LabelDef
        {
            public Integer outcome;
            public String label;
            
            public LabelDef ( int outcome, String label )
            {
                this.outcome = outcome;
                this.label = label;
            }
        }
        
        private ObservableList<LabelDef> tableItems;
        private BooleanProperty editInProgressProperty = new SimpleBooleanProperty ();
        
        public LabelsPane ()
        {
            super();
            initialize ( null, null );
        }
        
        @Override
        public void initialize ( URL location, ResourceBundle resources )
        {            
            setHeaderText ( "Add Labels to Roller Outcomes" );
            getButtonTypes ().addAll ( ButtonType.PREVIOUS, ButtonType.CANCEL, ButtonType.FINISH );

            editInProgressProperty.set ( false );
            
            canMoveBackwardProperty.bind ( editInProgressProperty.not () );
            canFinishProperty.bind ( editInProgressProperty.not () );
            
            AnchorPane pane = new AnchorPane();
            
            TableView<LabelDef> labelTable = new TableView<>();    
            labelTable.setEditable ( true );
            
            tableItems = labelTable.getItems ();
            
            TableColumn<LabelDef,Number> outcomeColumn = new TableColumn<>();
            outcomeColumn.setText ( "Outcome" );
            outcomeColumn.setPrefWidth ( 90 );
            outcomeColumn.setCellValueFactory ( cd -> Bindings.createIntegerBinding ( () -> cd.getValue ().outcome ) );
            
            TableColumn<LabelDef,String> labelColumn = new TableColumn<>();
            labelColumn.setText ( "Label" );
            labelColumn.setPrefWidth ( 200 );
            labelColumn.setCellValueFactory ( cd -> Bindings.createStringBinding ( () -> cd.getValue ().label ) );
            labelColumn.setCellFactory ( TextFieldTableCell.forTableColumn() );
            
            labelColumn.setOnEditStart ( event -> editInProgressProperty.set ( true ) );
            labelColumn.setOnEditCancel ( event -> editInProgressProperty.set ( false ) );
            labelColumn.setOnEditCommit ( event -> 
            {
                editInProgressProperty.set ( false );
                
                if ( event.getNewValue () == null || event.getNewValue ().isEmpty () )
                    productProperty.get ().labels ().remove ( event.getRowValue().outcome );
                else
                    productProperty.get ().labels().put ( event.getRowValue ().outcome, event.getNewValue () );
            });

            labelTable.getColumns ().add ( outcomeColumn );
            labelTable.getColumns ().add ( labelColumn );
            
            AnchorPane.setLeftAnchor ( labelTable, 30.0 );
            AnchorPane.setRightAnchor ( labelTable, 30.0 );
            AnchorPane.setTopAnchor ( labelTable, 30.0 );
            AnchorPane.setBottomAnchor ( labelTable, 30.0 );
            
            pane.getChildren ().add ( labelTable );
            
            setContent ( pane );

            onEnterForwardProperty.set ( () -> 
            {                
                try
                {
                    productProperty.get ().calculateProbabilities ( true );

                    long[] probabilities = productProperty.get ().rawProbabilities ();

                    tableItems.clear ();
                    
                    for ( int outcome = 0; outcome < probabilities.length; outcome ++ )
                        if ( probabilities[ outcome ] > 0 )
                            tableItems.add ( new LabelDef ( outcome, "" ) );
                }
                catch ( ProbablityCalculationCancelledException e )
                {
                }
            } );

            onExitBackwardProperty.set ( () -> productProperty.get ().labels ().clear () );
        }
    }
    
    protected class TriggersPane extends WizardPage<Roller>
    {                
        public TriggersPane ()
        {
            super();
            initialize ( null, null );
        }
        
        @Override
        public void initialize ( URL location, ResourceBundle resources )
        {            
            setHeaderText ( "Assign Triggers" );
            getButtonTypes ().addAll ( ButtonType.PREVIOUS, ButtonType.NEXT, ButtonType.CANCEL, ButtonType.FINISH );

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
                        productProperty.get ().triggers ().removeAll ( change.getRemoved () );
                    
                    if ( change.wasAdded () )
                        productProperty.get ().triggers ().addAll ( change.getAddedSubList () );
                }
            } );
            
            setContent ( pane );
            
            onEnterForwardProperty.set ( () -> triggerList.getSelectionModel ().clearSelection () );
            onExitBackwardProperty.set ( () -> productProperty.get ().triggers ().clear () );
        }
    }

    private Suite suite;
    
    public RollerWizard ( Suite suite )
    {        
        this.suite = suite;
        productProperty ().set ( new Roller().lua ( suite.lua () ) );
        
        setTitle ( "Create New Roller" );

        initModality ( Modality.WINDOW_MODAL );
        
        setWidth ( 600 );
        setHeight ( 500 );
        setResizable ( true );
        
        simpleSequence ( new DefinitionPane(), new TriggersPane(), new LabelsPane() );
    }
}
