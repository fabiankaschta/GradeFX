package org.openjfx.gradefx.view.tableview.columns;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableCellEditConverter;

import javafx.scene.control.TableColumn;

public class StudentSubgroupNameColumn extends TableColumn<Student, String> {

	public StudentSubgroupNameColumn(Group group, boolean editable) {
		super(TranslationController.translate("student_subgroupName"));
		this.setCellValueFactory(data -> data.getValue().subgroupNameProperty());
		this.setCellFactory(editable ? TableCellEditConverter.forTableColumn() : TableCellCustom.forTableColumn());
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(editable);
		this.visibleProperty().bind(group.useSubgroupsProperty());
		this.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(8));
	}
}
