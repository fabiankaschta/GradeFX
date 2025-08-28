package kafx.view.control;

import javafx.scene.control.Spinner;

public class IntegerSpinner extends Spinner<Integer> {

	public IntegerSpinner(int minValue, int maxValue, int initialValue) {
		super(minValue, maxValue, initialValue);
		getEditor().textProperty().addListener((_, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				getEditor().setText(oldValue);
			}
		});
	}
}
