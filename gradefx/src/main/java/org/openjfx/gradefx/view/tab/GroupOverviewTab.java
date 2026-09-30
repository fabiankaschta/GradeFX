package org.openjfx.gradefx.view.tab;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.view.pane.OverviewPane;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class GroupOverviewTab extends Tab {

	private final OverviewPane pane;

	public GroupOverviewTab(Group group) {
		Label label = new Label(TranslationController.translate("tab_overview_title"));
		label.setStyle("-fx-text-fill: -fx-text-base-color;");
		this.setGraphic(label);
		this.pane = new OverviewPane(group);
		this.getStyleClass().add("tab-bold-selected");
		this.setContent(this.pane);
		this.selectedProperty().addListener((_, _, selected) -> {
			if (selected) {
				GradeFXController.setSelectedTest(null);
				this.pane.selectStudent(GradeFXController.getSelectedStudent());
			}
		});
	}

	public Student getSelectedStudent() {
		return this.pane.getSelectStudent();
	}

}
