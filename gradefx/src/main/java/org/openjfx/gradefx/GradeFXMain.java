package org.openjfx.gradefx;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.view.GradeFXApplication;

import javafx.application.Application;
import javafx.application.Platform;

public class GradeFXMain {
	public static void main(String[] args) {
		Platform.startup(() -> {
			GradeFXController.init();
//			LogController.setDebugMode(true);
			// TODO AutoSave.start();
		});
		Application.launch(GradeFXApplication.class);
	}
}
