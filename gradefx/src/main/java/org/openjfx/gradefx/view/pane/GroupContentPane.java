package org.openjfx.gradefx.view.pane;

import java.util.List;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.dialog.DialogAddTest;
import org.openjfx.gradefx.view.tab.GroupOverviewTab;
import org.openjfx.gradefx.view.tab.TestTab;
import org.openjfx.kafx.view.pane.AddTabPane;
import org.openjfx.kafx.view.style.Styles;

import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.control.Tab;

public class GroupContentPane extends AddTabPane {

	private final Group group;

	public GroupContentPane(Group group) {
		this.group = group;
		this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		this.setSide(Side.BOTTOM);
		this.setTabDragPolicy(TabDragPolicy.REORDER);
		this.group.addTestsListener((ListChangeListener<Test>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					List<? extends Test> added = c.getAddedSubList();
					for (Test t : added) {
						addTestTab(t);
					}
				}
				if (c.wasRemoved()) {
					List<? extends Test> removed = c.getRemoved();
					for (Test t : removed) {
						removeTestTab(t);
					}
				}
			}
		});
		this.addFixedTab(new GroupOverviewTab(group));
		for (Test t : group.getTests()) {
			addTestTab(t);
		}

		this.getSelectionModel().selectedItemProperty().addListener((_, _, newTab) -> {
			if (newTab instanceof TestTab) {
				TestTab testTab = (TestTab) newTab;
				GradeFXController.setSelectedTest(testTab.getTest());
			} else if (newTab instanceof GroupOverviewTab) {
				GradeFXController.setSelectedTest(null);
			}
		});

		Styles.subscribeThemeColor(this, group.colorProperty());
		this.getStyleClass().addAll("tab-pane-selected-bold");
		
		this.getTabs().addListener((ListChangeListener<Tab>) c -> {
			while (c.next()) {
				if (c.wasPermutated()) {
					group.getTests().sort((t1, t2) -> {
						int index1 = tabIndexOf(t1);
						int index2 = tabIndexOf(t2);
						if (index1 == index2) {
							return 0;
						} else {
							return index1 - index2;
						}
					});
				}
			}
		});
	}

	private int tabIndexOf(Test test) {
		for (int i = 0; i < this.getTabs().size(); i++) {
			Tab t = this.getTabs().get(i);
			if ((t instanceof TestTab) && ((TestTab) t).getTest() == test) {
				return i;
			}
		}
		return -1;
	}

	private void removeTestTab(Test test) {
		TestTab toRemove = null;
		for (Tab t : this.getTabs()) {
			if ((t instanceof TestTab) && ((TestTab) t).getTest() == test) {
				toRemove = (TestTab) t;
				break;
			}
		}
		if (toRemove != null) {
			this.getTabs().remove(toRemove);
		}
	}

	private void addTestTab(Test test) {
		this.addTab(new TestTab(this.group, test));
	}

	@Override
	protected boolean createNewTab() {
		return new DialogAddTest(this.group).showAndWait().isPresent();
	}
}
