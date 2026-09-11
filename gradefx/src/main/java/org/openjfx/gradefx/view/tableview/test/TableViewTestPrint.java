package org.openjfx.gradefx.view.tableview.test;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.gradefx.view.tableview.columns.StudentFirstNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentLastNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentReturnColumn;
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
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;

public class TableViewTestPrint extends TableViewFullSize<Student> {

	private final StudentReturnColumn returnColumn;
	private final StudentLastNameColumn lastNameColumn;
	private final StudentFirstNameColumn firstNameColumn;
	private final StudentSubgroupNameColumn subgroupNameColumn;

	private final TestSumColumn sumColumn;
	private final TestRatioColumn ratioColumn;
	private final TestGradeColumn gradeColumn;
	private final TestAnnotationColumn annotationColumn;
	private final TestDateColumn dateColumn;

	public TableViewTestPrint(Group group, Test test) {
		// create a new list, so that sorting is not reflected to the "real" list
		// adding/removing is not supported (no need to)
		super(25, FXCollections.observableArrayList(group.getStudents()));
		
		this.setPadding(new Insets(0));

		this.setEditable(false);

		this.setSelectionModel(null);

		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));

		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));

		this.returnColumn = new StudentReturnColumn(test);
		this.lastNameColumn = new StudentLastNameColumn(false);
		this.firstNameColumn = new StudentFirstNameColumn(false);
		this.subgroupNameColumn = new StudentSubgroupNameColumn(group, false);

		this.getColumns().add(this.returnColumn);
		this.getColumns().add(this.lastNameColumn);
		this.getColumns().add(this.firstNameColumn);
		this.getColumns().add(this.subgroupNameColumn);

		this.sumColumn = new TestSumColumn(test);
		this.ratioColumn = new TestRatioColumn(test, this.sumColumn);
		this.gradeColumn = new TestGradeColumn(group, test, this.sumColumn);
		this.annotationColumn = new TestAnnotationColumn(test);
		this.dateColumn = new TestDateColumn(test);

		if (!test.getTasksRoot().isLeaf()) {
			for (TreeItem<TestTask> task : test.getTasksRoot().getChildren()) {
				this.getColumns().add(createTestTaskColumn((TestTask) task));
			}
		}
		this.getColumns().add(this.sumColumn);
		this.getColumns().add(this.ratioColumn);
		this.getColumns().add(this.gradeColumn);
		this.getColumns().add(this.annotationColumn);
		this.getColumns().add(this.dateColumn);

		FontSizeController.bindTableColumnWidthToFontSize(this);
		Styles.subscribeThemeColor(this, group.colorProperty());
	}

	private TestTaskColumn createTestTaskColumn(TestTask task) {
		return new TestTaskColumn(task, t -> createTestTaskColumn(t));
	}

	public StudentReturnColumn getReturnColumn() {
		return this.returnColumn;
	}

	public StudentFirstNameColumn getFirstNameColumn() {
		return this.firstNameColumn;
	}

	public StudentLastNameColumn getLastNameColumn() {
		return this.lastNameColumn;
	}

	public StudentSubgroupNameColumn getSubgroupNameColumn() {
		return this.subgroupNameColumn;
	}

	public TestAnnotationColumn getAnnotationColumn() {
		return this.annotationColumn;
	}

	public TestDateColumn getDateColumn() {
		return this.dateColumn;
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
