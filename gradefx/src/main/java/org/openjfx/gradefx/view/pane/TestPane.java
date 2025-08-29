package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.style.Styles;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public class TestPane extends ScrollPane {

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
		BorderPane fullPane = new BorderPane();
		fullPane.setCenter(centerPane);
		fullPane.setRight(this.testStatisticsSidePane);
		this.setFitToWidth(true);
		this.setFitToHeight(true);
		this.setContent(fullPane);
		
		Styles.subscribeScrollBarColor(this, group.colorProperty());
	}

	public ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return testContentPane.selectedStudentProperty();
	}

	public Student getSelectedStudent() {
		return testContentPane.getSelectedStudent();
	}

}
