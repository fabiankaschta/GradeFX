package org.openjfx.gradefx.view.pane.print;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.view.tableview.overview.TableViewOverviewPrint;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class GroupOverviewPrintPane extends BorderPane {

	public GroupOverviewPrintPane(Group group) {
		StringBuilder headerText = new StringBuilder();
		headerText.append(TranslationController.translate("print_grade_overview_title"));
		headerText.append(": ");
		headerText.append(group.getName());
		headerText.append(" (");
		headerText.append(group.getSubject().getName());
		headerText.append(")");
		Label header = new Label(headerText.toString());
		header.setStyle("-fx-font-weight: bold;");
		TableViewOverviewPrint table = new TableViewOverviewPrint(group);
		this.setTop(header);
		this.setCenter(table);
	}

}
