package gradefx.view.pane;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import kafx.controller.Controller;
import kafx.lang.Translator;

public class WelcomePane extends BorderPane {

	public WelcomePane() {
		Label label = new Label(Translator.get("empty_groups_message"));
		Controller.fontSizeProperty()
				.subscribe(fontSize -> label.setStyle("-fx-font-size: " + (fontSize.doubleValue() * 1.5)));
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.CENTER);
		this.setCenter(label);
	}

}
