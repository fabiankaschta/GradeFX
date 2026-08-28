package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.tableview.test.TableViewTest;

import javafx.beans.property.ReadOnlyObjectProperty;

public class TestContentPane extends TableViewTest {

	public TestContentPane(Group group, Test test) {
		super(group, test);
	}

	public ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return this.getSelectionModel().selectedItemProperty();
	}

	public Student getSelectedStudent() {
		return this.getSelectionModel().getSelectedItem();
	}

}
