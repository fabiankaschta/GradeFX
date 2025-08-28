package kafx.view.dialog.userinput;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class UserInputColorPicker extends UserInputSingle<Color> {

	private final ColorPicker colorPicker;

	public UserInputColorPicker(ColorPicker colorPicker) {
		this(colorPicker, null, true);
	}

	public UserInputColorPicker(ColorPicker colorPicker, boolean allowEmpty) {
		this(colorPicker, null, allowEmpty);
	}

	public UserInputColorPicker(ColorPicker colorPicker, Color defaultValue) {
		this(colorPicker, defaultValue, true);
	}

	public UserInputColorPicker(ColorPicker colorPicker, Color defaultValue, boolean allowEmpty) {
		super(defaultValue, defaultValue, allowEmpty);
		this.colorPicker = colorPicker;
		this.colorPicker.setValue(defaultValue);
		this.valueProperty().bindBidirectional(this.colorPicker.valueProperty());
		this.getChildren().add(this.colorPicker);
	}

	@Override
	public void requestFocus() {
		this.colorPicker.requestFocus();
	}

}
