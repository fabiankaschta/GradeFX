package org.openjfx.gradefx.view.tableview.overview.columns;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Grade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Consumer;

import org.openjfx.gradefx.model.Group;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.converter.BigDecimalConverter;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableColumnFooter;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

public class OverviewGradeColumn extends TableColumnFooter<Student, Grade> {

	private final BigDecimalConverter gradeAvgConverter = new BigDecimalConverter();

	public OverviewGradeColumn(Group group) {
		this(group, null);
	}

	public OverviewGradeColumn(Group group, Consumer<TableCell<Student, ?>> cellSubscription) {
		super(TranslationController.translate("tab_overview_grade"));
		this.setCellValueFactory(data -> Bindings.createObjectBinding(() -> {
			return group.getGradeSystem().calculateGrade(group.getTestGroupRoot().getAvgGrade(data.getValue()));
		}, group.getTestGroupRoot().avgGrade(data.getValue()), group.gradeSystemProperty()));
		this.setCellFactory(_ -> new TableCellCustom<>(group.getGradeSystem().getGradeConverter(), Pos.CENTER) {
			{
				if (cellSubscription != null) {
					cellSubscription.accept(this);
				}
			}
		});

		this.gradeAvgConverter.getDecimalFormat().setMinimumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setRoundingMode(RoundingMode.DOWN);
		this.footerText().bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = group.getAvgGrade();
			if (avg == null) {
				return "-";
			} else {
				return this.gradeAvgConverter.toString(avg);
			}
		}, group.avgGradeProperty()));

		this.setComparator(group.getGradeSystem().getGradeComparator());
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(false); // TODO editable end grade?
	}

}
