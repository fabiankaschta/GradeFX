package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.view.dialog.DialogAddStudent;
import org.openjfx.gradefx.view.style.Styles;
import org.openjfx.gradefx.view.tableview.TableViewOverview;
import org.openjfx.gradefx.view.tableview.TableViewStudent;
import org.openjfx.kafx.view.pane.SynchronizedScrollableTableView;

import javafx.beans.property.ReadOnlyObjectProperty;

public class GroupOverviewPane extends SynchronizedScrollableTableView<Student> {

	public GroupOverviewPane(Group group) {
		super(new TableViewStudent(group, true), new TableViewOverview(group));
		this.getPlaceholder().setOnMouseClicked(_ -> new DialogAddStudent(group).showAndWait());
		Styles.subscribeBackgroundColor(this, group.colorProperty());
	}

	public ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return this.getCenterTable().getSelectionModel().selectedItemProperty();
	}

	public Student getSelectedStudent() {
		return this.getCenterTable().getSelectionModel().getSelectedItem();
	}

}
