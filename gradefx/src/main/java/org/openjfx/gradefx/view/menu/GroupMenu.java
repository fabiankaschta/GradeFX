package org.openjfx.gradefx.view.menu;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.view.dialog.DialogAddGroup;
import org.openjfx.gradefx.view.dialog.DialogEditGroup;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.view.alert.AlertDelete;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class GroupMenu extends Menu {

	private final MenuItem menuItemNew, menuItemEdit, menuItemDelete;

	public GroupMenu() {
		super(Controller.translate("menu_group_title"));

		this.menuItemNew = new MenuItem(Controller.translate("menu_group_new"));
		this.menuItemNew.setOnAction(_ -> {
			new DialogAddGroup().showAndWait();
		});
		this.getItems().add(this.menuItemNew);

		this.menuItemEdit = new MenuItem(Controller.translate("menu_group_edit"));
		this.menuItemEdit.setOnAction(_ -> {
			new DialogEditGroup(GroupsPane.getSelectedGroup()).showAndWait();
		});
		this.getItems().add(this.menuItemEdit);

		this.menuItemDelete = new MenuItem(Controller.translate("menu_group_delete"));
		this.menuItemDelete.setOnAction(_ -> {
			Group g = GroupsPane.getSelectedGroup();
			new AlertDelete(Controller.translate("group") + " " + g.getName()).showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					Group.remove(g);
				} else {
					// abort delete, do nothing
				}
			});
		});
		this.getItems().add(this.menuItemDelete);

		this.menuItemEdit.disableProperty().bind(GroupsPane.selectedGroupProperty().isNull());
		this.menuItemDelete.disableProperty().bind(GroupsPane.selectedGroupProperty().isNull());
	}

}
