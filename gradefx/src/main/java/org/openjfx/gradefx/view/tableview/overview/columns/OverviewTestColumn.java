package org.openjfx.gradefx.view.tableview.overview.columns;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.GradeSystem.Grade;

import org.openjfx.gradefx.model.Group;
import org.openjfx.kafx.view.tableview.TableCellCustom;

import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;

public class OverviewTestColumn extends TableColumn<Student, Grade> {

	private final Test test;

	public OverviewTestColumn(Group group, Test test) {
		this.test = test;
		this.textProperty().bind(test.shortNameProperty());
		this.setCellValueFactory(data -> test.gradeProperty(data.getValue()));
		this.setCellFactory(TableCellCustom.forTableColumn(group.getGradeSystem().getGradeConverter(), Pos.CENTER));
		this.setSortable(true);
		this.setReorderable(false);
	}

	public Test getTest() {
		return this.test;
	}

}
