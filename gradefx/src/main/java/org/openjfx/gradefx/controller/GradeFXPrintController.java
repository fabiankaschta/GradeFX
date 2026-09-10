package org.openjfx.gradefx.controller;

import org.openjfx.kafx.controller.PrintController;
import org.openjfx.kafx.view.style.Styles;

import javafx.scene.control.Dialog;
import javafx.scene.Node;

public class GradeFXPrintController extends PrintController {

	@Override
	protected Dialog<Boolean> createPrintPreviewDialog(Node printable, Node options) {
		Dialog<Boolean> dialog = super.createPrintPreviewDialog(printable, options);
		Styles.subscribeThemeColor(dialog.getDialogPane(), GradeFXController.getSelectedGroup().colorProperty());
		// -theme-color-bright-heavy is used as faded background color.
		// replace with white to save ink
		dialog.getDialogPane().setStyle(dialog.getDialogPane().getStyle() + "-theme-color-bright-heavy: white;");
		return dialog;
	}

}
