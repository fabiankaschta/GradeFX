package kafx.view.dialog;

import java.util.List;

import javafx.beans.binding.BooleanExpression;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import kafx.lang.Translator;
import kafx.view.dialog.userinput.UserInput;

public abstract class DialogAdd<T> extends DialogUserInputButtonBinding<T> {

	private final Button addButton;
	private final Button addMoreButton;

	public DialogAdd(String title) {
		this(title, true);
	}

	public DialogAdd(String title, boolean addMoreButton) {
		super(title);

		ButtonType cancelButtonType = ButtonType.CANCEL;
		ButtonType addButtonType = new ButtonType(Translator.get("dialog_add_button_add"), ButtonData.OK_DONE);
		ButtonType addMoreButtonType = ButtonType.NEXT;
		this.getDialogPane().getButtonTypes().addAll(addMoreButtonType, addButtonType, cancelButtonType);

		this.addButton = (Button) this.getDialogPane().lookupButton(addButtonType);

		this.addMoreButton = (Button) this.getDialogPane().lookupButton(addMoreButtonType);
		this.addMoreButton.addEventFilter(ActionEvent.ACTION, e -> {
			create();
			userInputsUnmodifiable().get(0).requestFocus();
			resetInputsToDefault();
			e.consume();
		});
		this.addMoreButton.setVisible(addMoreButton);

		this.bindButtonDisable(this.addButton);
		this.bindButtonDisable(this.addMoreButton);

		this.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				return create();
			} else {
				return null;
			}
		});
	}

	public void showAddMoreButton(boolean show) {
		this.addMoreButton.setVisible(show);
	}

	/**
	 * {@inheritDoc} <br>
	 * This method returns just {@code
	 * userInput.visibleProperty().and(userInput.isSelectedExpression().not()) }, so
	 * that the value is if the userInput is visible and no value is selected in it.
	 * 
	 * @param userInput any user Input already added to this dialog
	 * @see #bindButtonDisable(Button)
	 * @see #setCustomButtonDisableExpression(UserInput, BooleanExpression)
	 */
	protected BooleanExpression getDefaultButtonDisableExpression(UserInput<?> userInput) {
		return userInput.visibleProperty().and(userInput.isSelectedExpression().not());
	}

	/**
	 * {@inheritDoc} <br>
	 * This defaults to if any of the expressions is {@code true}.
	 * 
	 * @param buttonDisableExpressions
	 * @return whether the button should become disabled
	 */
	protected boolean isButtonDisabled(List<BooleanExpression> buttonDisableExpressions) {
		for (BooleanExpression observable : buttonDisableExpressions) {
			if (observable.get()) {
				return true;
			}
		}
		return false;
	}

	public abstract T create();
}
