package duffrd.diceroller.view;

import duffrd.diceroller.model.DiceRollerException;
import duffrd.diceroller.model.ProbablityCalculationCancelledException;
import duffrd.diceroller.model.Roller;
import duffrd.diceroller.model.RollerBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.converter.DefaultStringConverter;

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
            builder.nameProperty().bind ( nameField.textProperty () );
            nameField.requestFocus ();
            AnchorPane.setTopAnchor ( nameField, 30.0 );
            AnchorPane.setLeftAnchor ( nameField, 150.0 );
            AnchorPane.setRightAnchor ( nameField, 30.0 );
            
            TextField definitionField = new TextField();
         
            builder.definitionProperty().bind ( definitionField.textProperty () );
            AnchorPane.setTopAnchor ( definitionField, 70.0 );
            AnchorPane.setLeftAnchor ( definitionField, 150.0 );
            AnchorPane.setRightAnchor ( definitionField, 30.0 );
            
            BooleanProperty validProperty = new SimpleBooleanProperty ();
            validProperty.bind ( Bindings.createBooleanBinding ( () -> builder.isDefinitionValid ( definitionField.textProperty ().get() ), definitionField.textProperty () ) );

            validProperty.addListener ( ( a, o, n ) -> 
            {
                if ( n )
                    definitionField.setStyle ( "-fx-control-inner-background: white" );
                else
                    definitionField.setStyle ( "-fx-control-inner-background: pink" );
            } );
            
            BooleanProperty completeProperty = new SimpleBooleanProperty ();
            completeProperty.bind ( validProperty.not ().or ( Bindings.equal ( builder.nameProperty(), "" ) ).or ( Bindings.equal ( builder.definitionProperty(), "" ) ) );
            
            Button nextButton = ( Button ) lookupButton ( ButtonType.NEXT );
            nextButton.disableProperty ().bind ( completeProperty );
            
            Button finishButton = ( Button ) lookupButton ( ButtonType.FINISH );
            finishButton.disableProperty ().bind ( completeProperty );
            
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
                builder.labelMap ().put ( event.getRowValue ().outcome, event.getNewValue () );
            });

            labelTable.getColumns ().add ( outcomeColumn );
            labelTable.getColumns ().add ( labelColumn );
            
            AnchorPane.setLeftAnchor ( labelTable, 30.0 );
            AnchorPane.setRightAnchor ( labelTable, 30.0 );
            AnchorPane.setTopAnchor ( labelTable, 30.0 );
            AnchorPane.setBottomAnchor ( labelTable, 30.0 );
            
            pane.getChildren ().add ( labelTable );
            
            setContent ( pane );

            builder.labelMap ().addListener ( ( MapChangeListener<Integer,String> ) change ->
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
        protected class Entry
        {
            public String triggerName;
            public String definition;
            
            public Entry ( String name, String definition )
            {
                this.triggerName = name;
                this.definition = definition;
            }

            @Override
            public int hashCode ()
            {
                final int prime = 31;
                int result = 1;
                result = prime * result + getOuterType ().hashCode ();
                result = prime * result + ( ( triggerName == null )? 0 : triggerName.hashCode () );
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
                if ( triggerName == null )
                {
                    if ( other.triggerName != null ) return false;
                }
                else if ( !triggerName.equals ( other.triggerName ) ) return false;
                return true;
            }

            private TriggersPane getOuterType ()
            {
                return TriggersPane.this;
            }
        }
        
        TriggersPane()
        {            
            setHeaderText ( "Create Triggers" );
            getButtonTypes ().addAll ( ButtonType.PREVIOUS, ButtonType.CANCEL, ButtonType.FINISH );

            AnchorPane pane = new AnchorPane();
            VBox vbox = new VBox();
            AnchorPane.setLeftAnchor ( vbox, 30.0 );
            AnchorPane.setRightAnchor ( vbox, 30.0 );
            AnchorPane.setTopAnchor ( vbox, 30.0 );
            AnchorPane.setBottomAnchor ( vbox, 30.0 );
            
            pane.getChildren ().add ( vbox );
            
            Button newTriggerButton = new Button();
            newTriggerButton.setText ( "Create New Trigger" );
            
            TableView<Entry> triggerTable = new TableView<>();     
            triggerTable.setEditable ( true );

            newTriggerButton.setOnAction ( event -> builder.triggerMap ().put ( "New Trigger", "" ) );
            
            TableColumn<Entry,String> triggerNameColumn = new TableColumn<>();
            triggerNameColumn.setText ( "Trigger Name" );
            triggerNameColumn.setPrefWidth ( 120 );
            triggerNameColumn.setCellValueFactory ( cd -> Bindings.createStringBinding ( () -> cd.getValue ().triggerName ) );
            triggerNameColumn.setCellFactory ( TextFieldTableCell.forTableColumn() );
            triggerNameColumn.setOnEditCommit ( event -> 
            {
                builder.triggerMap ().put ( event.getNewValue (), "" );
                builder.triggerMap ().remove ( event.getOldValue () );
            });
            
            TableColumn<Entry,String> definitionColumn = new TableColumn<>();
            definitionColumn.setText ( "Definition" );
            definitionColumn.setPrefWidth ( 200 );
            definitionColumn.setCellValueFactory ( cd -> Bindings.createStringBinding ( () -> cd.getValue ().definition ) );
            definitionColumn.setCellFactory ( cb ->
            {
              return new TextFieldTableCell<Entry,String>( new DefaultStringConverter () )
              {
                @Override
                public void updateItem ( String item, boolean empty )
                {
                    super.updateItem ( item, empty );
                    
                    if ( empty || item == null )
                    {
                        setText ( null );
                    }
                    else
                    {
                        setText ( item );
                        
                        if ( builder.isDefinitionValid ( item ) )
                        {
                            System.out.println ( "Valid: " + this.getStyle () );
                            this.setStyle ( "" );
                        }
                        else
                        {
                            System.out.println ( "Invalid" );
                            this.setStyle ( "-fx-background-color: pink" );                        
                        }
                    }
                }                  
              };  
            } );
            
            definitionColumn.setOnEditCommit ( event -> 
            {
                builder.triggerMap ().put ( event.getRowValue ().triggerName, event.getNewValue () );
            } );

            triggerTable.getColumns ().add ( triggerNameColumn );
            triggerTable.getColumns ().add ( definitionColumn );
            
            vbox.getChildren ().addAll ( newTriggerButton, triggerTable );
            
            setContent ( pane );

            builder.triggerMap ().addListener ( ( MapChangeListener<String,String> ) change ->
            {
                boolean removed = change.wasRemoved ();
                boolean added = change.wasAdded ();

                if ( removed && added )
                {
                    Entry entry = new Entry ( change.getKey (), change.getValueAdded () );
                    triggerTable.getItems ().set ( triggerTable.getItems ().indexOf ( entry ), entry );
                }
                else if ( added )
                {
                    triggerTable.getItems ().add ( new Entry ( change.getKey (), change.getValueAdded () ) );
                }
                else if ( removed )
                {
                    triggerTable.getItems ().remove ( new Entry ( change.getKey (), change.getValueRemoved () ) );
                }
            } );
        }
    }

    RollerBuilder builder;
    private DefinitionPane definitionPane;
    private LabelsPane labelsPane;
    private TriggersPane triggersPane;
    
    public RollerWizard ( String group )
    {
        setTitle ( "Create New Roller" );
        
        builder = new RollerBuilder ( group );
        
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
                probabilities = builder.build ().probabilities ();
            }
            catch ( ProbablityCalculationCancelledException | DiceRollerException e )
            {
                e.printStackTrace();
            }
            
            builder.labelMap ().clear ();
            
            for ( int outcome = 0; outcome < probabilities.length; outcome ++ )
                if ( probabilities[ outcome ] > 0 )
                    builder.labelMap ().put ( outcome, "" );
        } );
        
        labelsPane.lookupButton ( ButtonType.PREVIOUS ).addEventFilter ( ActionEvent.ACTION, event -> 
        { 
            event.consume (); 
            dialogPaneProperty ().set ( definitionPane );
            
            builder.labelMap ().clear ();
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
        try
        {
            return builder.build ();
        }
        catch ( DiceRollerException e )
        {
            e.printStackTrace();
            return null;
        }
    }
}
