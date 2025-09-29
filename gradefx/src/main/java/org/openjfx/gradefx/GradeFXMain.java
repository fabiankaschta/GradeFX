package org.openjfx.gradefx;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.view.GradeFXApplication;
import org.openjfx.kafx.controller.PropertiesController;

import javafx.application.Application;
import javafx.application.Platform;

public class GradeFXMain {
	public static void main(String[] args) {
		Platform.startup(() -> {
			GradeFXController.init();
//			LogController.setDebugMode(true);
			System.out.println(PropertiesController.getProperty("version"));
			// TODO AutoSave.start();
		});
		Application.launch(GradeFXApplication.class);
	}
}
