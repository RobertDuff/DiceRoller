package duffrd.diceroller.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import duffrd.diceroller.model.Model;
import duffrd.diceroller.model.Suite;
import duffrd.diceroller.model.SuiteInitializer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

public class NewSuiteDialog extends Dialog<ButtonType> implements Initializable
{
    private static final SuiteInitializer suiteInitializer = new SuiteInitializer ();
    
    private Model model;
    
    @FXML
    public TextField nameTextField;
    
    @FXML
    public ListView<String> templateListView;
    
    public NewSuiteDialog ( Model model ) throws IOException
    {
        this.model = model;        
        
        setTitle ( "Create New Suite" );
        setResizable ( true );
        getDialogPane().setPrefWidth ( 400 );
        
        FXMLLoader loader = new FXMLLoader ( getClass().getResource ( "NewSuitePanel.fxml" ) );
        loader.setController ( this );
        
        getDialogPane ().setContent ( loader.load () );
        getDialogPane ().getButtonTypes ().addAll ( ButtonType.CANCEL, ButtonType.OK );
    }
    
    @Override
    public void initialize ( URL location, ResourceBundle resources )
    {
        templateListView.getItems ().addAll ( suiteInitializer.suiteTemplates () );
        templateListView.getSelectionModel ().setSelectionMode ( SelectionMode.SINGLE );
        
        nameTextField.selectAll ();
    }

    public Suite suite()
    {
        Suite suite = model.newSuite ();            
        suite.name ( nameTextField.getText () );
        
        String template = templateListView.getSelectionModel ().getSelectedItem ();
        
        if ( template != null )
            suiteInitializer.apply ( suite, template );
        
        model.suites ().add ( suite );
        
        return suite;
    }
}
