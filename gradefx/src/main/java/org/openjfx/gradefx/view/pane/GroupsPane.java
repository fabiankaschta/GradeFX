package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.dialog.DialogAddGroup;
import org.openjfx.gradefx.view.tab.GroupOverviewTab;
import org.openjfx.gradefx.view.tab.GroupTab;
import org.openjfx.gradefx.view.tab.TestTab;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.view.pane.AddTabPane;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.control.Tab;

public class GroupsPane extends AddTabPane {

	private final static GroupsPane instance = new GroupsPane();

	public static GroupsPane get() {
		return instance;
	}

	public static Group getSelectedGroup() {
		return ((GroupTab) instance.getSelectionModel().getSelectedItem()).getGroup();
	}

	public static int getSelectedGroupIndex() {
		return instance.getSelectionModel().getSelectedIndex();
	}

	private final static ObjectProperty<Student> selectedStudentProperty = new SimpleObjectProperty<>();

	public static ReadOnlyObjectProperty<Student> selectedStudentProperty() {
		return selectedStudentProperty;
	}

	public static Student getSelectedStudent() {
		return selectedStudentProperty.get();
	}

	public static void setSelectedStudent(Student student) {
		selectedStudentProperty.set(student);
	}

	private final static ObjectProperty<Test> selectedTestProperty = new SimpleObjectProperty<>();

	public static ReadOnlyObjectProperty<Test> selectedTestProperty() {
		return selectedTestProperty;
	}

	private final static ObjectBinding<Group> selectedGroupProperty = Bindings.createObjectBinding(() -> {
		Tab tab = instance.getSelectionModel().getSelectedItem();
		if (tab instanceof GroupTab) {
			return ((GroupTab) tab).getGroup();
		} else {
			return null;
		}
	}, instance.getSelectionModel().selectedItemProperty());

	public static ObjectBinding<Group> selectedGroupProperty() {
		return selectedGroupProperty;
	}

	public static Test getSelectedTest() {
		return selectedTestProperty.get();
	}

	public static void setSelectedTest(Test test) {
		selectedTestProperty.set(test);
	}

	public static int getSelectedTabInGroupIndex(Group group) {
		for (Tab tab : instance.getTabs()) {
			if (tab instanceof GroupTab) {
				GroupTab groupTab = (GroupTab) tab;
				if (groupTab.getGroup() == group) {
					return groupTab.getSelectedTabIndex();
				}
			}
		}
		return 0;
	}

	public static Tab getSelectedTabInGroup() {
		return getSelectedTab().getSelectedTab();
	}

	public static GroupTab getSelectedTab() {
		for (Tab tab : instance.getTabs()) {
			if (tab instanceof GroupTab && tab.isSelected()) {
				return (GroupTab) tab;
			}
		}
		return null;
	}

	public static void select(int index) {
		instance.getSelectionModel().select(index);
	}

	public static void selectTab(Group group, int index) {
		for (Tab tab : instance.getTabs()) {
			GroupTab groupTab = (GroupTab) tab;
			if (groupTab.getGroup() == group) {
				groupTab.select(index);
				return;
			}
		}
	}

	public static void addTab(Group group) {
		instance.addTab(new GroupTab(group));
	}

	public static void removeTab(Group group) {
		GroupTab toRemove = null;
		for (Tab t : instance.getTabs()) {
			if ((t instanceof GroupTab) && ((GroupTab) t).getGroup() == group) {
				toRemove = (GroupTab) t;
				break;
			}
		}
		if (toRemove != null) {
			instance.getTabs().remove(toRemove);
		}
	}

	private GroupsPane() {
		this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		this.setSide(Side.LEFT);
		Controller.fontSizeProperty().subscribe(fontSize -> {
			this.setTabMinWidth(fontSize.doubleValue() * 4 + 5);
			this.setTabMinHeight(fontSize.doubleValue() * 4 + 5);
			this.setTabMaxWidth(fontSize.doubleValue() * 4 + 5);
			this.setTabMaxHeight(fontSize.doubleValue() * 4 + 5);
		});
		this.setTabDragPolicy(TabDragPolicy.REORDER);
		this.getSelectionModel().selectedItemProperty().addListener((_, _, newTab) -> {
			if (newTab instanceof GroupTab) {
				GroupTab groupTab = (GroupTab) newTab;
				Tab selectedSubTab = groupTab.getSelectedTab();
				if (selectedSubTab instanceof GroupOverviewTab) {
					GroupOverviewTab tab = (GroupOverviewTab) selectedSubTab;
					selectedStudentProperty.set(tab.getSelectedStudent());
					selectedTestProperty.set(null);
				} else if (selectedSubTab instanceof TestTab) {
					TestTab tab = (TestTab) selectedSubTab;
					selectedStudentProperty.set(tab.getSelectedStudent());
					selectedTestProperty.set(tab.getTest());
				} else {
					selectedStudentProperty.set(null);
					selectedTestProperty.set(null);
				}
			} else {
				selectedStudentProperty.set(null);
				selectedTestProperty.set(null);
			}
		});
		this.getTabs().addListener((ListChangeListener<Tab>) c -> {
			boolean permutate = false;
			while (c.next()) {
				if (c.wasPermutated()) {
					permutate = true;
				}
			}
			if (permutate) {
				Group[] newGroups = new Group[getTabs().size() - 1];
				for (int i = 0; i < getTabs().size() - 1; i++) {
					if (getTabs().get(i) instanceof GroupTab) {
						newGroups[i] = ((GroupTab) getTabs().get(i)).getGroup();
					}
				}
				Group.setGroups(newGroups);
			}
		});
		this.getStyleClass().addAll("tab-pane-offset", "tab-pane-selected-bold");
		this.setAddTabContent(new WelcomePane());
	}

	@Override
	protected boolean createNewTab() {
		return new DialogAddGroup().showAndWait().isPresent();
	}
}
