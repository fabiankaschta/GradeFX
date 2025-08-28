package gradefx.view.tab;

import kafx.lang.Translator;
import gradefx.model.Group;
import gradefx.model.Student;
import gradefx.view.pane.GroupOverviewPane;
import gradefx.view.pane.GroupsPane;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class GroupOverviewTab extends Tab {

	private final GroupOverviewPane pane;

	public GroupOverviewTab(Group group) {
		this.setGraphic(new Label(Translator.get("tab_overview_title")));
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
