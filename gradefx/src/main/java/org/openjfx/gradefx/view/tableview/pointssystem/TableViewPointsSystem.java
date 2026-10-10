package org.openjfx.gradefx.view.tableview.pointssystem;

import org.openjfx.gradefx.model.Grade;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.tableview.pointssystem.columns.PointsSystemAmountColumn;
import org.openjfx.gradefx.view.tableview.pointssystem.columns.PointsSystemGradeColumn;
import org.openjfx.gradefx.view.tableview.pointssystem.columns.PointsSystemLowerBoundColumn;
import org.openjfx.gradefx.view.tableview.pointssystem.columns.PointsSystemRatioColumn;
import org.openjfx.gradefx.view.tableview.pointssystem.columns.PointsSystemUpperBoundColumn;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.view.tableview.TableCellEditControl;
import org.openjfx.kafx.view.tableview.TableViewFullSize;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;

public class TableViewPointsSystem extends TableViewFullSize<Grade> {

	private final ObservableList<Student> students;

	public TableViewPointsSystem(Group group, Test test, boolean printMode) {
		super(FontSizeController.fontSizeProperty().multiply(2),
				FXCollections.observableArrayList(group.getGradeSystem().getPossibleGradesDESC()));

		this.setPadding(new Insets(0));

		ObservableList<Student> baseList = FXCollections.observableArrayList(student -> new Observable[] {
				test.onlyDefaultDateProperty(), test.dateProperty(), test.dateProperty(student) });
		baseList.addAll(group.getStudents());
		this.students = new FilteredList<>(baseList, student -> !test.isOnlyDefaultDate() || test.getDate() == null
				|| test.getDate(student) == null || test.getDate(student).equals(test.getDate()));
		group.getStudents().addListener((ListChangeListener<Student>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					baseList.addAll(c.getAddedSubList());
				}
				if (c.wasRemoved()) {
					baseList.removeAll(c.getRemoved());
				}
			}
		});

		PointsSystemLowerBoundColumn fromColumn = new PointsSystemLowerBoundColumn(group, test, printMode);
		PointsSystemUpperBoundColumn toColumn = new PointsSystemUpperBoundColumn(group, test, printMode);
		PointsSystemGradeColumn gradeColumn = new PointsSystemGradeColumn();
		PointsSystemAmountColumn amountColumn = new PointsSystemAmountColumn(test);
		PointsSystemRatioColumn ratioColumn = new PointsSystemRatioColumn(test);

		this.getColumns().add(fromColumn);
		this.getColumns().add(toColumn);
		this.getColumns().add(gradeColumn);
		this.getColumns().add(amountColumn);
		this.getColumns().add(ratioColumn);

		fromColumn.visibleProperty().bind(test.usePointsProperty());
		toColumn.visibleProperty().bind(test.usePointsProperty());

		FontSizeController.bindTableColumnWidthToFontSize(this);

		if (printMode) {
			this.setEditable(false);
			this.setSelectionModel(null);
		} else {
			this.setEditable(true);
			this.getSelectionModel().setCellSelectionEnabled(true);

			// both necessary to clear selection correctly
			this.focusedProperty().addListener((_, _, isFocused) -> {
				if (!isFocused && this.getEditingCell() == null) {
					this.getSelectionModel().clearSelection();
				}
			});
			this.addEventHandler(TableCellEditControl.FOCUS_LOST, _ -> {
				if (!this.isFocused()) {
					this.getSelectionModel().clearSelection();
				}
			});
		}
	}

	public ObservableList<Student> getFilteredStudents() {
		return this.students;
	}

}
