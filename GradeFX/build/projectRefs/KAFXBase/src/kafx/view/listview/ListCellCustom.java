package kafx.view.listview;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class ListCellCustom<T> extends ListCell<T> {

	public static Callback<ListView<String>, ListCell<String>> forList() {
		return _ -> new ListCellCustom<>(new DefaultStringConverter());
	}

	public static <T> Callback<ListView<T>, ListCell<T>> forList(final StringConverter<T> converter,
			final Pos alignment) {
		return _ -> new ListCellCustom<>(converter, alignment);
	}

	public static Callback<ListView<String>, ListCell<String>> forList(final Pos alignment) {
		return _ -> new ListCellCustom<>(new DefaultStringConverter(), alignment);
	}

	public static <T> Callback<ListView<T>, ListCell<T>> forList(final StringConverter<T> converter) {
		return _ -> new ListCellCustom<>(converter);
	}

	private final StringConverter<T> converter;

	public ListCellCustom() {
		this(null, Pos.CENTER_LEFT);
	}

	public ListCellCustom(Pos alignment) {
		this(null, alignment);
	}

	public ListCellCustom(StringConverter<T> converter) {
		this(converter, Pos.CENTER_LEFT);
	}

	public ListCellCustom(StringConverter<T> converter, Pos alignment) {
		this.converter = converter;
		this.setAlignment(alignment);
	}

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			super.setText(null);
			super.setGraphic(null);
		} else if (item == null) {
			super.setText("");
			super.setGraphic(null);
		} else if (item instanceof Node) {
			super.setText(null);
			super.setGraphic((Node) item);
			super.layout();
		} else {
			super.setText(this.converter == null ? item.toString() : converter.toString(item));
			super.setGraphic(null);
		}
	}

}
