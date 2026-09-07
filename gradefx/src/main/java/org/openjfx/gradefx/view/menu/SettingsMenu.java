package org.openjfx.gradefx.view.menu;

import org.openjfx.gradefx.view.dialog.DialogEditConfig;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.controller.UpdateController;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class SettingsMenu extends Menu {

	private final MenuItem menuItemEditSettings;
	private final MenuItem menuItemCheckForUpdates;

	public SettingsMenu() {
		super(TranslationController.translate("menu_settings_title"));

		this.menuItemEditSettings = new MenuItem(TranslationController.translate("menu_settings_edit"));
		this.menuItemEditSettings.setOnAction(_ -> {
			new DialogEditConfig().showAndWait();
		});
		this.getItems().add(this.menuItemEditSettings);

		this.menuItemCheckForUpdates = new MenuItem(TranslationController.translate("menu_check_for_updates"));
		this.menuItemCheckForUpdates.setOnAction(_ -> {
			UpdateController.checkForUpdates(false);
		});
		this.getItems().add(this.menuItemCheckForUpdates);
	}

}
