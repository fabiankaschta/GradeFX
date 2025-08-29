package org.openjfx.gradefx.view.alert;

import org.openjfx.gradefx.model.Group;
import org.openjfx.kafx.controller.Controller;

import javafx.scene.control.Alert;

public class AlertTestSystemChange extends Alert {

	public AlertTestSystemChange(Group group) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(group.getName());
		this.setTitle(Controller.translate("alert_testSystemChange_title"));
		this.setContentText(Controller.translate("alert_testSystemChange_main"));
		Controller.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
