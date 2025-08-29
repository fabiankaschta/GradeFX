package org.openjfx.gradefx.view;

import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.gradefx.view.dialog.DialogFirstStart;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.gradefx.view.pane.MainMenuBar;
import org.openjfx.kafx.controller.Controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GradeFXApplication extends Application {

	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setTop(MainMenuBar.get());
		root.setCenter(GroupsPane.get());
		Scene scene = new Scene(root, Double.valueOf(Controller.getConfigOption("WIDTH")),
				Double.valueOf(Controller.getConfigOption("HEIGHT")));
		scene.getStylesheets().add(GradeFXApplication.class.getResource("main.css").toExternalForm());
		scene.widthProperty().subscribe(width -> {
			if (!primaryStage.isMaximized()) {
				Controller.setConfigOption("WIDTH", String.valueOf(width));
			}
		});
		scene.heightProperty().subscribe(height -> {
			if (!primaryStage.isMaximized()) {
				Controller.setConfigOption("HEIGHT", String.valueOf(height));
			}
		});

		Controller.fontSizeProperty().subscribe(fontSize -> root.setStyle("-fx-font-size: " + fontSize));
		root.setOnScroll(event -> {
			if (event.isControlDown()) {
				event.consume();
				int fontSize = Controller.getFontSize();
				if (event.getDeltaY() < 0 && fontSize > 0) {
					Controller.setFontSize(fontSize - 1);
				} else if (event.getDeltaY() > 0) {
					Controller.setFontSize(fontSize + 1);
				}
			}
		});

		primaryStage.setMaximized(Boolean.valueOf(Controller.getConfigOption("MAXIMIZED")));
		primaryStage.maximizedProperty()
				.subscribe(maximized -> Controller.setConfigOption("MAXIMIZED", String.valueOf(maximized)));
		primaryStage.setTitle(Controller.translate("app_title"));
		primaryStage.setScene(scene);
		primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> Controller.close(e));

		if (!Controller.existsConfigOption("LAST_FILE") || !Controller.readFromFile()) {
			TestGroupSystem.setDefault();
			new DialogFirstStart().showAndWait().ifPresent(buttonType -> {
				if (buttonType == DialogFirstStart.NEW_FILE) {
					if (!Controller.saveAs()) {
						Controller.close();
					} else {
						primaryStage.show();
					}
				} else if (buttonType == DialogFirstStart.OPEN_FILE) {
					if (!Controller.openFile()) {
						Controller.close();
					} else {
						primaryStage.show();
					}
				} else {
					Controller.close();
				}
			});
		} else {
			primaryStage.show();
		}
	}

}
