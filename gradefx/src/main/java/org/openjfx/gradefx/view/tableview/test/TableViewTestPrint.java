package org.openjfx.gradefx.view.tableview.test;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.gradefx.view.tableview.columns.StudentFirstNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentLastNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentSubgroupNameColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestAnnotationColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestDateColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestGradeColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestRatioColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestSumColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestTaskColumn;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.style.Styles;
import org.openjfx.kafx.view.tableview.TableViewFullSize;

import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;

public class TableViewTestPrint extends TableViewFullSize<Student> {

	public TableViewTestPrint(Group group, Test test) {
		// create a new list, so that sorting is not reflected to the "real" list
		// adding/removing is not supported (no need to)
		super(25, FXCollections.observableArrayList(group.getStudents()));

		this.setEditable(false);

		this.setSelectionModel(null);

		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));

		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));

		StudentLastNameColumn lastNameCol = new StudentLastNameColumn(false);
		StudentFirstNameColumn firstNameCol = new StudentFirstNameColumn(false);
		StudentSubgroupNameColumn subgroupNameCol = new StudentSubgroupNameColumn(group, false);

		this.getColumns().add(lastNameCol);
		this.getColumns().add(firstNameCol);
		this.getColumns().add(subgroupNameCol);

		TestSumColumn sumColumn = new TestSumColumn(test);
		TestRatioColumn ratioColumn = new TestRatioColumn(test, sumColumn);
		TestGradeColumn gradeColumn = new TestGradeColumn(group, test, sumColumn);
		TestAnnotationColumn annotationColumn = new TestAnnotationColumn(test);
		TestDateColumn dateColumn = new TestDateColumn(test);

		if (!test.getTasksRoot().isLeaf()) {
			for (TreeItem<TestTask> task : test.getTasksRoot().getChildren()) {
				this.getColumns().add(createTestTaskColumn((TestTask) task));
			}
		}
		this.getColumns().add(sumColumn);
		this.getColumns().add(ratioColumn);
		this.getColumns().add(gradeColumn);
		this.getColumns().add(annotationColumn);
		this.getColumns().add(dateColumn);

		FontSizeController.bindTableColumnWidthToFontSize(this);
		Styles.subscribeThemeColor(this, group.colorProperty());
		this.getStyleClass().addAll("table-view-cell-highlight", "table-view-no-focus", "table-view-hide-empty");
	}

	private TestTaskColumn createTestTaskColumn(TestTask task) {
		return new TestTaskColumn(task, t -> createTestTaskColumn(t));
	}

	@Override
	protected double computePrefWidth(double height) {
		double width = 0;
		for (TableColumn<Student, ?> c : this.getColumns()) {
			if (c.isVisible()) {
				if (c instanceof TestTaskColumn) {
					width += ((TestTaskColumn) c).getWidthSum(d -> snapSizeX(d));
				} else {
					width += snapSizeX(c.getWidth());
				}
			}
		}
		return width + this.snappedLeftInset() + this.snappedRightInset();
	}

}
