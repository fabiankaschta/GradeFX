package org.openjfx.gradefx.view.tableview.test.columns;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.tableview.TableCellEditComparable;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;

public class TestTaskColumn extends TableColumn<Student, BigDecimal> {

	private final Label name = new Label();
	private final Label points = new Label();

	public TestTaskColumn(TestTask testTask, Function<TestTask, TestTaskColumn> createTestTaskColumn) {
		this(testTask, createTestTaskColumn, null);
	}

	public TestTaskColumn(TestTask testTask, Function<TestTask, TestTaskColumn> createTestTaskColumn,
			Consumer<TableCell<Student, ?>> cellSubscription) {
		this.setReorderable(false);
		BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

		this.name.textProperty().bind(testTask.nameProperty());
		this.name.setStyle("-fx-text-fill: -fx-text-base-color;");
		this.points.textProperty().bind(testTask.maxPointsProperty().map(
				v -> bigDecimalConverter.toString(v) + " " + TranslationController.translate("test_points_short")));
		this.points.setStyle("-fx-text-fill: -fx-text-base-color; -fx-font-style: italic; -fx-font-weight: normal;");
		BorderPane graphic = new BorderPane();
		BorderPane.setAlignment(this.name, Pos.CENTER);
		BorderPane.setAlignment(this.points, Pos.CENTER);
		graphic.setCenter(this.name);
		this.setGraphic(graphic);
		this.setCellValueFactory(data -> testTask.pointsProperty(data.getValue()));
		this.setCellFactory(
				_ -> new TableCellEditComparable<>(BigDecimal.ZERO, null, bigDecimalConverter, Pos.CENTER, true) {
					{
						// TODO if points > max ...
						if (cellSubscription != null) {
							cellSubscription.accept(this);
						}
					}
				});
		testTask.leafProperty().subscribe(isLeaf -> {
			if (isLeaf) {
				graphic.setBottom(this.points);
				getColumns().clear();
			} else {
				graphic.setBottom(null);
			}
		});
		if (!testTask.isLeaf()) {
			for (TreeItem<TestTask> t : testTask.getChildren()) {
				getColumns().add(createTestTaskColumn.apply((TestTask) t));
			}
		}
	}

	public double getWidthSum(Function<Double, Double> snapSizeX) {
		if (!this.getColumns().isEmpty()) {
			double width = 0;
			for (TableColumn<Student, ?> c : this.getColumns()) {
				if (c instanceof TestTaskColumn) {
					width += ((TestTaskColumn) c).getWidthSum(snapSizeX);
				} else {
					width += snapSizeX.apply(c.getWidth());
				}
			}
			return width;
		} else {
			return snapSizeX.apply(getWidth());
		}
	}

}