package org.openjfx.gradefx.view.tableview.overview.columns;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.gradefx.view.tableview.test.columns.TestGradeColumn;
import org.openjfx.kafx.converter.BigDecimalConverter;
import org.openjfx.kafx.view.tableview.TableColumnFooter;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class OverviewTestGroupColumn extends TableColumnFooter<Student, Integer> {

	private final BigDecimalConverter gradeAvgConverter = new BigDecimalConverter();

	private final TestGroup testGroup;

	public OverviewTestGroupColumn(Group group, TestGroup testGroup) {
		this(group, testGroup, null, null, null);
	}

	public OverviewTestGroupColumn(Group group, TestGroup testGroup, Consumer<TableCell<Student, ?>> cellSubscription) {
		this(group, testGroup, null, null, cellSubscription);
	}

	public OverviewTestGroupColumn(Group group, TestGroup testGroup,
			Function<TestGroup, OverviewTestGroupColumn> createTestGroupColumn,
			Function<Test, TestGradeColumn> createTestColumn, Consumer<TableCell<Student, ?>> cellSubscription) {
		this.testGroup = testGroup;
		this.textProperty().bind(testGroup.nameProperty());

		this.gradeAvgConverter.getDecimalFormat().setMinimumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setRoundingMode(RoundingMode.DOWN);
		this.footerText().bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = testGroup.getAvgGrade();
			if (avg == null) {
				return "-";
			} else {
				return this.gradeAvgConverter.toString(avg);
			}
		}, testGroup.avgGrade()));

		this.setReorderable(false);
	}

	public double getWidthSum(Function<Double, Double> snapSizeX) {
		if (!this.getColumns().isEmpty()) {
			double width = 0;
			for (TableColumn<Student, ?> c : this.getColumns()) {
				if (c instanceof OverviewTestGroupColumn) {
					width += ((OverviewTestGroupColumn) c).getWidthSum(snapSizeX);
				} else {
					width += snapSizeX.apply(c.getWidth());
				}
			}
			return width;
		} else {
			return snapSizeX.apply(getWidth());
		}
	}

	public TestGroup getTestGroup() {
		return this.testGroup;
	}
}
