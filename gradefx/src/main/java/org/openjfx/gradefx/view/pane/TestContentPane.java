package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.style.Styles;
import org.openjfx.gradefx.view.tableview.TableViewStudent;
import org.openjfx.gradefx.view.tableview.TableViewTest;
import org.openjfx.kafx.view.pane.SynchronizedScrollableTableView;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TableView;

public class TestContentPane extends SynchronizedScrollableTableView<Student> {

	private final TableView<Student> testTable;

	public TestContentPane(Group group, Test test) {
		super(new TableViewStudent(group, false), this.testTable = new TableViewTest(group, test));
		Styles.subscribeBackgroundColor(this, group.colorProperty());

	}

	public ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return testTable.getSelectionModel().selectedItemProperty();
	}

	public Student getSelectedStudent() {
		return testTable.getSelectionModel().getSelectedItem();
	}

}
