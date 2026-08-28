package org.openjfx.gradefx.controller;

import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.controller.PrintController;
import org.openjfx.kafx.view.style.Styles;

import javafx.scene.Node;
import javafx.scene.control.Dialog;

public class GradeFXPrintController extends PrintController {

	@Override
	protected Dialog<Boolean> createPrintPreviewDialog(Node printable) {
		Dialog<Boolean> dialog = super.createPrintPreviewDialog(printable);
		Styles.subscribeThemeColor(dialog.getDialogPane(), GroupsPane.getSelectedGroup().colorProperty());
		return dialog;
	}

}
