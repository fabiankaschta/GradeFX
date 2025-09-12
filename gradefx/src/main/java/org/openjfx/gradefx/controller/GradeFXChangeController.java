package org.openjfx.gradefx.controller;

import org.openjfx.gradefx.view.pane.MainMenuBar;
import org.openjfx.kafx.controller.ChangeController;
import org.openjfx.kafx.controller.TranslationController;

public class GradeFXChangeController extends ChangeController {

	@Override
	protected void handleChange() {
//		MainMenuBar.setStatus(TranslationController.translate("status_all_saved"));
		MainMenuBar.setStatus(getChangeCounter() == 0 ? ""
				: getChangeCounter() + " " + TranslationController.translate("status_unsaved_changes"));
	}
}
