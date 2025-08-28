package kafx.view.dialog.userinput;

import javafx.scene.control.CheckBox;

public class UserInputCheckBox extends UserInputSingle<Boolean> {

	private final CheckBox checkBox;

	public UserInputCheckBox(CheckBox checkBox) {
		this(checkBox, false);
	}

	public UserInputCheckBox(CheckBox checkBox, boolean defaultValue) {
		super(defaultValue, defaultValue, false);
		this.checkBox = checkBox;
		this.checkBox.setSelected(defaultValue);
		this.valueProperty().bindBidirectional(this.checkBox.selectedProperty());
		this.getChildren().add(this.checkBox);
	}

	@Override
	public void requestFocus() {
		this.checkBox.requestFocus();
	}

}
