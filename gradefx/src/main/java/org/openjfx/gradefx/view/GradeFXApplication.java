package org.openjfx.gradefx.view;

import org.openjfx.gradefx.model.Subject;
import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.gradefx.view.dialog.DialogFirstStart;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.gradefx.view.pane.MainMenuBar;
import org.openjfx.kafx.controller.CloseController;
import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.controller.FileController;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GradeFXApplication extends Application {

	@Override
	public void start(Stage primaryStage) {
		Controller.setPrimaryStage(primaryStage);

		primaryStage.setMaximized(Boolean.valueOf(ConfigController.get("MAXIMIZED")));
		primaryStage.maximizedProperty()
				.subscribe(maximized -> ConfigController.set("MAXIMIZED", String.valueOf(maximized)));
		primaryStage.setTitle(TranslationController.translate("app_title"));
		primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> CloseController.close(e));
		primaryStage.getIcons()
				.add(new Image(GradeFXApplication.class.getResourceAsStream("/org/openjfx/gradefx/img/icon.png")));

		BorderPane root = new BorderPane();
		root.setTop(MainMenuBar.get());
		root.setCenter(GroupsPane.get());

		Scene scene = new Scene(root, Double.valueOf(ConfigController.get("WIDTH")),
				Double.valueOf(ConfigController.get("HEIGHT")));
		scene.getStylesheets().add(Controller.getStylesheetURL().toExternalForm());
		scene.widthProperty().subscribe(width -> {
			if (!primaryStage.isMaximized()) {
				ConfigController.set("WIDTH", String.valueOf(width));
			}
		});
		scene.heightProperty().subscribe(height -> {
			if (!primaryStage.isMaximized()) {
				ConfigController.set("HEIGHT", String.valueOf(height));
			}
		});

		primaryStage.setScene(scene);

		FontSizeController.fontSizeProperty().subscribe(fontSize -> root.setStyle("-fx-font-size: " + fontSize));
		root.setOnScroll(event -> {
			if (event.isControlDown()) {
				event.consume();
				int fontSize = FontSizeController.getFontSize();
				if (event.getDeltaY() < 0 && fontSize > 0) {
					FontSizeController.setFontSize(fontSize - 1);
				} else if (event.getDeltaY() > 0) {
					FontSizeController.setFontSize(fontSize + 1);
				}
			}
		});

		if (!ConfigController.exists("LAST_FILE") || !FileController.readFromFile()) {
			TestGroupSystem.setDefault();
			Subject.setDefault();
			new DialogFirstStart().showAndWait().ifPresent(buttonType -> {
				if (buttonType == DialogFirstStart.NEW_FILE) {
					if (!FileController.saveAs()) {
						CloseController.close();
					} else {
						primaryStage.show();
					}
				} else if (buttonType == DialogFirstStart.OPEN_FILE) {
					if (!FileController.openFile()) {
						CloseController.close();
					} else {
						primaryStage.show();
					}
				} else {
					CloseController.close();
				}
			});
		} else {
			primaryStage.show();
		}
	}

}
