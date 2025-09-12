package org.openjfx.gradefx.view.tab;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.view.pane.GroupContentPane;
import org.openjfx.gradefx.view.style.Styles;
import org.openjfx.kafx.controller.FontSizeController;

import javafx.beans.binding.Bindings;
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
		label.textProperty().bind(Bindings.createStringBinding(() -> {
			String name = this.group.getName();
			String subject = this.group.getSubject().getShortName();
			if (subject != null && subject.length() > 0) {
				return name + '\n' + subject;
			} else {
				return name;
			}
		}, this.group.nameProperty(), this.group.subjectProperty()));
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setAlignment(Pos.CENTER);
		FontSizeController.fontSizeProperty().subscribe(fontSize -> {
			label.setPrefWidth(fontSize.doubleValue() * 4);
			label.setPrefHeight(fontSize.doubleValue() * 4);
		});

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
