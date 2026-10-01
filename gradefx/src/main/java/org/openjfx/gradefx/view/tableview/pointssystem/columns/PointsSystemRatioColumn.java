package org.openjfx.gradefx.view.tableview.pointssystem.columns;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.openjfx.gradefx.model.Grade;
import org.openjfx.gradefx.model.Test;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.converter.BigDecimalPercentConverter;
import org.openjfx.kafx.view.tableview.TableCellCustom;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;

public class PointsSystemRatioColumn extends TableColumn<Grade, BigDecimal> {

	public PointsSystemRatioColumn(Test test) {
		super("%");
		setCellValueFactory(data -> Bindings.createObjectBinding(() -> {
			int total = test.getGradedAmount();
			if (total == 0) {
				return null;
			} else {
				int amount = test.getGradeAmountRespectingDate(data.getValue());
				return BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(total), 5, RoundingMode.FLOOR);
			}
		}, test.gradedAmountRespectingDateProperty(), test.gradeAmountRespectingDateProperty(data.getValue())));
		setCellFactory(TableCellCustom.forTableColumn(new BigDecimalPercentConverter(2), Pos.CENTER));
		setSortable(false);
		setReorderable(false);
		this.minWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(5));
	}

}
