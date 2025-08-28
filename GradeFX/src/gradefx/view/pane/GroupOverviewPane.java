package gradefx.view.pane;

import gradefx.model.Group;
import gradefx.model.Student;
import gradefx.view.dialog.DialogAddStudent;
import gradefx.view.style.Styles;
import gradefx.view.tableview.TableViewOverview;
import gradefx.view.tableview.TableViewStudent;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TableView;
import kafx.view.pane.SynchronizedScrollableTableView;

public class GroupOverviewPane extends SynchronizedScrollableTableView<Student> {

	private final TableView<Student> gradesTable;

	@SuppressWarnings("preview")
	public GroupOverviewPane(Group group) {
		super(new TableViewStudent(group, true), this.gradesTable = new TableViewOverview(group));
		this.getPlaceholder().setOnMouseClicked(_ -> new DialogAddStudent(group).showAndWait());
		Styles.subscribeBackgroundColor(this, group.colorProperty());

	}

	public ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return gradesTable.getSelectionModel().selectedItemProperty();
	}

	public Student getSelectedStudent() {
		return gradesTable.getSelectionModel().getSelectedItem();
	}

}
