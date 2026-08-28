package org.openjfx.gradefx.view.tableview.overview.columns;

import java.math.BigDecimal;
import java.util.function.Function;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.TestGroup;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;

public class OverviewTestGroupColumn extends TableColumn<Student, Integer> {

	private final TestGroup testGroup;

	public OverviewTestGroupColumn(Group group, TestGroup testGroup) {
		this(group, testGroup, null, null);
	}

	public OverviewTestGroupColumn(Group group, TestGroup testGroup,
			Function<TestGroup, OverviewTestGroupColumn> createTestGroupColumn,
			Function<Test, OverviewTestColumn> createTestColumn) {
		this.testGroup = testGroup;
		this.textProperty().bind(testGroup.nameProperty());
		this.setReorderable(false);
		for (TreeItem<TestGroup> t : testGroup.getChildren()) {
			getColumns().add(createTestGroupColumn == null ? new OverviewTestGroupColumn(group, (TestGroup) t)
					: createTestGroupColumn.apply((TestGroup) t));
		}
		for (Test test : group.getTestsInTestGroup(testGroup)) {
			getColumns()
					.add(createTestColumn == null ? new OverviewTestColumn(group, test) : createTestColumn.apply(test));
		}
		this.getColumns().add(new OverviewAvgColumn(group, getColumns()));
	}

	public ObservableValue<BigDecimal> getAvg(Student student) {
		for (TableColumn<Student, ?> tc : getColumns()) {
			if (tc instanceof OverviewAvgColumn) {
				OverviewAvgColumn avgColumn = (OverviewAvgColumn) tc;
				return avgColumn.getCellObservableValue(student);
			}
		}
		throw new IllegalStateException("error");
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
