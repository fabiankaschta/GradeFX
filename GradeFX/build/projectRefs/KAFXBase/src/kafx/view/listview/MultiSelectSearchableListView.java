package kafx.view.listview;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;

/**
 * Modified version of listView Allows multi-select without ctrl/shift.
 * Deselection by clicking on selected item again. Shift+click selects
 * everything from the last selected item to the new selected item
 * 
 * @see javafx.scene.control.ListView<T> ListView<T>
 */
public class MultiSelectSearchableListView<T> extends MultiSelectListView<T> {

	private final ObservableList<T> items = FXCollections.observableArrayList();

	@SuppressWarnings("unchecked")
	public MultiSelectSearchableListView(TextField searchField, T... ts) {
		super(ts);
		FilteredList<T> filteredList = new FilteredList<>(items, _ -> true);
		this.setItems(filteredList);
		searchField.textProperty().addListener(((_, _, newValue) -> {
			filteredList.setPredicate(data -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				String lowerCaseSearch = newValue.toLowerCase();
				return String.valueOf(data).contains(lowerCaseSearch);
			});
		}));
	}

	public ObservableList<T> getItemsIntern() {
		return this.items;
	}

}
