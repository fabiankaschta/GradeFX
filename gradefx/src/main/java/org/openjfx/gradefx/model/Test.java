package org.openjfx.gradefx.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openjfx.gradefx.model.GradeSystem.Grade;
import org.openjfx.gradefx.view.converter.TestTaskConverter;
import org.openjfx.kafx.controller.ChangeController;
import org.openjfx.kafx.io.DataObject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.TreeItem;

public class Test {

	public static class TestTask extends TreeItem<TestTask> {

		private final static TestTaskConverter converter = new TestTaskConverter();

		public static TestTask createRoot(Test test, BigDecimal maxPoints) {
			return new TestTask(test, "root", maxPoints, true);
		}

		public static TestTask createTask(Test test, String name, BigDecimal maxPoints) {
			return new TestTask(test, name, maxPoints, false);
		}

		private final Test test;
		private final StringProperty name = new SimpleStringProperty(this, "name");
		private final ReadOnlyObjectWrapper<BigDecimal> maxPoints = new ReadOnlyObjectWrapper<>(this, "maxPoints");
		private final ObservableMap<Student, ObjectProperty<BigDecimal>> points = FXCollections.observableHashMap();
		private final BooleanProperty isRoot = new SimpleBooleanProperty(this, "isRoot");

		private TestTask(Test test, String name, BigDecimal maxPoints, boolean isRoot) {
			this.test = test;
			this.setName(name);
			this.setMaxPoints(maxPoints);
			this.setIsRoot(isRoot);
			this.nameProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			this.maxPointsProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			this.getChildren().addListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
			super.setValue(this);
			this.setExpanded(true);
		}

		private TestTask getParentTask() {
			TreeItem<TestTask> parent = this.getParent();
			return parent == null ? null : (TestTask) parent;
		}

		private void updateMaxPointsParent() {
			TestTask parent = getParentTask();
			if (parent == null) {
				this.test.calculateGrades();
			} else {
				parent.updateMaxPoints();
			}
		}

		private void updateMaxPoints() {
			if (!this.isLeaf()) {
				BigDecimal sum = BigDecimal.ZERO;
				boolean hasPointsSet = false;
				for (TreeItem<TestTask> t : this.getChildren()) {
					TestTask subTask = (TestTask) t;
					BigDecimal subTaskPoints = subTask.getMaxPoints();
					if (subTaskPoints != null) {
						hasPointsSet = true;
						sum = sum.add(subTaskPoints);
					}
				}
				if (hasPointsSet) {
					// if there were some points added up (even if there are zero points)
					if (this.maxPoints.get() == null || this.maxPoints.get().compareTo(sum) != 0) {
						this.maxPoints.set(sum);
						this.updateMaxPointsParent();
					}
				} else {
					// if there were no points
					if (this.maxPoints.get() != null) {
						this.maxPoints.set(null);
						this.updateMaxPointsParent();
					}
				}
			}
		}

		@SuppressWarnings("unused") // TODO remove unnecessary method?
		private void updatePointsParent() {
			for (Student student : points.keySet()) {
				this.updatePointsParent(student);
			}
		}

		private void updatePoints() {
			for (Student student : points.keySet()) {
				this.updatePoints(student);
			}
		}

		private void updatePointsParent(Student student) {
			TestTask parent = getParentTask();
			if (parent == null) {
				if (!this.test.isTotalPointsFixed(student)) {
					this.test.setTotalPoints(student, this.getPoints(student));
				}
			} else {
				parent.updatePoints(student);
			}
		}

		private void updatePoints(Student student) {
			if (!this.isLeaf()) {
				this.putStudentPropertiesIfNotExists(student);
				BigDecimal sum = BigDecimal.ZERO;
				boolean hasPointsSet = false;
				for (TreeItem<TestTask> t : this.getChildren()) {
					TestTask subTask = (TestTask) t;
					BigDecimal subTaskPoints = subTask.getPoints(student);
					if (subTaskPoints != null) {
						hasPointsSet = true;
						sum = sum.add(subTaskPoints);
					}
				}
				if (hasPointsSet) {
					// if there were some points added up (even if there are zero points)
					if (this.points.get(student).getValue() == null
							|| this.points.get(student).getValue().compareTo(sum) != 0) {
						this.points.get(student).set(sum);
						this.updatePointsParent(student);
					}
				} else {
					// if there were no points
					if (this.points.get(student).getValue() != null) {
						this.points.get(student).set(null);
						this.updatePointsParent(student);
					}
				}
			}
		}

		public String getName() {
			return name.get();
		}

		public StringProperty nameProperty() {
			return name;
		}

		public void setName(String name) {
			this.name.set(name);
		}

		public boolean isRoot() {
			return isRoot.get();
		}

		private void setIsRoot(boolean isRoot) {
			this.isRoot.set(isRoot);
		}

		public BigDecimal getMaxPoints() {
			return maxPoints.get();
		}

		public ReadOnlyObjectProperty<BigDecimal> maxPointsProperty() {
			return maxPoints.getReadOnlyProperty();
		}

		public void setMaxPoints(BigDecimal maxPoints) {
			if (this.isLeaf()) {
				this.maxPoints.set(maxPoints);
				this.updateMaxPointsParent();
			} else {
				throw new IllegalStateException("only leaf tasks may have max points set manually");
			}
		}

		private void removeStudent(Student student) {
			this.points.remove(student);
			for (TreeItem<TestTask> child : getChildren()) {
				((TestTask) child).removeStudent(student);
			}
		}

		private void putStudentPropertiesIfNotExists(Student student) {
			test.putStudentPropertiesIfNotExists(student);
			if (!this.points.containsKey(student)) {
				ObjectProperty<BigDecimal> pointsProperty = new SimpleObjectProperty<>(student,
						"points in test " + test);
				pointsProperty.addListener(ChangeController.getConditionalListenerUnsavedChanges(() -> this.isLeaf()));
				pointsProperty.addListener((_, _, _) -> {
					this.updatePointsParent(student);
				});
				this.points.put(student, pointsProperty);
			}
		}

		public void setPoints(Student student, BigDecimal points) {
			this.putStudentPropertiesIfNotExists(student);
			this.points.get(student).set(points);
		}

		public BigDecimal getPoints(Student student) {
			this.putStudentPropertiesIfNotExists(student);
			return this.points.get(student).getValue();
		}

		public ObjectProperty<BigDecimal> pointsProperty(Student student) {
			this.putStudentPropertiesIfNotExists(student);
			return this.points.get(student);
		}

		public void addSubtask(TestTask task) {
			this.getChildren().add(task);
			this.updateMaxPoints();
		}

		public void removeSubtask(TestTask task) {
			this.getChildren().remove(task);
			this.updateMaxPoints();
			this.updatePoints();
		}

		@Override
		public String toString() {
			return converter.toString(this);
		}

		private static class TestTaskS implements DataObject<TestTask> {

			private static final long serialVersionUID = 5830750857790701613L;

			private final String name;
			private final BigDecimal maxPoints;
			private final boolean isRoot;
			private final Map<DataObject<Student>, BigDecimal> points = new HashMap<>();
			private final List<DataObject<TestTask>> children = new ArrayList<>();

			private transient TestTask testTask;

			private TestTaskS(TestTask tt) {
				DataObject.putSerialized(tt, this);
				this.name = tt.getName();
				this.maxPoints = tt.getMaxPoints();
				this.isRoot = tt.isRoot();
				for (Entry<Student, ObjectProperty<BigDecimal>> sg : tt.points.entrySet()) {
					this.points.put(sg.getKey().serialize(), sg.getValue().getValue());
				}
				for (TreeItem<TestTask> t : tt.getChildren()) {
					this.children.add(((TestTask) t).serialize());
				}
				testTask = tt;
			}

			public TestTask deserialize(Object... params) {
				Test test = (Test) params[0];
				if (testTask == null) {
					if (isRoot) {
						testTask = TestTask.createRoot(test, maxPoints);
						test.setTasksRoot(testTask); // do this here so that listeners (ie grade) can fire already
					} else {
						testTask = TestTask.createTask(test, name, maxPoints);
					}
					for (Entry<DataObject<Student>, BigDecimal> p : points.entrySet()) {
						Student student = p.getKey().deserialize();
						testTask.putStudentPropertiesIfNotExists(student);
						testTask.points.get(student).set(p.getValue());
					}
					for (DataObject<TestTask> t : children) {
						testTask.addSubtask(t.deserialize(params[0]));
					}
				}
				return testTask;
			}
		}

		@SuppressWarnings("unchecked")
		public DataObject<TestTask> serialize() {
			DataObject<?> testTask = DataObject.getSerialized(this);
			if (testTask == null) {
				return new TestTaskS(this);
			} else {
				return (DataObject<TestTask>) testTask;
			}
		}

	}

	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty shortName = new SimpleStringProperty(this, "shortName");
	private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(this, "date");
	private final ObjectProperty<BigDecimal> weight = new SimpleObjectProperty<>(this, "weight");
	private final BooleanProperty useTasks = new SimpleBooleanProperty(this, "useTasks");
	private final BooleanProperty usePoints = new SimpleBooleanProperty(this, "usePoints");
	private final ObjectProperty<PointsSystem> pointsSystem = new SimpleObjectProperty<>(this, "pointsSystem");
	private final ObjectProperty<TestTask> tasksRoot = new SimpleObjectProperty<>(this, "tasksRoot");
	private final ObservableMap<Student, ReadOnlyBooleanWrapper> totalPointsFixed = FXCollections.observableHashMap();
	private final ObservableMap<Student, ObjectProperty<BigDecimal>> totalPoints = FXCollections.observableHashMap();
	private final ObservableMap<Student, ReadOnlyBooleanWrapper> gradesFixed = FXCollections.observableHashMap();
	private final ObservableMap<Student, ObjectProperty<Grade>> grades = FXCollections.observableHashMap();
	private final ObservableMap<Student, StringProperty> annotations = FXCollections.observableHashMap();
	private final ObservableMap<Student, ObjectProperty<LocalDate>> dates = FXCollections.observableHashMap();

	public Test(Group group, String name, String shortName, LocalDate date, BigDecimal weight, BigDecimal maxPoints,
			boolean useTasks, boolean usePoints) {
		this(name, shortName, date, weight, useTasks, usePoints);
		this.setTasksRoot(TestTask.createRoot(this, maxPoints));
		this.setPointsSystem(group.getGradeSystem().getDefaultPointsSystem(this.totalPointsProperty()));
	}

	private Test(String name, String shortName, LocalDate date, BigDecimal weight, boolean useTasks,
			boolean usePoints) {
		this.setName(name);
		this.setShortName(shortName);
		this.setDate(date);
		this.setWeight(weight);
		this.setUseTasks(useTasks);
		this.setUsePoints(usePoints);
		this.useTasksProperty().addListener((_, oldValue, newValue) -> {
			if (!oldValue && newValue) {
				this.setUsePoints(true);
				// keep currently set points, but fix them, but only if there are points
				for (Entry<Student, ReadOnlyBooleanWrapper> e : this.totalPointsFixed.entrySet()) {
					if (this.getTotalPoints(e.getKey()) != null) {
						e.getValue().removeListener(ChangeController.LISTENER_UNSAVED_CHANGES);
						e.getValue().set(true);
						e.getValue().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
					}
				}
			} else if (oldValue && !newValue) {
				// can't have fixed points without tasks
				for (ReadOnlyBooleanWrapper b : this.totalPointsFixed.values()) {
					b.removeListener(ChangeController.LISTENER_UNSAVED_CHANGES);
					b.set(false);
					b.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
				}
				this.tasksRoot.get().getChildren().clear();
			}
		});
		this.usePointsProperty().addListener((_, oldValue, newValue) -> {
			if (oldValue && !newValue) {
				this.setUseTasks(false);
				// can't have fixed grade without points
				for (ReadOnlyBooleanWrapper b : this.gradesFixed.values()) {
					b.removeListener(ChangeController.LISTENER_UNSAVED_CHANGES);
					b.set(false);
					b.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
				}
				// can't have total points without points
				for (ObjectProperty<BigDecimal> e : this.totalPoints.values()) {
					e.set(null);
				}
				this.tasksRoot.get().setMaxPoints(BigDecimal.ZERO);
			} else if (!oldValue && newValue) {
				// keep currently set grades, but fix them, but only if there are grades
				for (Entry<Student, ReadOnlyBooleanWrapper> e : this.gradesFixed.entrySet()) {
					if (this.getGrade(e.getKey()) != null) {
						e.getValue().removeListener(ChangeController.LISTENER_UNSAVED_CHANGES);
						e.getValue().set(true);
						e.getValue().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
					}
				}
			}
		});
		this.nameProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.shortNameProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.dateProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.weightProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.useTasksProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.usePointsProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getShortName() {
		return shortName.get();
	}

	public StringProperty shortNameProperty() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName.set(shortName);
	}

	public LocalDate getDate() {
		return date.get();
	}

	public ObjectProperty<LocalDate> dateProperty() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date.set(date);
	}

	public BigDecimal getWeight() {
		return weight.get();
	}

	public ObjectProperty<BigDecimal> weightProperty() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight.set(weight);
	}

	public boolean getUseTasks() {
		return useTasks.get();
	}

	public BooleanProperty useTasksProperty() {
		return useTasks;
	}

	public void setUseTasks(boolean useTasks) {
		this.useTasks.set(useTasks);
	}

	public void setUsePoints(boolean usePoints) {
		this.usePoints.set(usePoints);
	}

	public boolean getUsePoints() {
		return usePoints.get();
	}

	public BooleanProperty usePointsProperty() {
		return usePoints;
	}

	public PointsSystem getPointsSystem() {
		return this.pointsSystem.get();
	}

	private void setPointsSystem(PointsSystem pointsSystem) {
		this.pointsSystem.set(pointsSystem);
		if (this.tasksRoot.get() != null) {
			this.pointsSystem.get().totalPointsProperty().unbind();
			this.pointsSystem.get().totalPointsProperty().bind(this.tasksRoot.get().maxPointsProperty());
			this.calculateTotalPoints();
		}
		this.pointsSystem.get().subscribeBoundUpdates(() -> this.calculateGrades());
	}

	private void setTasksRoot(TestTask tasksRoot) {
		this.tasksRoot.set(tasksRoot);
		if (this.pointsSystem.get() != null) {
			this.pointsSystem.get().totalPointsProperty().unbind();
			this.pointsSystem.get().totalPointsProperty().bind(this.tasksRoot.get().maxPointsProperty());
			this.calculateTotalPoints();
		}
	}

	public TestTask getTasksRoot() {
		return tasksRoot.get();
	}

	public BigDecimal getTotalPoints() {
		return totalPointsProperty().getValue();
	}

	public ReadOnlyObjectProperty<BigDecimal> totalPointsProperty() {
		return tasksRoot.getValue().maxPointsProperty();
	}

	public void setTotalPoints(BigDecimal totalPoints) {
		if (!this.getUseTasks() && this.getUsePoints()) {
			tasksRoot.getValue().maxPoints.set(totalPoints);
			this.calculateGrades();
		} else {
			throw new IllegalStateException("only tasks without tasks but points may have max points set manually");
		}
	}

	public void removeStudent(Student student) {
		this.grades.remove(student);
		this.gradesFixed.remove(student);
		this.totalPoints.remove(student);
		this.totalPointsFixed.remove(student);
		this.annotations.remove(student);
		this.dates.remove(student);
		this.tasksRoot.get().removeStudent(student);
	}

	private void putStudentPropertiesIfNotExists(Student student) {
		if (!this.grades.containsKey(student)) {
			ObjectProperty<Grade> gradeProperty = new SimpleObjectProperty<>(student, "grade in test " + this);
			ReadOnlyBooleanWrapper gradeFixedProperty = new ReadOnlyBooleanWrapper(student,
					"gradeFixed in test " + this, false);
			ObjectProperty<BigDecimal> totalPointsProperty = new SimpleObjectProperty<>(student,
					"totalPoints in test " + this);
			ReadOnlyBooleanWrapper totalPointsFixedProperty = new ReadOnlyBooleanWrapper(student,
					"totalPointsFixed in test " + this, false);
			StringProperty annotationProperty = new SimpleStringProperty(student, "annotation in test " + this);
			ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>(student, "date in test " + this);
			gradeProperty.addListener(ChangeController
					.getConditionalListenerUnsavedChanges(() -> this.isGradeFixed(student) || !this.getUsePoints()));
			gradeFixedProperty.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			totalPointsProperty.addListener(ChangeController.getConditionalListenerUnsavedChanges(
					() -> this.isTotalPointsFixed(student) || !this.getUseTasks()));
			totalPointsFixedProperty.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			annotationProperty.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			dateProperty.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);

			gradeFixedProperty.addListener((_, oldValue, newValue) -> {
				if (oldValue && !newValue) {
					this.calculateGrade(student);
				}
			});

			totalPointsProperty.addListener((_, _, _) -> this.calculateGrade(student));

			totalPointsFixedProperty.addListener((_, oldValue, newValue) -> {
				if (oldValue && !newValue) {
					this.calculateTotalPoints(student);
				}
			});

			this.grades.put(student, gradeProperty);
			this.gradesFixed.put(student, gradeFixedProperty);
			this.totalPoints.put(student, totalPointsProperty);
			this.totalPointsFixed.put(student, totalPointsFixedProperty);
			this.annotations.put(student, annotationProperty);
			this.dates.put(student, dateProperty);

			this.calculateTotalPoints(student);
			this.calculateGrade(student);
		}
	}

	private void calculateTotalPoints() {
		if (this.getTasksRoot() != null) {
			for (Student student : this.grades.keySet()) {
				calculateTotalPoints(student);
			}
		}
	}

	private void calculateTotalPoints(Student student) {
		if (this.getTasksRoot() != null && !isTotalPointsFixed(student) && getUseTasks()) {
			this.putStudentPropertiesIfNotExists(student);
			this.totalPoints.get(student).set(this.getTasksRoot().getPoints(student));
		}
	}

	private void calculateGrades() {
		if (this.getTasksRoot() != null) {
			for (Student student : this.grades.keySet()) {
				calculateGrade(student);
			}
		}
	}

	private void calculateGrade(Student student) {
		if (this.getPointsSystem() != null && !isGradeFixed(student) && getUsePoints()) {
			this.putStudentPropertiesIfNotExists(student);
			BigDecimal totalPoints = this.getTotalPoints(student);
			if (totalPoints == null) {
				this.grades.get(student).set(null);
			} else {
				this.grades.get(student).set(this.pointsSystem.get().calculateGrade(totalPoints));
			}
		}
	}

	public void setTotalPoints(Student student, BigDecimal totalPoints) {
		this.putStudentPropertiesIfNotExists(student);
		this.totalPoints.get(student).set(totalPoints);
	}

	public BigDecimal getTotalPoints(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.totalPoints.get(student).getValue();
	}

	public ObjectProperty<BigDecimal> totalPointsProperty(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.totalPoints.get(student);
	}

	public void setTotalPointsFixed(Student student, boolean fixed) {
		this.putStudentPropertiesIfNotExists(student);
		this.totalPointsFixed.get(student).set(fixed);
	}

	public Boolean isTotalPointsFixed(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.totalPointsFixed.get(student).getValue();
	}

	public ReadOnlyBooleanProperty totalPointsFixedProperty(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.totalPointsFixed.get(student).getReadOnlyProperty();
	}

	public void setGrade(Student student, Grade grade) {
		this.putStudentPropertiesIfNotExists(student);
		this.grades.get(student).set(grade);
	}

	public Grade getGrade(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.grades.get(student).getValue();
	}

	public ObjectProperty<Grade> gradeProperty(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.grades.get(student);
	}

	public void setGradeFixed(Student student, boolean fixed) {
		this.putStudentPropertiesIfNotExists(student);
		this.gradesFixed.get(student).set(fixed);
	}

	public Boolean isGradeFixed(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.gradesFixed.get(student).getValue();
	}

	public ReadOnlyBooleanProperty gradeFixedProperty(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.gradesFixed.get(student).getReadOnlyProperty();
	}

	public ObservableMap<Student, ObjectProperty<Grade>> getGrades() {
		return this.grades;
	}

	public void setAnnotation(Student student, String annotation) {
		this.putStudentPropertiesIfNotExists(student);
		this.annotations.get(student).set(annotation);
	}

	public String getAnnotation(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.annotations.get(student).getValue();
	}

	public StringProperty annotationProperty(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.annotations.get(student);
	}

	public void setDate(Student student, LocalDate date) {
		this.putStudentPropertiesIfNotExists(student);
		this.dates.get(student).set(date);
	}

	public LocalDate getDate(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.dates.get(student).getValue();
	}

	public ObjectProperty<LocalDate> dateProperty(Student student) {
		this.putStudentPropertiesIfNotExists(student);
		return this.dates.get(student);
	}

	@Override
	public String toString() {
		return this.getName();
	}

	private static class TestS implements DataObject<Test> {

		private static final long serialVersionUID = 7931626192625102386L;

		private final String name;
		private final String shortName;
		private final LocalDate date;
		private final BigDecimal weight;
		private final boolean useTasks;
		private final boolean usePoints;
		private final DataObject<PointsSystem> pointsSystem;
		private final DataObject<TestTask> tasksRoot;
		private final Map<DataObject<Student>, Grade> grades = new HashMap<>();
		private final Map<DataObject<Student>, Boolean> gradesFixed = new HashMap<>();
		private final Map<DataObject<Student>, BigDecimal> totalPoints = new HashMap<>();
		private final Map<DataObject<Student>, Boolean> totalPointsFixed = new HashMap<>();
		private final Map<DataObject<Student>, String> annotations = new HashMap<>();
		private final Map<DataObject<Student>, LocalDate> dates = new HashMap<>();

		private transient Test test;

		private TestS(Test t) {
			DataObject.putSerialized(t, this);
			name = t.getName();
			shortName = t.getShortName();
			date = t.getDate();
			weight = t.getWeight();
			useTasks = t.getUseTasks();
			usePoints = t.getUsePoints();
			pointsSystem = t.getPointsSystem().serialize();
			tasksRoot = t.getTasksRoot().serialize();
			for (Entry<Student, ObjectProperty<BigDecimal>> st : t.totalPoints.entrySet()) {
				if (t.isTotalPointsFixed(st.getKey())) {
					this.totalPoints.put(st.getKey().serialize(), st.getValue().getValue());
					this.totalPointsFixed.put(st.getKey().serialize(), true);
				} else if (!t.getUseTasks()) {
					this.totalPoints.put(st.getKey().serialize(), st.getValue().getValue());
					this.totalPointsFixed.put(st.getKey().serialize(), false);
				}
			}
			for (Entry<Student, ObjectProperty<Grade>> sg : t.grades.entrySet()) {
				if (t.isGradeFixed(sg.getKey())) {
					this.grades.put(sg.getKey().serialize(), sg.getValue().getValue());
					this.gradesFixed.put(sg.getKey().serialize(), true);
				} else if (!t.getUsePoints()) {
					this.grades.put(sg.getKey().serialize(), sg.getValue().getValue());
					this.gradesFixed.put(sg.getKey().serialize(), false);
				}
			}
			for (Entry<Student, StringProperty> sa : t.annotations.entrySet()) {
				this.annotations.put(sa.getKey().serialize(), sa.getValue().getValue());
			}
			for (Entry<Student, ObjectProperty<LocalDate>> sd : t.dates.entrySet()) {
				this.dates.put(sd.getKey().serialize(), sd.getValue().getValue());
			}
			test = t;
		}

		public Test deserialize(Object... params) {
			if (test == null) {
				test = new Test(name, shortName, date, weight, useTasks, usePoints);
				tasksRoot.deserialize(test);
				test.setPointsSystem(pointsSystem.deserialize());
				test.calculateTotalPoints();
				for (Entry<DataObject<Student>, BigDecimal> st : totalPoints.entrySet()) {
					Student student = st.getKey().deserialize();
					test.putStudentPropertiesIfNotExists(student);
					test.setTotalPoints(student, st.getValue());
					if (totalPointsFixed.get(st.getKey())) {
						test.setTotalPointsFixed(student, true);
					}
				}
				for (Entry<DataObject<Student>, Grade> sg : grades.entrySet()) {
					Student student = sg.getKey().deserialize();
					test.putStudentPropertiesIfNotExists(student);
					test.setGrade(student, sg.getValue());
					if (gradesFixed.get(sg.getKey())) {
						test.setGradeFixed(student, true);
					}
				}
				for (Entry<DataObject<Student>, String> sa : annotations.entrySet()) {
					Student student = sa.getKey().deserialize();
					test.putStudentPropertiesIfNotExists(student);
					test.setAnnotation(student, sa.getValue());
				}
				for (Entry<DataObject<Student>, LocalDate> sd : dates.entrySet()) {
					Student student = sd.getKey().deserialize();
					test.putStudentPropertiesIfNotExists(student);
					test.setDate(student, sd.getValue());
				}
			}
			return test;
		}
	}

	@SuppressWarnings("unchecked")
	public DataObject<Test> serialize() {
		DataObject<?> test = DataObject.getSerialized(this);
		if (test == null) {
			return new TestS(this);
		} else {
			return (DataObject<Test>) test;
		}
	}
}
