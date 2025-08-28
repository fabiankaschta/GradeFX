package gradefx.view.alert;

import kafx.lang.Translator;
import javafx.scene.control.Alert;

public class AlertDelete extends Alert {

	public AlertDelete(String toBeDeleted) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(toBeDeleted);
		this.setTitle(Translator.get("alert_delete_title"));
		this.setContentText(Translator.get("alert_delete_main"));
	}

}
