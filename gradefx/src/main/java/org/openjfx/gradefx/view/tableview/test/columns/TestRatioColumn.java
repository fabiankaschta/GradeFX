package org.openjfx.gradefx.view.tableview.test.columns;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Consumer;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.converter.BigDecimalPercentConverter;
import org.openjfx.kafx.view.tableview.TableCellCustom;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class TestRatioColumn extends TableColumn<Student, BigDecimal> {

	public TestRatioColumn(Test test) {
		this(test, null);
	}

	public TestRatioColumn(Test test, Consumer<TableCell<Student, ?>> cellSubscription) {
		super("%");
		this.setCellValueFactory(data -> Bindings.createObjectBinding(() -> {
			BigDecimal sum = test.getTotalPoints(data.getValue());
			if (sum == null || test.getMaxPoints().compareTo(BigDecimal.ZERO) == 0) {
				return null;
			} else {
				return sum.divide(test.getMaxPoints(), 5, RoundingMode.FLOOR);
			}
		}, test.maxPointsProperty(), test.totalPointsProperty(data.getValue())));
		this.setCellFactory(_ -> new TableCellCustom<>(new BigDecimalPercentConverter(2), Pos.CENTER) {
			{
				if (cellSubscription != null) {
					cellSubscription.accept(this);
				}
			}
		});
		this.setSortable(true);
		this.setReorderable(false);
		this.editableProperty().bind(test.useTasksProperty().not());
		this.minWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(5));
		this.visibleProperty().bind(test.usePointsProperty());
	}

}