package org.openjfx.gradefx.view.tableview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openjfx.gradefx.model.GradeSystem.Grade;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.gradefx.view.style.Styles;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.tableview.TableCellCustom;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;

public class TableViewOverview extends TableView<Student> {

	private final Group group;
	private final Map<TestGroup, TestGroupColumn> testGroupColumns = new HashMap<>();
	private final Map<Test, TestColumn> testColumns = new HashMap<>();
	private final AvgColumn avgColumn;
	private final GradeColumn gradeColumn;

	public TableViewOverview(Group group) {
		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));
		this.setItems(group.getStudents());
		this.group = group;
		this.avgColumn = new AvgColumn(this.getColumns());
		this.gradeColumn = new GradeColumn(this.avgColumn);
		// not added here, this is done in setupTestColumns() after the test columns

		this.group.testGroupRootProperty().subscribe(root -> this.setupTestColumns(root));

		this.getSelectionModel().selectedItemProperty().subscribe(selected -> GroupsPane.setSelectedStudent(selected));

		this.setEditable(false);
		this.getSelectionModel().setCellSelectionEnabled(true);

		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));
		FontSizeController.bindTableColumnWidthToFontSize(this);
		Styles.subscribeTableColor(this, group.colorProperty());
	}

	private void setupTestColumns(TestGroup root) {
		this.getColumns().clear();
		testGroupColumns.clear();
		testColumns.clear();
		group.getTestsInTestGroup(root).addListener(new TestsChangedListener());
		for (TreeItem<TestGroup> t : group.getTestGroupRoot().getChildren()) {
			this.getColumns().add(new TestGroupColumn((TestGroup) t));
		}
		for (Test test : group.getTestsInTestGroup(root)) {
			getColumns().add(new TestColumn(test));
		}
		this.getColumns().add(this.avgColumn);
		this.getColumns().add(this.gradeColumn);
	}

	private class TestGroupColumn extends TableColumn<Student, Integer> {

		private final TestGroup testGroup;

		private TestGroupColumn(TestGroup testGroup) {
			this.textProperty().bind(testGroup.nameProperty());
			this.testGroup = testGroup;
			this.setReorderable(false);
			group.getTestsInTestGroup(testGroup).addListener(new TestsChangedListener());
			for (TreeItem<TestGroup> t : testGroup.getChildren()) {
				getColumns().add(new TestGroupColumn((TestGroup) t));
			}
			for (Test test : group.getTestsInTestGroup(testGroup)) {
				getColumns().add(new TestColumn(test));
			}
			this.getColumns().add(new AvgColumn(getColumns()));
			TableViewOverview.this.testGroupColumns.put(testGroup, this);
		}

		private ObservableValue<BigDecimal> getAvg(Student student) {
			for (TableColumn<Student, ?> tc : getColumns()) {
				if (tc instanceof AvgColumn) {
					AvgColumn avgColumn = (AvgColumn) tc;
					return avgColumn.getCellObservableValue(student);
				}
			}
			throw new IllegalStateException("error");
		}
	}

	private class TestColumn extends TableColumn<Student, Grade> {

		private final Test test;

		private TestColumn(Test test) {
			this.textProperty().bind(test.shortNameProperty());
			this.test = test;
			this.setCellValueFactory(data -> test.gradeProperty(data.getValue()));
			this.setCellFactory(TableCellCustom.forTableColumn(group.getGradeSystem().getGradeConverter(), Pos.CENTER));
			this.setSortable(true);
			this.setReorderable(false);
			TableViewOverview.this.testColumns.put(test, this);
		}

	}

	private class GradeColumn extends TableColumn<Student, Grade> {

		private GradeColumn(AvgColumn avgColumn) {
			super(TranslationController.translate("tab_overview_grade"));
			this.setCellValueFactory(data -> Bindings.createObjectBinding(() -> {
				return group.getGradeSystem().calculateGrade(avgColumn.getCellData(data.getValue()));
			}, avgColumn.getCellObservableValue(data.getValue()), group.gradeSystemProperty()));
			this.setCellFactory(TableCellCustom.forTableColumn(group.getGradeSystem().getGradeConverter(), Pos.CENTER));
			this.setSortable(true);
			this.setReorderable(false);
			this.setEditable(false); // TODO editable end grade?
		}

	}

	private class AvgColumn extends TableColumn<Student, BigDecimal> {

		private final Map<Student, ObjectProperty<BigDecimal>> values = new HashMap<>();
		private final ObservableList<TableColumn<Student, ?>> columns;
		private final Map<Student, ChangeListener<Object>> updateListener = new HashMap<>();

		private AvgColumn(ObservableList<TableColumn<Student, ?>> columns) {
			super("\u2300"); // avg symbol in unicode
			this.columns = columns;

			this.columns.addListener((ListChangeListener<TableColumn<Student, ?>>) _ -> this.updateValues());
			TableViewOverview.this.group.gradeSystemProperty().addListener((_, _, _) -> this.updateValues());

			this.setCellValueFactory(data -> {
				ObjectProperty<BigDecimal> value = values.get(data.getValue());
				if (value == null) {
					value = new SimpleObjectProperty<>();
					values.put(data.getValue(), value);
					updateValue(data.getValue());
				}
				return value;
			});
			BigDecimalConverter avgConverter = new BigDecimalConverter();
			avgConverter.getDecimalFormat().setMinimumFractionDigits(2);
			this.setCellFactory(TableCellCustom.forTableColumn(avgConverter, Pos.CENTER));
			this.setSortable(true);
			this.setReorderable(false);
			this.setEditable(false);
		}

		private void updateValues() {
			for (Student student : values.keySet()) {
				updateValue(student);
			}
		}

		private void updateValue(Student student) {
			ChangeListener<Object> listener = updateListener.get(student);
			if (listener == null) {
				listener = (_, _, _) -> updateValue(student);
				updateListener.put(student, listener);
			}

			List<BigDecimal> values = new ArrayList<>();
			List<BigDecimal> weights = new ArrayList<>();
			for (TableColumn<Student, ?> tc : columns) {
				if (tc instanceof TestColumn) {
					TestColumn testColumn = (TestColumn) tc;
					ObservableValue<Grade> grade = testColumn.getCellObservableValue(student);
					ObservableValue<BigDecimal> weight = testColumn.test.weightProperty();
					grade.removeListener(listener);
					grade.addListener(listener);
					weight.removeListener(listener);
					weight.addListener(listener);
					if (grade.getValue() != null) {
						values.add(new BigDecimal(grade.getValue().getNumericalValue()));
						weights.add(weight.getValue());
					}
				} else if (tc instanceof TestGroupColumn) {
					TestGroupColumn testGroupColumn = (TestGroupColumn) tc;
					ObservableValue<BigDecimal> avg = testGroupColumn.getAvg(student);
					ObservableValue<BigDecimal> weight = testGroupColumn.testGroup.weightProperty();
					avg.removeListener(listener);
					avg.addListener(listener);
					weight.removeListener(listener);
					weight.addListener(listener);
					if (avg.getValue() != null) {
						values.add(avg.getValue());
						weights.add(weight.getValue());
					}
				}
			}
			BigDecimal avg = group.getGradeSystem().calculateAverage(values.toArray(n -> new BigDecimal[n]),
					weights.toArray(n -> new BigDecimal[n]));
			this.values.get(student).set(avg);
		}

	}

	private class TestsChangedListener implements ListChangeListener<Test> {

		@Override
		public void onChanged(Change<? extends Test> change) {
			while (change.next()) {
				if (change.wasAdded()) {
					List<? extends Test> added = change.getAddedSubList();
					for (Test test : added) {
						TestGroup testGroup = group.getTestGroup(test);
						TestGroupColumn testGroupColumn = testGroupColumns.get(testGroup);
						if (testGroupColumn == null) { // root
							// -2 before avg and grade
							TableViewOverview.this.getColumns().add(TableViewOverview.this.getColumns().size() - 2,
									new TestColumn(test));
						} else {
							// -1 before avg
							testGroupColumn.getColumns().add(testGroupColumn.getColumns().size() - 1,
									new TestColumn(test));
						}
					}
				}
				if (change.wasRemoved()) {
					List<? extends Test> removed = change.getRemoved();
					for (Test test : removed) {
						TestColumn testColumn = testColumns.get(test);
						if (testColumn.getParentColumn() == null) { // root
							TableViewOverview.this.getColumns().remove(testColumn);
						} else {
							testColumn.getParentColumn().getColumns().remove(testColumn);
						}
						testColumns.remove(test);
					}
				}
			}
		}

	}

}
