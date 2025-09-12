package org.openjfx.gradefx.view.menu;

//import java.io.File;

//import org.openjfx.gradefx.io.ExportPDFForm;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.dialog.DialogAddTest;
import org.openjfx.gradefx.view.dialog.DialogEditTest;
import org.openjfx.gradefx.view.dialog.DialogEditTestGroupSystems;
import org.openjfx.gradefx.view.dialog.DialogEditTestTasks;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.alert.AlertDelete;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
//import javafx.stage.FileChooser;
//import javafx.stage.FileChooser.ExtensionFilter;

public class TestMenu extends Menu {

	private final MenuItem menuItemNew, menuItemEdit, menuItemDelete, menuItemTasks, menuItemGroups;

	public TestMenu() {
		super(TranslationController.translate("menu_test_title"));

		this.menuItemNew = new MenuItem(TranslationController.translate("menu_test_new"));
		this.menuItemNew.setOnAction(_ -> {
			new DialogAddTest(GroupsPane.getSelectedGroup()).showAndWait();
		});
		this.getItems().add(this.menuItemNew);

		this.menuItemEdit = new MenuItem(TranslationController.translate("menu_test_edit"));
		this.menuItemEdit.setOnAction(_ -> {
			new DialogEditTest(GroupsPane.getSelectedGroup(), GroupsPane.getSelectedTest()).showAndWait();
		});
		this.getItems().add(this.menuItemEdit);

		this.menuItemDelete = new MenuItem(TranslationController.translate("menu_test_delete"));
		this.menuItemDelete.setOnAction(_ -> {
			Group g = GroupsPane.getSelectedGroup();
			Test t = GroupsPane.getSelectedTest();
			new AlertDelete(TranslationController.translate("test") + " " + t.getName()).showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					g.removeTest(t);
				} else {
					// abort delete, do nothing
				}
			});
		});
		this.getItems().add(this.menuItemDelete);

		this.getItems().add(new SeparatorMenuItem());

		this.menuItemTasks = new MenuItem(TranslationController.translate("menu_test_tasks"));
		this.menuItemTasks.setOnAction(_ -> {
			new DialogEditTestTasks(GroupsPane.getSelectedTest()).showAndWait();
		});
		this.getItems().add(this.menuItemTasks);

		this.getItems().add(new SeparatorMenuItem());

		this.menuItemGroups = new MenuItem(TranslationController.translate("menu_test_groups"));
		this.menuItemGroups.setOnAction(_ -> {
			new DialogEditTestGroupSystems().showAndWait();
		});
		this.getItems().add(this.menuItemGroups);

		this.menuItemNew.disableProperty().bind(GroupsPane.selectedGroupProperty().isNull());
		this.menuItemEdit.disableProperty().bind(GroupsPane.selectedTestProperty().isNull());
		this.menuItemDelete.disableProperty().bind(GroupsPane.selectedTestProperty().isNull());
		if (GroupsPane.getSelectedTest() == null) {
			this.menuItemTasks.disableProperty().set(true);
		} else {
			this.menuItemTasks.disableProperty().bind(GroupsPane.getSelectedTest().useTasksProperty().not());
		}
		GroupsPane.selectedTestProperty().addListener((_, _, newValue) -> {
			this.menuItemTasks.disableProperty().unbind();
			if (newValue != null) {
				this.menuItemTasks.disableProperty().bind(newValue.useTasksProperty().not());
			} else {
				this.menuItemTasks.disableProperty().set(true);
			}
		});
//
//		this.getItems().add(new SeparatorMenuItem());
//
//		MenuItem menuItemExportBSG = new MenuItem(TranslationController.translate("menu_test_exportBSG"));
//		menuItemExportBSG.setOnAction(_ -> {
//			FileChooser fileChooser = new FileChooser();
//			if (Controller.existsConfigOption("LAST_FILE")) {
//				fileChooser.setInitialDirectory(new File(Controller.getConfigOption("LAST_FILE")).getParentFile());
//			} else {
//				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
//			}
//			fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF", "*.pdf"));
//			File file = fileChooser.showSaveDialog(getParentPopup());
//			if (file != null) {
//				ExportPDFForm.exportBSG_Umschlag(file, GroupsPane.getSelectedGroup(), GroupsPane.getSelectedTest());
//			}
//		});
//		this.getItems().add(menuItemExportBSG);
	}

}
