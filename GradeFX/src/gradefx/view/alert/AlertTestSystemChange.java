package gradefx.view.alert;

import kafx.controller.Controller;
import kafx.lang.Translator;
import gradefx.model.Group;
import javafx.scene.control.Alert;

public class AlertTestSystemChange extends Alert {

	public AlertTestSystemChange(Group group) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(group.getName());
		this.setTitle(Translator.get("alert_testSystemChange_title"));
		this.setContentText(Translator.get("alert_testSystemChange_main"));
		Controller.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
