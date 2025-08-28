package gradefx.view;

import gradefx.controller.Controller;
import gradefx.io.AutoSave;
import gradefx.io.Config;
import gradefx.io.Config.ConfigOption;
import kafx.lang.Translator;
import gradefx.view.alert.AlertSaveChanges;
import gradefx.view.pane.GroupsPane;
import gradefx.view.pane.MainMenuBar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GradeFXApplication extends Application {

	private static double width = 800, height = 600;

	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setTop(MainMenuBar.get());
		root.setCenter(GroupsPane.get());
		Scene scene = new Scene(root, width, height);
		scene.getStylesheets().add(GradeFXApplication.class.getResource("main.css").toExternalForm());
		scene.widthProperty().addListener((_, _, newValue) -> Config.set(ConfigOption.WIDTH, newValue.toString()));
		scene.heightProperty().addListener((_, _, newValue) -> Config.set(ConfigOption.HEIGHT, newValue.toString()));
		primaryStage.setMaximized(Boolean.valueOf(Config.get(ConfigOption.MAXIMIZED)));
		primaryStage.maximizedProperty()
				.addListener(_ -> Config.set(ConfigOption.MAXIMIZED, primaryStage.isMaximized() + ""));
		primaryStage.setTitle(Translator.get("app_title"));
		primaryStage.setScene(scene);
		primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			if (Controller.hasUnsavedChanges()) {
				new AlertSaveChanges(e);
			}
		});

		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		Config.store();
		AutoSave.stop();
		super.stop();
	}

	public static void setWidth(double width) {
		GradeFXApplication.width = width;
	}

	public static void setHeight(double height) {
		GradeFXApplication.height = height;
	}

}
