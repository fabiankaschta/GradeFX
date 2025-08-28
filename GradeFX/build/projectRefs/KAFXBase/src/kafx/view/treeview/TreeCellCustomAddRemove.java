package kafx.view.treeview;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

public class TreeCellCustomAddRemove<T> extends TreeCell<T> {

	private String addSymbol = "+";
	private String removeSymbol = "-";
	private final StringConverter<T> converter;
	private final Consumer<TreeItem<T>> addHandler;
	private final Function<TreeItem<T>, Optional<Boolean>> editHandler;
	private final Consumer<TreeItem<T>> removeHandler;

	{
		disclosureNodeProperty().addListener((_, oldValue, newValue) -> {
			if (oldValue instanceof StackPane) {
				final StackPane pane = (StackPane) oldValue;
				pane.translateYProperty().unbind();
			}
			if (newValue instanceof StackPane) {
				final StackPane pane = (StackPane) newValue;
				// these numbers work, for whatsoever reason
				pane.translateYProperty().bind(Bindings.createDoubleBinding(
						() -> getHeight() / 2.0 - pane.getHeight() / 1.5, heightProperty(), pane.heightProperty()));
			}
		});
	}

	public TreeCellCustomAddRemove(Consumer<TreeItem<T>> addHandler,
			Function<TreeItem<T>, Optional<Boolean>> editHandler, Consumer<TreeItem<T>> removeHandler) {
		this(null, addHandler, editHandler, removeHandler);
	}

	public TreeCellCustomAddRemove(StringConverter<T> converter, Consumer<TreeItem<T>> addHandler,
			Function<TreeItem<T>, Optional<Boolean>> editHandler, Consumer<TreeItem<T>> removeHandler) {
		this.addHandler = addHandler;
		this.editHandler = editHandler;
		if (this.editHandler != null) {
			setEditable(true);
		}
		this.removeHandler = removeHandler;
		this.converter = converter;
	}

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		// If the cell is empty we don't show anything.
		if (isEmpty()) {
			setGraphic(null);
			setText(null);
		} else {
			BorderPane graphic = new BorderPane();
			Label label = new Label();
			if (item == null) {
				label.setText("null");
			} else if (converter == null) {
				label.setText(item.toString());
			} else {
				label.setText(this.converter.toString(item));
			}
			ButtonBar buttons = new ButtonBar();
			Button addButton = new Button(addSymbol);
			addButton.setPrefWidth(30);
			buttons.getButtons().add(addButton);
			Button removeButton = new Button(removeSymbol);
			removeButton.setPrefWidth(30);
			buttons.getButtons().add(removeButton);
			// disable remove root
			removeButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
				return getTreeView().getRoot() == this.getTreeItem();
			}, getTreeView().rootProperty()));
			buttons.setButtonMinWidth(USE_COMPUTED_SIZE);
			graphic.setCenter(label);
			BorderPane.setAlignment(label, Pos.CENTER_LEFT);
			graphic.setRight(buttons);

			addButton.setOnAction(_ -> this.addHandler.accept(getTreeItem()));
			removeButton.setOnAction(_ -> this.removeHandler.accept(getTreeItem()));
			setGraphic(graphic);
			setText(null);
		}
	}

	public void setAddText(String addSymbol) {
		this.addSymbol = addSymbol;
	}

	public void setRemoveText(String removeSymbol) {
		this.removeSymbol = removeSymbol;
	}

	@Override
	public void startEdit() {
		super.startEdit();
		this.editHandler.apply(getTreeItem()).ifPresent(r -> {
			if (r) {
				commitEdit(getItem());
			}
		});
	}

}
