package gradefx.view.tab;

import gradefx.model.Group;
import gradefx.view.pane.GroupContentPane;
import gradefx.view.pane.GroupsPane;
import gradefx.view.style.Styles;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.text.TextAlignment;

public class GroupTab extends Tab {

	private final Group group;
	private final GroupContentPane pane;

	public GroupTab(Group group) {
		this.group = group;
		this.pane = new GroupContentPane(this.group);

		Label label = new Label();
		label.textProperty().bindBidirectional(this.group.nameProperty());
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setAlignment(Pos.CENTER);
		label.setPrefWidth(GroupsPane.TAB_HEAD_SIZE);
		label.setPrefHeight(GroupsPane.TAB_HEAD_SIZE);

		this.setGraphic(label);
		this.setContent(this.pane);
		Styles.subscribeColor(this, "-fx-base", this.group.colorProperty());
	}

	public Group getGroup() {
		return this.group;
	}

	public ReadOnlyObjectProperty<Tab> selectedTabProperty() {
		return this.pane.getSelectionModel().selectedItemProperty();
	}

	public Tab getSelectedTab() {
		return this.pane.getSelectionModel().getSelectedItem();
	}

	public int getSelectedTabIndex() {
		return this.pane.getSelectionModel().getSelectedIndex();
	}

	public void select(int index) {
		this.pane.getSelectionModel().select(index);
	}

}
