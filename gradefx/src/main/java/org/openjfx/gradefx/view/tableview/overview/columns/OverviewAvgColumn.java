package org.openjfx.gradefx.view.tableview.overview.columns;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.GradeSystem.Grade;
import org.openjfx.gradefx.model.Group;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.tableview.TableCellCustom;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;

public class OverviewAvgColumn extends TableColumn<Student, BigDecimal> {

	private final Group group;
	private final Map<Student, ObjectProperty<BigDecimal>> values = new HashMap<>();
	private final ObservableList<TableColumn<Student, ?>> columns;
	private final Map<Student, ChangeListener<Object>> updateListener = new HashMap<>();

	public OverviewAvgColumn(Group group, ObservableList<TableColumn<Student, ?>> columns) {
		super("\u2300"); // avg symbol in unicode
		this.group = group;
		this.columns = columns;

		this.columns.addListener((ListChangeListener<TableColumn<Student, ?>>) _ -> this.updateValues());
		this.group.gradeSystemProperty().addListener((_, _, _) -> this.updateValues());

		this.setCellValueFactory(data -> {
			ObjectProperty<BigDecimal> value = values.get(data.getValue());
			if (value == null) {
				value = new SimpleObjectProperty<>();
				values.put(data.getValue(), value);
				updateValue(data.getValue());
			}
			return value;
		});
		BigDecimalConverter avgConverter = new BigDecimalConverter();
		avgConverter.getDecimalFormat().setMinimumFractionDigits(2);
		this.setCellFactory(TableCellCustom.forTableColumn(avgConverter, Pos.CENTER));
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(false);
	}

	private void updateValues() {
		for (Student student : values.keySet()) {
			updateValue(student);
		}
	}

	private void updateValue(Student student) {
		ChangeListener<Object> listener = updateListener.get(student);
		if (listener == null) {
			listener = (_, _, _) -> updateValue(student);
			updateListener.put(student, listener);
		}

		List<BigDecimal> values = new ArrayList<>();
		List<BigDecimal> weights = new ArrayList<>();
		for (TableColumn<Student, ?> tc : columns) {
			if (tc instanceof OverviewTestColumn) {
				OverviewTestColumn testColumn = (OverviewTestColumn) tc;
				ObservableValue<Grade> grade = testColumn.getCellObservableValue(student);
				ObservableValue<BigDecimal> weight = testColumn.getTest().weightProperty();
				grade.removeListener(listener);
				grade.addListener(listener);
				weight.removeListener(listener);
				weight.addListener(listener);
				if (grade.getValue() != null) {
					values.add(new BigDecimal(grade.getValue().getNumericalValue()));
					weights.add(weight.getValue());
				}
			} else if (tc instanceof OverviewTestGroupColumn) {
				OverviewTestGroupColumn testGroupColumn = (OverviewTestGroupColumn) tc;
				ObservableValue<BigDecimal> avg = testGroupColumn.getAvg(student);
				ObservableValue<BigDecimal> weight = testGroupColumn.getTestGroup().weightProperty();
				avg.removeListener(listener);
				avg.addListener(listener);
				weight.removeListener(listener);
				weight.addListener(listener);
				if (avg.getValue() != null) {
					values.add(avg.getValue());
					weights.add(weight.getValue());
				}
			}
		}
		BigDecimal avg = this.group.getGradeSystem().calculateAverage(values.toArray(n -> new BigDecimal[n]),
				weights.toArray(n -> new BigDecimal[n]));
		this.values.get(student).set(avg);
	}

}