package duffrd.diceroller.view;

import java.net.URL;
import java.util.ResourceBundle;

import duffrd.diceroller.model.History;
import duffrd.diceroller.model.HistoryEntry;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ListChangeListener;
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

	/**
	 * This animation is used to indicate that the value of a the outcome has changed, in case the new value is the same as the old value.
	 */
	private Animation animation;

	public void setProperties ( ReadOnlyStringProperty outcomeProperty, ReadOnlyStringProperty triggerProperty )
	{
		if ( outcomeProperty == null )
		{
			outcomeLabel.textProperty ().unbind ();
			outcomeLabel.setText ( "" );
		}
		else
			outcomeLabel.textProperty ().bind ( outcomeProperty );
		
		if ( triggerProperty == null )
		{
			triggerLabel.textProperty ().unbind ();
			triggerLabel.setText ( "" );
		}
		else
			triggerLabel.textProperty ().bind ( triggerProperty );
	}

	@Override
	public void initialize ( URL location, ResourceBundle resources )
	{
		buildChangeAnimation();
	}

	private void buildChangeAnimation()
	{
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

		//
		// It would be best to trigger the animation on the setting of the outcome property, but since the 
		// value of the outcome could be the same as the previous roll, the StringProperty won't trigger if the same value
		// is set again.  So we trigger on adding an entry to the history.
		//
		History.history ().historyProperty ().addListener ( ( ListChangeListener.Change<? extends HistoryEntry> c ) ->
		{
			c.next ();
			
			if ( c.wasAdded () )
			{
				animation.stop ();
				animation.play ();
			}
		} );
	}
}
