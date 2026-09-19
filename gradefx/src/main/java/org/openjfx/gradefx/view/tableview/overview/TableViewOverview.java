package org.openjfx.gradefx.view.tableview.overview;

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

public class TableViewOverview extends TableView3<Student> {

	private final Group group;
	private final Map<TestGroup, OverviewTestGroupColumn> testGroupColumns = new HashMap<>();
	private final Map<Test, TestGradeColumn> testColumns = new HashMap<>();
	private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(this, "selectedRow", -1);
	private final Consumer<TableCell<Student, ?>> rowIndexSubscription = cell -> subscribeRowIndex(cell);
	private final BigDecimalConverter gradeAvgConverter = new BigDecimalConverter();

	public TableViewOverview(Group group) {
		super(group.getStudents());
		this.group = group;

		this.setEditable(true);

		this.getSelectionModel().selectedItemProperty()
				.subscribe(selected -> GradeFXController.setSelectedStudent(selected));
		this.getSelectionModel().selectedItemProperty()
				.subscribe(item -> this.selectedRowIndex.setValue(item == null ? -1 : this.getItems().indexOf(item)));
		this.getSelectionModel().setCellSelectionEnabled(true);

		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));
		this.getPlaceholder().setOnMouseClicked(_ -> new DialogAddStudent(group).showAndWait());

		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));

		StudentLastNameColumn lastNameCol = new StudentLastNameColumn(true, rowIndexSubscription);
		StudentFirstNameColumn firstNameCol = new StudentFirstNameColumn(true, rowIndexSubscription);
		StudentSubgroupNameColumn subgroupNameCol = new StudentSubgroupNameColumn(group, true, rowIndexSubscription);
		this.getColumns().add(lastNameCol);
		this.getColumns().add(firstNameCol);
		this.getColumns().add(subgroupNameCol);
		this.getFixedColumns().addAll(lastNameCol, firstNameCol, subgroupNameCol);

		this.gradeAvgConverter.getDecimalFormat().setMinimumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setRoundingMode(RoundingMode.DOWN);

		this.setFooterTextFixedColumns(TranslationController.translate("tab_overview_footer_avg") + ':');

		this.group.testGroupRootProperty().subscribe(root -> {
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
			OverviewAvgColumn avgColumn = new OverviewAvgColumn(group, root, rowIndexSubscription);
			OverviewGradeColumn gradeColumn = new OverviewGradeColumn(group, rowIndexSubscription);
			this.footerTextForColumn(avgColumn).bind(Bindings.createStringBinding(() -> {
				BigDecimal avg = group.getAvgGradeAvg();
				if (avg == null) {
					return "-";
				} else {
					return this.gradeAvgConverter.toString(avg);
				}
			}, group.avgGradeAvgProperty()));
			this.footerTextForColumn(gradeColumn).bind(Bindings.createStringBinding(() -> {
				BigDecimal avg = group.getAvgGrade();
				if (avg == null) {
					return "-";
				} else {
					return this.gradeAvgConverter.toString(avg);
				}
			}, group.avgGradeProperty()));
			this.getColumns().add(avgColumn);
			this.getColumns().add(gradeColumn);
		});

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

	public TestGradeColumn createTestColumn(Test test) {
		TestGradeColumn column = new TestGradeColumn(this.group, test, rowIndexSubscription);
		column.textProperty().bind(test.shortNameProperty());
		this.testColumns.put(test, column);
		this.footerTextForColumn(column).bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = test.getAvgGrade();
			if (avg == null) {
				return "-";
			} else {
				return this.gradeAvgConverter.toString(avg);
			}
		}, test.avgGradeProperty()));
		return column;
	}

	public OverviewTestGroupColumn createTestGroupColumn(TestGroup testGroup) {
		OverviewTestGroupColumn column = new OverviewTestGroupColumn(this.group, testGroup,
				tg -> createTestGroupColumn(tg), t -> createTestColumn(t), rowIndexSubscription);
		this.group.getTestsInTestGroup(testGroup).addListener(new TestsChangedListener());
		this.testGroupColumns.put(testGroup, column);
		for (TreeItem<TestGroup> t : testGroup.getChildren()) {
			column.getColumns().add(createTestGroupColumn((TestGroup) t));
		}
		for (Test test : group.getTestsInTestGroup(testGroup)) {
			column.getColumns().add(createTestColumn(test));
		}
		column.getColumns().add(new OverviewAvgColumn(group, testGroup, rowIndexSubscription));
		this.footerTextForColumn(column).bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = testGroup.getAvgGrade();
			if (avg == null) {
				return "-";
			} else {
				return this.gradeAvgConverter.toString(avg);
			}
		}, testGroup.avgGrade()));
		return column;
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
