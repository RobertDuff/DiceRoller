package duffrd.diceroller.view;

import java.net.URL;
import java.util.ResourceBundle;

import duffrd.diceroller.model.Outcome;
import duffrd.diceroller.model.Roller;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class OutcomePaneController implements Initializable
{
	@FXML
	public AnchorPane outcomePane;

	@FXML
	public Label outcomeLabel;

	@FXML
	public Label triggerLabel;

	private ObjectProperty<Outcome> outcomeProperty = new SimpleObjectProperty<> ();
	
	/**
	 * This animation is used to indicate that the value of a the outcome has changed, in case the new value is the same as the old value.
	 */
	private Animation animation;

	public void bind ( Roller roller )
	{
		if ( roller == null )
		{
		    outcomeProperty.unbind ();
		    outcomeProperty.set ( null );
		}
		else
		{
		    outcomeProperty.bind ( roller.outcomeProperty () );
		}
	}

	@Override
	public void initialize ( URL location, ResourceBundle resources )
	{	    
	    outcomeProperty.addListener ( ( a, o, n ) -> 
	    {
	        if ( n == null )
	        {
	            outcomeLabel.setText ( "" );
	            triggerLabel.setText ( "" );
	        }
	        else
	        {
	            outcomeLabel.setText ( n.outcome () );
	            triggerLabel.setText ( n.triggers () );
	            animation.stop ();
	            animation.play ();
	        }
	    } );
	    
		Timeline timeline = new Timeline();

		//
		// Outcome Label 
		//
		
		// Jump the Outcome Label to the right instantly.
		KeyValue outcomeJumpValue = new KeyValue ( outcomeLabel.translateXProperty (), outcomeLabel.translateXProperty ().get () + 100.0 );
		KeyValue triggerJumpValue = new KeyValue ( triggerLabel.translateYProperty (), triggerLabel.translateYProperty ().get () +  20.0 );
		
		KeyFrame jumpFrame = new KeyFrame ( Duration.ZERO, outcomeJumpValue, triggerJumpValue );

		// Slide the Outcome Label back into position
		KeyValue outcomeSlideValue = new KeyValue ( outcomeLabel.translateXProperty (), outcomeLabel.getTranslateX () );
		KeyValue triggerSlideValue = new KeyValue ( triggerLabel.translateYProperty (), triggerLabel.getTranslateY () );
		
		KeyFrame slideFrame = new KeyFrame ( Duration.seconds ( .5 ), outcomeSlideValue, triggerSlideValue );

		timeline.getKeyFrames ().addAll ( jumpFrame, slideFrame );
		timeline.setCycleCount ( 1 );
		timeline.setAutoReverse ( false );

		animation = timeline;
	}
}
