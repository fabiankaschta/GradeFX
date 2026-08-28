package org.openjfx.gradefx.view.pane.print;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.pane.statistics.StatisticsGrid;
import org.openjfx.gradefx.view.tableview.TableViewPointsSystem;
import org.openjfx.gradefx.view.tableview.test.TableViewTestPrint;
import org.openjfx.kafx.controller.TranslationController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalDateStringConverter;

public class TestPrintPane extends BorderPane {

	public TestPrintPane(Group group, Test test) {
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
		TableViewTestPrint table = new TableViewTestPrint(group, test);
		VBox statistics = new VBox(10);
		statistics.setPadding(new Insets(10));
		Label statisticsHeader = new Label(TranslationController.translate("test_pointsSytem"));
		statisticsHeader.setAlignment(Pos.CENTER);
		statisticsHeader.setStyle("-fx-font-weight: bold;");
		TableViewPointsSystem pointsSystem = new TableViewPointsSystem(group, test, true);
		pointsSystem.setSelectionModel(null); // disable selection
		VBox statisticsHeaderBox = new VBox(10, statisticsHeader, pointsSystem);
		statisticsHeaderBox.setAlignment(Pos.CENTER);
		statistics.getChildren().add(statisticsHeaderBox);
		statistics.getChildren().add(new StatisticsGrid(group, pointsSystem));
		this.setTop(header);
		this.setCenter(table);
		this.setRight(statistics);
		// FIXME this misses options (nur reguläres Datum!)
	}

}
