package org.openjfx.gradefx;

import java.util.Locale;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.view.GradeFXApplication;

import javafx.application.Application;
import javafx.application.Platform;

public class GradeFXMain {
	public static void main(String[] args) {
		Platform.startup(() -> {
			Locale.setDefault(Locale.GERMAN);
			GradeFXController.init();
			// org.openjfx.kafx.controller.LogController.setDebugMode(true);
			// TODO AutoSave.start();
		});
		Application.launch(GradeFXApplication.class);
	}
}
