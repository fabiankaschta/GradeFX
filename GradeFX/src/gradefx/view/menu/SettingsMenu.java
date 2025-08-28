package gradefx.view.menu;

import gradefx.view.dialog.DialogEditConfig;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import kafx.lang.Translator;

public class SettingsMenu extends Menu {

	private final MenuItem menuItemSettings;

	public SettingsMenu() {
		super(Translator.get("menu_settings_title"));

		this.menuItemSettings = new MenuItem(Translator.get("menu_settings"));
		this.menuItemSettings.setOnAction(_ -> {
			new DialogEditConfig().showAndWait();
		});
		this.getItems().add(this.menuItemSettings);
	}

}
