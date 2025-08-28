package gradefx.view.pane;

import gradefx.model.Group;
import gradefx.model.Student;
import gradefx.model.Test;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.BorderPane;

public class TestPane extends BorderPane {

	private final TestHeaderPane testHeaderPane;
	private final TestContentPane testContentPane;
	private final TestStatisticsSidePane testStatisticsSidePane;

	public TestPane(Group group, Test test) {
		BorderPane centerPane = new BorderPane();
		this.testHeaderPane = new TestHeaderPane(group, test);
		this.testContentPane = new TestContentPane(group, test);
		this.testStatisticsSidePane = new TestStatisticsSidePane(group, test);
		centerPane.setTop(this.testHeaderPane);
		centerPane.setCenter(this.testContentPane);
		super.setCenter(centerPane);
		super.setRight(this.testStatisticsSidePane);
	}

	public ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return testContentPane.selectedStudentProperty();
	}

	public Student getSelectedStudent() {
		return testContentPane.getSelectedStudent();
	}

}
