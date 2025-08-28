package kafx.view.dialog.userinput;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ListView;

public class UserInputListView<S> extends UserInputMulti<S> {

	protected final ListView<S> listView;

	@SafeVarargs
	public UserInputListView(ListView<S> listView, S... defaultValues) {
		this(listView, true, defaultValues);
	}

	@SafeVarargs
	public UserInputListView(ListView<S> listView, boolean allowEmpty, S... defaultValues) {
		super(defaultValues, allowEmpty);
		this.listView = listView;
		for (S s : defaultValues) {
			this.listView.getSelectionModel().select(s);
		}
		Bindings.bindContent(this.selectedValues(), this.listView.getSelectionModel().getSelectedItems());
		this.listView.selectionModelProperty().addListener((_, oldValue, newValue) -> {
			Bindings.unbindContent(this.selectedValues(), oldValue.getSelectedItems());
			Bindings.bindContent(this.selectedValues(), newValue.getSelectedItems());
		});
		this.selectedValues().addListener((ListChangeListener<S>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (S s : c.getAddedSubList()) {
						this.listView.getSelectionModel().select(s);
					}
				}
				if (c.wasRemoved()) {
					for (S s : c.getRemoved()) {
						this.listView.getSelectionModel().clearSelection(this.listView.getItems().indexOf(s));
					}
				}
			}
		});
		this.getChildren().add(this.listView);
	}

	@Override
	public void requestFocus() {
		this.listView.requestFocus();
	}

}
