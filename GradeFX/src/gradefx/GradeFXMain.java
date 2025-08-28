package gradefx;

import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import gradefx.controller.GradeFXController;
import gradefx.view.GradeFXApplication;
import javafx.application.Application;
import javafx.application.Platform;
import kafx.controller.Controller;
import kafx.lang.Translator;

public class GradeFXMain {
	public static void main(String[] args) {
		Platform.startup(() -> {
			try {
				Controller.init(new GradeFXController());
			} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
				Controller.exception(e);
			}
			Controller.setDebugMode(true); // TODO disable debug
			Translator.addBundleName("gradefx.lang.gradefx");
			// TODO AutoSave.start();
		});
		Application.launch(GradeFXApplication.class);
	}
}
