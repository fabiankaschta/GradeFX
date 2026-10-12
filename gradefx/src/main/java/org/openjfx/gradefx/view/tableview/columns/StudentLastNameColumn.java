package org.openjfx.gradefx.view.tableview.columns;

import org.openjfx.gradefx.model.Student;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableCellEditConverter;

import javafx.scene.control.TableColumn;

public class StudentLastNameColumn extends TableColumn<Student, String> {

	public StudentLastNameColumn(boolean editable) {
		super(TranslationController.translate("student_lastName"));
		this.setCellValueFactory(data -> data.getValue().lastNameProperty());
		this.setCellFactory(editable ? TableCellEditConverter.forTableColumn() : TableCellCustom.forTableColumn());
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(editable);
	}
}
