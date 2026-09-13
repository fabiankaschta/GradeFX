package org.openjfx.gradefx.view.tableview.overview;

import org.openjfx.gradefx.model.Group;

import javafx.collections.FXCollections;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.ScrollBar;

public class TableViewOverviewPrint extends TableViewOverview {

	public TableViewOverviewPrint(Group group) {
		super(group);
		// create a new list, so that sorting is not reflected to the "real" list
		// adding/removing is not supported (no need to)
		this.setItems(FXCollections.observableArrayList(group.getStudents()));
		this.getStyleClass().add("table-view-full-size");
		this.skinProperty().addListener((_, _, _) -> {
			ScrollBar scrollBarVertical = (ScrollBar) this
					.queryAccessibleAttribute(AccessibleAttribute.VERTICAL_SCROLLBAR);
			scrollBarVertical.setPrefSize(0, 0);
			scrollBarVertical.setVisible(false);
			ScrollBar scrollBarHorizontal = (ScrollBar) this
					.queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
			scrollBarHorizontal.setPrefSize(0, 0);
			scrollBarHorizontal.setVisible(false);
		});

		this.setColumnResizePolicy(_ -> true);
		// this leads to odd inital state
//		for(TableColumn<Student, ?> c : getColumns()) {
//			c.setResizable(false);
//		}
		
		this.setMinWidth(USE_PREF_SIZE);
		this.setMaxWidth(USE_PREF_SIZE);
		this.setMinHeight(USE_PREF_SIZE);
		this.setMaxHeight(USE_PREF_SIZE);
	}

}
