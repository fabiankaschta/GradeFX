package org.openjfx.gradefx.view.tableview;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableViewFullSize;

import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;

public class TableViewStudentImportPreview extends TableViewFullSize<Student> {

	private final TableColumn<Student, String> firstNameColumn, lastNameColumn, subgroupNameColumn;

	public TableViewStudentImportPreview(Group group) {
		super(FontSizeController.getFontSize() * 2 + 1);

		this.firstNameColumn = new TableColumn<Student, String>(TranslationController.translate("student_firstName"));
		this.firstNameColumn.setCellValueFactory(data -> data.getValue().firstNameProperty());
		this.firstNameColumn.setCellFactory(TableCellCustom.forTableColumn());
		this.firstNameColumn.setSortable(false);
		this.firstNameColumn.setReorderable(true);

		this.lastNameColumn = new TableColumn<Student, String>(TranslationController.translate("student_lastName"));
		this.lastNameColumn.setCellValueFactory(data -> data.getValue().lastNameProperty());
		this.lastNameColumn.setCellFactory(TableCellCustom.forTableColumn());
		this.lastNameColumn.setSortable(false);
		this.lastNameColumn.setReorderable(true);

		this.subgroupNameColumn = new TableColumn<Student, String>(
				TranslationController.translate("student_subgroupName"));
		this.subgroupNameColumn.setCellValueFactory(data -> data.getValue().subgroupNameProperty());
		this.subgroupNameColumn.setCellFactory(TableCellCustom.forTableColumn());
		this.subgroupNameColumn.setSortable(false);
		this.subgroupNameColumn.setReorderable(true);
		this.subgroupNameColumn.visibleProperty().bind(group.useSubgroupsProperty());

		this.setPadding(new Insets(0));

		this.getColumns().add(this.lastNameColumn);
		this.getColumns().add(this.firstNameColumn);
		this.getColumns().add(this.subgroupNameColumn);

		this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		this.setSelectionModel(null);

		FontSizeController.bindTableColumnWidthToFontSize(this);
		this.getStyleClass().addAll("table-view-no-focus");
	}

	public int indexOfFirstNameColumn() {
		return this.getColumns().indexOf(this.firstNameColumn);
	}

	public int indexOfLastNameColumn() {
		return this.getColumns().indexOf(this.lastNameColumn);
	}

	public int indexOfSubgroupNameColumn() {
		return this.getColumns().indexOf(this.subgroupNameColumn);
	}

}
