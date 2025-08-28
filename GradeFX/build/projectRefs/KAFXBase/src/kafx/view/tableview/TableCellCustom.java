package kafx.view.tableview;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class TableCellCustom<S, T> extends TableCell<S, T> {

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
		return _ -> new TableCellCustom<>(new DefaultStringConverter());
	}

	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter,
			final Pos alignment) {
		return _ -> new TableCellCustom<>(converter, alignment);
	}

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn(final Pos alignment) {
		return _ -> new TableCellCustom<>(new DefaultStringConverter(), alignment);
	}

	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final StringConverter<T> converter) {
		return _ -> new TableCellCustom<>(converter);
	}

	private final StringConverter<T> converter;

	public TableCellCustom() {
		this(null, Pos.CENTER_LEFT);
	}

	public TableCellCustom(Pos alignment) {
		this(null, alignment);
	}

	public TableCellCustom(StringConverter<T> converter) {
		this(converter, Pos.CENTER_LEFT);
	}

	public TableCellCustom(StringConverter<T> converter, Pos alignment) {
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
		} else {
			super.setText(this.converter == null ? item.toString() : converter.toString(item));
			super.setGraphic(null);
		}
	}

}
