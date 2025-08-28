package kafx.view.tableview;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableHeaderRow;
import kafx.view.dialog.userinput.UserInput;

public class LabeledUserInputTableView extends TableView<UserInput<?>> {

	private final static double minHeight = 40;

	private static class TableCellNode extends TableCell<UserInput<?>, Node> {

		private TableCellNode() {
			this.setMinHeight(minHeight);
			this.setAlignment(Pos.CENTER_LEFT);
			this.setStyle(
					"-fx-background: derive(-fx-base,26.4%);  -fx-control-inner-background: white; -fx-control-inner-background-alt: derive(-fx-control-inner-background,-2%);");
		}

		@Override
		protected void updateItem(Node item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				super.setText(null);
				super.setGraphic(null);
			} else if (item == null) {
				super.setText("");
				super.setGraphic(null);
			} else if (item instanceof Node) {
				super.setText(null);
				super.setGraphic((Node) item);
			}
		}
	}

	private final ObservableList<Node> labels = FXCollections.observableArrayList();
	private final ObservableList<UserInput<?>> userInputs = FXCollections.observableArrayList();

	public LabeledUserInputTableView() {
		TableColumn<UserInput<?>, Node> labelColumn = new TableColumn<>();
		labelColumn.setCellFactory(_ -> new TableCellNode());
		labelColumn.setCellValueFactory(
				data -> new SimpleObjectProperty<>(labels.get(userInputs.indexOf(data.getValue()))));
		TableColumn<UserInput<?>, Node> inputPaneColumn = new TableColumn<>();
		inputPaneColumn.setCellFactory(_ -> new TableCellNode());
		inputPaneColumn.setCellValueFactory(data -> {
			return new SimpleObjectProperty<>(data.getValue());
		});
		this.getColumns().add(labelColumn);
		this.getColumns().add(inputPaneColumn);
		this.setColumnResizePolicy(_ -> true);
		this.setEditable(false);
		this.setSelectionModel(null);
		this.setFocusModel(null);
		this.setStyle(
				"-fx-background-color: -fx-background; -fx-control-inner-background: derive(-fx-base,26.4%); -fx-control-inner-background-alt: -fx-control-inner-background; -fx-table-cell-border-color: transparent;");
		this.getItems().addListener((ListChangeListener<UserInput<?>>) _ -> this.autosize());
		Platform.runLater(() -> {
			TableHeaderRow header = (TableHeaderRow) this.queryAccessibleAttribute(AccessibleAttribute.HEADER);
			header.setPrefHeight(0);
			header.setVisible(false);
			ScrollBar scrollBarVertical = (ScrollBar) this
					.queryAccessibleAttribute(AccessibleAttribute.VERTICAL_SCROLLBAR);
			scrollBarVertical.setPrefSize(0, 0);
			scrollBarVertical.setVisible(false);
			ScrollBar scrollBarHorizontal = (ScrollBar) this
					.queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
			scrollBarHorizontal.setPrefSize(0, 0);
			scrollBarHorizontal.setVisible(false);
		});
	}

	@Override
	protected double computePrefWidth(double height) {
		double width = 0;
		for (TableColumn<UserInput<?>, ?> column : super.getColumns()) {
			width += column.getWidth();
		}
		return width + this.snappedLeftInset() + this.snappedRightInset();
	}

	@Override
	protected double computePrefHeight(double width) {
		double height = 0;
		for (UserInput<?> userInput : super.getItems()) {
			if (userInput.isVisible()) {
				height += Math.max(userInput.prefHeight(-1), minHeight);
			}
		}
		return height + this.snappedTopInset() + this.snappedBottomInset() + 10; // combobox resizes a little too high
	}

	public void addInput(int index, UserInput<?> userInput, Node label) {
		this.labels.add(index, label);
		this.userInputs.add(index, userInput);
		if (userInput.isVisible()) {
			this.getItems().add(getVisibleIndexOf(userInput), userInput);
		}
		userInput.visibleProperty().addListener((_, oldValue, newValue) -> {
			if (!oldValue && newValue) {
				this.getItems().add(getVisibleIndexOf(userInput), userInput);
			} else if (oldValue && !newValue) {
				this.getItems().remove(userInput);
			}
		});
	}

	private int getVisibleIndexOf(UserInput<?> userInput) {
		int index = 0;
		for (int i = 0; i < this.userInputs.indexOf(userInput); i++) {
			if (this.userInputs.get(i).isVisible()) {
				index++;
			}
		}
		return index;
	}

}
