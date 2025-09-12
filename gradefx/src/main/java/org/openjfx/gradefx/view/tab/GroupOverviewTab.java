package org.openjfx.gradefx.view.tab;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.view.pane.GroupOverviewPane;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.controller.TranslationController;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class GroupOverviewTab extends Tab {

	private final GroupOverviewPane pane;

	public GroupOverviewTab(Group group) {
		this.setGraphic(new Label(TranslationController.translate("tab_overview_title")));
		this.selectedProperty().addListener((_, _, selected) -> {
			if (selected) {
				GroupsPane.setSelectedTest(null);
			}
		});
		this.getStyleClass().add("tab-bold-selected");
		this.pane = new GroupOverviewPane(group);
		this.setContent(this.pane);
	}

	public ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return this.pane.selectedStudentProperty();
	}

	public Student getSelectedStudent() {
		return this.pane.getSelectedStudent();
	}

}
