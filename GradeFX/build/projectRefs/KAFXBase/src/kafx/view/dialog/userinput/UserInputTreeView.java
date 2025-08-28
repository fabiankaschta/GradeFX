package kafx.view.dialog.userinput;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class UserInputTreeView<S> extends UserInputMulti<TreeItem<S>> {

	private final TreeView<S> treeView;

	@SafeVarargs
	public UserInputTreeView(TreeView<S> treeView, TreeItem<S>... defaultValues) {
		this(treeView, true, defaultValues);
	}

	@SafeVarargs
	public UserInputTreeView(TreeView<S> treeView, boolean allowEmpty, TreeItem<S>... defaultValues) {
		super(defaultValues, defaultValues, allowEmpty);
		this.treeView = treeView;
		for (TreeItem<S> s : defaultValues) {
			this.treeView.getSelectionModel().select(s);
		}
		Bindings.bindContent(this.selectedValues(), this.treeView.getSelectionModel().getSelectedItems());
		this.treeView.selectionModelProperty().addListener((_, oldValue, newValue) -> {
			Bindings.unbindContent(this.selectedValues(), oldValue.getSelectedItems());
			Bindings.bindContent(this.selectedValues(), newValue.getSelectedItems());
		});
		this.selectedValues().addListener((ListChangeListener<TreeItem<S>>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (TreeItem<S> s : c.getAddedSubList()) {
						this.treeView.getSelectionModel().select(s);
					}
				}
				if (c.wasRemoved()) {
					for (TreeItem<S> s : c.getRemoved()) {
						this.treeView.getSelectionModel().clearSelection(this.treeView.getRow(s));
					}
				}
			}
		});
		this.getChildren().add(this.treeView);
	}

	@Override
	public void requestFocus() {
		this.treeView.requestFocus();
	}

}
