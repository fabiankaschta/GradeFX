package org.openjfx.gradefx.view.pane.print;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.pane.statistics.StatisticsGrid;
import org.openjfx.gradefx.view.tableview.TableViewPointsSystem;
import org.openjfx.gradefx.view.tableview.test.TableViewTestPrint;
import org.openjfx.kafx.controller.TranslationController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalDateStringConverter;

public class TestPrintPane extends BorderPane {
	private final Group group;
	private final Test test;

	private final Pane statisticsPane;
	private final TableViewTestPrint table;

	public TestPrintPane(Group group, Test test) {
		this.group = group;
		this.test = test;

		StringBuilder headerText = new StringBuilder();
		headerText.append(TranslationController.translate("print_test_title"));
		headerText.append(": ");
		headerText.append(group.getName());
		headerText.append(" (");
		headerText.append(group.getSubject().getName());
		headerText.append(") \u2014 "); // long dash
		headerText.append(test.getName());
		if (test.getDate() != null) {
			headerText.append(" (");
			headerText.append(new LocalDateStringConverter().toString(test.getDate()));
			headerText.append(")");
		}
		Label header = new Label(headerText.toString());
		header.setStyle("-fx-font-weight: bold;");

		this.table = new TableViewTestPrint(group, test);

		this.statisticsPane = new VBox(10);
		this.statisticsPane.setPadding(new Insets(10));

		Label statisticsHeader = new Label(TranslationController.translate("test_pointsSytem"));
		statisticsHeader.setAlignment(Pos.CENTER);
		statisticsHeader.setStyle("-fx-font-weight: bold;");

		TableViewPointsSystem pointsSystem = new TableViewPointsSystem(group, test, true);

		VBox statisticsHeaderBox = new VBox(10, statisticsHeader, pointsSystem);
		statisticsHeaderBox.setAlignment(Pos.CENTER);

		this.statisticsPane.getChildren().add(statisticsHeaderBox);
		this.statisticsPane.getChildren().add(new StatisticsGrid(group, pointsSystem));

		this.setTop(header);
		this.setCenter(this.table);
		setAlignment(this.table, Pos.TOP_LEFT);
		this.setRight(this.statisticsPane);
	}

	public Pane getOptionsPane() {
		HBox optionsPane = new HBox(10);

		CheckBox statisticsCheckBox = new CheckBox(TranslationController.translate("print_test_statistics"));
		statisticsCheckBox.setSelected(true);
		statisticsCheckBox.selectedProperty().subscribe(selected -> {
			this.setRight(selected ? this.statisticsPane : null);
		});
		optionsPane.getChildren().add(statisticsCheckBox);

		if (this.group.getUseSubgroups()) {
			CheckBox subgroupNameCheckBox = new CheckBox(TranslationController.translate("print_test_subgroup_name"));
			subgroupNameCheckBox.setSelected(true);
			this.table.getSubgroupNameColumn().visibleProperty().unbind();
			this.table.getSubgroupNameColumn().visibleProperty().bind(subgroupNameCheckBox.selectedProperty());
			optionsPane.getChildren().add(subgroupNameCheckBox);
		}

		CheckBox returnsCheckBox = new CheckBox(TranslationController.translate("print_test_returns"));
		returnsCheckBox.setSelected(test.getShowReturns());
		this.table.getReturnColumn().visibleProperty().unbind();
		this.table.getReturnColumn().visibleProperty().bind(returnsCheckBox.selectedProperty());
		optionsPane.getChildren().add(returnsCheckBox);

		CheckBox annotationsCheckBox = new CheckBox(TranslationController.translate("print_test_annotations"));
		annotationsCheckBox.setSelected(true);
		this.table.getAnnotationColumn().visibleProperty().bind(annotationsCheckBox.selectedProperty());
		optionsPane.getChildren().add(annotationsCheckBox);

		CheckBox dateCheckBox = new CheckBox(TranslationController.translate("print_test_date"));
		dateCheckBox.setSelected(true);
		this.table.getDateColumn().visibleProperty().bind(dateCheckBox.selectedProperty());
		optionsPane.getChildren().add(dateCheckBox);

		this.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

		return optionsPane;
	}

	@Override
	protected double computePrefHeight(double width) {
		if (this.getRight() != null) {
			return Math.max(this.table.prefHeight(width), this.statisticsPane.prefHeight(width))
					+ this.getTop().prefHeight(width);
		} else {
			return this.table.prefHeight(width) + this.getTop().prefHeight(width);
		}
	}

	@Override
	protected double computePrefWidth(double height) {
		if (this.getRight() != null) {
			return Math.max(this.table.prefWidth(height) + this.statisticsPane.prefWidth(height),
					this.getTop().prefWidth(height));
		} else {
			return Math.max(this.table.prefWidth(height), this.getTop().prefWidth(height));
		}
	}

}
