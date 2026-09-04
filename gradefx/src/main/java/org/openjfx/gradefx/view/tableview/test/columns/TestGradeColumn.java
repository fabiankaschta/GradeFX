package org.openjfx.gradefx.view.tableview.test.columns;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.Grade;
import org.openjfx.gradefx.model.GradeSystem;

import java.util.function.Consumer;

import org.openjfx.gradefx.model.Group;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellEditComparator;

import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Subscription;

public class TestGradeColumn extends TableColumn<Student, Grade> {

	public TestGradeColumn(Group group, Test test, TestSumColumn sumColumn) {
		this(group, test, sumColumn, null);
	}

	public TestGradeColumn(Group group, Test test, TestSumColumn sumColumn,
			Consumer<TableCell<Student, ?>> cellSubscription) {
		super(TranslationController.translate("test_grade"));
		this.setCellValueFactory(data -> test.gradeProperty(data.getValue()));
		GradeSystem gradeSystem = group.getGradeSystem();
		this.setCellFactory(_ -> new TableCellEditComparator<>(gradeSystem.getGradeComparator(), gradeSystem.getWorst(),
				gradeSystem.getBest(), gradeSystem.getGradeConverter(), Pos.CENTER, true) {

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
				if (cellSubscription != null) {
					cellSubscription.accept(this);
				}
			}
		});
		this.setComparator(gradeSystem.getGradeComparator());
		this.setSortable(true);
		this.setReorderable(false);
		this.setOnEditCommit(e -> {
			if (e.getNewValue() != e.getOldValue() && (e.getNewValue() == null && e.getOldValue() != null
					|| e.getNewValue() != null && e.getOldValue() == null
					|| gradeSystem.getGradeComparator().compare(e.getNewValue(), e.getOldValue()) != 0)) {
				if (test.getUsePoints()) {
					test.setGradeFixed(e.getRowValue(), true);
				}
				test.setGrade(e.getRowValue(), e.getNewValue());
			}
		});
	}

}
