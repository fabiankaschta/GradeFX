package kafx.view.dialog.userinput;

import javafx.scene.control.Spinner;

public class UserInputSpinner<S> extends UserInputSingle<S> {

	private final Spinner<S> spinner;

	public UserInputSpinner(Spinner<S> spinner, S defaultValue) {
		super(defaultValue, defaultValue, false);
		this.spinner = spinner;
		this.spinner.getValueFactory().setValue(defaultValue);
		this.spinner.valueProperty().subscribe(v -> this.valueProperty().set(v));
		this.valueProperty().subscribe(v -> this.spinner.getValueFactory().setValue(v));
		this.getChildren().add(this.spinner);
	}

	@Override
	public void requestFocus() {
		this.spinner.requestFocus();
	}

}
