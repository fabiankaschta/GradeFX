package org.openjfx.gradefx.view.tableview.overview;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.Grade;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.gradefx.view.dialog.DialogAddStudent;
import org.openjfx.gradefx.view.tableview.columns.StudentFirstNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentLastNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentSubgroupNameColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewAvgColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewGradeColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewTestGroupColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestGradeColumn;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.converter.BigDecimalConverter;
import org.openjfx.kafx.view.tableview.TableView3;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

public class TableViewOverview extends TableView3<Student> {

	private final Group group;
	private final Map<TestGroup, OverviewTestGroupColumn> testGroupColumns = new HashMap<>();
	private final Map<Test, TestGradeColumn> testColumns = new HashMap<>();
	private final OverviewAvgColumn avgColumn;
	private final OverviewGradeColumn gradeColumn;
	private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(this, "selectedRow", -1);
	private final Consumer<TableCell<Student, ?>> rowIndexSubscription = cell -> subscribeRowIndex(cell);

	private final Map<Test, StringProperty> testProperties = new HashMap<>();
	private final Map<TestGroup, StringProperty> testGroupProperties = new HashMap<>();
	private final ObjectProperty<BigDecimal> avgProperty;
	private final ObjectProperty<BigDecimal> gradeProperty;
	private final BigDecimalConverter gradeAvgConverter = new BigDecimalConverter();

	public TableViewOverview(Group group) {
		super(group.getStudents());

		this.setEditable(true);

		this.getSelectionModel().selectedItemProperty()
				.subscribe(selected -> GradeFXController.setSelectedStudent(selected));
		this.getSelectionModel().selectedItemProperty()
				.subscribe(item -> this.selectedRowIndex.setValue(item == null ? -1 : this.getItems().indexOf(item)));
		this.getSelectionModel().setCellSelectionEnabled(true);

		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));
		this.getPlaceholder().setOnMouseClicked(_ -> new DialogAddStudent(group).showAndWait());

		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));

		this.group = group;
		this.avgColumn = new OverviewAvgColumn(group, this.getColumns(), rowIndexSubscription);
		this.avgColumn.getAvgValuesMap().addListener((MapChangeListener<Student, ObjectProperty<BigDecimal>>) _ -> {
			bindAvgProperty();
			bindGradeProperty();
		});
		this.gradeColumn = new OverviewGradeColumn(group, this.avgColumn, rowIndexSubscription);
		// not added here, this is done in setupTestColumns() after the test columns

		StudentLastNameColumn lastNameCol = new StudentLastNameColumn(true, rowIndexSubscription);
		StudentFirstNameColumn firstNameCol = new StudentFirstNameColumn(true, rowIndexSubscription);
		StudentSubgroupNameColumn subgroupNameCol = new StudentSubgroupNameColumn(group, true, rowIndexSubscription);

		this.getColumns().add(lastNameCol);
		this.getColumns().add(firstNameCol);
		this.getColumns().add(subgroupNameCol);
		this.getFixedColumns().addAll(lastNameCol, firstNameCol, subgroupNameCol);

		this.group.testGroupRootProperty().subscribe(root -> this.setupTestColumns(root));

		this.gradeAvgConverter.getDecimalFormat().setMinimumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setRoundingMode(RoundingMode.DOWN);
		this.avgProperty = new SimpleObjectProperty<>(this, "avgProperty" + group, null);
		this.gradeProperty = new SimpleObjectProperty<>(this, "gradeProperty" + group, null);
		this.bindAvgProperty();
		this.bindGradeProperty();
		group.getStudents().addListener((ListChangeListener<Student>) _ -> {
			this.bindAvgProperty();
			this.bindGradeProperty();
			this.testProperties.keySet().forEach(test -> bindTestProperty(test));
			this.testGroupProperties.keySet().forEach(testGroup -> bindTestGroupProperty(testGroup));
		});

		this.setFooterTextFixedColumns(TranslationController.translate("tab_overview_footer_avg") + ':');
		this.footerTextForColumn(this.avgColumn)
				.bind(this.avgProperty.map(avg -> avg == null ? "-" : gradeAvgConverter.toString(avg)));
		this.footerTextForColumn(this.gradeColumn)
				.bind(this.gradeProperty.map(grade -> grade == null ? "-" : gradeAvgConverter.toString(grade)));
		

		// DEL / BACKSPACE remove fixed state
		this.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
				// single selection
				if (!TableViewOverview.this.getSelectionModel().getSelectedCells().isEmpty()) {
					TablePosition<?, ?> pos = TableViewOverview.this.getSelectionModel().getSelectedCells().getFirst();
					if (pos.getTableColumn() instanceof TestGradeColumn) {
						TestGradeColumn column = (TestGradeColumn) pos.getTableColumn();
						Student student = TableViewOverview.this.getSelectionModel().getSelectedItem();
						if (column.getTest().isGradeFixed(student)) {
							column.getTest().setGradeFixed(student, false);
						}
					}
				}
			}
		});

		FontSizeController.bindTableColumnWidthToFontSize(this);
		this.getStyleClass().addAll("table-view-cell-highlight", "table-view-no-focus", "table-view-hide-empty");
	}

	@Override
	protected double computePrefWidth(double height) {
		double width = 0;
		for (TableColumn<Student, ?> c : this.getColumns()) {
			if (c.isVisible()) {
				if (c instanceof OverviewTestGroupColumn) {
					width += ((OverviewTestGroupColumn) c).getWidthSum(d -> snapSizeX(d));
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

	private void setupTestColumns(TestGroup root) {
		this.getColumns().removeIf(c -> !this.getFixedColumns().contains(c));
		this.testGroupColumns.clear();
		this.testColumns.clear();
		this.group.getTestsInTestGroup(root).addListener(new TestsChangedListener());
		for (TreeItem<TestGroup> t : this.group.getTestGroupRoot().getChildren()) {
			this.getColumns().add(createTestGroupColumn((TestGroup) t));
		}
		for (Test test : this.group.getTestsInTestGroup(root)) {
			this.getColumns().add(createTestColumn(test));
		}
		this.getColumns().add(this.avgColumn);
		this.getColumns().add(this.gradeColumn);
	}

	public TestGradeColumn createTestColumn(Test test) {
		TestGradeColumn column = new TestGradeColumn(this.group, test, rowIndexSubscription);
		column.textProperty().bind(test.shortNameProperty());
		this.footerTextForColumn(column).bind(bindTestProperty(test));
		this.testColumns.put(test, column);
		return column;
	}

	public OverviewTestGroupColumn createTestGroupColumn(TestGroup testGroup) {
		OverviewTestGroupColumn column = new OverviewTestGroupColumn(this.group, testGroup,
				tg -> createTestGroupColumn(tg), t -> createTestColumn(t), rowIndexSubscription);
		this.group.getTestsInTestGroup(testGroup).addListener(new TestsChangedListener());
		this.footerTextForColumn(column).bind(bindTestGroupProperty(testGroup));
		column.getAvgColumn().getAvgValuesMap().addListener(
				(MapChangeListener<Student, ObjectProperty<BigDecimal>>) _ -> bindTestGroupProperty(testGroup));
		this.testGroupColumns.put(testGroup, column);
		return column;
	}

	private ObjectProperty<BigDecimal> bindAvgProperty() {
		this.avgProperty.unbind();
		this.avgProperty.bind(Bindings.createObjectBinding(() -> {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal amount = BigDecimal.ZERO;
			for (ObjectProperty<BigDecimal> a : this.avgColumn.getAvgValuesMap().values()) {
				BigDecimal avg = a.get();
				if (avg != null) {
					sum = sum.add(avg);
					amount = amount.add(BigDecimal.ONE);
				}
			}
			if (amount == BigDecimal.ZERO) {
				return null;
			} else {
				return sum.divide(amount, 2, RoundingMode.DOWN);
			}
		}, this.avgColumn.getAvgValuesMap().values().stream().toArray(n -> new Observable[n])));
		return this.avgProperty;
	}

	private ObjectProperty<BigDecimal> bindGradeProperty() {
		this.gradeProperty.unbind();
		this.gradeProperty.bind(Bindings.createObjectBinding(() -> {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal amount = BigDecimal.ZERO;
			for (ObjectProperty<BigDecimal> a : this.avgColumn.getAvgValuesMap().values()) {
				BigDecimal avg = a.get();
				if (avg != null) {
					Grade grade = this.group.getGradeSystem().calculateGrade(avg);
					sum = sum.add(BigDecimal.valueOf(grade.getNumericalValue()));
					amount = amount.add(BigDecimal.ONE);
				}
			}
			if (amount == BigDecimal.ZERO) {
				return null;
			} else {
				return sum.divide(amount, 2, RoundingMode.DOWN);
			}
		}, this.avgColumn.getAvgValuesMap().values().stream().toArray(n -> new Observable[n])));
		return this.gradeProperty;
	}

	private StringProperty bindTestProperty(Test test) {
		StringProperty property = this.testProperties.get(test);
		if (property == null) {
			property = new SimpleStringProperty(this, "testProperty" + test, "");
			this.testProperties.put(test, property);
		}
		property.unbind();
		property.bind(Bindings.createStringBinding(() -> {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal amount = BigDecimal.ZERO;
			for (Student s : this.group.getStudents()) {
				Grade grade = test.getGrade(s);
				if (grade != null) {
					sum = sum.add(BigDecimal.valueOf(grade.getNumericalValue()));
					amount = amount.add(BigDecimal.ONE);
				}
			}
			if (amount == BigDecimal.ZERO) {
				return "-";
			} else {
				return this.gradeAvgConverter.toString(sum.divide(amount, 2, RoundingMode.DOWN));
			}
		}, this.group.getStudents().stream().map(s -> test.gradeProperty(s)).toArray(n -> new Observable[n])));
		return property;
	}

	private StringProperty bindTestGroupProperty(TestGroup testGroup) {
		StringProperty property = this.testGroupProperties.get(testGroup);
		if (property == null) {
			property = new SimpleStringProperty(this, "testGroupProperty" + testGroup, "");
			this.testGroupProperties.put(testGroup, property);
		}
		property.unbind();
		property.bind(Bindings.createStringBinding(() -> {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal amount = BigDecimal.ZERO;
			for (ObjectProperty<BigDecimal> a : this.avgColumn.getAvgValuesMap().values()) {
				BigDecimal avg = a.get();
				if (avg != null) {
					sum = sum.add(avg);
					amount = amount.add(BigDecimal.ONE);
				}
			}
			if (amount == BigDecimal.ZERO) {
				return "-";
			} else {
				return this.gradeAvgConverter.toString(sum.divide(amount, 2, RoundingMode.DOWN));
			}
		}, this.testGroupColumns.get(testGroup).getAvgColumn().getAvgValuesMap().values()
				.toArray(n -> new Observable[n])));
		return property;
	}

	private class TestsChangedListener implements ListChangeListener<Test> {

		@Override
		public void onChanged(Change<? extends Test> change) {
			while (change.next()) {
				if (change.wasAdded()) {
					List<? extends Test> added = change.getAddedSubList();
					for (Test test : added) {
						TestGroup testGroup = TableViewOverview.this.group.getTestGroup(test);
						OverviewTestGroupColumn testGroupColumn = TableViewOverview.this.testGroupColumns
								.get(testGroup);
						TestGradeColumn testColumn = createTestColumn(test);
						if (testGroupColumn == null) { // root
							// -2 before avg and grade
							TableViewOverview.this.getColumns().add(TableViewOverview.this.getColumns().size() - 2,
									testColumn);
						} else {
							// -1 before avg
							testGroupColumn.getColumns().add(testGroupColumn.getColumns().size() - 1, testColumn);
						}
					}
				}
				if (change.wasRemoved()) {
					List<? extends Test> removed = change.getRemoved();
					for (Test test : removed) {
						TestGradeColumn testColumn = TableViewOverview.this.testColumns.get(test);
						if (testColumn.getParentColumn() == null) { // root
							TableViewOverview.this.getColumns().remove(testColumn);
						} else {
							testColumn.getParentColumn().getColumns().remove(testColumn);
						}
						TableViewOverview.this.testColumns.remove(test);
					}
				}
			}
		}

	}

}
