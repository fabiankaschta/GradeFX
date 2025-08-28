package gradefx.view.alert;

import java.io.PrintWriter;
import java.io.StringWriter;

import gradefx.io.Config;
import kafx.lang.Translator;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class AlertException extends Alert {

	public AlertException(Exception e) {
		super(AlertType.ERROR);
		this.setGraphic(null);
		this.setHeaderText(e.toString());
		this.setTitle(Translator.get("alert_error_title"));
		this.setContentText(Translator.get("alert_error_main"));
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		TextArea area = new TextArea(sw.toString());
		area.setWrapText(true);
		area.setEditable(false);
		this.getDialogPane().setExpandableContent(area);
		this.setResizable(true);
		this.showAndWait().ifPresent(_ -> {
			Config.store(); // TODO remove?
			Platform.exit();
		});
	}

}
