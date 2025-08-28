package kafx.view.tableview;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public abstract class TableViewAddRemove<T> extends TableView<T> {

	private String add = "+";
	private String remove = "-";
	private double prefWidth = 35;
	private final TableColumn<T, Object> columnButtons = new TableColumn<>();

	public TableViewAddRemove() {
		columnButtons.setCellValueFactory(v -> {
			return new SimpleObjectProperty<>(v.getValue() != null);
		});
		columnButtons.setCellFactory(_ -> {
			Button button = new Button();
			button.setPrefWidth(30);
			TableCell<T, Object> cell = new TableCell<>() {
				@Override
				protected void updateItem(Object value, boolean empty) {
					super.updateItem(value, empty);
					if (empty) {
						setGraphic(null);
					} else {
						if (getTableRow() != null) {
							if (getTableRow().getItem() == dummy()) {
								button.setText(add);
							} else {
								button.setText(remove);
							}
							setGraphic(button);
						} else {
							setGraphic(null);
						}
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			button.setOnAction(event -> {
				if (cell.getTableRow().getIndex() == TableViewAddRemove.this.getItems().size() - 1) {
					for (T e : TableViewAddRemove.this.getItems()) {
						if (e != dummy() && !isComplete(e)) {
							TableViewAddRemove.this.getSelectionModel().select(e);
							event.consume();
							return;
						}
					}
					TableViewAddRemove.this.getItems().add(newEntry());
				} else if (cell.getTableRow().getIndex() < TableViewAddRemove.this.getItems().size() - 1) {
					TableViewAddRemove.this.getItems().remove(cell.getTableRow().getIndex());
				}
				event.consume();
			});
			return cell;
		});
		columnButtons.setSortable(false);
		columnButtons.setReorderable(false);
		columnButtons.setResizable(false);
		columnButtons.setPrefWidth(prefWidth);

		getColumns().add(columnButtons);

		getItems().add(dummy());
		getItems().addListener(new ListChangeListener<T>() {

			@Override
			public void onChanged(Change<? extends T> c) {
				if (getItems().isEmpty()) {
					getItems().add(dummy());
				} else if (getItems().indexOf(dummy()) != getItems().size() - 1) {
					getItems().remove(dummy());
					getItems().add(dummy());
				} else if (!getItems().contains(dummy())) {
					getItems().add(dummy());
				}
			}

		});
	}

	public abstract T newEntry();

	public abstract T dummy();

	public abstract boolean isComplete(T t);

	public void setAddText(String add) {
		this.add = add;
	}

	public void setRemoveText(String remove) {
		this.remove = remove;
	}

}
