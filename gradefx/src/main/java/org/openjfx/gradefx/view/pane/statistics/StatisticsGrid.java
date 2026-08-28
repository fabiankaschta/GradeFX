package org.openjfx.gradefx.view.pane.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.view.tableview.TableViewPointsSystem;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.converter.BigDecimalConverter;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class StatisticsGrid extends GridPane {

	public StatisticsGrid(Group group, TableViewPointsSystem tableViewPointsSystem) {
		super(10, 0);
		this.setStyle("-fx-font-weight: bold;");
		BigDecimalConverter avgConverter = new BigDecimalConverter();
		avgConverter.getDecimalFormat().setMinimumFractionDigits(2);
		avgConverter.getDecimalFormat().setMaximumFractionDigits(2);
		avgConverter.getDecimalFormat().setRoundingMode(RoundingMode.DOWN);

		Label avgLabel = new Label(TranslationController.translate("test_avg") + ": ");
		Label avgValue = new Label();
		avgValue.textProperty().bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = tableViewPointsSystem.getGradeAVG();
			if (avg == null) {
				return "-"; // '\u2014'; // long dash
			} else {
				return avgConverter.toString(avg);
			}
		}, tableViewPointsSystem.gradeAVGProperty()));
		this.add(avgLabel, 0, 0);
		this.add(avgValue, 1, 0);

		Label gradedLabel = new Label(TranslationController.translate("test_graded") + ": ");
		Label gradedValue = new Label();
		gradedValue.textProperty().bind(Bindings.createStringBinding(() -> {
			int graded = tableViewPointsSystem.gradedProperty().get();
			int size = group.getStudents().size();
			return graded + " " + TranslationController.translate("test_graded_outOf") + " " + size;
		}, tableViewPointsSystem.gradedProperty(), group.getStudents()));
		this.add(gradedLabel, 0, 1);
		this.add(gradedValue, 1, 1);
	}
}
