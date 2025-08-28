package gradefx.view.pane;

import gradefx.model.Group;
import gradefx.model.Student;
import gradefx.model.Test;
import gradefx.view.style.Styles;
import gradefx.view.tableview.TableViewStudent;
import gradefx.view.tableview.TableViewTest;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TableView;
import kafx.view.pane.SynchronizedScrollableTableView;

public class TestContentPane extends SynchronizedScrollableTableView<Student> {

	private final TableView<Student> testTable;

	@SuppressWarnings("preview")
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
