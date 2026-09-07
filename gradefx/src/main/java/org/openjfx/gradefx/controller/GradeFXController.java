package org.openjfx.gradefx.controller;

import java.util.ResourceBundle;

import org.openjfx.kafx.controller.AutoSaveController;
import org.openjfx.kafx.controller.ChangeController;
import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.controller.EncryptionController;
import org.openjfx.kafx.controller.EncryptionControllerDefault;
import org.openjfx.kafx.controller.FileController;
import org.openjfx.kafx.controller.PluginController;
import org.openjfx.kafx.controller.PrintController;
import org.openjfx.kafx.controller.PropertiesController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.controller.UpdateController;
import org.pf4j.JarPluginManager;

public class GradeFXController extends Controller {

	public static void init() {
		Controller.init(".gradefx.cfg");
//		Controller.init(".gradefx.cfg", org.openjfx.kafx.controller.LogController.DEBUG);
		PropertiesController
				.addProperties(GradeFXController.class.getResourceAsStream("/org/openjfx/gradefx/gradefx.properties"));
		TranslationController.addBundle(ResourceBundle.getBundle("org.openjfx.gradefx.lang.gradefx"));
		EncryptionController.init(new EncryptionControllerDefault("KAFXBase".getBytes()));
		ChangeController.init(new GradeFXChangeController());
		FileController.init(new GradeFXFileController());
		AutoSaveController.init();
		PrintController.init(new GradeFXPrintController());
		PluginController.init(new JarPluginManager());
		UpdateController.init();
		UpdateController.register("GradeFX", () -> PropertiesController.getProperty("gradefx.version"),
				PropertiesController.getProperty("gradefx.url"));
		ConfigController.putIfNotExists("USE_HALF_POINTS", String.valueOf(true));
		ConfigController.putIfNotExists("TENDENCY_BOUND", String.valueOf(0.0));
		ConfigController.putIfNotExists("DEFAULT_GROUP_COLOR", "#e6e6e6");
	}
}
