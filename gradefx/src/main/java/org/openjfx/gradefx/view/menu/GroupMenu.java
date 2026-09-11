package org.openjfx.gradefx.view.menu;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.view.dialog.DialogAddGroup;
import org.openjfx.gradefx.view.dialog.DialogEditGroup;
import org.openjfx.gradefx.view.pane.print.GroupOverviewPrintPane;
import org.openjfx.kafx.controller.PrintController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.alert.AlertDelete;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class GroupMenu extends Menu {

	private final MenuItem menuItemNew, menuItemEdit, menuItemDelete, menuItemPrint;

	public GroupMenu() {
		super(TranslationController.translate("menu_group_title"));

		this.menuItemNew = new MenuItem(TranslationController.translate("menu_group_new"));
		this.menuItemNew.setOnAction(_ -> new DialogAddGroup().showAndWait());
		this.getItems().add(this.menuItemNew);

		this.menuItemEdit = new MenuItem(TranslationController.translate("menu_group_edit"));
		this.menuItemEdit.setOnAction(_ -> new DialogEditGroup(GradeFXController.getSelectedGroup()).showAndWait());
		this.getItems().add(this.menuItemEdit);

		this.menuItemDelete = new MenuItem(TranslationController.translate("menu_group_delete"));
		this.menuItemDelete.setOnAction(_ -> {
			Group g = GradeFXController.getSelectedGroup();
			new AlertDelete(TranslationController.translate("group") + " " + g.getName(), () -> Group.remove(g))
					.showAndWait();
		});
		this.getItems().add(this.menuItemDelete);

		this.menuItemPrint = new MenuItem(TranslationController.translate("menu_group_print"));
		this.menuItemPrint.setOnAction(_ -> PrintController.showPrintSinglePreview(
				new GroupOverviewPrintPane(GradeFXController.getSelectedGroup()), PrintController.A4_LANDSCAPE));
		getItems().add(this.menuItemPrint);

		this.menuItemEdit.disableProperty().bind(GradeFXController.selectedGroupProperty().isNull());
		this.menuItemDelete.disableProperty().bind(GradeFXController.selectedGroupProperty().isNull());
		this.menuItemPrint.disableProperty().bind(GradeFXController.selectedGroupProperty().isNull());
	}

}
