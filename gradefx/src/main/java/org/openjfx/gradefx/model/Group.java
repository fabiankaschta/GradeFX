package org.openjfx.gradefx.model;

import java.util.ArrayList;
import java.util.List;

import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.controller.ChangeController;
import org.openjfx.kafx.io.DataObject;
import org.openjfx.kafx.view.color.ColorHelper;

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
import javafx.scene.paint.Color;

public class Group {

	private final static ObservableList<Group> groups = FXCollections.observableArrayList();
	private final static ListChangeListener<Group> listener = new ListChangeListener<>() {

		@Override
		public void onChanged(Change<? extends Group> c) {
			while (c.next()) {
				if (c.wasAdded()) {
					List<? extends Group> added = c.getAddedSubList();
					for (Group g : added) {
						GroupsPane.addTab(g);
					}
				}
				if (c.wasRemoved()) {
					List<? extends Group> removed = c.getRemoved();
					for (Group g : removed) {
						GroupsPane.removeTab(g);
					}
				}
			}
		}

	};

	static {
		groups.addListener(listener);
		groups.addListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
	}

	public static ObservableList<Group> getGroups() {
		return FXCollections.unmodifiableObservableList(groups);
	}

	public static boolean remove(Group g) {
		return groups.remove(g);
	}

	public static void clearGroups() {
		groups.clear();
	}

	public static void setGroups(Group[] newGroups) {
		groups.removeListener(listener);
		groups.clear();
		groups.addAll(newGroups);
		groups.addListener(listener);
	}

	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final ObjectProperty<Subject> subject = new SimpleObjectProperty<>(this, "subject");
	private final BooleanProperty useSubgroups = new SimpleBooleanProperty(this, "useSubgroups");
	private final ObservableList<Student> students = FXCollections.observableArrayList();
	private final ObservableList<Test> tests = FXCollections.observableArrayList(); // order of tabs
	private final ObjectProperty<GradeSystem> gradeSystem = new SimpleObjectProperty<>(this, "gradeSystem");
	private final ObjectProperty<TestGroupSystem> testGroupSystem = new SimpleObjectProperty<>(this, "testGroupSystem");
	private final ObjectProperty<TestGroup> testGroupRoot = new SimpleObjectProperty<>(this, "testGroupRoot");
	private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this, "color");

	{
		testGroupSystem.addListener(_ -> {
			testGroupRoot.set(testGroupSystem.get().getNewTestGroupRoot());
			for (Test test : tests) {
				testGroupRoot.get().addTest(test);
			}
		});
	}

	public Group(String name, Subject subject, boolean useSubgroups, GradeSystem gradeSystem,
			TestGroupSystem testGroupSystem, Color color) {
		this.setName(name);
		this.setSubject(subject);
		this.setUseSubgroups(useSubgroups);
		this.setGradeSystem(gradeSystem);
		this.setTestGroupSystem(testGroupSystem);
		this.setColor(color);
		groups.add(this);
		this.nameProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.subjectProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.useSubgroupsProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.addStudentsListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
//		this.addTestsListener(ChangeController.LISTLISTENER_UNSAVED_CHANGES);
		this.gradeSystemProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.testGroupSystemProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.testGroupRootProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.colorProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.students.addListener((ListChangeListener<Student>) c -> {
			while (c.next()) {
				if (c.wasRemoved()) {
					for (Student student : c.getRemoved()) {
						this.tests.forEach(test -> test.removeStudent(student));
					}
				}
			}
		});
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

	public Subject getSubject() {
		return subject.get();
	}

	public ObjectProperty<Subject> subjectProperty() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject.set(subject);
	}

	public boolean getUseSubgroups() {
		return useSubgroups.get();
	}

	public BooleanProperty useSubgroupsProperty() {
		return useSubgroups;
	}

	public void setUseSubgroups(boolean useSubgroups) {
		this.useSubgroups.set(useSubgroups);
	}

	public ObservableList<Student> getStudents() {
		return students;
	}

	public void addStudentsListener(ListChangeListener<? super Student> listener) {
		students.addListener(listener);
	}

	public void addStudent(Student student) {
		this.students.add(student);
	}

	public void removeStudent(Student student) {
		this.students.remove(student);
	}

	public ObservableList<Test> getTests() {
		return tests;
	}

	public TestGroup getTestGroup(Test test) {
		return getTestGroupHelper(test, getTestGroupRoot());
	}

	public void setTestGroup(Test test, TestGroup testGroup) {
		if (!getTests().contains(test)) {
			addTest(test, testGroup);
		} else {
			TestGroup old = getTestGroup(test);
			if (old != null) {
				old.removeTest(test);
			}
			testGroup.addTest(test);
		}
	}

	private TestGroup getTestGroupHelper(Test test, TestGroup root) {
		if (root.getTests().contains(test)) {
			return root;
		} else {
			TestGroup result = null;
			for (TreeItem<TestGroup> child : root.getChildren()) {
				TestGroup testGroup = (TestGroup) child;
				result = getTestGroupHelper(test, testGroup);
				if (result != null) {
					return result;
				}
			}
			return null;
		}
	}

	public ObservableList<Test> getTestsInTestGroup(TestGroup testGroup) {
		return testGroup.getTests();
	}

	public void addTestsListener(ListChangeListener<? super Test> listener) {
		tests.addListener(listener);
	}

	public void addTest(Test test, TestGroup testGroup) {
		testGroup.addTest(test);
		this.tests.add(test);
	}

	public void removeTest(Test test) {
		TestGroup testGroup = getTestGroup(test);
		if (testGroup != null) {
			testGroup.removeTest(test);
			this.tests.remove(test);
		}
	}

	public GradeSystem getGradeSystem() {
		return gradeSystem.get();
	}

	public ObjectProperty<GradeSystem> gradeSystemProperty() {
		return gradeSystem;
	}

	public void setGradeSystem(GradeSystem gradeSystem) {
		this.gradeSystem.set(gradeSystem);
	}

	public TestGroupSystem getTestGroupSystem() {
		return testGroupSystem.get();
	}

	public ObjectProperty<TestGroupSystem> testGroupSystemProperty() {
		return testGroupSystem;
	}

	public void setTestGroupSystem(TestGroupSystem testGroupSystem) {
		this.testGroupSystem.set(testGroupSystem);
	}

	public TestGroup getTestGroupRoot() {
		return testGroupRoot.get();
	}

	public ObjectProperty<TestGroup> testGroupRootProperty() {
		return testGroupRoot;
	}

	public Color getColor() {
		return color.get();
	}

	public ObjectProperty<Color> colorProperty() {
		return color;
	}

	public void setColor(Color color) {
		this.color.set(color);
	}

	@Override
	public String toString() {
		return this.getName();
	}

	private static class GroupS implements DataObject<Group> {

		private static final long serialVersionUID = 6323505892334093500L;

		private final String name;
		private final DataObject<Subject> subject;
		private final boolean useSubgroups;
		private final List<DataObject<Student>> students = new ArrayList<>();
		private final List<DataObject<Test>> tests = new ArrayList<>();
		private final GradeSystem gradeSystem;
		private final DataObject<TestGroupSystem> testGroupSystem;
		private final DataObject<TestGroup> testGroupRoot;
		private final String color;

		private transient Group group;

		private GroupS(Group g) {
			DataObject.putSerialized(g, this);
			this.name = g.getName();
			this.subject = g.getSubject().serialize();
			this.useSubgroups = g.getUseSubgroups();
			this.gradeSystem = g.getGradeSystem();
			this.testGroupSystem = g.getTestGroupSystem().serialize();
			this.testGroupRoot = g.getTestGroupRoot().serialize();
			this.color = ColorHelper.toHexString(g.getColor());
			for (Student s : g.students) {
				this.students.add(s.serialize());
			}
			for (Test t : g.tests) {
				this.tests.add(t.serialize());
			}
			group = g;
		}

		public Group deserialize(Object... params) {
			if (group == null) {
				group = new Group(name, subject.deserialize(), useSubgroups, gradeSystem, testGroupSystem.deserialize(),
						Color.web(color));
				group.testGroupRoot.set(testGroupRoot.deserialize());
				for (DataObject<Student> s : students) {
					group.students.add(s.deserialize());
				}
				for (DataObject<Test> t : tests) {
					group.tests.add(t.deserialize());
				}
			}
			return group;
		}
	}

	@SuppressWarnings("unchecked")
	public DataObject<Group> serialize() {
		DataObject<?> group = DataObject.getSerialized(this);
		if (group == null) {
			return new GroupS(this);
		} else {
			return (DataObject<Group>) group;
		}
	}

}
