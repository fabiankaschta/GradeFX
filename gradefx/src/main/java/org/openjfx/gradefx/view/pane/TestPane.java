package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.pane.statistics.TestStatisticsSidePane;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public class TestPane extends ScrollPane {

	private final TestHeaderPane testHeaderPane;
//	private final TableViewTest testTableView;
	private final TestContentPane testContentPane;
	private final TestStatisticsSidePane testStatisticsSidePane;

	public TestPane(Group group, Test test) {
		BorderPane centerPane = new BorderPane();
		this.testHeaderPane = new TestHeaderPane(group, test);
//		this.testTableView = new TableViewTest(group, test);
		this.testContentPane = new TestContentPane(group, test);
		this.testStatisticsSidePane = new TestStatisticsSidePane(group, test);
		centerPane.setTop(this.testHeaderPane);
//		centerPane.setCenter(this.testTableView);
		centerPane.setCenter(this.testContentPane);
		BorderPane fullPane = new BorderPane();
		fullPane.setCenter(centerPane);
		fullPane.setRight(this.testStatisticsSidePane);
		this.setFitToWidth(true);
		this.setFitToHeight(true);
		this.setContent(fullPane);
	}

	public void selectStudent(Student student) {
//		this.testTableView.getSelectionModel().select(student);
		this.testContentPane.selectStudent(student);
	}

	public Student getSelectStudent() {
//		return this.testTableView.getSelectionModel().getSelectedItem();
		return this.testContentPane.getSelectStudent();
	}

}
