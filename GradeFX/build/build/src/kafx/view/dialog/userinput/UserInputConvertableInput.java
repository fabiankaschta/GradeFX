package kafx.view.dialog.userinput;

import kafx.view.control.ConverterField;

public class UserInputConvertableInput<T> extends UserInputSingle<T> {

	private final ConverterField<T> input;

	public UserInputConvertableInput(ConverterField<T> input) {
		this(input, null, true);
	}

	public UserInputConvertableInput(ConverterField<T> input, T defaultValue) {
		this(input, defaultValue, true);
	}

	public UserInputConvertableInput(ConverterField<T> input, boolean allowEmpty) {
		this(input, null, allowEmpty);
	}

	public UserInputConvertableInput(ConverterField<T> input, T defaultValue, boolean allowEmpty) {
		super(defaultValue, defaultValue, allowEmpty);
		this.input = input;
		this.input.setValue(defaultValue);
		this.valueProperty().bindBidirectional(this.input.valueProperty());
		this.getChildren().add(this.input);
	}

	@Override
	public void requestFocus() {
		this.input.requestFocus();
	}

}
