package org.openjfx.gradefx.view.tableview.test.columns;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.gradefx.model.Test.TestTask.TestTaskPointsDecoration;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.converter.BigDecimalConverter;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.style.Styles;
import org.openjfx.kafx.view.tableview.TableCellEditComparable;

import javafx.geometry.Pos;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class TestTaskColumn extends TableColumn<Student, BigDecimal> {

	private final Label name = new Label();
	private final Label points = new Label();
	private final TestTask testTask;

	public TestTaskColumn(TestTask testTask, Function<TestTask, TestTaskColumn> createTestTaskColumn) {
		this(testTask, createTestTaskColumn, null);
	}

	public TestTaskColumn(TestTask testTask, Function<TestTask, TestTaskColumn> createTestTaskColumn,
			Consumer<TableCell<Student, ?>> cellSubscription) {
		this.testTask = testTask;
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

						Label label = new Label();
						label.setStyle("-fx-text-fill: red;");
						ColorPicker contextMenuColorPicker = new ColorPicker();
						// FIXME without this, custom colors hyperlink crashes
						contextMenuColorPicker.getStyleClass().add("color-picker-no-custom");
						this.tableRowProperty().subscribe(row -> {
							if (row != null) {
								row.itemProperty().subscribe(student -> {
									if (student != null) {
										label.textProperty().unbind();
										label.textProperty().bind(testTask.pointsDecorationProperty(student).asString());
										contextMenuColorPicker.setValue(testTask.getDecorationColor(student));
										contextMenuColorPicker.valueProperty().subscribe(color -> testTask
												.setDecorationColor(this.getTableRow().getItem(), color));
										testTask.decorationColorProperty(student).subscribe(color -> label
												.setStyle("-fx-text-fill: " + Styles.toHexString(color) + ";"));
									} else {
										label.textProperty().unbind();
										label.setText("");
										contextMenuColorPicker.setValue(Color.RED);
										label.setStyle("-fx-text-fill: red;");
									}
								});
							} else {
								label.textProperty().unbind();
								label.setText("");
								contextMenuColorPicker.setValue(Color.RED);
								label.setStyle("-fx-text-fill: red;");
							}
						});
						graphic = label;
						CustomMenuItem contextMenuColorPickerMenuItem = new CustomMenuItem(contextMenuColorPicker);
						ContextMenu contextMenu = new ContextMenu(contextMenuColorPickerMenuItem);
						contextMenu.setOnShown(_ -> {
							// hide actual context menu behind color picker ui
							contextMenuColorPicker.setTranslateY(-contextMenu.getHeight());
							contextMenuColorPicker.setTranslateX(-10);
							contextMenuColorPicker.show();
						});
						this.setContextMenu(contextMenu);
					}

					@Override
					protected ComparableField<BigDecimal> createControl() {
						ComparableField<BigDecimal> field = super.createControl();
						// this allows changing the tendency marking even when typing the points
						// although it isn't visible in that stage
						// works only since the keys used are not typed anyway
						field.setOnKeyPressed(event -> {
							if (event.getCode() == KeyCode.PLUS || event.getCode() == KeyCode.ADD) {
								Student student = this.getTableRow().getItem();
								if (testTask.getPointsDecoration(student) == TestTaskPointsDecoration.UP) {
									testTask.setPointsDecoration(student, TestTaskPointsDecoration.NONE);
								} else {
									testTask.setPointsDecoration(student, TestTaskPointsDecoration.UP);
								}
								event.consume();
							} else if (event.getCode() == KeyCode.MINUS || event.getCode() == KeyCode.SUBTRACT) {
								Student student = this.getTableRow().getItem();
								if (testTask.getPointsDecoration(student) == TestTaskPointsDecoration.DOWN) {
									testTask.setPointsDecoration(student, TestTaskPointsDecoration.NONE);
								} else {
									testTask.setPointsDecoration(student, TestTaskPointsDecoration.DOWN);
								}
								event.consume();
							}
						});
						return field;
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

	public TestTask getTestTask() {
		return this.testTask;
	}

}