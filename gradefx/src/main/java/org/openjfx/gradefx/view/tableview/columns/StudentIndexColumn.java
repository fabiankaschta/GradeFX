package org.openjfx.gradefx.view.tableview.columns;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.view.tableview.TableCellCustom;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;

public class StudentIndexColumn extends TableColumn<Student, String> {

	public StudentIndexColumn(Group group) {
		this.setCellValueFactory(data -> Bindings
				.createIntegerBinding(() -> 1 + group.getStudentsUnmodifiable().indexOf(data.getValue()), group.getStudentsUnmodifiable())
				.asString("%02d"));
		this.setCellFactory(TableCellCustom.forTableColumn(Pos.CENTER));
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(false);
		this.setResizable(false);
		this.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));
		this.visibleProperty().bind(ConfigController.getObservable("LINE_NUMBERS").map(s -> Boolean.valueOf(s)));
	}
}
