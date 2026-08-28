package org.openjfx.gradefx.view.tableview.overview;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.gradefx.view.tableview.columns.StudentFirstNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentLastNameColumn;
import org.openjfx.gradefx.view.tableview.columns.StudentSubgroupNameColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewAvgColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewGradeColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewTestColumn;
import org.openjfx.gradefx.view.tableview.overview.columns.OverviewTestGroupColumn;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.style.Styles;
import org.openjfx.kafx.view.tableview.TableViewFullSize;

import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;

public class TableViewOverviewPrint extends TableViewFullSize<Student> {

	private final Group group;
	private final OverviewAvgColumn avgColumn;
	private final OverviewGradeColumn gradeColumn;
	private final TableColumn<Student, String> firstNameCol, lastNameCol, subgroupNameCol;

	public TableViewOverviewPrint(Group group) {
		// create a new list, so that sorting is not reflected to the "real" list
		// adding/removing is not supported (no need to)
		super(25, FXCollections.observableArrayList(group.getStudents()));
		this.group = group;

		this.setEditable(false);

		this.setSelectionModel(null);

		this.setPlaceholder(new Text(TranslationController.translate("tab_overview_no_students")));

		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));

		this.avgColumn = new OverviewAvgColumn(group, this.getColumns());
		this.gradeColumn = new OverviewGradeColumn(group, this.avgColumn);
		// not added here, this is done in setupTestColumns() after the test columns

		this.lastNameCol = new StudentLastNameColumn(false);
		this.firstNameCol = new StudentFirstNameColumn(false);
		this.subgroupNameCol = new StudentSubgroupNameColumn(group, false);

		this.getColumns().add(this.lastNameCol);
		this.getColumns().add(this.firstNameCol);
		this.getColumns().add(this.subgroupNameCol);
		for (TreeItem<TestGroup> t : this.group.getTestGroupRoot().getChildren()) {
			this.getColumns().add(new OverviewTestGroupColumn(this.group, (TestGroup) t));
		}
		for (Test test : this.group.getTestsInTestGroup(this.group.getTestGroupRoot())) {
			this.getColumns().add(new OverviewTestColumn(this.group, test));
		}
		this.getColumns().add(this.avgColumn);
		this.getColumns().add(this.gradeColumn);

		FontSizeController.bindTableColumnWidthToFontSize(this);
		Styles.subscribeThemeColor(this, group.colorProperty());
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

}
