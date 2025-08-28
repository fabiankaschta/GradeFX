package gradefx.view.pane;

import java.util.List;

import gradefx.model.Group;
import gradefx.model.Test;
import gradefx.view.dialog.DialogAddTest;
import gradefx.view.style.Styles;
import gradefx.view.tab.GroupOverviewTab;
import gradefx.view.tab.TestTab;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import kafx.view.pane.AddTabPane;

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
						Platform.runLater(() -> addTestTab(t));
					}
				}
				if (c.wasRemoved()) {
					List<? extends Test> removed = c.getRemoved();
					for (Test t : removed) {
						Platform.runLater(() -> removeTestTab(t));
					}
				}
			}
		});
		this.addFixedTab(new GroupOverviewTab(group));
		for (Test t : group.getTests()) {
			Platform.runLater(() -> addTestTab(t));
		}

		this.getStyleClass().addAll("tab-pane-selected-bold");
		Styles.subscribeTabPaneColor(this, this.group.colorProperty());
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
