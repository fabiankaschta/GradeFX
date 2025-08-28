package kafx.view.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class ComparableField<T extends Comparable<T>> extends TextField {

	private final ObjectProperty<T> value = new SimpleObjectProperty<>();
	private final ObjectProperty<T> minValue = new SimpleObjectProperty<>();
	private final ObjectProperty<T> maxValue = new SimpleObjectProperty<>();
	private final StringConverter<T> converter;
	private final BooleanProperty allowNull = new SimpleBooleanProperty();

	public T getValue() {
		return this.value.getValue();
	}

	public void setValue(T value) {
		this.value.setValue(value);
	}

	public ObjectProperty<T> valueProperty() {
		return this.value;
	}

	public T getMin() {
		return this.minValue.getValue();
	}

	public void setMin(T min) {
		this.minValue.setValue(min);
	}

	public ObjectProperty<T> minValueProperty() {
		return this.minValue;
	}

	public T getMax() {
		return this.maxValue.getValue();
	}

	public void setMax(T value) {
		this.maxValue.setValue(value);
	}

	public ObjectProperty<T> maxValueProperty() {
		return this.maxValue;
	}

	public boolean allowNull() {
		return this.allowNull.getValue();
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull.setValue(allowNull);
	}

	public BooleanProperty allowNullProperty() {
		return this.allowNull;
	}

	public ComparableField(ObjectProperty<T> minValue, ObjectProperty<T> maxValue, StringConverter<T> converter) {
		this(minValue, maxValue, converter, false);
	}

	public ComparableField(ObjectProperty<T> minValue, ObjectProperty<T> maxValue, StringConverter<T> converter,
			boolean allowNull) {
		this(minValue.get(), maxValue.get(), converter);
		this.minValue.bind(minValue);
		this.maxValue.bind(maxValue);
		this.allowNull.set(allowNull);
	}

	public ComparableField(ObjectProperty<T> minValue, ObjectProperty<T> maxValue, T initialValue,
			StringConverter<T> converter) {
		this(minValue, maxValue, initialValue, converter, false);
	}

	public ComparableField(ObjectProperty<T> minValue, ObjectProperty<T> maxValue, T initialValue,
			StringConverter<T> converter, boolean allowNull) {
		this(minValue.get(), maxValue.get(), initialValue, converter);
		this.minValue.bind(minValue);
		this.maxValue.bind(maxValue);
		this.allowNull.set(allowNull);
	}

	public ComparableField(T minValue, T maxValue, StringConverter<T> converter) {
		this(minValue, maxValue, converter, false);
	}

	public ComparableField(T minValue, T maxValue, StringConverter<T> converter, boolean allowNull) {
		this(minValue, maxValue, minValue, converter, allowNull);
	}

	public ComparableField(T minValue, T maxValue, T initialValue, StringConverter<T> converter) {
		this(minValue, maxValue, initialValue, converter, false);
	}

	public ComparableField(T minValue, T maxValue, T initialValue, StringConverter<T> converter, boolean allowNull) {
		if (minValue != null && maxValue != null && minValue.compareTo(maxValue) > 0) {
			throw new IllegalArgumentException("min value " + minValue + " greater than max value " + maxValue);
		}
		if (initialValue != null && (minValue != null && minValue.compareTo(initialValue) > 0
				|| maxValue != null && maxValue.compareTo(initialValue) < 0)) {
			throw new IllegalArgumentException(
					"initialValue " + initialValue + " not between " + minValue + " and " + maxValue);
		}

		// initialize the field values.
		this.minValue.set(minValue);
		this.maxValue.set(maxValue);
		this.value.set(initialValue);
		this.converter = converter;
		this.allowNull.set(allowNull);

		setText(initialValue == null ? "" : this.converter.toString(initialValue));

		// make sure the value property is clamped to the required range
		// and update the field's text to be in sync with the value.
		value.addListener((_, oldValue, newValue) -> {
			if (newValue == null) {
				setText("");
			} else if (oldValue != null && newValue.compareTo(oldValue) != 0) {
				if (getMin() != null && newValue.compareTo(getMin()) < 0) {
					value.setValue(getMin());
					return;
				}
				if (getMax() != null && newValue.compareTo(getMax()) > 0) {
					value.setValue(getMax());
					return;
				}
				try {
					if (newValue.compareTo(this.converter.fromString(textProperty().get())) != 0) {
						setText(this.converter.toString(newValue));
					}
				} catch (Exception e) {
					setText(this.converter.toString(newValue));
				}
			}
		});

		// if focus is lost, restore value in text
		this.focusedProperty().addListener((_, _, newValue) -> {
			if (!newValue) {
				String text = getText();
				if (allowNull() && (text == null || "".equals(text))) {
					value.set(null);
				} else if (text == null || "".equals(text)) {
					setText(value.get() == null ? "" : this.converter.toString(value.get()));
				} else {
					try {
						T newValueGrade = this.converter.fromString(text);
						if (getMin() != null && getMin().compareTo(newValueGrade) > 0
								|| getMax() != null && newValueGrade.compareTo(getMax()) > 0) {
							value.set(newValueGrade);
						}
					} catch (Exception e) {
						setText(value.get() == null ? "" : this.converter.toString(value.get()));
					}
				}
			}
		});

		// ensure any entered values lie inside the required range.
		this.textProperty().addListener((_, oldValue, newValue) -> {
			if (allowNull() && (newValue == null || "".equals(newValue))) {
				value.set(null);
				return;
			}
			if (newValue == null || newValue.equals(oldValue) || "".equals(newValue)) {
				return;
			}
			try {
				T newValueGrade = this.converter.fromString(newValue);
				if (value.get() != null && newValueGrade.compareTo(value.get()) == 0) {
					return;
				}

				if ((getMin() == null || getMin().compareTo(newValueGrade) <= 0)
						&& (getMax() == null || newValueGrade.compareTo(getMax()) <= 0)) {
					value.set(newValueGrade);
				}
			} catch (Exception e) {
				setText(oldValue);
			}
		});
	}
}