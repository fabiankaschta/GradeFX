package kafx.view.dialog.userinput;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class UserInputChoiceBoxButtons<S> extends UserInputChoiceBox<S> {

	private final HBox pane;

	public UserInputChoiceBoxButtons(ChoiceBox<S> choiceBox) {
		this(choiceBox, choiceBox.getItems().size() == 0 ? null : choiceBox.getItems().get(0));
	}

	public UserInputChoiceBoxButtons(ChoiceBox<S> choiceBox, S defaultValue) {
		super(choiceBox, defaultValue);
		this.getChildren().clear();
		this.pane = new HBox(5, choiceBox);
		HBox.setHgrow(choiceBox, Priority.ALWAYS);
		this.getChildren().add(this.pane);
	}

	public void addButton(Button button) {
		this.pane.getChildren().add(button);
	}

}
