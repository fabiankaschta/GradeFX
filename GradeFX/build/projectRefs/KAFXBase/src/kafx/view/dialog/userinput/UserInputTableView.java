package kafx.view.dialog.userinput;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableView;

public class UserInputTableView<S> extends UserInputMulti<S> {

	private final TableView<S> tableView;

	@SafeVarargs
	public UserInputTableView(TableView<S> tableView, S... defaultValues) {
		this(tableView, true, defaultValues);
	}

	@SafeVarargs
	public UserInputTableView(TableView<S> tableView, boolean allowEmpty, S... defaultValues) {
		super(defaultValues, defaultValues, allowEmpty);
		this.tableView = tableView;
		for (S s : defaultValues) {
			this.tableView.getSelectionModel().select(s);
		}
		Bindings.bindContent(this.selectedValues(), this.tableView.getSelectionModel().getSelectedItems());
		this.tableView.selectionModelProperty().addListener((_, oldValue, newValue) -> {
			Bindings.unbindContent(this.selectedValues(), oldValue.getSelectedItems());
			Bindings.bindContent(this.selectedValues(), newValue.getSelectedItems());
		});
		this.selectedValues().addListener((ListChangeListener<S>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (S s : c.getAddedSubList()) {
						this.tableView.getSelectionModel().select(s);
					}
				}
				if (c.wasRemoved()) {
					for (S s : c.getRemoved()) {
						this.tableView.getSelectionModel().clearSelection(this.tableView.getItems().indexOf(s));
					}
				}
			}
		});
		this.getChildren().add(this.tableView);
	}

	@Override
	public void requestFocus() {
		this.tableView.requestFocus();
	}

}
