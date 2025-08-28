package gradefx.view.menu;

import java.io.File;

import gradefx.controller.Controller;
import gradefx.io.Config;
import gradefx.io.FileInput;
import gradefx.io.FileOutput;
import gradefx.io.Config.ConfigOption;
import kafx.lang.Translator;
import gradefx.model.Group;
import gradefx.model.TestGroup.TestGroupSystem;
import gradefx.view.alert.AlertSaveChanges;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

public class FileMenu extends Menu {

	private final MenuItem menuItemNew, menuItemOpen, menuItemSave, menuItemSaveAs;
	private final FileChooser fileChooser = new FileChooser();

	public FileMenu() {
		super(Translator.get("menu_file_title"));

		this.menuItemNew = new MenuItem(Translator.get("menu_file_new"));
		this.menuItemNew.setOnAction(e -> {
			if (Controller.hasUnsavedChanges()) {
				new AlertSaveChanges(e, () -> actionNew());
			} else {
				actionNew();
			}
		});
		this.getItems().add(this.menuItemNew);

		this.menuItemOpen = new MenuItem(Translator.get("menu_file_open"));
		this.menuItemOpen.setOnAction(e -> {
			if (Controller.hasUnsavedChanges()) {
				new AlertSaveChanges(e, () -> actionOpen());
			} else {
				actionOpen();
			}
		});
		this.getItems().add(this.menuItemOpen);

		this.menuItemSave = new MenuItem(Translator.get("menu_file_save"));
		this.menuItemSave.setOnAction(_ -> actionSave());
		this.getItems().add(this.menuItemSave);

		this.menuItemSaveAs = new MenuItem(Translator.get("menu_file_save_as"));
		this.menuItemSaveAs.setOnAction(_ -> actionSaveAs());
		this.getItems().add(this.menuItemSaveAs);

		if (!Config.exists(ConfigOption.LAST_FILE)) {
			// TODO startup message before save dialog if no last file
			if (!actionSaveAs()) {
				Platform.exit();
			}
		}
	}

	private void actionNew() {
		TestGroupSystem.setDefault();
		Group.clearGroups();
		actionSaveAs();
	}

	private void actionOpen() {
		if (Config.exists(ConfigOption.LAST_FILE)) {
			fileChooser.setInitialDirectory(new File(Config.get(ConfigOption.LAST_FILE)).getParentFile());
		}
		File file = this.fileChooser.showOpenDialog(getParentPopup());
		if (file != null) {
			FileInput.readFromFile(file);
		}
	}

	private void actionSave() {
		FileOutput.writeToFile(Config.get(ConfigOption.LAST_FILE));
	}

	private boolean actionSaveAs() {
		if (Config.exists(ConfigOption.LAST_FILE)) {
			fileChooser.setInitialDirectory(new File(Config.get(ConfigOption.LAST_FILE)).getParentFile());
			fileChooser.setInitialFileName(new File(Config.get(ConfigOption.LAST_FILE)).getName());
		}
		File file = this.fileChooser.showSaveDialog(getParentPopup());
		if (file != null) {
			FileOutput.writeToFile(file);
			return true;
		} else {
			return false;
		}
	}

}
