package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.view.tableview.overview.TableViewOverview;
import org.openjfx.kafx.view.style.Styles;

import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;

public class OverviewPane extends BorderPane {

	private final TableViewOverview overviewTableView;

	public OverviewPane(Group group) {
		this.overviewTableView = new TableViewOverview(group);
		this.setLeft(overviewTableView);
		group.colorProperty().subscribe(color -> this.setBackground(Background.fill(Styles.deriveBrightHeavy(color))));
	}

	public void selectStudent(Student student) {
		this.overviewTableView.getSelectionModel().select(student);
	}

	public Student getSelectStudent() {
		return this.overviewTableView.getSelectionModel().getSelectedItem();
	}

}
