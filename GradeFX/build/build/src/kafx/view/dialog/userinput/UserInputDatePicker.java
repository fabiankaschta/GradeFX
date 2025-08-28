package kafx.view.dialog.userinput;

import java.time.LocalDate;

import javafx.scene.control.DatePicker;

public class UserInputDatePicker extends UserInputSingle<LocalDate> {

	private final DatePicker datePicker;

	public UserInputDatePicker(DatePicker datePicker) {
		this(datePicker, LocalDate.now(), true);
	}

	public UserInputDatePicker(DatePicker datePicker, boolean allowEmpty) {
		this(datePicker, LocalDate.now(), allowEmpty);
	}

	public UserInputDatePicker(DatePicker datePicker, LocalDate defaultValue) {
		this(datePicker, defaultValue, true);
	}

	public UserInputDatePicker(DatePicker datePicker, LocalDate defaultValue, boolean allowEmpty) {
		super(defaultValue, defaultValue, allowEmpty);
		this.datePicker = datePicker;
		this.datePicker.setValue(defaultValue);
		this.valueProperty().bindBidirectional(this.datePicker.valueProperty());
		this.getChildren().add(this.datePicker);
	}

	@Override
	public void requestFocus() {
		this.datePicker.requestFocus();
	}

}
