package kafx.view.dialog;

import java.util.List;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import kafx.view.dialog.userinput.UserInput;

public abstract class DialogUserInputButtonBinding<T> extends DialogUserInput<T> {

	private final ObservableList<BooleanExpression> buttonDisableExpressions = FXCollections.observableArrayList();

	protected DialogUserInputButtonBinding(String title) {
		super(title);
	}

	public void addInput(int index, UserInput<?> userInput, Node label) {
		super.addInput(index, userInput, label);
		this.buttonDisableExpressions.add(index, getDefaultButtonDisableExpression(userInput));
	}

	/**
	 * Defines the default {@link BooleanExpression} that enables or disables any
	 * bound {@link Button}.
	 * 
	 * @param userInput any user Input already added to this dialog
	 * @see #bindButtonDisable(Button)
	 * @see #setCustomButtonDisableExpression(UserInput, BooleanExpression)
	 */
	protected abstract BooleanExpression getDefaultButtonDisableExpression(UserInput<?> userInput);

	/**
	 * Sets a custom {@link BooleanExpression} that enables or disables any bound
	 * {@link Button}. <br>
	 * The default expression for any input is created by calling
	 * {@link #getDefaultButtonDisableExpression(UserInput)}.
	 * 
	 * @param userInput    any user Input already added to this dialog
	 * @param oberservable if false, will disable bound buttons
	 * @see #bindButtonDisable(Button)
	 * @see #getDefaultButtonDisableExpression(UserInput)
	 */
	public final void setCustomButtonDisableExpression(UserInput<?> userInput, BooleanExpression oberservable) {
		this.buttonDisableExpressions.set(super.userInputsUnmodifiable().indexOf(userInput), oberservable);
	}

	/**
	 * Decides, depending on the {@link BooleanExpression}s that were provided for
	 * the input panes, whether the button should become disabled.
	 * 
	 * @param buttonDisableObservables
	 * @return whether the button should become disabled
	 */
	protected abstract boolean isButtonDisabled(List<BooleanExpression> buttonDisableObservables);

	private BooleanBinding createButtonDisableBinding() {
		return Bindings.createBooleanBinding(() -> isButtonDisabled(this.buttonDisableExpressions),
				this.buttonDisableExpressions.toArray(n -> new Observable[n]));
	}

	/**
	 * Binds a buttons disable property (i.e. for a confirmation button) to the
	 * {@link BooleanExpression}s that were provided for the input panes. <br>
	 * The binding is also updated, should more inputs be added or some removed.
	 * 
	 * @param button any button to bind its {@link Node#disableProperty disable
	 *               property} to
	 * @see #getDefaultButtonDisableExpression(UserInput)
	 * @see #setCustomButtonDisableExpression(UserInput, BooleanExpression)
	 */
	protected final void bindButtonDisable(Button button) {
		button.disableProperty().bind(createButtonDisableBinding());
		this.buttonDisableExpressions.addListener((ListChangeListener<BooleanExpression>) _ -> {
			button.disableProperty().unbind();
			button.disableProperty().bind(createButtonDisableBinding());
		});
	}

}
