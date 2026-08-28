package org.openjfx.gradefx.view.tableview.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.controlsfx.control.tableview2.TableView2;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.gradefx.view.pane.GroupsPane;
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
import org.openjfx.kafx.view.tableview.TableCellEditControl;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TreeItem;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

public class TableViewTest extends TableView2<Student> {

	private final Map<TestTask, TestTaskColumn> testTaskColumns = new HashMap<>();
	private final TestSumColumn sumColumn;
	private final TestRatioColumn ratioColumn;
	private final TestGradeColumn gradeColumn;
	private final TestAnnotationColumn annotationColumn;
	private final TestDateColumn dateColumn;
	private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(this, "selectedRow", -1);

	public TableViewTest(Group group, Test test) {
		super(group.getStudents());

		this.setEditable(true);

		this.getSelectionModel().setCellSelectionEnabled(true);
		this.getSelectionModel().selectedItemProperty().subscribe(item -> this.selectedRowIndex
				.setValue(item == null ? -1 : this.getSelectionModel().getSelectedCells().getFirst().getRow()));
		this.getSelectionModel().selectedItemProperty().subscribe(selected -> GroupsPane.setSelectedStudent(selected));

		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));

		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));

		Consumer<TableCell<Student, ?>> rowIndexSubscription = cell -> subscribeRowIndex(cell);

		StudentLastNameColumn lastNameCol = new StudentLastNameColumn(false, rowIndexSubscription);
		StudentFirstNameColumn firstNameCol = new StudentFirstNameColumn(false, rowIndexSubscription);
		StudentSubgroupNameColumn subgroupNameCol = new StudentSubgroupNameColumn(group, false, rowIndexSubscription);

		this.getColumns().add(lastNameCol);
		this.getColumns().add(firstNameCol);
		this.getColumns().add(subgroupNameCol);
		this.getFixedColumns().addAll(lastNameCol, firstNameCol, subgroupNameCol);

		this.sumColumn = new TestSumColumn(test, rowIndexSubscription);
		this.ratioColumn = new TestRatioColumn(test, this.sumColumn, rowIndexSubscription);
		this.gradeColumn = new TestGradeColumn(group, test, this.sumColumn, rowIndexSubscription);
		this.annotationColumn = new TestAnnotationColumn(test, rowIndexSubscription);
		this.dateColumn = new TestDateColumn(test, rowIndexSubscription);

		// DEL / BACKSPACE remove fixed state
		this.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
				// single selection
				if (!TableViewTest.this.getSelectionModel().getSelectedCells().isEmpty()) {
					TablePosition<?, ?> pos = TableViewTest.this.getSelectionModel().getSelectedCells().getFirst();
					if (pos.getTableColumn() == this.gradeColumn) {
						Student student = TableViewTest.this.getSelectionModel().getSelectedItem();
						if (test.isGradeFixed(student)) {
							test.setGradeFixed(student, false);
						}
					} else if (pos.getTableColumn() == this.sumColumn) {
						Student student = TableViewTest.this.getSelectionModel().getSelectedItem();
						if (test.isTotalPointsFixed(student)) {
							test.setTotalPointsFixed(student, false);
						}
					}
				}
			}
		});

		this.setupTaskColumns(test.getTasksRoot());

		FontSizeController.bindTableColumnWidthToFontSize(this);
		this.getStyleClass().addAll("table-view-cell-highlight", "table-view-no-focus", "table-view-hide-empty");

		// both necessary to clear selection correctly
		this.focusedProperty().addListener((_, _, isFocused) -> {
			if (!isFocused && this.getEditingCell() == null) {
				this.getSelectionModel().clearSelection();
			}
		});
		this.addEventHandler(TableCellEditControl.FOCUS_LOST, _ -> {
			this.getSelectionModel().clearSelection();
		});
	}

	@Override
	protected double computePrefHeight(double width) {
		double height = 0;
		TableHeaderRow header = (TableHeaderRow) this.queryAccessibleAttribute(AccessibleAttribute.HEADER);
		if (header != null) {
			height = snapSizeY(header.getHeight()) + header.snappedTopInset() + header.snappedBottomInset();
		}
		height += snapSizeY(this.getFixedCellSize()) * this.getItems().size();
		return height + this.snappedTopInset() + this.snappedBottomInset();
	}

	@Override
	protected double computePrefWidth(double height) {
		double width = 0;
		for (TableColumn<Student, ?> c : this.getColumns()) {
			if (c.isVisible()) {
				if (c instanceof TestTaskColumn) {
					width += snapSizeX(((TestTaskColumn) c).getWidthSum(d -> snapSizeX(d)));
				} else {
					width += snapSizeX(c.getWidth());
				}
			}
		}
		return width + this.snappedLeftInset() + this.snappedRightInset();
	}

	private void subscribeRowIndex(TableCell<Student, ?> cell) {
		this.selectedRowIndex.subscribe(index -> {
			if (cell.getTableRow() != null && index.intValue() == cell.getTableRow().getIndex()) {
				cell.pseudoClassStateChanged(PseudoClass.getPseudoClass("faint-selection"), true);
			} else {
				cell.pseudoClassStateChanged(PseudoClass.getPseudoClass("faint-selection"), false);
			}
		});
		cell.tableRowProperty().subscribe(row -> {
			if (row == null || this.selectedRowIndex.intValue() != row.getIndex()) {
				cell.pseudoClassStateChanged(PseudoClass.getPseudoClass("faint-selection"), false);
			} else {
				cell.pseudoClassStateChanged(PseudoClass.getPseudoClass("faint-selection"), true);
			}
		});
	}

	private void setupTaskColumns(TestTask root) {
		root.getChildren().addListener(new TasksChangedListener());
		if (!root.isLeaf()) {
			for (TreeItem<TestTask> task : root.getChildren()) {
				this.getColumns().add(createTestTaskColumn((TestTask) task));
			}
		}
		this.getColumns().add(this.sumColumn);
		this.getColumns().add(this.ratioColumn);
		this.getColumns().add(this.gradeColumn);
		this.getColumns().add(this.annotationColumn);
		this.getColumns().add(this.dateColumn);
	}

	private TestTaskColumn createTestTaskColumn(TestTask task) {
		TestTaskColumn column = new TestTaskColumn(task, t -> createTestTaskColumn(t), cell -> subscribeRowIndex(cell));
		task.getChildren().addListener(new TasksChangedListener());
		this.testTaskColumns.put(task, column);
		return column;
	}

	private class TasksChangedListener implements ListChangeListener<TreeItem<TestTask>> {

		@Override
		public void onChanged(Change<? extends TreeItem<TestTask>> change) {
			while (change.next()) {
				if (change.wasAdded()) {
					List<? extends TreeItem<TestTask>> added = change.getAddedSubList();
					for (TreeItem<TestTask> t : added) {
						TestTask task = (TestTask) t;
						TreeItem<TestTask> parent = task.getParent();
						TestTaskColumn testTaskColumn = TableViewTest.this.testTaskColumns.get(parent);
						if (testTaskColumn == null) { // root
							getColumns().add(createTestTaskColumn(task));
							if (getColumns().indexOf(TableViewTest.this.gradeColumn) != getColumns().size() - 1) {
								getColumns().remove(TableViewTest.this.sumColumn);
								getColumns().remove(TableViewTest.this.ratioColumn);
								getColumns().remove(TableViewTest.this.gradeColumn);
								getColumns().remove(TableViewTest.this.annotationColumn);
								getColumns().remove(TableViewTest.this.dateColumn);
								getColumns().add(TableViewTest.this.sumColumn);
								getColumns().add(TableViewTest.this.ratioColumn);
								getColumns().add(TableViewTest.this.gradeColumn);
								getColumns().add(TableViewTest.this.annotationColumn);
								getColumns().add(TableViewTest.this.dateColumn);
							}
						} else {
							testTaskColumn.getColumns().add(createTestTaskColumn(task));
						}
					}
				}
				if (change.wasRemoved()) {
					List<? extends TreeItem<TestTask>> removed = change.getRemoved();
					for (TreeItem<TestTask> t : removed) {
						TestTask task = (TestTask) t;
						TestTaskColumn testTaskColumn = TableViewTest.this.testTaskColumns.get(task);
						if (testTaskColumn.getParentColumn() == null) { // root
							TableViewTest.this.getColumns().remove(testTaskColumn);
						} else {
							testTaskColumn.getParentColumn().getColumns().remove(testTaskColumn);
						}
						TableViewTest.this.testTaskColumns.remove(task);
					}
				}
			}
		}

	}

}
