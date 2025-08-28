package kafx.view.tableview;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;
import kafx.view.control.ComparableField;

public class TableCellEditComparable<S, T extends Comparable<T>> extends TableCellEditControl<S, T> {

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final StringConverter<T> converter) {
		return _ -> new TableCellEditComparable<>(converter);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final ObjectProperty<T> minValue, final ObjectProperty<T> maxValue, final StringConverter<T> converter) {
		return _ -> new TableCellEditComparable<>(minValue, maxValue, converter);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final T minValue, final T maxValue, final StringConverter<T> converter) {
		return _ -> new TableCellEditComparable<>(minValue, maxValue, converter);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final StringConverter<T> converter, final Pos alignment) {
		return _ -> new TableCellEditComparable<>(converter, alignment);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final ObjectProperty<T> minValue, final ObjectProperty<T> maxValue, final StringConverter<T> converter,
			final Pos alignment) {
		return _ -> new TableCellEditComparable<>(minValue, maxValue, converter, alignment);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final T minValue, final T maxValue, final StringConverter<T> converter, final Pos alignment) {
		return _ -> new TableCellEditComparable<>(minValue, maxValue, converter, alignment);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final StringConverter<T> converter, final boolean allowNull) {
		return _ -> new TableCellEditComparable<>(converter, allowNull);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final ObjectProperty<T> minValue, final ObjectProperty<T> maxValue, final StringConverter<T> converter,
			final boolean allowNull) {
		return _ -> new TableCellEditComparable<>(minValue, maxValue, converter, allowNull);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final T minValue, final T maxValue, final StringConverter<T> converter, final boolean allowNull) {
		return _ -> new TableCellEditComparable<>(minValue, maxValue, converter, allowNull);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final StringConverter<T> converter, final Pos alignment, final boolean allowNull) {
		return _ -> new TableCellEditComparable<>(converter, alignment, allowNull);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final ObjectProperty<T> minValue, final ObjectProperty<T> maxValue, final StringConverter<T> converter,
			final Pos alignment, final boolean allowNull) {
		return _ -> new TableCellEditComparable<>(minValue, maxValue, converter, alignment, allowNull);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final T minValue, final T maxValue, final StringConverter<T> converter, final Pos alignment,
			final boolean allowNull) {
		return _ -> new TableCellEditComparable<>(minValue, maxValue, converter, alignment, allowNull);
	}

	private final StringConverter<T> converter;
	private ObjectProperty<T> minValue = new SimpleObjectProperty<>();
	private ObjectProperty<T> maxValue = new SimpleObjectProperty<>();
	private BooleanProperty allowNull = new SimpleBooleanProperty();

	public TableCellEditComparable(StringConverter<T> converter) {
		this(converter, Pos.CENTER_LEFT, false);
	}

	public TableCellEditComparable(StringConverter<T> converter, boolean allowNull) {
		this(converter, Pos.CENTER_LEFT, allowNull);
	}

	public TableCellEditComparable(ObjectProperty<T> minValue, ObjectProperty<T> maxValue,
			StringConverter<T> converter) {
		this(minValue, maxValue, converter, Pos.CENTER_LEFT, false);
	}

	public TableCellEditComparable(ObjectProperty<T> minValue, ObjectProperty<T> maxValue, StringConverter<T> converter,
			boolean allowNull) {
		this(minValue, maxValue, converter, Pos.CENTER_LEFT, allowNull);
	}

	public TableCellEditComparable(T minValue, T maxValue, StringConverter<T> converter) {
		this(minValue, maxValue, converter, Pos.CENTER_LEFT, false);
	}

	public TableCellEditComparable(T minValue, T maxValue, StringConverter<T> converter, boolean allowNull) {
		this(minValue, maxValue, converter, Pos.CENTER_LEFT, allowNull);
	}

	public TableCellEditComparable(StringConverter<T> converter, Pos alignment) {
		this(converter, alignment, false);
	}

	public TableCellEditComparable(StringConverter<T> converter, Pos alignment, boolean allowNull) {
		this.minValue.set(null);
		this.maxValue.set(null);
		this.converter = converter;
		this.allowNull.set(allowNull);
		this.setAlignment(alignment);
		this.getStyleClass().add("text-input-table-cell");
	}

	public TableCellEditComparable(ObjectProperty<T> minValue, ObjectProperty<T> maxValue, StringConverter<T> converter,
			Pos alignment) {
		this(minValue, maxValue, converter, alignment, false);
	}

	public TableCellEditComparable(ObjectProperty<T> minValue, ObjectProperty<T> maxValue, StringConverter<T> converter,
			Pos alignment, boolean allowNull) {
		this.minValue.bind(minValue);
		this.maxValue.bind(maxValue);
		this.converter = converter;
		this.allowNull.set(allowNull);
		this.setAlignment(alignment);
		this.getStyleClass().add("text-input-table-cell");
	}

	public TableCellEditComparable(T minValue, T maxValue, StringConverter<T> converter, Pos alignment) {
		this(minValue, maxValue, converter, alignment, false);
	}

	public TableCellEditComparable(T minValue, T maxValue, StringConverter<T> converter, Pos alignment,
			boolean allowNull) {
		this.minValue.set(minValue);
		this.maxValue.set(maxValue);
		this.converter = converter;
		this.allowNull.set(allowNull);
		this.setAlignment(alignment);
		this.getStyleClass().add("text-input-table-cell");
	}

	public T getMinValue() {
		return minValue.get();
	}

	public void setMinValue(T minValue) {
		this.minValue.set(minValue);
	}

	public ObjectProperty<T> minValueProperty() {
		return this.minValue;
	}

	public T getMaxValue() {
		return maxValue.get();
	}

	public void setMaxValue(T maxValue) {
		this.maxValue.set(maxValue);
	}

	public ObjectProperty<T> maxValueProperty() {
		return this.maxValue;
	}

	public boolean isAllowNull() {
		return this.allowNull.get();
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull.set(allowNull);
	}

	public BooleanProperty allowNullProperty() {
		return this.allowNull;
	}

	@Override
	protected void setCellText() {
		this.setText(this.getItem() == null ? ""
				: (this.converter == null ? this.getItem().toString() : this.converter.toString(this.getItem())));
	}

	@Override
	protected void setControlValue() {
		@SuppressWarnings("unchecked")
		ComparableField<T> field = (ComparableField<T>) getControl();
		field.setValue(this.getItem());
	}

	@Override
	protected T getFromControl() {
		@SuppressWarnings("unchecked")
		ComparableField<T> field = (ComparableField<T>) getControl();
		return field.getValue();
	}

	@Override
	protected void startEditControl() {
		@SuppressWarnings("unchecked")
		ComparableField<T> field = (ComparableField<T>) getControl();
		field.selectAll();
		field.requestFocus();
	}

	@Override
	protected ComparableField<T> createControl() {
		ComparableField<T> field = new ComparableField<>(this.minValue, this.maxValue, this.getItem(), this.converter);
		field.alignmentProperty().bind(this.alignmentProperty());
		field.getStyleClass().add("text-input-table-cell");
		field.allowNullProperty().bind(this.allowNull);
		return field;
	}
}
