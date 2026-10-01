package org.openjfx.gradefx.view.tableview.pointssystem.columns;

import java.math.BigDecimal;

import org.openjfx.gradefx.model.Grade;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.PointsSystem;
import org.openjfx.gradefx.model.Test;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.converter.BigDecimalConverter;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableCellEditComparable;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;

public class PointsSystemUpperBoundColumn extends TableColumn<Grade, BigDecimal> {

	public PointsSystemUpperBoundColumn(Group group, Test test, boolean printMode) {
		super(TranslationController.translate("pointsSystem_to"));
		PointsSystem pointsSystem = test.getPointsSystem();
		BigDecimalConverter pointsConverter = new BigDecimalConverter();
		this.setCellValueFactory(data -> pointsSystem.upperBoundForGrade(data.getValue()));
		this.setCellFactory(printMode ? TableCellCustom.forTableColumn(pointsConverter, Pos.CENTER) : _ -> {
			TableCellEditComparable<Grade, BigDecimal> cell = new TableCellEditComparable<>(BigDecimal.ZERO, null,
					pointsConverter, Pos.CENTER);
			cell.tableRowProperty().addListener((_, _, newValue) -> {
				int index = newValue.getIndex();
				Grade[] grades = group.getGradeSystem().getPossibleGradesDESC();
				cell.setEditable(index != 0);
				cell.minValueProperty().unbind();
				cell.maxValueProperty().unbind();
				if (index < grades.length - 1) {
					cell.minValueProperty().bind(pointsSystem.upperBoundForGrade(grades[index + 1])
							.map(v -> v.add(pointsSystem.isUseHalfPoints() ? BigDecimal.valueOf(.5) : BigDecimal.ONE)));
				} else {
					cell.minValueProperty().bind(new SimpleObjectProperty<>(BigDecimal.ZERO));
				}
				if (index > 0) {
					cell.maxValueProperty().bind(pointsSystem.upperBoundForGrade(grades[index - 1]).map(
							v -> v.subtract(pointsSystem.isUseHalfPoints() ? BigDecimal.valueOf(.5) : BigDecimal.ONE)));
				} else {
					cell.maxValueProperty().bind(pointsSystem.totalPointsProperty());
				}
			});
			return cell;
		});
		this.setSortable(false);
		this.setReorderable(false);
	}

}
