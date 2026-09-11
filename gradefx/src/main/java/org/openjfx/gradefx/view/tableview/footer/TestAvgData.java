package org.openjfx.gradefx.view.tableview.footer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.openjfx.gradefx.model.Grade;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.gradefx.view.tableview.test.columns.TestGradeColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestRatioColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestSumColumn;
import org.openjfx.gradefx.view.tableview.test.columns.TestTaskColumn;
import org.openjfx.kafx.converter.BigDecimalConverter;
import org.openjfx.kafx.converter.BigDecimalPercentConverter;
import org.openjfx.kafx.view.tableview.FooterData;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;

public class TestAvgData extends FooterData<Student> {

	private final Group group;
	private final Test test;
	private final Map<TestTask, StringProperty> testTaskProperties = new HashMap<>();
	private final ObjectProperty<BigDecimal> sumProperty;
	private final ObjectProperty<BigDecimal> ratioProperty;
	private final ObjectProperty<BigDecimal> gradeProperty;
	private final BigDecimalConverter avgConverter = new BigDecimalConverter();
	private final BigDecimalConverter gradeAvgConverter = new BigDecimalConverter();
	private final BigDecimalPercentConverter percentConverter = new BigDecimalPercentConverter(2);

	public TestAvgData(Group group, Test test) {
		this.group = group;
		this.test = test;

		this.sumProperty = new SimpleObjectProperty<>(this, "sumProperty" + test, null);
		this.ratioProperty = new SimpleObjectProperty<>(this, "ratioProperty" + test, null);
		this.gradeProperty = new SimpleObjectProperty<>(this, "gradeProperty" + test, null);
		this.bindSumProperty();
		this.bindRatioProperty();
		this.bindGradeProperty();
		this.gradeAvgConverter.getDecimalFormat().setMinimumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.gradeAvgConverter.getDecimalFormat().setRoundingMode(RoundingMode.DOWN);
		this.testTaskProperties.keySet().forEach(testTask -> bindTestTaskProperty(testTask));
		this.group.getStudents().addListener((ListChangeListener<Student>) _ -> {
			this.bindSumProperty();
			this.bindGradeProperty();
			this.testTaskProperties.keySet().forEach(testTask -> bindTestTaskProperty(testTask));
		});
	}

	@Override
	protected ObservableValue<String> getDataForColumn(TableColumn<Student, ?> mainColumn) {
		if (mainColumn instanceof TestTaskColumn) {
			TestTaskColumn testTaskColumn = (TestTaskColumn) mainColumn;
			TestTask testTask = testTaskColumn.getTestTask();
			return bindTestTaskProperty(testTask);
		} else if (mainColumn instanceof TestSumColumn) {
			return this.sumProperty.map(sum -> sum == null ? "-" : avgConverter.toString(sum));
		} else if (mainColumn instanceof TestRatioColumn) {
			return this.ratioProperty.map(ratio -> ratio == null ? "-" : percentConverter.toString(ratio));
		} else if (mainColumn instanceof TestGradeColumn) {
			return this.gradeProperty.map(grade -> grade == null ? "-" : gradeAvgConverter.toString(grade));
		}
		return new SimpleStringProperty("");
	}

	private ObjectProperty<BigDecimal> bindSumProperty() {
		this.sumProperty.unbind();
		this.sumProperty.bind(Bindings.createObjectBinding(() -> {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal amount = BigDecimal.ZERO;
			for (Student s : this.group.getStudents()) {
				BigDecimal points = this.test.getTotalPoints(s);
				if (points != null) {
					sum = sum.add(points);
					amount = amount.add(BigDecimal.ONE);
				}
			}
			if (amount == BigDecimal.ZERO) {
				return null;
			} else {
				return sum.divide(amount, 1, RoundingMode.HALF_UP);
			}
		}, this.group.getStudents().stream().map(s -> this.test.totalPointsProperty(s))
				.toArray(n -> new Observable[n])));
		return this.sumProperty;
	}

	private ObjectProperty<BigDecimal> bindRatioProperty() {
		this.ratioProperty.bind(Bindings.createObjectBinding(() -> {
			if (this.sumProperty.get() == null) {
				return null;
			} else {
				return this.sumProperty.get().divide(test.getTotalPoints(), 5, RoundingMode.HALF_UP);
			}
		}, this.sumProperty, this.test.totalPointsProperty()));
		return this.ratioProperty;
	}

	private ObjectProperty<BigDecimal> bindGradeProperty() {
		this.gradeProperty.bind(Bindings.createObjectBinding(() -> {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal amount = BigDecimal.ZERO;
			for (Student s : this.group.getStudents()) {
				Grade grade = this.test.getGrade(s);
				if (grade != null) {
					sum = sum.add(BigDecimal.valueOf(grade.getNumericalValue()));
					amount = amount.add(BigDecimal.ONE);
				}
			}
			if (amount == BigDecimal.ZERO) {
				return null;
			} else {
				return sum.divide(amount, 2, RoundingMode.HALF_UP);
			}
		}, this.group.getStudents().stream().map(s -> this.test.gradeProperty(s)).toArray(n -> new Observable[n])));
		return this.gradeProperty;
	}

	private StringProperty bindTestTaskProperty(TestTask testTask) {
		StringProperty property = this.testTaskProperties.get(testTask);
		if (property == null) {
			property = new SimpleStringProperty(this, "testTaskProperty" + testTask, "");
			this.testTaskProperties.put(testTask, property);
		}
		property.unbind();
		property.bind(Bindings.createStringBinding(() -> {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal amount = BigDecimal.ZERO;
			for (Student s : this.group.getStudents()) {
				BigDecimal points = testTask.getPoints(s);
				if (points != null) {
					sum = sum.add(points);
					amount = amount.add(BigDecimal.ONE);
				}
			}
			if (amount == BigDecimal.ZERO) {
				return "-";
			} else {
				return (new BigDecimalConverter()).toString(sum.divide(amount, 1, RoundingMode.HALF_UP));
			}
		}, this.group.getStudents().stream().map(s -> testTask.pointsProperty(s)).toArray(n -> new Observable[n])));
		return property;
	}

}
