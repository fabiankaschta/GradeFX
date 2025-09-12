package org.openjfx.gradefx.view.menu;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.view.dialog.DialogAddGroup;
import org.openjfx.gradefx.view.dialog.DialogEditGroup;
import org.openjfx.gradefx.view.dialog.DialogEditSubjects;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.alert.AlertDelete;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class GroupMenu extends Menu {

	public GroupMenu() {
		super(TranslationController.translate("menu_group_title"));

		MenuItem menuItemNew = new MenuItem(TranslationController.translate("menu_group_new"));
		menuItemNew.setOnAction(_ -> new DialogAddGroup().showAndWait());
		this.getItems().add(menuItemNew);

		MenuItem menuItemEdit = new MenuItem(TranslationController.translate("menu_group_edit"));
		menuItemEdit.setOnAction(_ -> new DialogEditGroup(GroupsPane.getSelectedGroup()).showAndWait());
		this.getItems().add(menuItemEdit);

		MenuItem menuItemDelete = new MenuItem(TranslationController.translate("menu_group_delete"));
		menuItemDelete.setOnAction(_ -> {
			Group g = GroupsPane.getSelectedGroup();
			new AlertDelete(TranslationController.translate("group") + " " + g.getName()).showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					Group.remove(g);
				} else {
					// abort delete, do nothing
				}
			});
		});
		this.getItems().add(menuItemDelete);

		this.getItems().add(new SeparatorMenuItem());

		MenuItem menuItemEditSubjects = new MenuItem(TranslationController.translate("menu_group_editSubjects"));
		menuItemEditSubjects.setOnAction(_ -> new DialogEditSubjects().showAndWait());
		this.getItems().add(menuItemEditSubjects);

		menuItemEdit.disableProperty().bind(GroupsPane.selectedGroupProperty().isNull());
		menuItemDelete.disableProperty().bind(GroupsPane.selectedGroupProperty().isNull());
	}

}
