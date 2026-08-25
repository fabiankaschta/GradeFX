package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.view.dialog.DialogAddStudent;
import org.openjfx.gradefx.view.tableview.TableViewOverview;

import javafx.beans.property.ReadOnlyObjectProperty;

public class GroupOverviewPane extends TableViewOverview {

	public GroupOverviewPane(Group group) {
		super(group);
		this.getPlaceholder().setOnMouseClicked(_ -> new DialogAddStudent(group).showAndWait());
	}

	public ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return this.getSelectionModel().selectedItemProperty();
	}

	public Student getSelectedStudent() {
		return this.getSelectionModel().getSelectedItem();
	}

}
