package org.openjfx.gradefx.view.tableview.columns;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.kafx.controller.FontSizeController;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;

public class StudentReturnColumn extends TableColumn<Student, Boolean> {

	public StudentReturnColumn(Test test) {
		// does not use a label due to short column width
//		super(TranslationController.translate("student_return"));
		this.setCellValueFactory(data -> test.hasReturnedProperty(data.getValue()));
		this.setCellFactory(CheckBoxTableCell.forTableColumn(this));
		this.visibleProperty().bind(test.showReturnsProperty());
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(true);
		this.setResizable(false);
		this.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));
	}
}
