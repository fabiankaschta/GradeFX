package gradefx.view.alert;

import java.io.File;

import gradefx.io.Config;
import gradefx.io.Config.ConfigOption;
import gradefx.io.FileOutput;
import kafx.lang.Translator;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

public class AlertSaveChanges extends Alert {

	public AlertSaveChanges(Event e) {
		this(e, () -> {
		});
	}

	public AlertSaveChanges(Event e, Runnable onSuccess) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(null);
		this.setTitle(Translator.get("alert_saveChanges_title"));
		this.setContentText(Translator.get("alert_saveChanges_main"));
		this.getDialogPane().getButtonTypes().addAll(ButtonType.NO);
		this.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				if (Config.exists(ConfigOption.LAST_FILE)) {
					FileOutput.writeToFile(Config.get(ConfigOption.LAST_FILE));
					onSuccess.run();
				} else {
					File file = new FileChooser().showSaveDialog(this.getOwner());
					if (file != null) {
						FileOutput.writeToFile(file);
						onSuccess.run();
					} else {
						e.consume();
					}
				}
			} else if (response == ButtonType.CANCEL) {
				e.consume();
			} else if (response == ButtonType.NO) {
				onSuccess.run();
			}
		});
	}

}
