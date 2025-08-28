package kafx.view.pane;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import kafx.lang.Translator;

public class SynchronizedScrollableTableView<T> extends BorderPane {

	private final TableView<T> left, center;
	private final Text placeholder = new Text(Translator.get("tab_overview_no_students"));

	public SynchronizedScrollableTableView(TableView<T> left, TableView<T> center) {
		this.left = left;
		this.center = center;

		// allow full row selection even if cell selection is active
		// used for sync'ing selection
		if (left.getSelectionModel().isCellSelectionEnabled()) {
			left.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}
		// needed to make tables fit when setting up
		left.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
		// fix for white border between if center is focused
		left.getStyleClass().add("table-view-no-right-focus");
		center.getStyleClass().add("table-view-no-left-focus");

		// hacky solution - setup is not working otherwise if no items in views
		setup();
		left.getItems().addListener((ListChangeListener<T>) _ -> {
			setup();
		});
		left.itemsProperty().addListener((_, _, _) -> {
			setup();
			left.getItems().addListener((ListChangeListener<T>) _ -> {
				setup();
			});
		});

		// hacky solution - call after both skins are applied
		left.skinProperty().addListener(_ -> {
			if (center.getSkin() != null) {
				init();
				left.autosize();
			}
		});
		center.skinProperty().addListener(_ -> {
			if (left.getSkin() != null) {
				init();
				left.autosize();
			}
		});
		// allows left to hide columns
		left.getVisibleLeafColumns().addListener((ListChangeListener<TableColumn<T, ?>>) _ -> {
			if (left.getSkin() != null) {
				init();
				left.autosize();
			}
		});
	}

	private void setup() {
		if (left.getItems().isEmpty()) {
			this.setCenter(placeholder);
		} else {
			this.setLeft(left);
			this.setCenter(center);
		}
	}

	@SuppressWarnings("unchecked")
	private void init() {
		// fix padding (white border between)
		center.setPadding(new Insets(0, 0, 0, -1));
		left.setPadding(new Insets(0));
		// end padding

		// bind scrollBars
		ScrollBar leftScrollBarVertical = (ScrollBar) left
				.queryAccessibleAttribute(AccessibleAttribute.VERTICAL_SCROLLBAR);
		ScrollBar centerScrollBarVertical = (ScrollBar) center
				.queryAccessibleAttribute(AccessibleAttribute.VERTICAL_SCROLLBAR);
		hideScrollBar(leftScrollBarVertical);

		centerScrollBarVertical.valueProperty().bindBidirectional(leftScrollBarVertical.valueProperty());

		ScrollBar leftScrollBarHorizontal = (ScrollBar) left
				.queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
		ScrollBar centerScrollBarHorizontal = (ScrollBar) center
				.queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
		hideScrollBar(leftScrollBarHorizontal);
		leftScrollBarHorizontal.setDisable(true);
		centerScrollBarHorizontal.visibleProperty().addListener((_, oldValue, newValue) -> {
			if (!oldValue && newValue) {
				// FIXME scroll bar gets height of 100 sometimes so 13 is fixed here
				left.setPadding(new Insets(0, 0, 13, 0));
			} else {
				left.setPadding(new Insets(0, 0, 1, 0));
			}
		});
		// end scrollBars

		// bind header height
		Pane leftHeader = (Pane) left.queryAccessibleAttribute(AccessibleAttribute.HEADER);
		Pane centerHeader = (Pane) center.queryAccessibleAttribute(AccessibleAttribute.HEADER);
		leftHeader.prefHeightProperty().bind(centerHeader.heightProperty());
		// end header height

		// bind selection
		center.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
			left.getSelectionModel().isSelected(0, null);
			if (newValue != null && left.getSelectionModel().getSelectedItem() != newValue) {
				left.getSelectionModel().clearSelection();
				left.getSelectionModel().select(newValue);
			} else if (newValue != null && left.getSelectionModel().getSelectedItem() == newValue) {
				// fix if same row as selected in left is selected in center didn't select
				// full row in left
				// assume that there is only one cell selected at a time in center table
				int row = center.getSelectionModel().getSelectedCells().get(0).getRow();
				for (TableColumn<T, ?> col : left.getColumns()) {
					if (!left.getSelectionModel().isSelected(row, col)) {
						left.getSelectionModel().select(row, col);
					}
				}
			}
		});
		left.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
			if (newValue != null && center.getSelectionModel().getSelectedItem() != newValue) {
				center.getSelectionModel().clearSelection();
			}
		});
		// end selection

		// column resize
		left.setSnapToPixel(true);
		bindColumnWidths(left);
		left.getColumns().addListener((ListChangeListener<TableColumn<T, ?>>) _ -> {
			bindColumnWidths(left);
		});
		left.setColumnResizePolicy(r -> {
			if (r.getColumn() != null) {
				r.setColumnWidth(r.getColumn(), r.getColumn().getWidth() + r.getDelta());
				return true;
			} else {
				// handled by prefWidthProptery
				return true;
			}
		});
		// end column resize

		// for now fixed by not allowing height change
		// FIXME center editing does not work - border pane renders in order of adding
		// sync row height when editing cells
//		left.editingCellProperty().addListener((_, oldValue, newValue) -> {
//			if (oldValue != null && center.getColumns().size() > 0) {
//				int row = oldValue.getRow();
//				TableRow<T> rowCenter = ((TableCell<T, ?>) center
//						.queryAccessibleAttribute(AccessibleAttribute.CELL_AT_ROW_COLUMN, row, 0)).getTableRow();
//				rowCenter.prefHeightProperty().unbind();
//				rowCenter.setPrefHeight(USE_COMPUTED_SIZE);
//			}
//			if (newValue != null && center.getColumns().size() > 0) {
//				int row = newValue.getRow();
//				int col = newValue.getColumn();
//				TableCell<T, ?> cell = (TableCell<T, ?>) left
//						.queryAccessibleAttribute(AccessibleAttribute.CELL_AT_ROW_COLUMN, row, col);
//				TableRow<T> rowCenter = ((TableCell<T, ?>) center
//						.queryAccessibleAttribute(AccessibleAttribute.CELL_AT_ROW_COLUMN, row, 0)).getTableRow();
//				rowCenter.prefHeightProperty().bind(cell.heightProperty());
//			}
//		});
//		center.editingCellProperty().addListener((_, oldValue, newValue) -> {
//			if (oldValue != null && left.getColumns().size() > 0) {
//				int row = oldValue.getRow();
//				TableRow<T> rowLeft = ((TableCell<T, ?>) left
//						.queryAccessibleAttribute(AccessibleAttribute.CELL_AT_ROW_COLUMN, row, 0)).getTableRow();
//				rowLeft.prefHeightProperty().unbind();
//				rowLeft.setPrefHeight(USE_COMPUTED_SIZE);
//			}
//			if (newValue != null && left.getColumns().size() > 0) {
//				int row = newValue.getRow();
//				int col = newValue.getColumn();
//				TableCell<T, ?> cell = (TableCell<T, ?>) center
//						.queryAccessibleAttribute(AccessibleAttribute.CELL_AT_ROW_COLUMN, row, col);
//				TableRow<T> rowLeft = ((TableCell<T, ?>) left
//						.queryAccessibleAttribute(AccessibleAttribute.CELL_AT_ROW_COLUMN, row, 0)).getTableRow();
//				rowLeft.prefHeightProperty().bind(cell.heightProperty());
//			}
//		});
		// end sync row height when editing cells
	}

	private void bindColumnWidths(TableView<?> tableView) {
		tableView.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
			double sum = 1;
			for (TableColumn<?, ?> col : tableView.getColumns()) {
				if (col.isVisible()) {
					sum += snapSizeX(col.getWidth());
				}
			}
			return sum;
		}, tableView.getColumns().stream().map(c -> c.widthProperty()).toArray(n -> new Observable[n])));
	}

	private void hideScrollBar(ScrollBar scrollBar) {
		scrollBar.setManaged(false);
		scrollBar.setPrefWidth(0);
		scrollBar.setPrefHeight(0);
		scrollBar.setOpacity(0);
	}

	public Node getPlaceholder() {
		return this.placeholder;
	}

}
