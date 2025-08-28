package kafx.view.treeview;

import javafx.animation.PauseTransition;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class DragAndDropCellFactory<T> implements Callback<TreeView<T>, TreeCell<T>> {
	private TreeCell<T> dropZone;
	private boolean dropAbove;
	private TreeItem<T> draggedItem;
	private final Callback<TreeView<T>, TreeCell<T>> otherFactory;
	private final StringConverter<T> converter;
	private final PauseTransition expandTimer = new PauseTransition(Duration.seconds(1));

	public DragAndDropCellFactory() {
		this(null, null);
	}

	public DragAndDropCellFactory(StringConverter<T> converter) {
		this(converter, null);
	}

	public DragAndDropCellFactory(Callback<TreeView<T>, TreeCell<T>> otherFactory) {
		this(null, otherFactory);
	}

	public DragAndDropCellFactory(StringConverter<T> converter, Callback<TreeView<T>, TreeCell<T>> otherFactory) {
		this.converter = converter;
		this.otherFactory = otherFactory;
	}

	@Override
	public TreeCell<T> call(TreeView<T> treeView) {
		TreeCell<T> cell = otherFactory == null ? new TreeCell<>() : otherFactory.call(treeView);

		cell.setOnDragDetected(event -> dragDetected(event, cell, treeView));
		cell.setOnDragOver(event -> dragOver(event, cell, treeView));
		cell.setOnDragDropped(event -> dragDrop(event, cell, treeView));
		cell.setOnDragEntered(event -> dragEnter(event, cell, treeView));
		cell.setOnDragExited(event -> dragExit(event, cell, treeView));
		cell.setOnDragDone(event -> dragDone(event, cell, treeView));

		return cell;
	}

	private void dragDetected(MouseEvent event, TreeCell<T> treeCell, TreeView<T> treeView) {
		draggedItem = treeCell.getTreeItem();
		if(draggedItem == null) 
			return;
		
		// root can't be dragged
		if (draggedItem.getParent() == null)
			return;
		Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);

		ClipboardContent content = new ClipboardContent();
		content.putString(
				converter == null ? draggedItem.getValue().toString() : converter.toString(draggedItem.getValue()));
		db.setContent(content);
		db.setDragView(treeCell.snapshot(null, null));
		event.consume();
	}

	private void dragEnter(DragEvent event, TreeCell<T> treeCell, TreeView<T> treeView) {
		expandTimer.setOnFinished(_ -> {
			if (!treeCell.getTreeItem().isExpanded()) {
				treeCell.getTreeItem().setExpanded(true);
			}
		});
		expandTimer.playFromStart();
	}

	private void dragExit(DragEvent event, TreeCell<T> treeCell, TreeView<T> treeView) {
		expandTimer.stop();
	}

	private void dragOver(DragEvent event, TreeCell<T> treeCell, TreeView<T> treeView) {
		if (!event.getDragboard().hasContent(DataFormat.PLAIN_TEXT))
			return;
		TreeItem<T> thisItem = treeCell.getTreeItem();

		// can't drop on itself
		if (draggedItem == null || thisItem == null)
			return;
		// ignore if this is the root
		if (draggedItem.getParent() == null) {
			clearDropLocation();
			return;
		}
		event.acceptTransferModes(TransferMode.MOVE);
		clearDropLocation();
		this.dropZone = treeCell;
		if (event.getY() < treeCell.getHeight() / 2) {
			this.dropZone.setStyle(
					"-fx-border-color: -fx-accent; -fx-border-width: 2.0 0.0 0.0 0.0; -fx-padding: 1.0 3.0 3.0 3.0;");
			this.dropAbove = true;
		} else {
			this.dropZone.setStyle(
					"-fx-border-color: -fx-accent; -fx-border-width: 0.0 0.0 2.0 0.0; -fx-padding: 3.0 3.0 1.0 3.0;");
			this.dropAbove = false;
		}
	}

	private void dragDrop(DragEvent event, TreeCell<T> treeCell, TreeView<T> treeView) {
		expandTimer.stop();
		Dragboard db = event.getDragboard();
		if (!db.hasContent(DataFormat.PLAIN_TEXT))
			return;

		TreeItem<T> thisItem = treeCell.getTreeItem();
		TreeItem<T> droppedItemParent = draggedItem.getParent();

		if (thisItem == draggedItem) {
			event.setDropCompleted(false);
		} else {
			// remove from previous location
			droppedItemParent.getChildren().remove(draggedItem);
			if (thisItem.getParent() == null) {
				// drop on root makes it the first child
				thisItem.getChildren().add(0, draggedItem);
			} else if (dropAbove) {
				// drop above item
				int index = thisItem.getParent().getChildren().indexOf(thisItem);
				thisItem.getParent().getChildren().add(index, draggedItem);
			} else if (!thisItem.isLeaf() && thisItem.isExpanded()) {
				// drop below expanded makes it the first child
				thisItem.getChildren().add(0, draggedItem);
			} else {
				// drop below collapsed / leaf makes it next sibling
				int index = thisItem.getParent().getChildren().indexOf(thisItem);
				thisItem.getParent().getChildren().add(index + 1, draggedItem);
			}
			event.setDropCompleted(true);
		}
	}

	private void dragDone(DragEvent event, TreeCell<T> cell, TreeView<T> treeView) {
		expandTimer.stop();
		clearDropLocation();
		treeView.getSelectionModel().select(cell.getTreeItem());
		cell.requestFocus();
	}

	private void clearDropLocation() {
		if (dropZone != null) {
			this.dropZone.setStyle("");
		}
	}
}
