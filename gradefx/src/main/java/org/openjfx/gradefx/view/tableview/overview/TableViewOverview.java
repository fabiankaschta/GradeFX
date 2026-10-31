package org.openjfx.gradefx.view.tableview.overview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.gradefx.view.dialog.DialogAddStudent;
import org.openjfx.gradefx.view.tableview.columns.StudentFirstNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentIndexColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentLastNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentSubgroupNameColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewAvgColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewGradeColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewTestGroupColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestGradeColumn;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableCellEditConverter;
import org.openjfx.kafx.view.tableview.TableViewKafx;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.converter.DefaultStringConverter;

public class TableViewOverview extends TableViewKafx<Student> {

	private final Group group;
	private final Map<TestGroup, OverviewTestGroupColumn> testGroupColumns = new HashMap<>();
	private final Map<Test, TestGradeColumn> testColumns = new HashMap<>();

	public TableViewOverview(Group group) {
		// create a new list, so that sorting is not reflected to the "real" list
		// adding/removing is not supported (no need to)
		super(FXCollections.observableArrayList(group.getStudentsUnmodifiable()));
		this.group = group;
		this.group.addStudentsListener((ListChangeListener<Student>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (Student student : c.getAddedSubList()) {
						this.getItems().add(student);
					}
				}
				if (c.wasRemoved()) {
					for (Student student : c.getRemoved()) {
						this.getItems().remove(student);
					}
				}
			}
			this.sort();
		});

		this.setEditable(true);
		this.setFooterVisible(true);

		this.getSelectionModel().selectedItemProperty()
				.subscribe(selected -> GradeFXController.setSelectedStudent(selected));
		this.getSelectionModel().setCellSelectionEnabled(true);

		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));
		this.getPlaceholder().setOnMouseClicked(_ -> new DialogAddStudent(group).showAndWait());

		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));

		StudentIndexColumn indexCol = new StudentIndexColumn(group);
		indexCol.setCellFactory(_ -> {
			TableCell<Student, String> cell = new TableCellCustom<>(Pos.CENTER);
			this.addDragAndDropFeature(cell, () -> this.setSortOrderString("+:0"),
					(from, to) -> this.group.moveStudent(from, to));
			return cell;
		});
		StudentLastNameColumn lastNameCol = new StudentLastNameColumn(true);
		lastNameCol.setCellFactory(_ -> {
			TableCell<Student, String> cell = new TableCellEditConverter<>(new DefaultStringConverter());
			this.addDragAndDropFeature(cell, () -> this.setSortOrderString("+:0"),
					(from, to) -> this.group.moveStudent(from, to));
			return cell;
		});
		StudentFirstNameColumn firstNameCol = new StudentFirstNameColumn(true);
		firstNameCol.setCellFactory(_ -> {
			TableCell<Student, String> cell = new TableCellEditConverter<>(new DefaultStringConverter());
			this.addDragAndDropFeature(cell, () -> this.setSortOrderString("+:0"),
					(from, to) -> this.group.moveStudent(from, to));
			return cell;
		});
		StudentSubgroupNameColumn subgroupNameCol = new StudentSubgroupNameColumn(group, true);
		this.getColumns().add(indexCol);
		this.getColumns().add(lastNameCol);
		this.getColumns().add(firstNameCol);
		this.getColumns().add(subgroupNameCol);
		this.setFixedColumnsIndex(3);
		this.setFixedColumnsFooterText(TranslationController.translate("tab_overview_footer_avg") + ':');

		this.group.testGroupRootProperty().subscribe(root -> {
			this.getColumns()
					.removeIf(c -> c != indexCol && c != lastNameCol && c != firstNameCol && c != subgroupNameCol);
			this.testGroupColumns.clear();
			this.testColumns.clear();
			this.group.getTestsInTestGroup(root).addListener(new TestsChangedListener());
			for (TreeItem<TestGroup> t : this.group.getTestGroupRoot().getChildren()) {
				this.getColumns().add(createTestGroupColumn((TestGroup) t));
			}
			for (Test test : this.group.getTestsInTestGroup(root)) {
				this.getColumns().add(createTestColumn(test));
			}
			OverviewAvgColumn avgColumn = new OverviewAvgColumn(group, root);
			OverviewGradeColumn gradeColumn = new OverviewGradeColumn(group);
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

	public TestGradeColumn createTestColumn(Test test) {
		TestGradeColumn column = new TestGradeColumn(this.group, test);
		column.textProperty().bind(test.shortNameProperty());
		this.testColumns.put(test, column);
		return column;
	}

	public OverviewTestGroupColumn createTestGroupColumn(TestGroup testGroup) {
		OverviewTestGroupColumn column = new OverviewTestGroupColumn(this.group, testGroup);
		this.group.getTestsInTestGroup(testGroup).addListener(new TestsChangedListener());
		this.testGroupColumns.put(testGroup, column);
		for (TreeItem<TestGroup> t : testGroup.getChildren()) {
			column.getColumns().add(createTestGroupColumn((TestGroup) t));
		}
		for (Test test : group.getTestsInTestGroup(testGroup)) {
			column.getColumns().add(createTestColumn(test));
		}
		column.getColumns().add(new OverviewAvgColumn(group, testGroup));
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
