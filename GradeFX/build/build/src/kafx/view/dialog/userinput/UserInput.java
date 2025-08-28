package kafx.view.dialog.userinput;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public abstract sealed class UserInput<S> extends VBox
		permits UserInputTextInput, UserInputSingle, UserInputMulti {

	private final BooleanProperty allowEmptyProperty;

	protected UserInput(boolean allowEmpty) {
		this.allowEmptyProperty = new SimpleBooleanProperty(this, "allowEmpty", allowEmpty);
		this.setAlignment(Pos.CENTER_LEFT);
	}

	/**
	 * Indicates if the method {@link #isSelectedExpression()} should view an empty
	 * selection (i.e. {@code null} or an empty {@codeString}) as a valid selection
	 * and therefore always be true.
	 */
	public final BooleanProperty allowEmptyProperty() {
		return this.allowEmptyProperty;
	}

	public final boolean isAllowEmpty() {
		return this.allowEmptyProperty.get();
	}

	public final void setAllowEmpty(boolean allowEmpty) {
		this.allowEmptyProperty.set(allowEmpty);
	}

	/**
	 * Indicates if any (valid) value is selected.
	 * 
	 * @see #allowEmptyProperty
	 */
	public abstract BooleanExpression isSelectedExpression();

	/**
	 * Indicates if the default value is selected.
	 * 
	 * @see #defaultValueProperty
	 */
	public abstract BooleanExpression isSelectedDefaultExpression();

	/**
	 * Selects the default for this input pane.
	 * 
	 * @see #defaultValueProperty()
	 */
	public abstract void selectDefault();

	/**
	 * Requests focus for this InputPane. Should be delegated to the control it is
	 * containing.
	 */
	@Override
	public abstract void requestFocus();

}
