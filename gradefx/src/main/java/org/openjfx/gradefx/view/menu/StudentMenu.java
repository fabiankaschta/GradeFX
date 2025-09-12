package org.openjfx.gradefx.view.menu;

import java.io.File;
import java.io.IOException;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.view.dialog.DialogAddStudent;
import org.openjfx.gradefx.view.dialog.DialogEditStudent;
import org.openjfx.gradefx.view.dialog.StudentParserDialog;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.ExceptionController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.alert.AlertDelete;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;

public class StudentMenu extends Menu {

	private final MenuItem menuItemNew, menuItemEdit, menuItemDelete, menuItemImport;
	private final FileChooser fileChooser = new FileChooser();

	public StudentMenu() {
		super(TranslationController.translate("menu_student_title"));

		this.menuItemNew = new MenuItem(TranslationController.translate("menu_student_new"));
		this.menuItemNew.setOnAction(_ -> {
			new DialogAddStudent(GroupsPane.getSelectedGroup()).showAndWait();
		});
		this.getItems().add(this.menuItemNew);

		this.menuItemEdit = new MenuItem(TranslationController.translate("menu_student_edit"));
		this.menuItemEdit.setOnAction(_ -> {
			new DialogEditStudent(GroupsPane.getSelectedGroup(), GroupsPane.getSelectedStudent()).showAndWait();
		});
		this.getItems().add(this.menuItemEdit);

		this.menuItemDelete = new MenuItem(TranslationController.translate("menu_student_delete"));
		this.menuItemDelete.setOnAction(_ -> {
			Group g = GroupsPane.getSelectedGroup();
			Student s = GroupsPane.getSelectedStudent();
			new AlertDelete(TranslationController.translate("student") + " " + s.getFirstName() + " " + s.getLastName()).showAndWait()
					.ifPresent(response -> {
						if (response == ButtonType.OK) {
							g.removeStudent(s);
						} else {
							// abort delete, do nothing
						}
					});
		});
		this.getItems().add(this.menuItemDelete);

		this.getItems().add(new SeparatorMenuItem());

		this.menuItemImport = new MenuItem(TranslationController.translate("menu_student_import"));
		this.menuItemImport.setOnAction(_ -> {
			Group group = GroupsPane.getSelectedGroup();
			if (ConfigController.exists("LAST_FILE")) {
				fileChooser.setInitialDirectory(new File(ConfigController.get("LAST_FILE")).getParentFile());
			}
			File file = this.fileChooser.showOpenDialog(getParentPopup());
			if (file != null) {
				try {
					new StudentParserDialog(group, file).showAndWait();
				} catch (IOException e) {
					ExceptionController.exception(e);
				}
			}
		});
		this.getItems().add(this.menuItemImport);

		this.menuItemNew.disableProperty().bind(GroupsPane.selectedGroupProperty().isNull());
		this.menuItemEdit.disableProperty().bind(GroupsPane.selectedStudentProperty().isNull());
		this.menuItemDelete.disableProperty().bind(GroupsPane.selectedStudentProperty().isNull());
	}

}
