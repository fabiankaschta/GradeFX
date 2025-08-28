package gradefx.view.pane;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class WelcomePane extends BorderPane {

	public WelcomePane() {
		// TODO make custom welcome page
		this.setCenter(new Label("hi"));
	}

}
