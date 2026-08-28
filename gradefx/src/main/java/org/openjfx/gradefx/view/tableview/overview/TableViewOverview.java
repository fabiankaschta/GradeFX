package org.openjfx.gradefx.view.tableview.overview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.tableview2.TableView2;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.gradefx.view.tableview.columns.StudentFirstNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentLastNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentSubgroupNameColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewAvgColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewGradeColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewTestColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewTestGroupColumn;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellEditControl;

import javafx.collections.ListChangeListener;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.text.Text;

public class TableViewOverview extends TableView2<Student> {

	private final Group group;
	private final Map<TestGroup, OverviewTestGroupColumn> testGroupColumns = new HashMap<>();
	private final Map<Test, OverviewTestColumn> testColumns = new HashMap<>();
	private final OverviewAvgColumn avgColumn;
	private final OverviewGradeColumn gradeColumn;

	public TableViewOverview(Group group) {
		super(group.getStudents());

		this.setEditable(true);

		this.getSelectionModel().selectedItemProperty().subscribe(selected -> GroupsPane.setSelectedStudent(selected));
		this.getSelectionModel().setCellSelectionEnabled(true);

		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));

		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));

		this.group = group;
		this.avgColumn = new OverviewAvgColumn(group, this.getColumns());
		this.gradeColumn = new OverviewGradeColumn(group, this.avgColumn);
		// not added here, this is done in setupTestColumns() after the test columns

		StudentLastNameColumn lastNameCol = new StudentLastNameColumn(true);
		StudentFirstNameColumn firstNameCol = new StudentFirstNameColumn(true);
		StudentSubgroupNameColumn subgroupNameCol = new StudentSubgroupNameColumn(group, true);

		this.getColumns().add(lastNameCol);
		this.getColumns().add(firstNameCol);
		this.getColumns().add(subgroupNameCol);
		this.getFixedColumns().addAll(lastNameCol, firstNameCol, subgroupNameCol);

		this.group.testGroupRootProperty().subscribe(root -> this.setupTestColumns(root));

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
				if (c instanceof OverviewTestGroupColumn) {
					width += ((OverviewTestGroupColumn) c).getWidthSum(d -> snapSizeX(d));
				} else {
					width += snapSizeX(c.getWidth());
				}
			}
		}
		return width + this.snappedLeftInset() + this.snappedRightInset();
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

	public OverviewTestColumn createTestColumn(Test test) {
		OverviewTestColumn column = new OverviewTestColumn(this.group, test);
		this.testColumns.put(test, column);
		return column;
	}

	public OverviewTestGroupColumn createTestGroupColumn(TestGroup testGroup) {
		OverviewTestGroupColumn column = new OverviewTestGroupColumn(this.group, testGroup,
				tg -> createTestGroupColumn(tg), t -> createTestColumn(t));
		this.group.getTestsInTestGroup(testGroup).addListener(new TestsChangedListener());
		this.testGroupColumns.put(testGroup, column);
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
						OverviewTestColumn testColumn = createTestColumn(test);
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
						OverviewTestColumn testColumn = TableViewOverview.this.testColumns.get(test);
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
