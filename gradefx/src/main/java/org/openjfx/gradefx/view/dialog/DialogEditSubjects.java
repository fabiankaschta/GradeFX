package org.openjfx.gradefx.view.dialog;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Subject;
import org.openjfx.gradefx.view.converter.SubjectConverter;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.view.alert.AlertDelete;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.dialog.DialogUserInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputChoiceBoxButtons;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;

public class DialogEditSubjects extends DialogUserInput<Boolean> {

	private final UserInputChoiceBoxButtons<Subject> subject;
	private final UserInputTextInput name;
	private final UserInputTextInput shortName;

	public DialogEditSubjects() {
		super(Controller.translate("dialog_edit_subjects_title"));

		this.name = new UserInputTextInput(new TextFieldPromptText(Controller.translate("subject_name")));
		this.shortName = new UserInputTextInput(new TextFieldPromptText(Controller.translate("subject_shortName")));

		ChoiceBox<Subject> choiceBox = new ChoiceBox<>(Subject.getSubjects());
		choiceBox.setConverter(new SubjectConverter());
		choiceBox.getSelectionModel().selectedItemProperty().addListener((_, oldItem, newItem) -> {
			if (oldItem != null) {
				oldItem.nameProperty().unbind();
				oldItem.shortNameProperty().unbind();
			}
			if (newItem != null) {
				name.setValue(newItem.getName());
				shortName.setValue(newItem.getShortName());
				newItem.nameProperty().bind(name.valueProperty());
				newItem.shortNameProperty().bind(shortName.valueProperty());
			} else {
				name.setValue("");
				shortName.setValue("");
			}
			// TODO editing does not update text of item list in choicebox and labels of groups
		});

		Button add = new Button(Controller.translate("dialog_edit_subjects_add"));
		add.setOnAction(_ -> {
			Subject.addSubject("", "");
			choiceBox.getSelectionModel().selectLast();
		});
		Button remove = new Button(Controller.translate("dialog_edit_subjects_remove"));
		remove.setOnAction(_ -> new AlertDelete(
				Controller.translate("subject") + " " + choiceBox.getSelectionModel().getSelectedItem().getName())
				.showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						int index = choiceBox.getSelectionModel().getSelectedIndex();
						Subject item = choiceBox.getSelectionModel().getSelectedItem();
						choiceBox.getItems().remove(item);
						Subject.removeSubject(item);
						if (choiceBox.getItems().size() == 0) {
							choiceBox.getSelectionModel().select(null);
						} else if (index > 0) {
							choiceBox.getSelectionModel().select(index - 1);
						} else {
							choiceBox.getSelectionModel().select(index);
						}
					} else {
						// abort delete, do nothing
					}
				}));
		// disable if any group uses selected subject
		remove.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			if (choiceBox.getSelectionModel().getSelectedItem() == null) {
				return true;
			}
			Subject selected = choiceBox.getSelectionModel().getSelectedItem();
			for (Group group : Group.getGroups()) {
				if (group.getSubject() == selected) {
					return true;
				}
			}
			return false;
		}, choiceBox.getSelectionModel().selectedItemProperty()));

		this.subject = new UserInputChoiceBoxButtons<>(choiceBox);
		this.subject.addButton(add);
		this.subject.addButton(remove);
		super.addInput(this.subject, Controller.translate("subject"));

		super.addInput(this.name, Controller.translate("subject_name"));
		super.addInput(this.shortName, Controller.translate("subject_shortName"));

		ButtonType doneButtonType = new ButtonType(Controller.translate("dialog_button_done"), ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(doneButtonType);

		this.setResultConverter(_ -> true);
	}

}
