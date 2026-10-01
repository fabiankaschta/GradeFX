package org.openjfx.gradefx.view.tableview.pointssystem.columns;

import org.openjfx.gradefx.model.Grade;
import org.openjfx.gradefx.model.Test;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellCustom;

import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;

public class PointsSystemAmountColumn extends TableColumn<Grade, Integer> {

	public PointsSystemAmountColumn(Test test) {
		super(TranslationController.translate("pointsSystem_amount"));
		this.setCellFactory(_ -> new TableCellCustom<>(Pos.CENTER));
		this.setSortable(false);
		this.setReorderable(false);
		this.setCellValueFactory(data -> test.gradeAmountRespectingDateProperty(data.getValue()).asObject());
	}

}
