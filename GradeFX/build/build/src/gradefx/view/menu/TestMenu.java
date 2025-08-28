package gradefx.view.menu;

import kafx.lang.Translator;
import gradefx.model.Group;
import gradefx.model.Test;
import gradefx.view.alert.AlertDelete;
import gradefx.view.dialog.DialogAddTest;
import gradefx.view.dialog.DialogEditTest;
import gradefx.view.dialog.DialogEditTestGroupSystems;
import gradefx.view.dialog.DialogEditTestTasks;
import gradefx.view.pane.GroupsPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class TestMenu extends Menu {

	private final MenuItem menuItemNew, menuItemEdit, menuItemDelete, menuItemTasks, menuItemGroups;

	public TestMenu() {
		super(Translator.get("menu_test_title"));

		this.menuItemNew = new MenuItem(Translator.get("menu_test_new"));
		this.menuItemNew.setOnAction(_ -> {
			new DialogAddTest(GroupsPane.getSelectedGroup()).showAndWait();
		});
		this.getItems().add(this.menuItemNew);

		this.menuItemEdit = new MenuItem(Translator.get("menu_test_edit"));
		this.menuItemEdit.setOnAction(_ -> {
			new DialogEditTest(GroupsPane.getSelectedGroup(), GroupsPane.getSelectedTest()).showAndWait();
		});
		this.getItems().add(this.menuItemEdit);

		this.menuItemDelete = new MenuItem(Translator.get("menu_test_delete"));
		this.menuItemDelete.setOnAction(_ -> {
			Group g = GroupsPane.getSelectedGroup();
			Test t = GroupsPane.getSelectedTest();
			new AlertDelete(Translator.get("test") + " " + t.getName()).showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					g.removeTest(t);
				} else {
					// abort delete, do nothing
				}
			});
		});
		this.getItems().add(this.menuItemDelete);

		this.getItems().add(new SeparatorMenuItem());

		this.menuItemTasks = new MenuItem(Translator.get("menu_test_tasks"));
		this.menuItemTasks.setOnAction(_ -> {
			new DialogEditTestTasks(GroupsPane.getSelectedTest()).showAndWait();
		});
		this.getItems().add(this.menuItemTasks);

		this.getItems().add(new SeparatorMenuItem());

		this.menuItemGroups = new MenuItem(Translator.get("menu_test_groups"));
		this.menuItemGroups.setOnAction(_ -> {
			new DialogEditTestGroupSystems().showAndWait();
		});
		this.getItems().add(this.menuItemGroups);

		this.menuItemNew.disableProperty().bind(GroupsPane.selectedGroupProperty().isNull());
		this.menuItemEdit.disableProperty().bind(GroupsPane.selectedTestProperty().isNull());
		this.menuItemDelete.disableProperty().bind(GroupsPane.selectedTestProperty().isNull());
		if (GroupsPane.getSelectedTest() == null) {
			this.menuItemTasks.disableProperty().set(true);
		} else {
			this.menuItemTasks.disableProperty().bind(GroupsPane.getSelectedTest().useTasksProperty().not());
		}
		GroupsPane.selectedTestProperty().addListener((_, _, newValue) -> {
			this.menuItemTasks.disableProperty().unbind();
			if (newValue != null) {
				this.menuItemTasks.disableProperty().bind(newValue.useTasksProperty().not());
			} else {
				this.menuItemTasks.disableProperty().set(true);
			}
		});
	}

}
