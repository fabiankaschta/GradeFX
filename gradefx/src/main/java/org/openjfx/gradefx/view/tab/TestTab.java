package org.openjfx.gradefx.view.tab;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.pane.TestPane;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class TestTab extends Tab {

	private final Test test;
	private final TestPane pane;

	public TestTab(Group group, Test test) {
		this.test = test;

		Label label = new Label();
		label.textProperty().bind(test.shortNameProperty());
		label.setStyle("-fx-text-fill: -fx-text-base-color;");
		this.setGraphic(label);

		this.pane = new TestPane(group, this.test);
		this.setContent(this.pane);

		this.selectedProperty().addListener((_, _, selected) -> {
			if (selected) {
				GradeFXController.setSelectedTest(test);
				this.pane.selectStudent(GradeFXController.getSelectedStudent());
			}
		});
	}

	public Test getTest() {
		return this.test;
	}

	public Student getSelectedStudent() {
		return this.pane.getSelectStudent();
	}

}
