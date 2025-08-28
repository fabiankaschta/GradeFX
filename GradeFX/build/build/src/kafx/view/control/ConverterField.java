package kafx.view.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class ConverterField<T> extends TextField {

	private final ObjectProperty<T> value = new SimpleObjectProperty<>();
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

	public boolean allowNull() {
		return this.allowNull.getValue();
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull.setValue(allowNull);
	}

	public BooleanProperty allowNullProperty() {
		return this.allowNull;
	}

	public ConverterField(StringConverter<T> converter) {
		this(null, converter, true);
	}

	public ConverterField(StringConverter<T> converter, boolean allowNull) {
		this(null, converter, allowNull);
	}

	public ConverterField(T initialValue, StringConverter<T> converter) {
		this(initialValue, converter, false);
	}

	public ConverterField(T initialValue, StringConverter<T> converter, boolean allowNull) {
		this.value.set(initialValue);
		this.converter = converter;
		this.allowNull.set(allowNull);

		setText(initialValue == null ? "" : this.converter.toString(initialValue));

		// make sure the value property is clamped to the required range
		// and update the field's text to be in sync with the value.
		value.addListener((_, oldValue, newValue) -> {
			if (newValue == null) {
				setText("");
			} else if (oldValue != null && !newValue.equals(oldValue)) {
				try {
					if (!newValue.equals(this.converter.fromString(textProperty().get()))) {
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
				if (value.get() != null && newValueGrade.equals(value.get())) {
					return;
				}

				value.set(this.converter.fromString(textProperty().get()));
			} catch (Exception e) {
				setText(oldValue);
			}
		});
	}
}