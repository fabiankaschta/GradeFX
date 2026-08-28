package org.openjfx.gradefx.view.tableview.overview.columns;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.GradeSystem.Grade;
import org.openjfx.gradefx.model.Group;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellCustom;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;

public class OverviewGradeColumn extends TableColumn<Student, Grade> {

	public OverviewGradeColumn(Group group, OverviewAvgColumn avgColumn) {
		super(TranslationController.translate("tab_overview_grade"));
		this.setCellValueFactory(data -> Bindings.createObjectBinding(() -> {
			return group.getGradeSystem().calculateGrade(avgColumn.getCellData(data.getValue()));
		}, avgColumn.getCellObservableValue(data.getValue()), group.gradeSystemProperty()));
		this.setCellFactory(TableCellCustom.forTableColumn(group.getGradeSystem().getGradeConverter(), Pos.CENTER));
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(false); // TODO editable end grade?
	}

}
