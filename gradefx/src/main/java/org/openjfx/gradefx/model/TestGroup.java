package org.openjfx.gradefx.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.openjfx.gradefx.converter.TestGroupConverter;
import org.openjfx.kafx.controller.ChangeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.io.DataObject;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TreeItem;

public class TestGroup extends TreeItem<TestGroup> {

	public static class TestGroupSystem extends TreeItem<TestGroupSystem> {

		private final static ObservableList<TestGroupSystem> testGroupSystems = FXCollections.observableArrayList();
		public final static TestGroupSystem NONE = createRoot(
				TranslationController.translate("testGroupSystem_NO_GROUPS"));

		static {
			testGroupSystems.add(NONE);
		}

		public static ObservableList<TestGroupSystem> getTestGroupSystems() {
			return testGroupSystems;
		}

		public static void removeTestGroupSystem(TestGroupSystem testGroupSystem) {
			testGroupSystems.remove(testGroupSystem);
		}

		public static void clearTestGroupSystems() {
			testGroupSystems.clear();
			testGroupSystems.add(NONE);
		}

		public static TestGroupSystem createRoot(String name) {
			return new TestGroupSystem(name, BigDecimal.ONE);
		}

		public static TestGroupSystem createSubSystem(String name, BigDecimal weight) {
			return new TestGroupSystem(name, weight, false);
		}

		public static void setDefault() {
			clearTestGroupSystems();
			{
				TestGroupSystem root = createRoot(TranslationController.translate("testGroupSystem_SMALL_LARGE"));
				root.getChildren().removeListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
				root.addSubgroup(
						createSubSystem(TranslationController.translate("testGroupSystem_small"), BigDecimal.ONE));
				root.addSubgroup(
						createSubSystem(TranslationController.translate("testGroupSystem_large"), BigDecimal.TWO));
				root.getChildren().addListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
			}
			{
				TestGroupSystem root = createRoot(TranslationController.translate("testGroupSystem_SMALL_LARGE_EQUAL"));
				root.getChildren().removeListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
				root.addSubgroup(
						createSubSystem(TranslationController.translate("testGroupSystem_small"), BigDecimal.ONE));
				root.addSubgroup(
						createSubSystem(TranslationController.translate("testGroupSystem_large"), BigDecimal.ONE));
				root.getChildren().addListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
			}
		}

		private final StringProperty name = new SimpleStringProperty(this, "name");
		private final ObjectProperty<BigDecimal> weight = new SimpleObjectProperty<>(this, "weight");
		private final BooleanProperty isRoot = new SimpleBooleanProperty(this, "isRoot");

		private TestGroupSystem(String name, BigDecimal weight) {
			this(name, weight, true);
			testGroupSystems.add(this);
		}

		private TestGroupSystem(String name, BigDecimal weight, boolean isRoot) {
			this.setName(name);
			this.setWeight(weight);
			this.setIsRoot(isRoot);
			this.nameProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			this.weightProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			this.getChildren().addListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
			super.setValue(this);
			super.setExpanded(true);
		}

		public TestGroup getNewTestGroupRoot(Group group) {
			TestGroup root = new TestGroup(group, getName(), getWeight(), isRoot());
			for (TreeItem<TestGroupSystem> t : getChildren()) {
				root.addSubgroup(((TestGroupSystem) t).getNewTestGroupRoot(group));
			}
			return root;
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

		public BigDecimal getWeight() {
			return weight.get();
		}

		public ObjectProperty<BigDecimal> weightProperty() {
			return weight;
		}

		public void setWeight(BigDecimal weight) {
			this.weight.set(weight);
		}

		public boolean isRoot() {
			return isRoot.get();
		}

		private void setIsRoot(boolean isRoot) {
			this.isRoot.set(isRoot);
		}

		public void addSubgroup(TestGroupSystem group) {
			this.getChildren().add(group);
		}

		public void removeSubgroup(TestGroupSystem group) {
			this.getChildren().remove(group);
		}

		@Override
		public String toString() {
			return isRoot() ? getName()
					: getName() + " (" + TranslationController.translate("testGroup_weight") + ": " + getWeight() + ")";
		}

		private static class TestGroupSystemS implements DataObject<TestGroupSystem> {

			private static final long serialVersionUID = 7996475276141553948L;

			private final String name;
			private final BigDecimal weight;
			private final boolean isRoot;
			private final List<DataObject<TestGroupSystem>> children = new ArrayList<>();

			private transient TestGroupSystem testGroupSystem;

			private TestGroupSystemS(TestGroupSystem tgs) {
				DataObject.putSerialized(tgs, this);
				this.name = tgs.getName();
				this.weight = tgs.getWeight();
				this.isRoot = tgs.isRoot();
				for (TreeItem<TestGroupSystem> t : tgs.getChildren()) {
					this.children.add(((TestGroupSystem) t).serialize());
				}
				testGroupSystem = tgs;
			}

			@Override
			public TestGroupSystem deserialize(Object... params) {
				if (testGroupSystem == null) {
					if (isRoot) {
						testGroupSystem = createRoot(name);
					} else {
						testGroupSystem = createSubSystem(name, weight);
					}
					for (DataObject<TestGroupSystem> t : children) {
						testGroupSystem.addSubgroup(t.deserialize());
					}
				}
				return testGroupSystem;
			}

		}

		@SuppressWarnings("unchecked")
		public DataObject<TestGroupSystem> serialize() {
			if (this == NONE) {
				return null;
			} else {
				DataObject<?> testGroupSystem = DataObject.getSerialized(this);
				if (testGroupSystem == null) {
					return new TestGroupSystemS(this);
				} else {
					return (DataObject<TestGroupSystem>) testGroupSystem;
				}
			}
		}

	}

	private final static TestGroupConverter converter = new TestGroupConverter();

	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final ObjectProperty<BigDecimal> weight = new SimpleObjectProperty<>(this, "weight");
	private final BooleanProperty isRoot = new SimpleBooleanProperty(this, "isRoot");
	private final ObservableList<Test> tests = FXCollections.observableArrayList();

	// these are never stored in file, always calculated
	private final ObservableMap<Student, ReadOnlyObjectWrapper<BigDecimal>> avgGrades = FXCollections
			.observableHashMap();
	private final ReadOnlyObjectWrapper<BigDecimal> avgGrade = new ReadOnlyObjectWrapper<>(this, "avgGrade");

	private TestGroup(Group group, String name, BigDecimal weight, boolean isRoot) {
		this.setName(name);
		this.setWeight(weight);
		this.setIsRoot(isRoot);
		this.nameProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.weightProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.addTestsListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
		this.getChildren().addListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
		super.setValue(this);

		Consumer<Student> recalculateGradeAvgStudent = student -> {
			putStudentPropertiesIfNotExists(student);

			List<Observable> observables = new ArrayList<>();
			observables.add(group.gradeSystemProperty());
			this.tests.forEach(test -> observables.add(test.gradeProperty(student)));
			this.tests.forEach(test -> observables.add(test.weightProperty()));
			this.getChildren().forEach(testGroup -> observables.add(((TestGroup) testGroup).avgGrade(student)));
			this.getChildren().forEach(testGroup -> observables.add(((TestGroup) testGroup).weightProperty()));

			ReadOnlyObjectWrapper<BigDecimal> avgProperty = this.avgGrades.get(student);
			avgProperty.unbind();
			avgProperty.bind(Bindings.createObjectBinding(() -> {
				List<BigDecimal> avgs = new ArrayList<>();
				List<BigDecimal> weights = new ArrayList<>();
				for (Test test : this.tests) {
					Grade grade = test.getGrade(student);
					if (grade != null) {
						avgs.add(BigDecimal.valueOf(grade.getNumericalValue()));
						weights.add(test.getWeight());
					}
				}
				for (TreeItem<TestGroup> child : this.getChildren()) {
					TestGroup testGroup = (TestGroup) child;
					BigDecimal avg = testGroup.getAvgGrade(student);
					if (avg != null) {
						avgs.add(testGroup.getAvgGrade(student));
						weights.add(testGroup.getWeight());
					}
				}
				return group.getGradeSystem().calculateAverage(avgs.toArray(n -> new BigDecimal[n]),
						weights.toArray(n -> new BigDecimal[n]));
			}, observables.toArray(n -> new Observable[n])));
		};
		this.tests.addListener((ListChangeListener<Test>) _ -> this.avgGrades.keySet()
				.forEach(student -> recalculateGradeAvgStudent.accept(student)));
		this.getChildren().subscribe(() -> this.avgGrades.keySet()
				.forEach(student -> recalculateGradeAvgStudent.accept(student)));
		for (Student student : group.getStudents()) {
			recalculateGradeAvgStudent.accept(student);
		}
		group.getStudents().addListener((ListChangeListener<Student>) c -> {
			while (c.next()) {
				if (c.wasRemoved()) {
					for (Student student : c.getRemoved()) {
						this.avgGrades.remove(student);
					}
				}
				if (c.wasAdded()) {
					for (Student student : c.getAddedSubList()) {
						recalculateGradeAvgStudent.accept(student);
					}
				}
			}
		});

		Runnable recalculateAvgGrade = () -> {
			this.avgGrade.unbind();
			this.avgGrade.bind(Bindings.createObjectBinding(() -> {
				BigDecimal sum = BigDecimal.ZERO;
				int amount = 0;
				for (ReadOnlyObjectWrapper<BigDecimal> a : this.avgGrades.values()) {
					BigDecimal avg = a.get();
					if (avg != null) {
						sum = sum.add(avg);
						amount++;
					}
				}
				if (amount == 0) {
					return null;
				} else {
					return sum.divide(BigDecimal.valueOf(amount), 7, RoundingMode.DOWN);
				}
			}, this.avgGrades.values().toArray(n -> new Observable[n])));
		};
		// listen to map changes (students added or removed)
		this.avgGrades
				.addListener((MapChangeListener<Student, ObjectProperty<BigDecimal>>) _ -> recalculateAvgGrade.run());
		recalculateAvgGrade.run(); // call once to set initial value
	}

	private void putStudentPropertiesIfNotExists(Student student) {
		if (!this.avgGrades.containsKey(student)) {
			this.avgGrades.put(student, new ReadOnlyObjectWrapper<>(this, "avgGrade for student " + student));
		}
	}

	public String getName() {
		return name.get();
	}

	private void setName(String name) {
		this.name.set(name);
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public BigDecimal getWeight() {
		return weight.get();
	}

	private void setWeight(BigDecimal weight) {
		this.weight.set(weight);
	}

	public ObjectProperty<BigDecimal> weightProperty() {
		return this.weight;
	}

	public boolean isRoot() {
		return isRoot.get();
	}

	private void setIsRoot(boolean isRoot) {
		this.isRoot.set(isRoot);
	}

	ObservableList<Test> getTests() {
		return tests;
	}

	void addTest(Test test) {
		this.tests.add(test);
	}

	void removeTest(Test test) {
		this.tests.remove(test);
	}

	public void addTestsListener(ListChangeListener<? super Test> listener) {
		tests.addListener(listener);
	}

	private void addSubgroup(TestGroup group) {
		this.getChildren().add(group);
	}

	public BigDecimal getAvgGrade(Student student) {
		putStudentPropertiesIfNotExists(student);
		return this.avgGrades.get(student).get();
	}

	public ReadOnlyObjectProperty<BigDecimal> avgGrade(Student student) {
		putStudentPropertiesIfNotExists(student);
		return this.avgGrades.get(student).getReadOnlyProperty();
	}

	public BigDecimal getAvgGrade() {
		return this.avgGrade.get();
	}

	public ReadOnlyObjectProperty<BigDecimal> avgGrade() {
		return this.avgGrade.getReadOnlyProperty();
	}

	@Override
	public String toString() {
		return converter.toString(this);
	}

	private static class TestGroupS implements DataObject<TestGroup> {

		private static final long serialVersionUID = -1114481201878933216L;

		private final String name;
		private final BigDecimal weight;
		private final boolean isRoot;
		private final List<DataObject<Test>> tests = new ArrayList<>();
		private final List<DataObject<TestGroup>> subgroups = new ArrayList<>();

		private transient TestGroup testGroup;

		private TestGroupS(TestGroup tg) {
			DataObject.putSerialized(tg, this);
			this.name = tg.getName();
			this.weight = tg.getWeight();
			this.isRoot = tg.isRoot();
			for (TreeItem<TestGroup> t : tg.getChildren()) {
				this.subgroups.add(((TestGroup) t).serialize());
			}
			for (Test t : tg.getTests()) {
				this.tests.add(t.serialize());
			}
			testGroup = tg;
		}

		public TestGroup deserialize(Object... params) {
			if (testGroup == null) {
				Group group = (Group) params[0];
				testGroup = new TestGroup(group, name, weight, isRoot);
				for (DataObject<TestGroup> t : subgroups) {
					testGroup.addSubgroup(t.deserialize(group));
				}
				for (DataObject<Test> t : tests) {
					testGroup.addTest(t.deserialize());
				}
			}
			return testGroup;
		}
	}

	@SuppressWarnings("unchecked")
	public DataObject<TestGroup> serialize() {
		DataObject<?> testGroup = DataObject.getSerialized(this);
		if (testGroup == null) {
			return new TestGroupS(this);
		} else {
			return (DataObject<TestGroup>) testGroup;
		}
	}

}
