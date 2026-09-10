package org.openjfx.gradefx;

import java.util.Locale;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.view.GradeFXApplication;
import org.openjfx.kafx.controller.ConfigController;

import javafx.application.Application;
import javafx.application.Platform;

public class GradeFXMain {
	public static void main(String[] args) {
		Locale.setDefault(Locale.GERMAN);
		Platform.startup(() -> {
			GradeFXController.init();
			if (args.length > 0) {
				ConfigController.set("LAST_FILE", args[0]);
			}
		});
		Application.launch(GradeFXApplication.class);
	}
}
