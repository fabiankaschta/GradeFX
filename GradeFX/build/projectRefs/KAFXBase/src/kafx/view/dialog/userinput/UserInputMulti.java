package kafx.view.dialog.userinput;

import java.util.Arrays;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

abstract non-sealed class UserInputMulti<S> extends UserInput<S> {

	private final ObservableList<S> selectedValues;
	private final ObservableList<S> defaultValues;
	private final BooleanExpression isSelectedExpression;
	private final BooleanExpression isSelectedDefaultExpression;

	/**
	 * Creates an input pane with the given values as both the selected and the
	 * default values. {@link #allowEmptyProperty()} is set to {@code false}.
	 * 
	 * @param selectedValues the values to be selected and set as default
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	@SafeVarargs
	protected UserInputMulti(S... selectedValues) {
		this(selectedValues, false);
	}

	/**
	 * Creates an input pane with the given values as both the selected and the
	 * default values.
	 * 
	 * @param selectedValues the values to be selected and set as default
	 * @param allowEmpty     if an empty selection should be considered a valid
	 *                       selection
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	protected UserInputMulti(S[] selectedValues, boolean allowEmpty) {
		this(selectedValues, selectedValues, allowEmpty);
	}

	/**
	 * Creates an input pane with the given values as the selected and the default
	 * values respectively.
	 * 
	 * @param selectedValues the values to be selected
	 * @param defaultValues  the values to be set as default
	 * @param allowEmpty     if an empty selection should be considered a valid
	 *                       selection
	 * @see #valueProperty
	 * @see #defaultValueProperty
	 */
	protected UserInputMulti(S[] selectedValues, S[] defaultValues, boolean allowEmpty) {
		super(allowEmpty);

		this.selectedValues = FXCollections.observableArrayList(selectedValues);

		this.defaultValues = FXCollections.observableArrayList(defaultValues);

		this.isSelectedExpression = BooleanExpression.booleanExpression(Bindings.createBooleanBinding(() -> {
			if (isAllowEmpty()) {
				return true;
			} else {
				return this.selectedValues.isEmpty();
			}
		}, super.allowEmptyProperty(), this.selectedValues).asObject());

		this.isSelectedDefaultExpression = BooleanExpression.booleanExpression(Bindings.createBooleanBinding(() -> {
			if (this.selectedValues.size() != this.defaultValues.size()) {
				return false;
			} else {
				return this.selectedValues.containsAll(this.defaultValues);
			}
		}, this.defaultValues, this.selectedValues));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BooleanExpression isSelectedExpression() {
		return this.isSelectedExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BooleanExpression isSelectedDefaultExpression() {
		return this.isSelectedDefaultExpression;
	}

	/**
	 * The currently selected values of this input pane. Its content should be bound
	 * to list of selected values of the control this is containing.
	 * 
	 * @see Bindings#bindContent(ObservableList, ObservableList)
	 */
	public final ObservableList<S> selectedValues() {
		return this.selectedValues;
	}

	/**
	 * Adds the given values to the currently selected values.
	 * 
	 * @param values the additional values to select
	 */
	@SafeVarargs
	public final void selectValues(S... values) {
		if (values == null) {
			this.clearSelection();
		} else {
			for (S value : values) {
				if (!this.selectedValues.contains(value)) {
					this.selectedValues.add(value);
				}
			}
		}
	}

	/**
	 * Removes the given values from the currently selected values.
	 * 
	 * @param values the values to deselect
	 */
	@SafeVarargs
	public final void deselectValues(S... values) {
		if (values != null) {
			this.selectedValues.removeAll(Arrays.asList(values));
		}
	}

	/**
	 * Clears the current selection, so that no value is selected
	 */
	public final void clearSelection() {
		this.selectedValues.clear();
	}

	/**
	 * The default selected values of this input pane.
	 */
	public final ObservableList<S> defaultValues() {
		return this.defaultValues;
	}

	/**
	 * The default selected values of this input pane.
	 */
	@SafeVarargs
	public final void setDefaultValues(S... values) {
		this.defaultValues.clear();
		if (values != null) {
			for (S value : values) {
				if (!this.defaultValues.contains(value)) {
					this.defaultValues.add(value);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void selectDefault() {
		if (this.defaultValues.isEmpty()) {
			this.clearSelection();
		} else {
			this.selectedValues.removeIf(s -> !this.defaultValues.contains(s));
			this.selectedValues.addAll(defaultValues);
		}
	}

}
