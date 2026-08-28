package org.openjfx.gradefx.view.tableview.test.columns;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.tableview.TableCellEditComparable;

import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;
import javafx.util.Subscription;

public class TestSumColumn extends TableColumn<Student, BigDecimal> {

	private final Label name = new Label();
	private final Label points = new Label();

	public TestSumColumn(Test test) {
		this(test, null);
	}

	public TestSumColumn(Test test, Consumer<TableCell<Student, ?>> cellSubscription) {
		this.setCellValueFactory(data -> test.totalPointsProperty(data.getValue()));
		BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();
		this.setCellFactory(
				_ -> new TableCellEditComparable<>(BigDecimal.ZERO, null, bigDecimalConverter, Pos.CENTER, true) {

					Subscription studentSubscription;
					Subscription fixedSubscription;
					{
						this.tableRowProperty().subscribe(row -> {
							if (row != null) {
								if (studentSubscription != null) {
									studentSubscription.unsubscribe();
								}
								studentSubscription = row.itemProperty().subscribe(student -> {
									if (fixedSubscription != null) {
										fixedSubscription.unsubscribe();
									}
									if (student != null) {
										fixedSubscription = test.totalPointsFixedProperty(student)
												.subscribe(fixed -> this.pseudoClassStateChanged(
														PseudoClass.getPseudoClass("fixed-value"), fixed));
									}
								});
							}
						});
						if (cellSubscription != null) {
							cellSubscription.accept(this);
						}
					}
				});
		this.name.setText("\u2211");
		this.name.setStyle("-fx-text-fill: -fx-text-base-color;");
		this.points.textProperty().bind(test.totalPointsProperty().map(
				v -> bigDecimalConverter.toString(v) + " " + TranslationController.translate("test_points_short")));
		this.points.setStyle("-fx-text-fill: -fx-text-base-color; -fx-font-style: italic; -fx-font-weight: normal;");
		BorderPane graphic = new BorderPane();
		BorderPane.setAlignment(this.name, Pos.CENTER);
		BorderPane.setAlignment(this.points, Pos.CENTER);
		graphic.setCenter(this.name);
		graphic.setBottom(this.points);
		this.setGraphic(graphic);
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(true);
		this.setOnEditCommit(e -> {
			if (e.getNewValue() == null && e.getOldValue() == null) {
				return;
			} else if (e.getNewValue() == null && e.getOldValue() != null
					|| e.getNewValue() != null && e.getOldValue() == null
					|| e.getNewValue().compareTo(e.getOldValue()) != 0) {
				if (test.getUseTasks()) {
					test.setTotalPointsFixed(e.getRowValue(), true);
				}
				test.setTotalPoints(e.getRowValue(), e.getNewValue());
			}
		});
		this.visibleProperty().bind(test.usePointsProperty());
	}

}
