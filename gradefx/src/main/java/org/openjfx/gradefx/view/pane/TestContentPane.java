package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.tableview.footer.TestAvgData;
import org.openjfx.gradefx.view.tableview.test.TableViewTest;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableView2Footer;

import javafx.scene.layout.StackPane;

public class TestContentPane extends StackPane {

	private final TableViewTest testTableView;
	private final TableView2Footer<Student> footer;

	public TestContentPane(Group group, Test test) {
		this.testTableView = new TableViewTest(group, test);
		this.footer = new TableView2Footer<Student>(this.testTableView,
				TranslationController.translate("tab_test_footer_avg") + ':', new TestAvgData(group, test));
		this.getChildren().add(this.footer);
	}

	public void selectStudent(Student student) {
		this.testTableView.getSelectionModel().select(student);
	}

	public Student getSelectStudent() {
		return this.testTableView.getSelectionModel().getSelectedItem();
	}

}
