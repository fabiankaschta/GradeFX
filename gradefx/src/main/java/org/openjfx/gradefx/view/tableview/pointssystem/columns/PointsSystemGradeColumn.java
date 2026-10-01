package org.openjfx.gradefx.view.tableview.pointssystem.columns;

import org.openjfx.gradefx.model.Grade;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellCustom;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;

public class PointsSystemGradeColumn extends TableColumn<Grade, Grade> {

	public PointsSystemGradeColumn() {
		super(TranslationController.translate("pointsSystem_grade"));
		this.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
		this.setCellFactory(_ -> new TableCellCustom<>(Pos.CENTER));
		this.setSortable(false);
		this.setReorderable(false);
	}
}
