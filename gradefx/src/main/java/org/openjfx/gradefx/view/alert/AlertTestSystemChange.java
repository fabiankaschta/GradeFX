package org.openjfx.gradefx.view.alert;

import org.openjfx.gradefx.model.Group;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Alert;

public class AlertTestSystemChange extends Alert {

	public AlertTestSystemChange(Group group) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(group.getName());
		this.setTitle(TranslationController.translate("alert_testSystemChange_title"));
		this.setContentText(TranslationController.translate("alert_testSystemChange_main"));
		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
