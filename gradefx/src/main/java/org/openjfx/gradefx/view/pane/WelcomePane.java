package org.openjfx.gradefx.view.pane;

import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;

public class WelcomePane extends BorderPane {

	public WelcomePane() {
		Label label = new Label(TranslationController.translate("empty_groups_message"));
		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> label.setStyle("-fx-font-size: " + (fontSize.doubleValue() * 1.5)));
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.CENTER);
		this.setCenter(label);
	}

}
