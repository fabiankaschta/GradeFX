package kafx.view.tableview;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

abstract class TableCellEditControl<S, T> extends TableCell<S, T> {

	private Control control;
	private boolean canceled;

	protected TableCellEditControl() {
		this.setAlignment(Pos.CENTER_LEFT);
	}

	protected final Control getControl() {
		return control;
	}

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		updateItem(null, null, control);
	}

	@Override
	public final void startEdit() {
		super.startEdit();
		if (!isEditing()) {
			return;
		}

		if (control == null) {
			control = createControlHelper();
		}

		startEdit(null, null, control);
	}

	@Override
	public final void cancelEdit() {
		if (canceled) {
			super.cancelEdit();
			cancelEdit(null);
		} else {
			commitEdit(getFromControl());
		}
	}

	private void updateItem(final HBox hbox, final Node graphic, final Control control) {
		if (this.isEmpty()) {
			this.setText(null);
			this.setGraphic(null);
		} else {
			if (this.isEditing()) {
				if (control != null) {
					this.setControlValue();
				}
				this.setText(null);

				if (graphic != null) {
					hbox.getChildren().setAll(graphic, control);
					this.setGraphic(hbox);
				} else {
					this.setGraphic(control);
				}
			} else {
				this.setCellText();
				this.setGraphic(graphic);
			}
		}
		canceled = false;
	}

	protected abstract void setCellText();

	protected abstract void setControlValue();

	protected abstract T getFromControl();

	private void startEdit(final HBox hbox, final Node graphic, final Control control) {
		if (control != null) {
			setControlValue();
		}
		this.setText(null);

		if (graphic != null) {
			hbox.getChildren().setAll(graphic, control);
			this.setGraphic(hbox);
		} else {
			this.setGraphic(control);
		}
		startEditControl();
	}

	protected void startEditControl() {
	}

	private void cancelEdit(Node graphic) {
		this.setCellText();
		this.setGraphic(graphic);
	}

	private Control createControlHelper() {
		Control control = createControl();
		control.focusedProperty().addListener((_, wasFocused, _) -> {
			// if user clicks outside of table
			if (wasFocused && isEditing()) {
				commitEdit(getFromControl());
			}
		});
		control.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			TablePosition<S, ?> current = getTableView().getEditingCell();
			int row = current.getRow();
			int col = getTableView().getVisibleLeafIndex(current.getTableColumn());
			// enter => confirm edit
			if (event.getCode() == KeyCode.ENTER) {
				commitEdit(getFromControl());
				event.consume();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				canceled = true;
				cancelEdit();
				event.consume();
			} else if (event.getCode() == KeyCode.RIGHT || (!event.isShiftDown() && event.getCode() == KeyCode.TAB)) {
				commitEdit(getFromControl());
				getTableView().fireEvent(event); // select, scroll
				getTableView().edit(row, getTableView().getVisibleLeafColumn(col + 1));
				event.consume();
			} else if (event.getCode() == KeyCode.LEFT || (event.isShiftDown() && event.getCode() == KeyCode.TAB)) {
				commitEdit(getFromControl());
				getTableView().fireEvent(event);
				getTableView().edit(row, getTableView().getVisibleLeafColumn(col - 1));
				event.consume();
			} else if (event.getCode() == KeyCode.UP) {
				commitEdit(getFromControl());
				getTableView().fireEvent(event);
				getTableView().edit(row - 1, getTableView().getVisibleLeafColumn(col));
				event.consume();
			} else if (event.getCode() == KeyCode.DOWN) {
				commitEdit(getFromControl());
				getTableView().fireEvent(event);
				getTableView().edit(row + 1, getTableView().getVisibleLeafColumn(col));
				event.consume();
			}
		});
		return control;
	}

	protected abstract Control createControl();
}
