package kafx.view.dialog.userinput;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TextInputControl;

public non-sealed class UserInputTextInput extends UserInput<String> {

	private final TextInputControl textInput;

	private final StringProperty defaultValueProperty;
	private final BooleanExpression isSelectedExpression;
	private final BooleanExpression isSelectedDefaultExpression;

	/**
	 * Creates an text input pane with an empty {@code String} as both the selected
	 * and the default value. {@link #allowEmptyProperty()} is set to {@code false}.
	 * 
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	public UserInputTextInput(TextInputControl textInput) {
		this(textInput, "", "", false);
	}

	/**
	 * Creates an text input pane with an empty {@code String} as both the selected
	 * and the default value.
	 * 
	 * @param allowEmpty if an empty String should be considered a valid selection
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	public UserInputTextInput(TextInputControl textInput, boolean allowEmpty) {
		this(textInput, "", "", allowEmpty);
	}

	/**
	 * Creates an input pane with the given value as both the selected and the
	 * default value. {@link #allowEmptyProperty()} is set to {@code false}.
	 * 
	 * @param value the value to be selected and set as default
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	public UserInputTextInput(TextInputControl textInput, String value) {
		this(textInput, value == null ? "" : value, value == null ? "" : value, false);
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
	public UserInputTextInput(TextInputControl textInput, String value, String defaultValue) {
		this(textInput, value == null ? "" : value, defaultValue == null ? "" : defaultValue, false);
	}

	/**
	 * Creates an input pane with the given value the selected value. Depending on
	 * the parameter {@code allowEmpty} the default value is either set to an empty
	 * {@code String}, if allowed, or the same value as was selected.
	 * 
	 * @param value      the value to be selected and set as default probably
	 * @param allowEmpty if an empty String should be considered a valid selection
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	public UserInputTextInput(TextInputControl textInput, String value, boolean allowEmpty) {
		this(textInput, value == null ? "" : value, allowEmpty ? "" : (value == null ? "" : value), allowEmpty);
	}

	/**
	 * Creates an input pane with the given values as the selected and the default
	 * value respectively.
	 * 
	 * @param value        the value to be selected
	 * @param defaultValue the value to be set as default
	 * @param allowEmpty   if an empty String should be considered a valid selection
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	public UserInputTextInput(TextInputControl textInput, String value, String defaultValue, boolean allowEmpty) {
		super(allowEmpty);
		this.textInput = textInput;
		this.textInput.setText(value);

		this.defaultValueProperty = new SimpleStringProperty(this, "defaultValue",
				defaultValue == null ? "" : defaultValue);

		this.isSelectedExpression = BooleanExpression.booleanExpression(Bindings.createBooleanBinding(() -> {
			if (isAllowEmpty()) {
				return true;
			} else {
				return getValue().length() > 0;
			}
		}, super.allowEmptyProperty(), this.textInput.textProperty()).asObject());

		this.isSelectedDefaultExpression = BooleanExpression.booleanExpression(Bindings.createBooleanBinding(() -> {
			return getValue().equals(getDefaultValue());
		}, this.defaultValueProperty, this.textInput.textProperty()));

		this.getChildren().add(this.textInput);
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
	public final StringProperty valueProperty() {
		return this.textInput.textProperty();
	}

	public final String getValue() {
		return this.textInput.textProperty().get();
	}

	public final void setValue(String value) {
		this.textInput.textProperty().set(value == null ? "" : value);
	}

	/**
	 * The default value of this input pane.
	 * 
	 * @see #setDefaultValue(value)
	 */
	public final StringProperty defaultValueProperty() {
		return this.defaultValueProperty;
	}

	public final String getDefaultValue() {
		return this.defaultValueProperty.get();
	}

	public final void setDefaultValue(String value) {
		this.defaultValueProperty.set(value == null ? "" : value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void selectDefault() {
		this.setValue(this.getDefaultValue());
	}

	@Override
	public void requestFocus() {
		this.textInput.requestFocus();
	}

}
