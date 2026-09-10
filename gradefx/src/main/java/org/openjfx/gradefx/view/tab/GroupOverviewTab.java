package org.openjfx.gradefx.view.tab;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.view.tableview.overview.TableViewOverview;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class GroupOverviewTab extends Tab {

	private final TableViewOverview tableView;

	public GroupOverviewTab(Group group) {
		Label label = new Label(TranslationController.translate("tab_overview_title"));
		label.setStyle("-fx-text-fill: -fx-text-base-color;");
		this.setGraphic(label);
		this.tableView = new TableViewOverview(group);
		this.getStyleClass().add("tab-bold-selected");
		this.setContent(this.tableView);
		this.selectedProperty().addListener((_, _, selected) -> {
			if (selected) {
				GradeFXController.setSelectedTest(null);
				this.tableView.getSelectionModel().select(GradeFXController.getSelectedStudent());
			}
		});
	}

	public Student getSelectedStudent() {
		return this.tableView.getSelectionModel().getSelectedItem();
	}

}
