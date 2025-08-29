package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.view.menu.GroupMenu;
import org.openjfx.gradefx.view.menu.SettingsMenu;
import org.openjfx.gradefx.view.menu.StudentMenu;
import org.openjfx.gradefx.view.menu.TestMenu;
import org.openjfx.kafx.view.menu.FileMenu;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

public class MainMenuBar extends BorderPane {

	private final static StringProperty message = new SimpleStringProperty();
	private static MainMenuBar instance;

	public static MainMenuBar get() {
		if (instance == null) {
			instance = new MainMenuBar();
		}
		return instance;
	}

	public static void setStatus(String message) {
		MainMenuBar.message.set(message);
	}

	private final Label statusIndicator = new Label("status");

	private MainMenuBar() {
		MenuBar menuBar = new MenuBar(new FileMenu(), new GroupMenu(), new StudentMenu(), new TestMenu(),
				new SettingsMenu());
		this.statusIndicator.textProperty().bind(message);
		// cant' use css menu-item because of transparent background
		this.statusIndicator.setStyle("-fx-padding: 0.333333em 0.41777em 0.333333em 0.41777em;");
		this.statusIndicator.getStyleClass().add("menu-bar");
		this.setCenter(menuBar);
		this.setRight(statusIndicator);
	}

}
