package org.openjfx.gradefx.view.chart;

import org.openjfx.gradefx.model.Grade;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.chart.axis.IntegerAxis;
import org.openjfx.kafx.controller.FontSizeController;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;

public class StatisticsChart extends BarChart<String, Integer> {

	public StatisticsChart(Group group, Test test) {
		super(new CategoryAxis(), new IntegerAxis());
		this.setLegendVisible(false);
		CategoryAxis categoryAxis = (CategoryAxis) super.getXAxis();
		this.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(10));
		this.prefHeightProperty().bind(FontSizeController.fontSizeProperty().multiply(15));
		group.gradeSystemProperty().subscribe(gradeSystem -> {
			categoryAxis.getCategories().clear();
			Series<String, Integer> data = new Series<>();
			for (Grade grade : gradeSystem.getPossibleGradesDESC()) {
				categoryAxis.getCategories().add(grade.toString());
				Data<String, Integer> gradeData = new Data<>(grade.toString(), 0);
				test.gradeAmountRespectingDateProperty(grade)
						.subscribe(amount -> gradeData.setYValue(amount.intValue()));
				data.getData().add(gradeData);
			}
			this.getData().clear();
			this.getData().add(data);
		});
	}

}
