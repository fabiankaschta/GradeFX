package kafx.view.dialog.userinput;

import javafx.scene.control.Slider;

public class UserInputSlider extends UserInputSingle<Double> {

	private final Slider slider;

	public UserInputSlider(Slider slider) {
		super(slider.getValue(), slider.getValue(), false);
		this.slider = slider;
		this.valueProperty().bindBidirectional(slider.valueProperty().asObject());
		this.getChildren().add(this.slider);
	}

	@Override
	public void requestFocus() {
		this.slider.requestFocus();
	}

}
