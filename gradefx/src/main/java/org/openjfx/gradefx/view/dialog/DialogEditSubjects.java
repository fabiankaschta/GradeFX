package org.openjfx.gradefx.view.dialog;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Subject;
import org.openjfx.gradefx.view.converter.SubjectConverter;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.alert.AlertDelete;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.dialog.DialogUserInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputComboBoxButtons;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class DialogEditSubjects extends DialogUserInput<Boolean> {

	private final UserInputComboBoxButtons<Subject> subject;
	private final UserInputTextInput name;
	private final UserInputTextInput shortName;

	public DialogEditSubjects() {
		super(TranslationController.translate("dialog_edit_subjects_title"));

		this.name = new UserInputTextInput(new TextFieldPromptText(TranslationController.translate("subject_name")));
		this.shortName = new UserInputTextInput(
				new TextFieldPromptText(TranslationController.translate("subject_shortName")));

		ObservableList<Subject> baseList = FXCollections.observableArrayList(
				subject -> new Observable[] { subject.nameProperty(), subject.shortNameProperty() });
		baseList.addAll(Subject.getSubjects());

		ComboBox<Subject> comboBox = new ComboBox<>(baseList);
		comboBox.setConverter(new SubjectConverter());
		comboBox.getSelectionModel().selectedItemProperty().addListener((_, oldItem, newItem) -> {
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
		});

		Button add = new Button(TranslationController.translate("dialog_edit_subjects_add"));
		add.setOnAction(_ -> {
			Subject subject = Subject.addSubject("", "");
			comboBox.getItems().add(subject);
			comboBox.getSelectionModel().select(subject);
		});
		Button remove = new Button(TranslationController.translate("dialog_edit_subjects_remove"));
		remove.setOnAction(_ -> new AlertDelete(TranslationController.translate("subject") + " "
				+ comboBox.getSelectionModel().getSelectedItem().getName(), () -> {
					int index = comboBox.getSelectionModel().getSelectedIndex();
					Subject item = comboBox.getSelectionModel().getSelectedItem();
					comboBox.getItems().remove(item);
					Subject.removeSubject(item);
					if (comboBox.getItems().size() == 0) {
						comboBox.getSelectionModel().select(null);
					} else if (index > 0) {
						comboBox.getSelectionModel().select(index - 1);
					} else {
						comboBox.getSelectionModel().select(index);
					}
				}).showAndWait());
		// disable if any group uses selected subject
		remove.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			if (comboBox.getSelectionModel().getSelectedItem() == null) {
				return true;
			}
			Subject selected = comboBox.getSelectionModel().getSelectedItem();
			for (Group group : Group.getGroups()) {
				if (group.getSubject() == selected) {
					return true;
				}
			}
			return false;
		}, comboBox.getSelectionModel().selectedItemProperty()));

		this.subject = new UserInputComboBoxButtons<>(comboBox);
		this.subject.addButton(add);
		this.subject.addButton(remove);
		super.addInput(this.subject, TranslationController.translate("subject"));

		super.addInput(this.name, TranslationController.translate("subject_name"));
		super.addInput(this.shortName, TranslationController.translate("subject_shortName"));

		this.getDialogPane().getButtonTypes().add(DONE);

		this.setResultConverter(_ -> true);
	}

}
