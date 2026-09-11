package org.openjfx.gradefx.view.dialog;

import org.openjfx.gradefx.converter.GroupConverter;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.dialog.DialogCustom;
import org.openjfx.kafx.view.dialog.userinput.UserInputChoiceBox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class DialogImportStudentsFromGroup extends DialogCustom<Boolean> {

	private final Group toGroup;
	private final UserInputChoiceBox<Group> fromGroup;

	public DialogImportStudentsFromGroup(Group toGroup) {
		this.toGroup = toGroup;
		this.setTitle(TranslationController.translate("dialog_import_students_group_title"));

		ObservableList<Group> baseList = FXCollections.observableArrayList(Group.getGroups());
		baseList.remove(this.toGroup);

		HBox content = new HBox(10);
		Label groupLabel = new Label(TranslationController.translate("dialog_import_students_group_source") + ':');
		groupLabel.prefHeightProperty().bind(content.heightProperty());

		ChoiceBox<Group> groupChoiceBox = new ChoiceBox<>(baseList);
		groupChoiceBox.setConverter(new GroupConverter());
		groupChoiceBox.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(10));
		this.fromGroup = new UserInputChoiceBox<>(groupChoiceBox);

		content.getChildren().addAll(groupLabel, this.fromGroup);

		this.getDialogPane().setContent(content);

		this.getDialogPane().getButtonTypes().addAll(IMPORT, CANCEL);

		this.setResultConverter(button -> {
			if (button == IMPORT) {
				for (Student student : this.fromGroup.getValue().getStudents()) {
					this.toGroup.addStudent(student);
				}
				return true;
			} else {
				return false;
			}
		});
	}

}
