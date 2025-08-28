package kafx.view.dialog.userinput;

import javafx.scene.control.ChoiceBox;
import javafx.util.Subscription;

public class UserInputChoiceBox<S> extends UserInputSingle<S> {

	private final ChoiceBox<S> choiceBox;
	private Subscription valueSubscription;

	public UserInputChoiceBox(ChoiceBox<S> choiceBox) {
		this(choiceBox, choiceBox.getItems().size() == 0 ? null : choiceBox.getItems().get(0));
	}

	public UserInputChoiceBox(ChoiceBox<S> choiceBox, S defaultValue) {
		super(defaultValue, defaultValue, false);
		this.choiceBox = choiceBox;
		this.choiceBox.getSelectionModel().select(defaultValue);
		this.choiceBox.selectionModelProperty().subscribe(m -> {
			if (this.valueSubscription != null) {
				this.valueSubscription.unsubscribe();
			}
			this.valueSubscription = m.selectedItemProperty().subscribe(v -> this.valueProperty().set(v));
		});
		this.valueProperty().subscribe(
				v -> this.choiceBox.getSelectionModel().clearAndSelect(this.choiceBox.getItems().indexOf(v)));
		this.getChildren().add(this.choiceBox);
	}

	@Override
	public void requestFocus() {
		this.choiceBox.requestFocus();
	}

}
