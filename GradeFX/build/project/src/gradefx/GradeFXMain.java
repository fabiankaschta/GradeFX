package gradefx;

import gradefx.io.Config;
import gradefx.io.FileInput;
import gradefx.io.Config.ConfigOption;
import gradefx.model.Group;
import gradefx.model.TestGroup.TestGroupSystem;
import gradefx.view.GradeFXApplication;
import javafx.application.Application;
import javafx.application.Platform;
import kafx.lang.Translator;

public class GradeFXMain {
	public static void main(String[] args) {
		Platform.startup(() -> {
			Translator.addBundleName("gradefx.lang.gradefx");
			// nötig um Listener zu aktivieren
			Group.clearGroups();

			if (Config.exists(ConfigOption.LAST_FILE)) {
				FileInput.readFromFile(Config.get(ConfigOption.LAST_FILE));
			} else {
				TestGroupSystem.setDefault();
			}

			if (Config.exists(ConfigOption.WIDTH)) {
				GradeFXApplication.setWidth(Double.valueOf(Config.get(ConfigOption.WIDTH)));
			}
			if (Config.exists(ConfigOption.HEIGHT)) {
				GradeFXApplication.setHeight(Double.valueOf(Config.get(ConfigOption.HEIGHT)));
			}

//			TODO AutoSave.start();
		});
		Application.launch(GradeFXApplication.class);
	}
}
