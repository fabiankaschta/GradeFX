package kafx.view.dialog.userinput;

import kafx.view.control.ComparableField;

public class UserInputComparableInput<T extends Comparable<T>> extends UserInputSingle<T> {

	private final ComparableField<T> input;

	public UserInputComparableInput(ComparableField<T> input) {
		this(input, null, true);
	}

	public UserInputComparableInput(ComparableField<T> input, T defaultValue) {
		this(input, defaultValue, true);
	}

	public UserInputComparableInput(ComparableField<T> input, boolean allowEmpty) {
		this(input, null, allowEmpty);
	}

	public UserInputComparableInput(ComparableField<T> input, T defaultValue, boolean allowEmpty) {
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
