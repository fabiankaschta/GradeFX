package gradefx.view.pane;

import gradefx.view.menu.FileMenu;
import gradefx.view.menu.GroupMenu;
import gradefx.view.menu.StudentMenu;
import gradefx.view.menu.TestMenu;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
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
		MenuBar menuBar = new MenuBar(new FileMenu(), new GroupMenu(), new StudentMenu(), new TestMenu());
		this.statusIndicator.textProperty().bind(message);
		this.statusIndicator.getStyleClass().add("menu-bar");
		this.statusIndicator.prefHeightProperty().bind(menuBar.heightProperty());
		this.setCenter(menuBar);
		this.setRight(statusIndicator);
	}

}
