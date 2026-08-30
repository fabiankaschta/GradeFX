package org.openjfx.gradefx.view.tableview.columns;

import java.util.function.Consumer;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;

public class StudentReturnColumn extends TableColumn<Student, Boolean> {

	public StudentReturnColumn(Test test) {
		this(test, null);
	}

	public StudentReturnColumn(Test test, Consumer<TableCell<Student, ?>> cellSubscription) {
		// does not use a label due to short column width
//		super(TranslationController.translate("student_return"));
		this.setCellValueFactory(data -> test.hasReturnedProperty(data.getValue()));
		if (cellSubscription == null) {
			this.setCellFactory(CheckBoxTableCell.forTableColumn(this));
		} else {
			this.setCellFactory(_ -> new CheckBoxTableCell<>() {
				{
					cellSubscription.accept(this);
				}
			});
		}
		this.visibleProperty().bind(test.showReturnsProperty());
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(true);
	}
}
