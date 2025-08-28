package kafx.view.treeview;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;

public class TreeCellCustom<T> extends TreeCell<T> {

	@Override
	protected void updateItem(T item, boolean empty) {
		if (item == getItem())
			return;

		super.updateItem(item, empty);

		if (item == null) {
			super.setText(null);
			super.setGraphic(null);
		} else if (item instanceof Node) {
			super.setText(null);
			super.setGraphic((Node) item);
		} else {
			super.setText(item.toString());
			super.setGraphic(null);
		}
	}

}
