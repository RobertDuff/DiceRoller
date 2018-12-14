package duffrd.diceroller.view;

import java.net.URL;
import java.util.ResourceBundle;

import duffrd.diceroller.model.Roller;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class RollerListPaneController implements Initializable
{
	@FXML
	public ListView<Roller> rollerList;

	private ObjectProperty<Roller> rollerProperty;

	public RollerListPaneController ( ObjectProperty<Roller> rollerProperty )
	{
		this.rollerProperty = rollerProperty;
	}

	@Override
	public void initialize ( URL location, ResourceBundle resources )
	{
		Callback<ListView<Roller>,ListCell<Roller>> cellFactory = new Callback<ListView<Roller>,ListCell<Roller>>()
		{
			@Override
			public ListCell<Roller> call ( ListView<Roller> param )
			{
				ListCell<Roller> cell = new ListCell<Roller>()
				{
					@Override
					protected void updateItem ( Roller roller, boolean empty )
					{
						super.updateItem ( roller, empty );

						if ( empty || roller == null )
						{
							setText ( null );
							setGraphic ( null );
							return;
						}

						setText ( roller.name () );

						Tooltip tip = new Tooltip ();
						tip.setText ( roller.definition () );

						setTooltip ( tip );

						setOnMouseClicked ( new EventHandler<MouseEvent>()
						{
							@Override
							public void handle ( MouseEvent event )
							{
								if ( event.getButton () == MouseButton.PRIMARY )
								{
									rollerProperty.set ( roller );
									
									if ( event.getClickCount () == 2 )
										getItem ().roll ();
								}
							}
						} );
					}
				};

				return cell;
			}			
		};

		rollerList.setCellFactory ( cellFactory );
	}

	public void addRollers ( Roller... rollers )
	{
		rollerList.getItems ().addAll ( rollers );
	}
}
