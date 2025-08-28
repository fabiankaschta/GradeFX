package kafx.view.pane;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

public class CustomDragTabPane extends TabPane {

	private double min, max;

	private final List<Tab> fixedTabs = new ArrayList<>();

	public void addFixedTab(Tab tab) {
		if (tab.getGraphic() == null) {
			throw new IllegalArgumentException("tab must have a graphic set");
		}
		fixedTabs.add(tab);
		getTabs().add(tab);
	}

	public void addFixedTab(int index, Tab tab) {
		if (tab.getGraphic() == null) {
			throw new IllegalArgumentException("tab must have a graphic set");
		}
		fixedTabs.add(tab);
		getTabs().add(index, tab);
	}

	public void setFixedTab(int index, Tab tab) {
		if (tab.getGraphic() == null) {
			throw new IllegalArgumentException("tab must have a graphic set");
		}
		fixedTabs.add(tab);
		getTabs().set(index, tab);
	}

	public CustomDragTabPane() {
		// default: reorder possible
		setTabDragPolicy(TabDragPolicy.REORDER);
		// removed tabs are removed from list of fixed tabs
		getTabs().addListener(new ListChangeListener<>() {
			@Override
			public void onChanged(Change<? extends Tab> c) {
				while (c.next()) {
					if (c.wasRemoved()) {
						fixedTabs.removeAll(c.getRemoved());
					}
				}
			}

		});
		addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			// set last recorded mouse position
			if (!(e.getTarget() instanceof Node)) {
				throw new IllegalArgumentException("all tabs need to use setGraphic()!");
			}
			Node draggedContainer = getTargetContainer((Node) e.getTarget());
			TabPane draggedTabPane = getTargetTabPane(draggedContainer);
			if (draggedTabPane == null || draggedTabPane != this) {
				// nothing to do here, if this is not our tab
				return;
			}
			Tab draggedTab = getContainerTab(draggedContainer);
			if (draggedTab == null) {
				throw new IllegalArgumentException("all tabs need to use setGraphic()!");
			}
			// abort drag on fixed tabs
			if (fixedTabs.contains(draggedTab)) {
				e.consume();
				return;
			}
			Tab prevFixed = getPrevFixedTab(draggedTab);
			Tab nextFixed = getNextFixedTab(draggedTab);
			Tab prev = getPrevTab(draggedTab);
			Tab next = getNextTab(draggedTab);
			Tab first = getTabs().get(0);
			Tab last = getTabs().get(getTabs().size() - 1);
			// +/- 5 feels better, dunno why
			if (this.getSide() == Side.LEFT || this.getSide() == Side.RIGHT) { // vertical
				if (prev == null) {
					min = e.getSceneY();
				} else if (prevFixed == null) {
					min = getTabContainerBounds(first).getMinY()
							+ (e.getSceneY() - getTabContainerBounds(draggedTab).getMinY()) - 5;
				} else {
					min = getTabContainerBounds(prevFixed).getMaxY()
							+ (e.getSceneY() - getTabContainerBounds(prev).getMaxY());
				}
				if (next == null) {
					max = e.getSceneY();
				} else if (nextFixed == null) {
					max = getTabContainerBounds(last).getMaxY()
							- (getTabContainerBounds(draggedTab).getMaxY() - e.getSceneY()) + 5;
				} else {
					max = getTabContainerBounds(nextFixed).getMinY()
							- (getTabContainerBounds(next).getMinY() - e.getSceneY());
				}
			} else { // horizontal
				if (prev == null) {
					min = e.getSceneX();
				} else if (prevFixed == null) {
					min = getTabContainerBounds(first).getMinX()
							+ (e.getSceneX() - getTabContainerBounds(draggedTab).getMinX()) - 5;
				} else {
					min = getTabContainerBounds(prevFixed).getMaxX()
							+ (e.getSceneX() - getTabContainerBounds(prev).getMaxX());
				}
				if (next == null) {
					max = e.getSceneX();
				} else if (nextFixed == null) {
					max = getTabContainerBounds(last).getMaxX()
							- (getTabContainerBounds(draggedTab).getMaxX() - e.getSceneX()) + 5;
				} else {
					max = getTabContainerBounds(nextFixed).getMinX()
							- (getTabContainerBounds(next).getMinX() - e.getSceneX());
				}
			}
		});

		addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			if (!(e.getTarget() instanceof Node)) {
				throw new IllegalArgumentException("all tabs need to use setGraphic()!");
			}
			Node draggedContainer = getTargetContainer((Node) e.getTarget());
			TabPane draggedTabPane = getTargetTabPane(draggedContainer);
			if (draggedTabPane == null || draggedTabPane != this) {
				// nothing to do here, if this is not our tab
				return;
			}
			Tab draggedTab = getContainerTab(draggedContainer);
			if (draggedTab == null) {
				throw new IllegalArgumentException("all tabs need to use setGraphic()!");
			}
			// abort drag on fixed tabs
			if (fixedTabs.contains(draggedTab)) {
				e.consume();
				return;
			}
			if (this.getSide() == Side.LEFT || this.getSide() == Side.RIGHT) { // vertical
				if (e.getSceneY() < min || e.getSceneY() > max) {
					e.consume();
				}
			} else { // horizontal
				if (e.getSceneX() < min || e.getSceneX() > max) {
					e.consume();
				}
			}
		});
	}

	private Node getTargetContainer(Node target) {
		if (target == null) {
			return null;
		}
		Node tabContainer = target;
		while (!tabContainer.getStyleClass().contains("tab-container")) {
			tabContainer = tabContainer.getParent();
			if (tabContainer == null) {
				return null;
			}
		}
		return tabContainer;
	}

	protected Node getTabContainer(Tab tab) {
		return getTargetContainer(tab.getGraphic());
	}

	private Tab getContainerTab(Node tabContainer) {
		for (Tab tab : getTabs()) {
			if (tabContainer == getTabContainer(tab)) {
				return tab;
			}
		}
		return null;
	}

	private Bounds getTargetContainerBounds(Node target) {
		Node tabContainer = getTargetContainer(target);
		if (tabContainer == null) {
			return null;
		}
		return tabContainer.localToScene(tabContainer.getBoundsInLocal());
	}

	private Bounds getTabContainerBounds(Tab tab) {
		return getTargetContainerBounds(tab.getGraphic());
	}

	private TabPane getTargetTabPane(Node target) {
		if (target == null) {
			return null;
		}
		Node targetTabParent = target;
		while (!targetTabParent.getStyleClass().contains("tab-pane")) {
			targetTabParent = targetTabParent.getParent();
			if (targetTabParent == null) {
				return null;
			}
		}
		return (TabPane) targetTabParent;
	}

	private Tab getNextTab(Tab from) {
		int tabIndex = getTabs().indexOf(from);
		if (tabIndex + 1 < getTabs().size()) {
			return getTabs().get(tabIndex + 1);
		}
		return null;
	}

	private Tab getPrevTab(Tab from) {
		int tabIndex = getTabs().indexOf(from);
		if (tabIndex - 1 >= 0) {
			return getTabs().get(tabIndex - 1);
		}
		return null;
	}

	private Tab getNextFixedTab(Tab from) {
		int tabIndex = getTabs().indexOf(from);
		for (int i = tabIndex + 1; i < getTabs().size(); i++) {
			Tab tab = getTabs().get(i);
			if (fixedTabs.contains(tab)) {
				return tab;
			}
		}
		return null;
	}

	private Tab getPrevFixedTab(Tab from) {
		int tabIndex = getTabs().indexOf(from);
		for (int i = tabIndex - 1; i >= 0; i--) {
			Tab tab = getTabs().get(i);
			if (fixedTabs.contains(tab)) {
				return tab;
			}
		}
		return null;
	}

}
