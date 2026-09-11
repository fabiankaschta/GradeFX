package org.openjfx.gradefx.view.menu;

import java.io.File;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.view.dialog.DialogAddStudent;
import org.openjfx.gradefx.view.dialog.DialogEditStudent;
import org.openjfx.gradefx.view.dialog.DialogImportStudentsFromGroup;
import org.openjfx.gradefx.view.tableview.TableViewStudentImportPreview;
import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.io.CSVParser;
import org.openjfx.kafx.view.alert.AlertDelete;
import org.openjfx.kafx.view.dialog.DialogCSVImport;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;

public class StudentMenu extends Menu {

	private final MenuItem menuItemNew, menuItemEdit, menuItemDelete, menuItemImportCSV, menuItemImportGroup;
	private final FileChooser fileChooser = new FileChooser();

	public StudentMenu() {
		super(TranslationController.translate("menu_student_title"));

		this.menuItemNew = new MenuItem(TranslationController.translate("menu_student_new"));
		this.menuItemNew.setOnAction(_ -> {
			new DialogAddStudent(GradeFXController.getSelectedGroup()).showAndWait();
		});
		this.getItems().add(this.menuItemNew);

		this.menuItemEdit = new MenuItem(TranslationController.translate("menu_student_edit"));
		this.menuItemEdit.setOnAction(_ -> {
			new DialogEditStudent(GradeFXController.getSelectedGroup(), GradeFXController.getSelectedStudent())
					.showAndWait();
		});
		this.getItems().add(this.menuItemEdit);

		this.menuItemDelete = new MenuItem(TranslationController.translate("menu_student_delete"));
		this.menuItemDelete.setOnAction(_ -> {
			Group g = GradeFXController.getSelectedGroup();
			Student s = GradeFXController.getSelectedStudent();
			new AlertDelete(TranslationController.translate("student") + " " + s.getFirstName() + " " + s.getLastName(),
					() -> g.removeStudent(s)).showAndWait();
		});
		this.getItems().add(this.menuItemDelete);

		this.getItems().add(new SeparatorMenuItem());

		this.menuItemImportGroup = new MenuItem(TranslationController.translate("menu_student_import_group"));
		this.menuItemImportGroup.setOnAction(_ -> {
			Group group = GradeFXController.getSelectedGroup();
			new DialogImportStudentsFromGroup(group).showAndWait();
		});
		this.getItems().add(this.menuItemImportGroup);

		this.menuItemImportCSV = new MenuItem(TranslationController.translate("menu_student_import_csv"));
		this.menuItemImportCSV.setOnAction(_ -> {
			Group group = GradeFXController.getSelectedGroup();
			if (ConfigController.exists("LAST_FILE")) {
				fileChooser.setInitialDirectory(new File(ConfigController.get("LAST_FILE")).getParentFile());
			}
			File file = this.fileChooser.showOpenDialog(getParentPopup());
			if (file != null) {
				TableViewStudentImportPreview preview = new TableViewStudentImportPreview(group);
				CSVParser<Student> csvParser = new CSVParser<>(values -> {
					if (values.length < 2) {
						return null;
					} else if (values.length == 2) {
						return new Student(values[preview.indexOfFirstNameColumn()],
								values[preview.indexOfLastNameColumn()]);
					} else {
						return new Student(values[preview.indexOfFirstNameColumn()],
								values[preview.indexOfLastNameColumn()], values[preview.indexOfSubgroupNameColumn()]);
					}
				}, file, ';');
				new DialogCSVImport<>(TranslationController.translate("dialog_import_students_csv_title"), csvParser,
						preview, 3, s -> group.addStudent(s)).showAndWait();
			}
		});
		this.getItems().add(this.menuItemImportCSV);

		this.menuItemNew.disableProperty().bind(GradeFXController.selectedGroupProperty().isNull());
		this.menuItemEdit.disableProperty().bind(GradeFXController.selectedStudentProperty().isNull());
		this.menuItemDelete.disableProperty().bind(GradeFXController.selectedStudentProperty().isNull());
		this.menuItemImportGroup.disableProperty().bind(GradeFXController.selectedGroupProperty().isNull());
		this.menuItemImportCSV.disableProperty().bind(GradeFXController.selectedGroupProperty().isNull());
	}

}
