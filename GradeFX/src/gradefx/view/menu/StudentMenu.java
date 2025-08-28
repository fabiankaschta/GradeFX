package gradefx.view.menu;

import java.io.File;
import java.io.IOException;

import gradefx.model.Group;
import gradefx.model.Student;
import gradefx.view.dialog.DialogAddStudent;
import gradefx.view.dialog.DialogEditStudent;
import gradefx.view.dialog.StudentParserDialog;
import gradefx.view.pane.GroupsPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;
import kafx.controller.Controller;
import kafx.lang.Translator;
import kafx.view.alert.AlertDelete;

public class StudentMenu extends Menu {

	private final MenuItem menuItemNew, menuItemEdit, menuItemDelete, menuItemImport;
	private final FileChooser fileChooser = new FileChooser();

	public StudentMenu() {
		super(Translator.get("menu_student_title"));

		this.menuItemNew = new MenuItem(Translator.get("menu_student_new"));
		this.menuItemNew.setOnAction(_ -> {
			new DialogAddStudent(GroupsPane.getSelectedGroup()).showAndWait();
		});
		this.getItems().add(this.menuItemNew);

		this.menuItemEdit = new MenuItem(Translator.get("menu_student_edit"));
		this.menuItemEdit.setOnAction(_ -> {
			new DialogEditStudent(GroupsPane.getSelectedGroup(), GroupsPane.getSelectedStudent()).showAndWait();
		});
		this.getItems().add(this.menuItemEdit);

		this.menuItemDelete = new MenuItem(Translator.get("menu_student_delete"));
		this.menuItemDelete.setOnAction(_ -> {
			Group g = GroupsPane.getSelectedGroup();
			Student s = GroupsPane.getSelectedStudent();
			new AlertDelete(Translator.get("student") + " " + s.getFirstName() + " " + s.getLastName()).showAndWait()
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

		this.menuItemImport = new MenuItem(Translator.get("menu_student_import"));
		this.menuItemImport.setOnAction(_ -> {
			Group group = GroupsPane.getSelectedGroup();
			if (Controller.existsConfigOption("LAST_FILE")) {
				fileChooser.setInitialDirectory(new File(Controller.getConfigOption("LAST_FILE")).getParentFile());
			}
			File file = this.fileChooser.showOpenDialog(getParentPopup());
			if (file != null) {
				try {
					new StudentParserDialog(group, file).showAndWait();
				} catch (IOException e) {
					Controller.exception(e);
				}
			}
		});
		this.getItems().add(this.menuItemImport);

		this.menuItemNew.disableProperty().bind(GroupsPane.selectedGroupProperty().isNull());
		this.menuItemEdit.disableProperty().bind(GroupsPane.selectedStudentProperty().isNull());
		this.menuItemDelete.disableProperty().bind(GroupsPane.selectedStudentProperty().isNull());
	}

}
