package kafx.view.dialog.userinput;

import javafx.scene.control.ComboBox;
import javafx.util.Subscription;

public class UserInputComboBox<S> extends UserInputSingle<S> {

	private final ComboBox<S> comboBox;
	private Subscription valueSubscription;

	public UserInputComboBox(ComboBox<S> comboBox) {
		this(comboBox, null, true);
	}

	public UserInputComboBox(ComboBox<S> comboBox, boolean allowEmpty) {
		this(comboBox, null, allowEmpty);
	}

	public UserInputComboBox(ComboBox<S> comboBox, S defaultValue) {
		this(comboBox, defaultValue, true);
	}

	public UserInputComboBox(ComboBox<S> comboBox, S defaultValue, boolean allowEmpty) {
		super(defaultValue, defaultValue, allowEmpty);
		this.comboBox = comboBox;
		this.comboBox.getSelectionModel().select(defaultValue);
		this.comboBox.selectionModelProperty().subscribe(m -> {
			if (this.valueSubscription != null) {
				this.valueSubscription.unsubscribe();
			}
			this.valueSubscription = m.selectedItemProperty().subscribe(v -> this.valueProperty().set(v));
		});
		this.valueProperty()
				.subscribe(v -> this.comboBox.getSelectionModel().clearAndSelect(this.comboBox.getItems().indexOf(v)));
		this.getChildren().add(this.comboBox);
	}

	@Override
	public void requestFocus() {
		this.comboBox.requestFocus();
	}

}
