package org.openjfx.gradefx.view.dialog;

import org.openjfx.kafx.controller.Controller;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class DialogFirstStart extends Dialog<ButtonType> {

	public final static ButtonType NEW_FILE = new ButtonType(Controller.translate("dialog_firstStart_newFile"),
			ButtonData.OTHER);
	public final static ButtonType OPEN_FILE = new ButtonType(Controller.translate("dialog_firstStart_openFile"),
			ButtonData.OTHER);
	public final static ButtonType CLOSE = new ButtonType(Controller.translate("dialog_firstStart_cancel"),
			ButtonData.CANCEL_CLOSE);

	public DialogFirstStart() {
		this.setTitle(Controller.translate("dialog_firstStart_title"));
		Controller.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
		Label startMessage = new Label(Controller.translate("dialog_firstStart_message"));
		startMessage.setWrapText(true);
		startMessage.setTextAlignment(TextAlignment.CENTER);
		this.getDialogPane().setContent(startMessage);
		this.getDialogPane().getButtonTypes().addAll(NEW_FILE, OPEN_FILE, CLOSE);
	}

}
