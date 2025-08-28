package kafx.view.listview;

import java.util.LinkedList;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;

/**
 * Modified version of listView Allows multi-select without ctrl/shift.
 * Deselection by clicking on selected item again. Shift+click selects
 * everything from the last selected item to the new selected item
 * 
 * @see javafx.scene.control.ListView<T> ListView<T>
 */
public class MultiSelectListView<T> extends ListView<T> {

	private final ObjectProperty<T> selectedItemWorkaround = new SimpleObjectProperty<>() {
		// workaround: if selected item would not change, change it anyway using null
		public void set(T newValue) {
			if (this.get() != null && this.get().equals(newValue)) {
				super.set(null);
				super.set(newValue);
			} else {
				super.set(newValue);
			}
		}
	};

	@SuppressWarnings("unchecked")
	public MultiSelectListView(T... ts) {
		this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.setPrefHeight(5 * 26);
		this.setCellFactory(lv -> {
			ListCell<T> cell = new ListCell<>();
			cell.textProperty().bind(
					Bindings.when(cell.emptyProperty()).then("").otherwise(Bindings.format("%s", cell.itemProperty())));

			cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				if (event.getClickCount() > 1 && event.getSource() instanceof ListCell<?>) {
					doubleClick((ListCell<T>) event.getSource());
					event.consume();
				}
			});

			cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
				lv.requestFocus();
				int index = cell.getIndex();
				// T t = cell.getItem();
				if (event.getClickCount() > 1) {
					event.consume();
				}
				if (event.isShiftDown()) {
					int indexFrom = lv.getSelectionModel().getSelectedIndex();
					for (int i = indexFrom; i <= index; i++) {
						lv.getSelectionModel().select(i);
					}
				} else {
					if (lv.getSelectionModel().getSelectedIndices().contains(index)) {
						lv.getSelectionModel().clearSelection(index);
					} else {
						// TODO shows selected items on top but scrolling missing
//						lv.getItems().remove(index);
//						lv.getItems().add(0, t);
//						lv.getSelectionModel().select(0);
						lv.getSelectionModel().select(index);
					}
				}
				event.consume();
			});
			return cell;
		});
		// workaround: missing update on clearSelection(int)
		this.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends T> c) -> {
			while (c.next()) {
				if (c.wasAdded()) {
					selectedItemWorkaround.set(c.getAddedSubList().get(0));
				} else if (c.wasRemoved()) {
					LinkedList<? extends T> remaining = new LinkedList<>(c.getList());
					remaining.removeAll(c.getRemoved());
					if (remaining.size() == 0) {
						selectedItemWorkaround.set(null);
					} else {
						selectedItemWorkaround.set(remaining.getLast());
					}
				}
			}
		});
		this.addItems(ts);
	}

	protected void doubleClick(ListCell<T> cell) {

	}

	public void addItem(T t) {
		this.getItems().add(t);
	}

	public void addItems(ObservableList<T> ts) {
		this.getItems().addAll(ts);
	}

	@SuppressWarnings("unchecked")
	public void addItems(T... ts) {
		this.getItems().addAll(ts);
	}

	public void deselectAll() {
		this.getSelectionModel().clearSelection();
	}

	public boolean isSelectedAny() {
		return this.getSelectionModel().getSelectedItems().size() > 0;
	}

	@SuppressWarnings("unchecked")
	public T[] getSelectedItems(T[] t) {
		return this.getSelectionModel().getSelectedItems().toArray((T[]) java.lang.reflect.Array
				.newInstance(t.getClass().getComponentType(), this.getSelectionModel().getSelectedItems().size()));
	}

	public ObjectProperty<T> selectedProperty() {
		return selectedItemWorkaround;
	}

}
