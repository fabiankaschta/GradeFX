package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.style.Styles;
import org.openjfx.gradefx.view.tableview.TableViewStudent;
import org.openjfx.gradefx.view.tableview.TableViewTest;
import org.openjfx.kafx.view.pane.SynchronizedScrollableTableView;

import javafx.beans.property.ReadOnlyObjectProperty;

public class TestContentPane extends SynchronizedScrollableTableView<Student> {

	public TestContentPane(Group group, Test test) {
		super(new TableViewStudent(group, false), new TableViewTest(group, test));
		Styles.subscribeBackgroundColor(this, group.colorProperty());

	}

	public ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return this.getCenterTable().getSelectionModel().selectedItemProperty();
	}

	public Student getSelectedStudent() {
		return this.getCenterTable().getSelectionModel().getSelectedItem();
	}

}
