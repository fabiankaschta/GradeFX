package org.openjfx.gradefx.view.menu;

import org.openjfx.gradefx.view.dialog.DialogEditConfig;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class SettingsMenu extends Menu {

	private final MenuItem menuItemSettings;

	public SettingsMenu() {
		super(TranslationController.translate("menu_settings_title"));

		this.menuItemSettings = new MenuItem(TranslationController.translate("menu_settings"));
		this.menuItemSettings.setOnAction(_ -> {
			new DialogEditConfig().showAndWait();
		});
		this.getItems().add(this.menuItemSettings);
	}

}
