package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.view.dialog.DialogAddGroup;
import org.openjfx.gradefx.view.tab.GroupTab;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.view.pane.AddTabPane;

import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.control.Tab;

public class GroupsPane extends AddTabPane {

	private final static GroupsPane instance = new GroupsPane();

	public static GroupsPane get() {
		return instance;
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
		FontSizeController.fontSizeProperty().subscribe(fontSize -> {
			this.setTabMinWidth(fontSize.doubleValue() * 4 + 5);
			this.setTabMinHeight(fontSize.doubleValue() * 4 + 5);
			this.setTabMaxWidth(fontSize.doubleValue() * 4 + 5);
			this.setTabMaxHeight(fontSize.doubleValue() * 4 + 5);
		});
		this.setTabDragPolicy(TabDragPolicy.REORDER);
		this.getSelectionModel().selectedItemProperty().addListener((_, _, newTab) -> {
			if (newTab instanceof GroupTab) {
				GroupTab groupTab = (GroupTab) newTab;
				GradeFXController.setSelectedGroup(groupTab.getGroup());
			} else {
				GradeFXController.setSelectedGroup(null);
			}
			GradeFXController.setSelectedTest(null);
			GradeFXController.setSelectedStudent(null);
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

	public static void select(int index) {
		instance.getSelectionModel().select(index);
	}
}
