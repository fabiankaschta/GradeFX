package gradefx.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import gradefx.controller.Controller;
import kafx.io.DataObject;
import kafx.lang.Translator;
import gradefx.view.converter.TestGroupConverter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class TestGroup extends TreeItem<TestGroup> {

	public static class TestGroupSystem extends TreeItem<TestGroupSystem> {

		private final static ObservableList<TestGroupSystem> testGroupSystems = FXCollections.observableArrayList();

		public static ObservableList<TestGroupSystem> getTestGroupSystems() {
			return testGroupSystems;
		}

		public static void removeTestGroupSystem(TestGroupSystem testGroupSystem) {
			testGroupSystems.remove(testGroupSystem);
		}

		public static TestGroupSystem get(String name) {
			for (TestGroupSystem testGroupSystem : testGroupSystems) {
				if (testGroupSystem.getName().equals(name)) {
					return testGroupSystem;
				}
			}
			return null;
		}

		public static TestGroupSystem createRoot(String name) {
			return new TestGroupSystem(name, BigDecimal.ONE);
		}

		public static TestGroupSystem createSubSystem(String name, BigDecimal weight) {
			return new TestGroupSystem(name, weight, false);
		}

		public static void setDefault() {
			testGroupSystems.clear();
			{
				createRoot(Translator.get("testGroupSystem_NO_GROUPS"));
			}
			{
				TestGroupSystem root = createRoot(Translator.get("testGroupSystem_SMALL_LARGE"));
				root.addSubgroup(createSubSystem(Translator.get("testGroupSystem_small"), BigDecimal.ONE));
				root.addSubgroup(createSubSystem(Translator.get("testGroupSystem_large"), BigDecimal.TWO));
			}
			{
				TestGroupSystem root = createRoot(Translator.get("testGroupSystem_SMALL_LARGE_EQUAL"));
				root.addSubgroup(createSubSystem(Translator.get("testGroupSystem_small"), BigDecimal.ONE));
				root.addSubgroup(createSubSystem(Translator.get("testGroupSystem_large"), BigDecimal.ONE));
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
			this.nameProperty().addListener(Controller.LISTENER_UNSAVED_CHANGES);
			this.weightProperty().addListener(Controller.LISTENER_UNSAVED_CHANGES);
			this.getChildren().addListener(Controller.LISTLISTENER_UNSAVED_CHANGES);
			super.setValue(this);
			super.setExpanded(true);
		}

		public TestGroup getNewTestGroupRoot() {
			TestGroup root = new TestGroup(getName(), getWeight(), isRoot());
			for (TreeItem<TestGroupSystem> t : getChildren()) {
				root.addSubgroup(((TestGroupSystem) t).getNewTestGroupRoot());
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
					: getName() + " (" + Translator.get("testGroup_weight") + ": " + getWeight() + ")";
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
					testGroupSystem = TestGroupSystem.get(name);
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
				}
				return testGroupSystem;
			}

		}

		@SuppressWarnings("unchecked")
		public DataObject<TestGroupSystem> serialize() {
			DataObject<?> testGroupSystem = DataObject.getSerialized(this);
			if (testGroupSystem == null) {
				return new TestGroupSystemS(this);
			} else {
				return (DataObject<TestGroupSystem>) testGroupSystem;
			}
		}

	}

	private final static TestGroupConverter converter = new TestGroupConverter();

	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final ObjectProperty<BigDecimal> weight = new SimpleObjectProperty<>(this, "weight");
	private final BooleanProperty isRoot = new SimpleBooleanProperty(this, "isRoot");
	private final ObservableList<Test> tests = FXCollections.observableArrayList();

	private TestGroup(String name, BigDecimal weight, boolean isRoot) {
		this.setName(name);
		this.setWeight(weight);
		this.setIsRoot(isRoot);
		this.nameProperty().addListener(Controller.LISTENER_UNSAVED_CHANGES);
		this.weightProperty().addListener(Controller.LISTENER_UNSAVED_CHANGES);
		this.addTestsListener(Controller.LISTLISTENER_UNSAVED_CHANGES);
		this.getChildren().addListener(Controller.LISTLISTENER_UNSAVED_CHANGES);
		super.setValue(this);
	}

	public String getName() {
		return name.get();
	}

	private void setName(String name) {
		this.name.set(name);
	}

	private StringProperty nameProperty() {
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
				testGroup = new TestGroup(name, weight, isRoot);
				for (DataObject<TestGroup> t : subgroups) {
					testGroup.addSubgroup(t.deserialize());
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
