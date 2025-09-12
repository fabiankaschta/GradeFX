package org.openjfx.gradefx.view.tableview;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.openjfx.gradefx.model.GradeSystem.Grade;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.PointsSystem;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.style.Styles;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.converter.BigDecimalPercentConverter;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableCellEditComparable;
import org.openjfx.kafx.view.tableview.TableViewFullSize;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;

public class TableViewPointsSystem extends TableViewFullSize<Grade> {

	private final Group group;
	private final Test test;
	private final Grade[] grades;
	private final FilteredList<Student> students;
	private final BooleanProperty filtered = new SimpleBooleanProperty(this, "filtered", false);
	private final AmountColumn amountColumn;
	private final ReadOnlyObjectWrapper<BigDecimal> gradeAVG = new ReadOnlyObjectWrapper<>(this, "gradeAVG");
	private final ReadOnlyIntegerWrapper graded = new ReadOnlyIntegerWrapper(this, "graded");

	public TableViewPointsSystem(Group group, Test test) {
		super(FontSizeController.fontSizeProperty().multiply(2),
				FXCollections.observableArrayList(group.getGradeSystem().getPossibleGradesDESC()));
		this.group = group;
		this.test = test;
		this.grades = group.getGradeSystem().getPossibleGradesDESC();
		this.students = new FilteredList<>(group.getStudents());
		this.updateStudentFilter();
		this.group.getStudents().addListener((ListChangeListener<Student>) _ -> updateStudentFilter());
		this.setPadding(new Insets(0));

		PointsSystem pointsSystem = test.getPointsSystem();
		// TODO fix 15 vs 15.0 on update
		BigDecimalConverter pointsConverter = new BigDecimalConverter();

		TableColumn<Grade, BigDecimal> fromColumn = new TableColumn<>(
				TranslationController.translate("pointsSystem_from"));
		fromColumn.setCellValueFactory(data -> pointsSystem.lowerBoundForGrade(data.getValue()));
		fromColumn.setCellFactory(_ -> {
			TableCellEditComparable<Grade, BigDecimal> cell = new TableCellEditComparable<>(BigDecimal.ZERO, null,
					pointsConverter, Pos.CENTER);
			cell.tableRowProperty().addListener((_, _, newValue) -> {
				int index = newValue.getIndex();
				cell.setEditable(index != this.grades.length - 1);
				cell.minValueProperty().unbind();
				cell.maxValueProperty().unbind();
				if (index < this.grades.length - 1) {
					cell.minValueProperty().bind(pointsSystem.lowerBoundForGrade(this.grades[index + 1])
							.map(v -> v.add(pointsSystem.isUseHalfPoints() ? BigDecimal.valueOf(.5) : BigDecimal.ONE)));
				} else {
					cell.minValueProperty().bind(new SimpleObjectProperty<>(BigDecimal.ZERO));
				}
				if (index > 0) {
					cell.maxValueProperty().bind(pointsSystem.lowerBoundForGrade(this.grades[index - 1]).map(
							v -> v.subtract(pointsSystem.isUseHalfPoints() ? BigDecimal.valueOf(.5) : BigDecimal.ONE)));
				} else {
					cell.maxValueProperty().bind(pointsSystem.totalPointsProperty());
				}
			});
			return cell;
		});
		fromColumn.setSortable(false);
		fromColumn.setReorderable(false);

		TableColumn<Grade, BigDecimal> toColumn = new TableColumn<>(TranslationController.translate("pointsSystem_to"));
		toColumn.setCellValueFactory(data -> pointsSystem.upperBoundForGrade(data.getValue()));
		toColumn.setCellFactory(_ -> {
			TableCellEditComparable<Grade, BigDecimal> cell = new TableCellEditComparable<>(BigDecimal.ZERO, null,
					pointsConverter, Pos.CENTER);
			cell.tableRowProperty().addListener((_, _, newValue) -> {
				int index = newValue.getIndex();
				cell.setEditable(index != 0);
				cell.minValueProperty().unbind();
				cell.maxValueProperty().unbind();
				if (index < this.grades.length - 1) {
					cell.minValueProperty().bind(pointsSystem.upperBoundForGrade(this.grades[index + 1])
							.map(v -> v.add(pointsSystem.isUseHalfPoints() ? BigDecimal.valueOf(.5) : BigDecimal.ONE)));
				} else {
					cell.minValueProperty().bind(new SimpleObjectProperty<>(BigDecimal.ZERO));
				}
				if (index > 0) {
					cell.maxValueProperty().bind(pointsSystem.upperBoundForGrade(this.grades[index - 1]).map(
							v -> v.subtract(pointsSystem.isUseHalfPoints() ? BigDecimal.valueOf(.5) : BigDecimal.ONE)));
				} else {
					cell.maxValueProperty().bind(pointsSystem.totalPointsProperty());
				}
			});
			return cell;
		});
		toColumn.setSortable(false);
		toColumn.setReorderable(false);

		TableColumn<Grade, Grade> gradeColumn = new TableColumn<>(
				TranslationController.translate("pointsSystem_grade"));
		gradeColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
		gradeColumn.setCellFactory(_ -> new TableCellCustom<>(Pos.CENTER));
		gradeColumn.setSortable(false);
		gradeColumn.setReorderable(false);

		this.amountColumn = new AmountColumn();
		this.graded.bind(Bindings.createIntegerBinding(() -> {
			int sum = 0;
			for (IntegerProperty amount : amountColumn.amounts) {
				sum += amount.get();
			}
			return sum;
		}, this.amountColumn.amounts));

		this.getColumns().add(fromColumn);
		this.getColumns().add(toColumn);
		this.getColumns().add(gradeColumn);
		this.getColumns().add(this.amountColumn);
		this.getColumns().add(new RatioColumn(this.amountColumn));

		fromColumn.visibleProperty().bind(test.usePointsProperty());
		toColumn.visibleProperty().bind(test.usePointsProperty());

		this.setEditable(true);
		this.getSelectionModel().setCellSelectionEnabled(true);

		FontSizeController.bindTableColumnWidthToFontSize(this);
		Styles.subscribeTableColor(this, group.colorProperty());
	}

	public ReadOnlyIntegerProperty gradedProperty() {
		return this.graded.getReadOnlyProperty();
	}

	public Integer getGraded() {
		return this.graded.get();
	}

	public ReadOnlyObjectProperty<BigDecimal> gradeAVGProperty() {
		return this.gradeAVG.getReadOnlyProperty();
	}

	public BigDecimal getGradeAVG() {
		return this.gradeAVG.get();
	}

	private int indexOf(Grade grade) {
		for (int i = 0; i < this.grades.length; i++) {
			if (grade != null && grade.getNumericalValue().equals(this.grades[i].getNumericalValue())) {
				return i;
			}
		}
		return -1;
	}

	public void setOnlyDefaultDate(boolean set) {
		this.filtered.setValue(set);
	}

	private void updateStudentFilter() {
		List<Observable> observables = new ArrayList<>();
		observables.add(this.filtered);
		observables.add(this.test.dateProperty());
		observables.addAll(this.group.getStudents().stream().map(student -> this.test.dateProperty(student)).toList());
		this.students.predicateProperty().bind(Bindings.createObjectBinding(() -> {
			return student -> !this.filtered.get() || this.test.getDate() == null || this.test.getDate(student) == null
					|| this.test.getDate(student).equals(this.test.getDate());
		}, observables.toArray(n -> new Observable[n])));
	}

	private class RatioColumn extends TableColumn<Grade, BigDecimal> {

		private RatioColumn(AmountColumn amountColumn) {
			super("%");
			ObservableList<Observable> observables = FXCollections.observableArrayList();
			setCellValueFactory(data -> Bindings.createObjectBinding(() -> {
				observables.add(amountColumn.getCellObservableValue(data.getValue()));
				observables.add(test.totalPointsProperty());
				int sum = 0;
				for (IntegerProperty amount : amountColumn.amounts) {
					sum += amount.get();
				}
				int amount = amountColumn.getCellData(data.getValue());
				if (sum == 0) {
					return null;
				} else {
					return BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(sum), 5, RoundingMode.FLOOR);
				}
			}, amountColumn.amounts));
			setCellFactory(TableCellCustom.forTableColumn(new BigDecimalPercentConverter(2), Pos.CENTER));
			setSortable(false);
			setReorderable(false);
			this.minWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(5));
		}

	}

	private class AmountColumn extends TableColumn<Grade, Integer> {

		private final IntegerProperty[] amounts = new IntegerProperty[TableViewPointsSystem.this.grades.length];

		private final ChangeListener<Grade> updateAmountsListenerGrade = (_, oldValue, newValue) -> {
			if (oldValue != null) {
				int i = TableViewPointsSystem.this.indexOf(oldValue);
				this.amounts[i].set(this.amounts[i].get() - 1);
			}
			if (newValue != null) {
				int i = TableViewPointsSystem.this.indexOf(newValue);
				this.amounts[i].set(this.amounts[i].get() + 1);
			}
		};

		private AmountColumn() {
			super(TranslationController.translate("pointsSystem_amount"));
			this.setCellFactory(_ -> new TableCellCustom<>(Pos.CENTER));
			this.setSortable(false);
			this.setReorderable(false);
			this.setCellValueFactory(
					data -> this.amounts[TableViewPointsSystem.this.indexOf(data.getValue())].asObject());

			// set initial values for amounts
			for (int i = 0; i < this.amounts.length; i++) {
				Grade grade = TableViewPointsSystem.this.grades[i];
				this.amounts[i] = new SimpleIntegerProperty(this, "amount for grade " + grade, 0);
				this.amounts[i].subscribe(_ -> updateAVG());
			}
			// subscribe to predicate if filter changes
			TableViewPointsSystem.this.students.predicateProperty().subscribe(_ -> {
				for (int i = 0; i < this.amounts.length; i++) {
					Grade grade = TableViewPointsSystem.this.grades[i];
					this.amounts[i].set(0);
					for (Student student : TableViewPointsSystem.this.students) {
						ObjectProperty<Grade> g = TableViewPointsSystem.this.test.gradeProperty(student);
						// use numerical values here since we don't want to take tendencies into account
						if (g.get() != null && g.get().getNumericalValue().equals(grade.getNumericalValue())) {
							this.amounts[i].set(this.amounts[i].get() + 1);
						}
					}
				}
				// add listeners to all existing grades
				for (Entry<Student, ObjectProperty<Grade>> e : TableViewPointsSystem.this.test.getGrades().entrySet()) {
					e.getValue().removeListener(this.updateAmountsListenerGrade);
					if (TableViewPointsSystem.this.students.contains(e.getKey())) {
						e.getValue().addListener(this.updateAmountsListenerGrade);
					}
				}
			});
			// update listener if new student / grade is entered
			TableViewPointsSystem.this.test.getGrades()
					.addListener((MapChangeListener<Student, ObjectProperty<Grade>>) c -> {
						if (TableViewPointsSystem.this.students.contains(c.getKey())) {
							// new mapping
							if (c.getValueRemoved() == null) {
								c.getValueAdded().addListener(this.updateAmountsListenerGrade);
								Grade grade = c.getValueAdded().get();
								if (grade != null) {
									int i = TableViewPointsSystem.this.indexOf(grade);
									this.amounts[i].set(this.amounts[i].get() + 1);
								}
							}
							// mapping was removed
							else if (c.getValueAdded() == null) {
								c.getValueRemoved().removeListener(this.updateAmountsListenerGrade);
								Grade grade = c.getValueRemoved().get();
								if (grade != null) {
									int i = TableViewPointsSystem.this.indexOf(grade);
									this.amounts[i].set(this.amounts[i].get() - 1);
								}
							}
							// mapping was replaced
							else {
								c.getValueRemoved().removeListener(this.updateAmountsListenerGrade);
								c.getValueAdded().addListener(this.updateAmountsListenerGrade);
							}
						}
					});
		}

		void updateAVG() {
			int sum = 0;
			int count = 0;
			for (int i = 0; i < this.amounts.length; i++) {
				if (this.amounts[i] != null) {
					sum += this.amounts[i].get() * TableViewPointsSystem.this.grades[i].getNumericalValue();
					count += this.amounts[i].get();
				}
			}
			if (count != 0) {
				TableViewPointsSystem.this.gradeAVG
						.set(BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(count), 7, RoundingMode.DOWN));
			} else {
				TableViewPointsSystem.this.gradeAVG.set(null);
			}
		}

	}

}
