package gradefx.view.menu;

import kafx.lang.Translator;
import kafx.view.alert.AlertDelete;
import gradefx.model.Group;
import gradefx.view.dialog.DialogAddGroup;
import gradefx.view.dialog.DialogEditGroup;
import gradefx.view.pane.GroupsPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class GroupMenu extends Menu {

	private final MenuItem menuItemNew, menuItemEdit, menuItemDelete;

	public GroupMenu() {
		super(Translator.get("menu_group_title"));

		this.menuItemNew = new MenuItem(Translator.get("menu_group_new"));
		this.menuItemNew.setOnAction(_ -> {
			new DialogAddGroup().showAndWait();
		});
		this.getItems().add(this.menuItemNew);

		this.menuItemEdit = new MenuItem(Translator.get("menu_group_edit"));
		this.menuItemEdit.setOnAction(_ -> {
			new DialogEditGroup(GroupsPane.getSelectedGroup()).showAndWait();
		});
		this.getItems().add(this.menuItemEdit);

		this.menuItemDelete = new MenuItem(Translator.get("menu_group_delete"));
		this.menuItemDelete.setOnAction(_ -> {
			Group g = GroupsPane.getSelectedGroup();
			new AlertDelete(Translator.get("group") + " " + g.getName()).showAndWait().ifPresent(response -> {
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
