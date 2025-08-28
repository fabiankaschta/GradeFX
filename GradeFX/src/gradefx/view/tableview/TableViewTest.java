package gradefx.view.tableview;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafx.controller.Controller;
import kafx.lang.Translator;
import gradefx.model.GradeSystem.Grade;
import gradefx.model.Group;
import gradefx.model.Student;
import gradefx.model.Test;
import gradefx.model.Test.TestTask;
import gradefx.view.pane.GroupsPane;
import gradefx.view.style.Styles;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Subscription;
import kafx.view.converter.BigDecimalConverter;
import kafx.view.converter.BigDecimalPercentConverter;
import kafx.view.tableview.TableCellCustom;
import kafx.view.tableview.TableCellEditComparable;

public class TableViewTest extends TableView<Student> {

	private final Group group;
	private final Test test;
	private final Map<TestTask, TestTaskColumn> testTaskColumns = new HashMap<>();
	private final SumColumn sumColumn;
	private final RatioColumn ratioColumn;
	private final GradeColumn gradeColumn;
	private final BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

	public TableViewTest(Group group, Test test) {
		super(group.getStudents());
		this.test = test;
		this.group = group;

		this.setPlaceholder(new Text(Translator.get("tab_overview_no_students")));

		this.sumColumn = new SumColumn();
		this.sumColumn.visibleProperty().bind(test.usePointsProperty());
		this.ratioColumn = new RatioColumn(this.sumColumn);
		this.ratioColumn.visibleProperty().bind(test.usePointsProperty());
		this.gradeColumn = new GradeColumn(this.sumColumn);

		this.getSelectionModel().selectedItemProperty().subscribe(selected -> GroupsPane.setSelectedStudent(selected));

		this.setEditable(true);
		this.getSelectionModel().setCellSelectionEnabled(true);

		// DEL / BACKSPACE remove fixed state
		this.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
				// single selection
				TablePosition<?, ?> pos = TableViewTest.this.getSelectionModel().getSelectedCells().get(0);
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
		});

		this.setupTaskColumns(test.getTasksRoot());

		Controller.bindTableColumnWidthToFontSize(this);
		Styles.subscribeTableColor(this, group.colorProperty());
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
	}

	private class TestTaskColumn extends TableColumn<Student, BigDecimal> {

		private final Label name = new Label();
		private final Label points = new Label();

		private TestTaskColumn(TestTask testTask) {
			this.setReorderable(false);
			this.name.textProperty().bind(testTask.nameProperty());
			this.points.textProperty().bind(testTask.maxPointsProperty()
					.map(v -> bigDecimalConverter.toString(v) + " " + Translator.get("test_points_short")));
			this.points.setStyle("-fx-font-style: italic; -fx-font-weight: normal;");
			BorderPane graphic = new BorderPane();
			BorderPane.setAlignment(this.name, Pos.CENTER);
			BorderPane.setAlignment(this.points, Pos.CENTER);
			graphic.setCenter(this.name);
			this.setGraphic(graphic);
			this.setCellValueFactory(data -> testTask.pointsProperty(data.getValue()));
			this.setCellFactory(
					// TODO if points > max ...
					TableCellEditComparable.forTableColumn(BigDecimal.ZERO, null, bigDecimalConverter, Pos.CENTER,
							true));
			testTask.leafProperty().subscribe(isLeaf -> {
				if (isLeaf) {
					graphic.setBottom(this.points);
					getColumns().clear();
				} else {
					graphic.setBottom(null);
					for (TreeItem<TestTask> t : testTask.getChildren()) {
						getColumns().add(new TestTaskColumn((TestTask) t));
					}
				}
			});
			testTask.getChildren().addListener(new TasksChangedListener());
			testTaskColumns.put(testTask, this);
		}
	}

	private class GradeColumn extends TableColumn<Student, Grade> {

		private GradeColumn(SumColumn sumColumn) {
			super(Translator.get("tab_test_grade"));
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
				}
			});
			this.setSortable(true);
			this.setReorderable(false);
			this.setOnEditCommit(e -> {
				if (e.getNewValue() == null && e.getOldValue() != null
						|| e.getNewValue() != null && e.getOldValue() == null
						|| e.getNewValue().compareTo(e.getOldValue()) != 0) {
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
						}
					});
			this.name.setText("\u2211");
			this.points.textProperty().bind(test.totalPointsProperty()
					.map(v -> bigDecimalConverter.toString(v) + " " + Translator.get("test_points_short")));
			this.points.setStyle("-fx-font-style: italic; -fx-font-weight: normal;");
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
			ObservableList<Observable> observables = FXCollections.observableArrayList();
			setCellValueFactory(data -> Bindings.createObjectBinding(() -> {
				observables.add(sumColumn.getCellObservableValue(data.getValue()));
				observables.add(test.totalPointsProperty());
				BigDecimal sum = sumColumn.getCellData(data.getValue());
				if (sum == null || test.getTotalPoints().compareTo(BigDecimal.ZERO) == 0) {
					return null;
				} else {
					return sum.divide(test.getTotalPoints(), 5, RoundingMode.FLOOR);
				}
			}, observables.toArray(n -> new Observable[n])));
			setCellFactory(TableCellCustom.forTableColumn(new BigDecimalPercentConverter(2), Pos.CENTER));
			setSortable(true);
			setReorderable(false);
			editableProperty().bind(test.useTasksProperty().not());
			this.minWidthProperty().bind(Controller.fontSizeProperty().multiply(5));
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
								getColumns().add(sumColumn);
								getColumns().add(ratioColumn);
								getColumns().add(gradeColumn);
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
