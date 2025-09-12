package org.openjfx.gradefx.view.tableview;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.gradefx.view.style.Styles;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableCellEditConverter;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class TableViewStudent extends TableView<Student> {

	public TableViewStudent(Group group, boolean editable) {
		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));
		this.setItems(group.getStudents());

		TableColumn<Student, String> firstNameCol = new TableColumn<Student, String>(
				TranslationController.translate("student_firstName"));
		firstNameCol.setCellValueFactory(data -> data.getValue().firstNameProperty());
		if (editable) {
			firstNameCol.setCellFactory(TableCellEditConverter.forTableColumn());
		} else {
			firstNameCol.setCellFactory(TableCellCustom.forTableColumn());
		}
		firstNameCol.setSortable(true);
		firstNameCol.setReorderable(false);
		firstNameCol.setEditable(editable);

		TableColumn<Student, String> lastNameCol = new TableColumn<Student, String>(TranslationController.translate("student_lastName"));
		lastNameCol.setCellValueFactory(data -> data.getValue().lastNameProperty());
		if (editable) {
			lastNameCol.setCellFactory(TableCellEditConverter.forTableColumn());
		} else {
			lastNameCol.setCellFactory(TableCellCustom.forTableColumn());
		}
		lastNameCol.setSortable(true);
		lastNameCol.setReorderable(false);
		lastNameCol.setEditable(editable);

		TableColumn<Student, String> subgroupNameCol = new TableColumn<Student, String>(
				TranslationController.translate("student_subgroupName"));
		subgroupNameCol.setCellValueFactory(data -> data.getValue().subgroupNameProperty());
		if (editable) {
			subgroupNameCol.setCellFactory(TableCellEditConverter.forTableColumn());
		} else {
			subgroupNameCol.setCellFactory(TableCellCustom.forTableColumn());
		}
		subgroupNameCol.setSortable(true);
		subgroupNameCol.setReorderable(false);
		subgroupNameCol.setEditable(editable);
		subgroupNameCol.visibleProperty().bind(group.useSubgroupsProperty());

		this.getColumns().add(lastNameCol);
		this.getColumns().add(firstNameCol);
		this.getColumns().add(subgroupNameCol);

		this.getSelectionModel().selectedItemProperty().addListener((_, _, selected) -> {
			GroupsPane.setSelectedStudent(selected);
		});

		this.setEditable(editable);
//		this.prefWidthProperty().bind(Controller.fontSizeProperty().multiply(20));

		FontSizeController.bindTableColumnWidthToFontSize(this);
		Styles.subscribeTableColor(this, group.colorProperty());
	}

}
