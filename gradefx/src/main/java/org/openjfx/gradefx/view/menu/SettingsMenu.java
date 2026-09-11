package org.openjfx.gradefx.view.menu;

import org.openjfx.gradefx.view.dialog.DialogEditConfig;
import org.openjfx.gradefx.view.dialog.DialogEditSubjects;
import org.openjfx.gradefx.view.dialog.DialogEditTestGroupSystems;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.controller.UpdateController;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class SettingsMenu extends Menu {

	private final MenuItem menuItemEditSettings, menuItemCheckForUpdates, menuItemEditSubjects, menuItemEditTestGroups;

	public SettingsMenu() {
		super(TranslationController.translate("menu_settings_title"));

		this.menuItemEditSettings = new MenuItem(TranslationController.translate("menu_settings_edit"));
		this.menuItemEditSettings.setOnAction(_ -> new DialogEditConfig().showAndWait());
		this.getItems().add(this.menuItemEditSettings);

		this.menuItemEditSubjects = new MenuItem(TranslationController.translate("menu_settings_edit_subjects"));
		this.menuItemEditSubjects.setOnAction(_ -> new DialogEditSubjects().showAndWait());
		this.getItems().add(this.menuItemEditSubjects);

		this.menuItemEditTestGroups = new MenuItem(TranslationController.translate("menu_settings_edit_test_groups"));
		this.menuItemEditTestGroups.setOnAction(_ -> new DialogEditTestGroupSystems().showAndWait());
		this.getItems().add(this.menuItemEditTestGroups);

		this.menuItemCheckForUpdates = new MenuItem(TranslationController.translate("menu_check_for_updates"));
		this.menuItemCheckForUpdates.setOnAction(_ -> UpdateController.checkForUpdates(false));
		this.getItems().add(this.menuItemCheckForUpdates);
	}

}
