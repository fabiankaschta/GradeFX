package org.openjfx.gradefx;

import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.view.GradeFXApplication;
import org.openjfx.kafx.controller.Controller;

import javafx.application.Application;
import javafx.application.Platform;

public class GradeFXMain {
	public static void main(String[] args) {
		Platform.startup(() -> {
			try {
				Controller.init(new GradeFXController());
			} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
				Controller.exception(e);
			}
//			Controller.setDebugMode(true); // TODO disable debug
			// TODO AutoSave.start();
		});
		Application.launch(GradeFXApplication.class);
	}
}
