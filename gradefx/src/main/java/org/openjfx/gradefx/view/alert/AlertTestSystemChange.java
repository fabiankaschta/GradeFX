package org.openjfx.gradefx.view.alert;

import org.openjfx.gradefx.model.Group;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.alert.AlertCustom;

public class AlertTestSystemChange extends AlertCustom {

	public AlertTestSystemChange(Group group) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(group.getName());
		this.setTitle(TranslationController.translate("alert_testSystemChange_title"));
		this.setContentText(TranslationController.translate("alert_testSystemChange_main"));
	}

}
