package kafx.view.tableview;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import kafx.view.control.ConverterField;

public class TableCellEditConverter<S, T> extends TableCellEditControl<S, T> {

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
		return _ -> new TableCellEditConverter<>(new DefaultStringConverter());
	}

	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final StringConverter<T> converter) {
		return _ -> new TableCellEditConverter<>(converter);
	}

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn(final Pos alignment) {
		return _ -> new TableCellEditConverter<>(new DefaultStringConverter(), alignment);
	}

	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter,
			final Pos alignment) {
		return _ -> new TableCellEditConverter<>(converter, alignment);
	}

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn(final boolean allowNull) {
		return _ -> new TableCellEditConverter<>(new DefaultStringConverter(), allowNull);
	}

	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter,
			final boolean allowNull) {
		return _ -> new TableCellEditConverter<>(converter, allowNull);
	}

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn(final Pos alignment,
			final boolean allowNull) {
		return _ -> new TableCellEditConverter<>(new DefaultStringConverter(), alignment, allowNull);
	}

	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter,
			final Pos alignment, final boolean allowNull) {
		return _ -> new TableCellEditConverter<>(converter, alignment, allowNull);
	}

	private final StringConverter<T> converter;
	private final BooleanProperty allowNull = new SimpleBooleanProperty();

	public TableCellEditConverter(StringConverter<T> converter) {
		this(converter, Pos.CENTER_LEFT, false);
	}

	public TableCellEditConverter(StringConverter<T> converter, boolean allowNull) {
		this(converter, Pos.CENTER_LEFT, allowNull);
	}

	public TableCellEditConverter(StringConverter<T> converter, Pos alignment) {
		this(converter, alignment, false);
	}

	public TableCellEditConverter(StringConverter<T> converter, Pos alignment, boolean allowNull) {
		this.converter = converter;
		this.allowNull.set(allowNull);
		this.setAlignment(alignment);
		this.getStyleClass().add("text-input-table-cell");
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull.set(allowNull);
	}

	public boolean allowNull() {
		return this.allowNull.get();
	}

	@Override
	protected void setCellText() {
		this.setText(this.getItem() == null ? ""
				: (this.converter == null ? this.getItem().toString() : this.converter.toString(this.getItem())));
	}

	@Override
	protected void setControlValue() {
		@SuppressWarnings("unchecked")
		ConverterField<T> field = (ConverterField<T>) getControl();
		field.setValue(this.getItem());
	}

	@Override
	protected T getFromControl() {
		@SuppressWarnings("unchecked")
		ConverterField<T> field = (ConverterField<T>) getControl();
		return field.getValue();
	}

	@Override
	protected void startEditControl() {
		@SuppressWarnings("unchecked")
		ConverterField<T> field = (ConverterField<T>) getControl();
		field.selectAll();
		field.requestFocus();
	}

	@Override
	protected ConverterField<T> createControl() {
		ConverterField<T> field = new ConverterField<>(this.getItem(), this.converter);
		field.alignmentProperty().bind(this.alignmentProperty());
		field.getStyleClass().add("text-input-table-cell");
		field.allowNullProperty().bind(this.allowNull);
		return field;
	}
}
