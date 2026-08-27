package org.openjfx.gradefx.view.tableview;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.tableview2.TableView2;
import org.openjfx.gradefx.model.GradeSystem.Grade;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.converter.BigDecimalPercentConverter;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableCellEditComparable;
import org.openjfx.kafx.view.tableview.TableCellEditControl;
import org.openjfx.kafx.view.tableview.TableCellEditConverter;
import org.openjfx.kafx.view.tableview.TableCellEditDatePicker;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TreeItem;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Subscription;
import javafx.util.converter.DefaultStringConverter;

public class TableViewTest extends TableView2<Student> {

	private final Group group;
	private final Test test;
	private final Map<TestTask, TestTaskColumn> testTaskColumns = new HashMap<>();
	private final SumColumn sumColumn;
	private final RatioColumn ratioColumn;
	private final GradeColumn gradeColumn;
	private final AnnotationColumn annotationColumn;
	private final DateColumn dateColumn;
	private final BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();
	private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(this, "selectedRow", -1);

	public TableViewTest(Group group, Test test) {
		super(group.getStudents());
		this.test = test;
		this.group = group;
		this.setEditable(true);
		this.getSelectionModel().setCellSelectionEnabled(true);

		this.getSelectionModel().selectedItemProperty().addListener((_, _, newItem) -> {
			if (newItem == null) {
				selectedRowIndex.setValue(-1);
			} else {
				selectedRowIndex.setValue(this.getSelectionModel().getSelectedCells().getFirst().getRow());
			}
		});

		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));
		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));
		TableColumn<Student, String> firstNameCol = new TableColumn<Student, String>(
				TranslationController.translate("student_firstName"));
		firstNameCol.setCellValueFactory(data -> data.getValue().firstNameProperty());
		firstNameCol.setCellFactory(_ -> new TableCellCustom<>() {
			{
				subscribeRowIndex(this);
			}
		});
		firstNameCol.setSortable(true);
		firstNameCol.setReorderable(false);
		firstNameCol.setEditable(false);

		TableColumn<Student, String> lastNameCol = new TableColumn<Student, String>(
				TranslationController.translate("student_lastName"));
		lastNameCol.setCellValueFactory(data -> data.getValue().lastNameProperty());
		lastNameCol.setCellFactory(_ -> new TableCellCustom<>() {
			{
				subscribeRowIndex(this);
			}
		});
		lastNameCol.setSortable(true);
		lastNameCol.setReorderable(false);
		lastNameCol.setEditable(false);

		TableColumn<Student, String> subgroupNameCol = new TableColumn<Student, String>(
				TranslationController.translate("student_subgroupName"));
		subgroupNameCol.setCellValueFactory(data -> data.getValue().subgroupNameProperty());
		subgroupNameCol.setCellFactory(_ -> new TableCellCustom<>() {
			{
				subscribeRowIndex(this);
			}
		});
		subgroupNameCol.setSortable(true);
		subgroupNameCol.setReorderable(false);
		subgroupNameCol.setEditable(false);
		subgroupNameCol.visibleProperty().bind(group.useSubgroupsProperty());

		this.getColumns().add(lastNameCol);
		this.getColumns().add(firstNameCol);
		this.getColumns().add(subgroupNameCol);
		this.getFixedColumns().addAll(lastNameCol, firstNameCol, subgroupNameCol);

		this.sumColumn = new SumColumn();
		this.sumColumn.visibleProperty().bind(test.usePointsProperty());
		this.ratioColumn = new RatioColumn(this.sumColumn);
		this.ratioColumn.visibleProperty().bind(test.usePointsProperty());
		this.gradeColumn = new GradeColumn(this.sumColumn);
		this.annotationColumn = new AnnotationColumn();
		this.dateColumn = new DateColumn();

		this.getSelectionModel().selectedItemProperty().subscribe(selected -> GroupsPane.setSelectedStudent(selected));

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
			height = header.getHeight();
		}
		height += this.getFixedCellSize() * this.getItems().size();
		return height + this.snappedTopInset() + this.snappedBottomInset();
	}

	@Override
	protected double computePrefWidth(double height) {
		double width = 0;
		for (TableColumn<Student, ?> c : this.getColumns()) {
			if (c instanceof TestTaskColumn) {
				width += ((TestTaskColumn) c).getWidthSum();
			} else {
				width += c.getWidth();
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
				getColumns().add(new TestTaskColumn((TestTask) task));
			}
		}
		this.getColumns().add(sumColumn);
		this.getColumns().add(ratioColumn);
		this.getColumns().add(gradeColumn);
		this.getColumns().add(annotationColumn);
		this.getColumns().add(dateColumn);
	}

	private class TestTaskColumn extends TableColumn<Student, BigDecimal> {

		private final Label name = new Label();
		private final Label points = new Label();

		private TestTaskColumn(TestTask testTask) {
			this.setReorderable(false);
			this.name.textProperty().bind(testTask.nameProperty());
			this.name.setStyle("-fx-text-fill: -fx-text-base-color;");
			this.points.textProperty().bind(testTask.maxPointsProperty().map(
					v -> bigDecimalConverter.toString(v) + " " + TranslationController.translate("test_points_short")));
			this.points
					.setStyle("-fx-text-fill: -fx-text-base-color; -fx-font-style: italic; -fx-font-weight: normal;");
			BorderPane graphic = new BorderPane();
			BorderPane.setAlignment(this.name, Pos.CENTER);
			BorderPane.setAlignment(this.points, Pos.CENTER);
			graphic.setCenter(this.name);
			this.setGraphic(graphic);
			this.setCellValueFactory(data -> testTask.pointsProperty(data.getValue()));
			this.setCellFactory(
					_ -> new TableCellEditComparable<>(BigDecimal.ZERO, null, bigDecimalConverter, Pos.CENTER, true) {
						{
							// TODO if points > max ...
							subscribeRowIndex(this);
						}
					});
			testTask.leafProperty().subscribe(isLeaf -> {
				if (isLeaf) {
					graphic.setBottom(this.points);
					getColumns().clear();
				} else {
					graphic.setBottom(null);
				}
			});
			if (!testTask.isLeaf()) {
				for (TreeItem<TestTask> t : testTask.getChildren()) {
					getColumns().add(new TestTaskColumn((TestTask) t));
				}
			}
			testTask.getChildren().addListener(new TasksChangedListener());
			testTaskColumns.put(testTask, this);
		}

		public double getWidthSum() {
			if (!this.getColumns().isEmpty()) {
				double width = 0;
				for (TableColumn<Student, ?> c : this.getColumns()) {
					if (c instanceof TestTaskColumn) {
						width += ((TestTaskColumn) c).getWidthSum();
					} else {
						width += c.getWidth();
					}
				}
				return width;
			} else {
				return getWidth();
			}
		}

	}

	private class GradeColumn extends TableColumn<Student, Grade> {

		private GradeColumn(SumColumn sumColumn) {
			super(TranslationController.translate("test_grade"));
			this.setCellValueFactory(data -> test.gradeProperty(data.getValue()));
			this.setCellFactory(_ -> new TableCellEditComparable<>(group.getGradeSystem().getWorst(),
					group.getGradeSystem().getBest(), group.getGradeSystem().getGradeConverter(), Pos.CENTER, true) {

				Subscription studentSubscription;
				Subscription fixedSubscription;
				{
					this.tableRowProperty().subscribe(row -> {
						if (row != null) {
							if (studentSubscription != null) {
								studentSubscription.unsubscribe();
							}
							studentSubscription = row.itemProperty().subscribe(student -> {
								if (fixedSubscription != null) {
									fixedSubscription.unsubscribe();
								}
								if (student != null) {
									fixedSubscription = test.gradeFixedProperty(student).subscribe(fixed -> this
											.pseudoClassStateChanged(PseudoClass.getPseudoClass("fixed-value"), fixed));
								}
							});
						}
					});
					subscribeRowIndex(this);
				}
			});
			this.setSortable(true);
			this.setReorderable(false);
			this.setOnEditCommit(e -> {
				if (e.getNewValue() != e.getOldValue() && (e.getNewValue() == null && e.getOldValue() != null
						|| e.getNewValue() != null && e.getOldValue() == null
						|| e.getNewValue().compareTo(e.getOldValue()) != 0)) {
					if (test.getUsePoints()) {
						test.setGradeFixed(e.getRowValue(), true);
					}
					test.setGrade(e.getRowValue(), e.getNewValue());
				}
			});
		}

	}

	private class SumColumn extends TableColumn<Student, BigDecimal> {

		private final Label name = new Label();
		private final Label points = new Label();

		private SumColumn() {
			this.setCellValueFactory(data -> test.totalPointsProperty(data.getValue()));
			this.setCellFactory(
					_ -> new TableCellEditComparable<>(BigDecimal.ZERO, null, bigDecimalConverter, Pos.CENTER, true) {

						Subscription studentSubscription;
						Subscription fixedSubscription;
						{
							this.tableRowProperty().subscribe(row -> {
								if (row != null) {
									if (studentSubscription != null) {
										studentSubscription.unsubscribe();
									}
									studentSubscription = row.itemProperty().subscribe(student -> {
										if (fixedSubscription != null) {
											fixedSubscription.unsubscribe();
										}
										if (student != null) {
											fixedSubscription = test.totalPointsFixedProperty(student)
													.subscribe(fixed -> this.pseudoClassStateChanged(
															PseudoClass.getPseudoClass("fixed-value"), fixed));
										}
									});
								}
							});
							subscribeRowIndex(this);
						}
					});
			this.name.setText("\u2211");
			this.name.setStyle("-fx-text-fill: -fx-text-base-color;");
			this.points.textProperty().bind(test.totalPointsProperty().map(
					v -> bigDecimalConverter.toString(v) + " " + TranslationController.translate("test_points_short")));
			this.points
					.setStyle("-fx-text-fill: -fx-text-base-color; -fx-font-style: italic; -fx-font-weight: normal;");
			BorderPane graphic = new BorderPane();
			BorderPane.setAlignment(this.name, Pos.CENTER);
			BorderPane.setAlignment(this.points, Pos.CENTER);
			graphic.setCenter(this.name);
			graphic.setBottom(this.points);
			this.setGraphic(graphic);
			this.setSortable(true);
			this.setReorderable(false);
			this.setEditable(true);
			this.setOnEditCommit(e -> {
				if (e.getNewValue() == null && e.getOldValue() == null) {
					return;
				} else if (e.getNewValue() == null && e.getOldValue() != null
						|| e.getNewValue() != null && e.getOldValue() == null
						|| e.getNewValue().compareTo(e.getOldValue()) != 0) {
					if (test.getUseTasks()) {
						test.setTotalPointsFixed(e.getRowValue(), true);
					}
					test.setTotalPoints(e.getRowValue(), e.getNewValue());
				}
			});
		}

	}

	private class RatioColumn extends TableColumn<Student, BigDecimal> {

		private RatioColumn(SumColumn sumColumn) {
			super("%");
			this.setCellValueFactory(data -> Bindings.createObjectBinding(() -> {
				BigDecimal sum = sumColumn.getCellData(data.getValue());
				if (sum == null || test.getTotalPoints().compareTo(BigDecimal.ZERO) == 0) {
					return null;
				} else {
					return sum.divide(test.getTotalPoints(), 5, RoundingMode.FLOOR);
				}
			}, test.totalPointsProperty(), sumColumn.getCellObservableValue(data.getValue())));
			this.setCellFactory(_ -> new TableCellCustom<>(new BigDecimalPercentConverter(2), Pos.CENTER) {
				{
					subscribeRowIndex(this);
				}
			});
			this.setSortable(true);
			this.setReorderable(false);
			this.editableProperty().bind(test.useTasksProperty().not());
			this.minWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(5));
		}

	}

	private class AnnotationColumn extends TableColumn<Student, String> {

		private AnnotationColumn() {
			super(TranslationController.translate("test_annotation"));
			this.setCellValueFactory(data -> test.annotationProperty(data.getValue()));
			this.setCellFactory(_ -> new TableCellEditConverter<>(new DefaultStringConverter(), true) {
				{
					subscribeRowIndex(this);
				}
			});
			this.setSortable(true);
			this.setReorderable(false);
			this.setEditable(true);
			this.minWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(15));
		}

	}

	private class DateColumn extends TableColumn<Student, LocalDate> {

		private DateColumn() {
			super(TranslationController.translate("test_date"));
			this.setCellValueFactory(data -> test.dateProperty(data.getValue()));
			this.setCellFactory(_ -> new TableCellEditDatePicker<>(true) {
				{
					subscribeRowIndex(this);
				}
			});
			this.setSortable(true);
			this.setReorderable(false);
			this.setEditable(true);
			this.minWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(7));
		}

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
						TestTaskColumn testTaskColumn = testTaskColumns.get(parent);
						if (testTaskColumn == null) { // root
							getColumns().add(new TestTaskColumn(task));
							if (getColumns().indexOf(gradeColumn) != getColumns().size() - 1) {
								getColumns().remove(sumColumn);
								getColumns().remove(ratioColumn);
								getColumns().remove(gradeColumn);
								getColumns().remove(annotationColumn);
								getColumns().remove(dateColumn);
								getColumns().add(sumColumn);
								getColumns().add(ratioColumn);
								getColumns().add(gradeColumn);
								getColumns().add(annotationColumn);
								getColumns().add(dateColumn);
							}
						} else {
							testTaskColumn.getColumns().add(new TestTaskColumn(task));
						}
					}
				}
				if (change.wasRemoved()) {
					List<? extends TreeItem<TestTask>> removed = change.getRemoved();
					for (TreeItem<TestTask> t : removed) {
						TestTask task = (TestTask) t;
						TestTaskColumn testTaskColumn = testTaskColumns.get(task);
						if (testTaskColumn.getParentColumn() == null) { // root
							TableViewTest.this.getColumns().remove(testTaskColumn);
						} else {
							testTaskColumn.getParentColumn().getColumns().remove(testTaskColumn);
						}
						testTaskColumns.remove(task);
					}
				}
			}
		}

	}

}
