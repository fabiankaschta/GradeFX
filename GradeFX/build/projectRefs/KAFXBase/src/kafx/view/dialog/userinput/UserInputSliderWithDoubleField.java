package kafx.view.dialog.userinput;

import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.converter.DoubleStringConverter;
import kafx.view.control.ComparableField;

public class UserInputSliderWithDoubleField extends UserInputSlider {

	private final ComparableField<Double> field;

	public UserInputSliderWithDoubleField(Slider slider) {
		super(slider);
		this.field = new ComparableField<Double>(slider.getMin(), slider.getMax(), slider.getValue(),
				new DoubleStringConverter());
		this.field.setPrefColumnCount(4);
		slider.valueProperty().asObject().bindBidirectional(field.valueProperty());
		GridPane pane = new GridPane();
		pane.setHgap(10);
		pane.add(slider, 0, 0);
		pane.add(field, 1, 0);
		pane.getColumnConstraints().add(new ColumnConstraints() {
			{
				this.setPercentWidth(80);
				this.setFillWidth(true);
			}
		});
		this.getChildren().clear();
		this.getChildren().add(pane);
	}

}
