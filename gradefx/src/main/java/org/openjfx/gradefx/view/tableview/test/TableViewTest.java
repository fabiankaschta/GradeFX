package org.openjfx.gradefx.view.tableview.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.gradefx.model.Test.TestTask.TestTaskPointsDecoration;
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
import org.openjfx.kafx.converter.BigDecimalConverter;
import org.openjfx.kafx.converter.BigDecimalPercentConverter;
import org.openjfx.kafx.view.tableview.TableView3;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

public class TableViewTest extends TableView3<Student> {

	private final StudentReturnColumn returnColumn;
	private final StudentLastNameColumn lastNameColumn;
	private final StudentFirstNameColumn firstNameColumn;
	private final StudentSubgroupNameColumn subgroupNameColumn;
	private final Map<TestTask, TestTaskColumn> testTaskColumns = new HashMap<>();
	private final TestSumColumn sumColumn;
	private final TestRatioColumn ratioColumn;
	private final TestGradeColumn gradeColumn;
	private final TestAnnotationColumn annotationColumn;
	private final TestDateColumn dateColumn;

	private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(this, "selectedRow", -1);

	private final BigDecimalConverter avgConverter = new BigDecimalConverter();
	private final BigDecimalConverter gradeAvgConverter = new BigDecimalConverter();
	private final BigDecimalPercentConverter percentConverter = new BigDecimalPercentConverter(2);

	@SuppressWarnings("unchecked")
	public TableViewTest(Group group, Test test) {
		super(group.getStudents());

		this.setEditable(true);

		this.getSelectionModel().setCellSelectionEnabled(true);
		this.getSelectionModel().selectedItemProperty()
				.subscribe(item -> this.selectedRowIndex.setValue(item == null ? -1 : this.getItems().indexOf(item)));
		this.getSelectionModel().selectedItemProperty()
				.subscribe(selected -> GradeFXController.setSelectedStudent(selected));

		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));

		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));

		Consumer<TableCell<Student, ?>> rowIndexSubscription = cell -> subscribeRowIndex(cell);

		this.returnColumn = new StudentReturnColumn(test, rowIndexSubscription);
		this.lastNameColumn = new StudentLastNameColumn(false, rowIndexSubscription);
		this.firstNameColumn = new StudentFirstNameColumn(false, rowIndexSubscription);
		this.subgroupNameColumn = new StudentSubgroupNameColumn(group, false, rowIndexSubscription);

		this.getColumns().add(this.returnColumn);
		this.getColumns().add(this.lastNameColumn);
		this.getColumns().add(this.firstNameColumn);
		this.getColumns().add(this.subgroupNameColumn);
		this.getFixedColumns().addAll(this.returnColumn, this.lastNameColumn, this.firstNameColumn,
				this.subgroupNameColumn);

		this.sumColumn = new TestSumColumn(test, rowIndexSubscription);
		this.ratioColumn = new TestRatioColumn(test, rowIndexSubscription);
		this.gradeColumn = new TestGradeColumn(group, test, rowIndexSubscription);
		this.annotationColumn = new TestAnnotationColumn(test, rowIndexSubscription);
		this.dateColumn = new TestDateColumn(test, rowIndexSubscription);

		this.avgConverter.getDecimalFormat().setMinimumFractionDigits(1);
		this.avgConverter.getDecimalFormat().setMaximumFractionDigits(1);
		this.gradeAvgConverter.getDecimalFormat().setRoundingMode(RoundingMode.HALF_UP);
		this.gradeAvgConverter.getDecimalFormat().setMinimumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setRoundingMode(RoundingMode.DOWN);

		this.setFooterTextFixedColumns(TranslationController.translate("tab_test_footer_avg") + ':');
		this.footerTextForColumn(this.sumColumn).bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = test.getAvgPoints();
			if (avg == null) {
				return "-";
			} else {
				return this.avgConverter.toString(avg);
			}
		}, test.avgPointsProperty()));
		this.footerTextForColumn(this.ratioColumn).bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = test.getAvgPointsRatio();
			if (avg == null) {
				return "-";
			} else {
				return this.percentConverter.toString(avg);
			}
		}, test.avgPointsRatioProperty()));
		this.footerTextForColumn(this.gradeColumn).bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = test.getAvgGrade();
			if (avg == null) {
				return "-";
			} else {
				return this.gradeAvgConverter.toString(avg);
			}
		}, test.avgGradeProperty()));

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
			} else if (event.getCode() == KeyCode.PLUS || event.getCode() == KeyCode.ADD) {
				if (!TableViewTest.this.getSelectionModel().getSelectedCells().isEmpty()) {
					TablePosition<?, ?> pos = TableViewTest.this.getSelectionModel().getSelectedCells().getFirst();
					if (pos.getTableColumn() instanceof TestTaskColumn) {
						Student student = TableViewTest.this.getSelectionModel().getSelectedItem();
						TestTask testTask = ((TestTaskColumn) pos.getTableColumn()).getTestTask();
						if (testTask.getPointsDecoration(student) == TestTaskPointsDecoration.UP) {
							testTask.setPointsDecoration(student, TestTaskPointsDecoration.NONE);
						} else {
							testTask.setPointsDecoration(student, TestTaskPointsDecoration.UP);
						}
					}
				}
			} else if (event.getCode() == KeyCode.MINUS || event.getCode() == KeyCode.SUBTRACT) {
				if (!TableViewTest.this.getSelectionModel().getSelectedCells().isEmpty()) {
					TablePosition<?, ?> pos = TableViewTest.this.getSelectionModel().getSelectedCells().getFirst();
					if (pos.getTableColumn() instanceof TestTaskColumn) {
						Student student = TableViewTest.this.getSelectionModel().getSelectedItem();
						TestTask testTask = ((TestTaskColumn) pos.getTableColumn()).getTestTask();
						if (testTask.getPointsDecoration(student) == TestTaskPointsDecoration.DOWN) {
							testTask.setPointsDecoration(student, TestTaskPointsDecoration.NONE);
						} else {
							testTask.setPointsDecoration(student, TestTaskPointsDecoration.DOWN);
						}
					}
				}
			}
		});

		this.setupTaskColumns(test.getTasksRoot());

		FontSizeController.bindTableColumnWidthToFontSize(this);
		this.getStyleClass().addAll("table-view-cell-highlight", "table-view-no-focus", "table-view-hide-empty");

		// this fixes cell selection (visibly) changing when showing/hiding columns
		for (TableColumn<Student, ?> column : this.getColumns()) {
			column.visibleProperty().addListener((_, _, _) -> {
				if (!this.getSelectionModel().getSelectedCells().isEmpty()) {
					TablePosition<?, ?> pos = this.getSelectionModel().getSelectedCells().getFirst();
					this.getSelectionModel().select(pos.getRow(), (TableColumn<Student, ?>) pos.getTableColumn());
				}
			});
		}
	}

	public StudentReturnColumn getReturnColumn() {
		return this.returnColumn;
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
			if (row != null) {
				row.indexProperty().subscribe(index -> {
					if (this.selectedRowIndex.intValue() != index.intValue()) {
						cell.pseudoClassStateChanged(PseudoClass.getPseudoClass("faint-selection"), false);
					} else {
						cell.pseudoClassStateChanged(PseudoClass.getPseudoClass("faint-selection"), true);
					}
				});
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
		this.footerTextForColumn(column).bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = task.getAvgPoints();
			if (avg == null) {
				return "-";
			} else {
				return this.avgConverter.toString(avg.setScale(1, RoundingMode.HALF_UP));
			}
		}, task.avgPointsProperty()));
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
