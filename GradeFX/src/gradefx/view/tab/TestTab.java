package gradefx.view.tab;

import gradefx.model.Group;
import gradefx.model.Student;
import gradefx.model.Test;
import gradefx.view.pane.GroupsPane;
import gradefx.view.pane.TestPane;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class TestTab extends Tab {

	private final Test test;
	private final TestPane pane;

	public TestTab(Group group, Test test) {
		this.test = test;

		Label label = new Label();
		label.textProperty().bind(test.shortNameProperty());
		this.setGraphic(label);

		this.selectedProperty().addListener((_, _, selected) -> {
			if (selected) {
				GroupsPane.setSelectedTest(test);
			} else {
				GroupsPane.setSelectedTest(null);
			}
		});

		this.getStyleClass().add("tab-bold-selected");
		this.pane = new TestPane(group, this.test);
		this.setContent(this.pane);
	}

	public Test getTest() {
		return this.test;
	}

	public ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return this.pane.selectedStudentProperty();
	}

	public Student getSelectedStudent() {
		return this.pane.getSelectedStudent();
	}
}
