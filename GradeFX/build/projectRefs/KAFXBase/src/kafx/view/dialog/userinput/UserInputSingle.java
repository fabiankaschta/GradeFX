package kafx.view.dialog.userinput;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

abstract non-sealed class UserInputSingle<S> extends UserInput<S> {

	private final ObjectProperty<S> valueProperty;
	private final ObjectProperty<S> defaultValueProperty;
	private final BooleanExpression isSelectedExpression;
	private final BooleanExpression isSelectedDefaultExpression;

	/**
	 * Creates an input pane with {@code null} as both the selected and the default
	 * value. {@link #allowEmptyProperty()} is set to {@code false}.
	 * 
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	protected UserInputSingle() {
		this(null, null, false);
	}

	/**
	 * Creates an input pane with the given value as both the selected and the
	 * default value. {@link #allowEmptyProperty()} is set to {@code false}.
	 * 
	 * @param value the value to be selected and set as default
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	protected UserInputSingle(S value) {
		this(value, value, false);
	}

	/**
	 * Creates an input pane with the given values as the selected and the default
	 * value respectively. {@link #allowEmptyProperty()} is set to {@code false}.
	 * 
	 * @param value        the value to be selected
	 * @param defaultValue the value to be set as default
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	protected UserInputSingle(S value, S defaultValue) {
		this(value, defaultValue, false);
	}

	/**
	 * Creates an input pane with the given value the selected value. Depending on
	 * the parameter {@code allowEmpty} the default value is either set to
	 * {@code null}, if allowed, or the same value as was selected.
	 * 
	 * @param value      the value to be selected and set as default probably
	 * @param allowEmpty if null should be considered a valid selection
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	protected UserInputSingle(S value, boolean allowEmpty) {
		this(value, allowEmpty ? null : value, allowEmpty);
	}

	/**
	 * Creates an input pane with the given values as the selected and the default
	 * value respectively.
	 * 
	 * @param value        the value to be selected
	 * @param defaultValue the value to be set as default
	 * @param allowEmpty   if null should be considered a valid selection
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	protected UserInputSingle(S value, S defaultValue, boolean allowEmpty) {
		super(allowEmpty);

		this.valueProperty = new SimpleObjectProperty<>(this, "value", value);

		this.defaultValueProperty = new SimpleObjectProperty<>(this, "defaultValue", defaultValue);

		this.isSelectedExpression = BooleanExpression.booleanExpression(Bindings.createBooleanBinding(() -> {
			if (isAllowEmpty()) {
				return true;
			} else {
				return getValue() != null;
			}
		}, super.allowEmptyProperty(), this.valueProperty).asObject());

		this.isSelectedDefaultExpression = BooleanExpression.booleanExpression(Bindings.createBooleanBinding(() -> {
			S currentValue = getValue();
			S currentDefaultValue = getDefaultValue();
			if (currentValue == null) {
				return currentDefaultValue == null;
			} else {
				return currentValue.equals(currentDefaultValue);
			}
		}, this.defaultValueProperty, this.valueProperty));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BooleanExpression isSelectedExpression() {
		return this.isSelectedExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BooleanExpression isSelectedDefaultExpression() {
		return this.isSelectedDefaultExpression;
	}

	/**
	 * The currently selected value of this input pane. Should be bound to the value
	 * property of the control this is containing.
	 */
	public final ObjectProperty<S> valueProperty() {
		return this.valueProperty;
	}

	public final S getValue() {
		return this.valueProperty.get();
	}

	public final void setValue(S value) {
		this.valueProperty.set(value);
	}

	/**
	 * The default value of this input pane.
	 * 
	 * @see #setDefaultValue(value)
	 */
	public final ObjectProperty<S> defaultValueProperty() {
		return this.defaultValueProperty;
	}

	public final S getDefaultValue() {
		return this.defaultValueProperty.get();
	}

	public final void setDefaultValue(S value) {
		this.defaultValueProperty.set(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void selectDefault() {
		this.setValue(this.getDefaultValue());
	}

}
