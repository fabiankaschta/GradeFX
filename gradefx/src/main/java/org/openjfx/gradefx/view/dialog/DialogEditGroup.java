package org.openjfx.gradefx.view.dialog;

import org.openjfx.gradefx.model.GradeSystem;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Subject;
import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.gradefx.view.alert.AlertTestSystemChange;
import org.openjfx.gradefx.view.converter.GradeSystemConverter;
import org.openjfx.gradefx.view.converter.SubjectConverter;
import org.openjfx.gradefx.view.converter.TestGroupSystemConverter;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.dialog.DialogEdit;
import org.openjfx.kafx.view.dialog.userinput.UserInputCheckBox;
import org.openjfx.kafx.view.dialog.userinput.UserInputChoiceBox;
import org.openjfx.kafx.view.dialog.userinput.UserInputColorPicker;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class DialogEditGroup extends DialogEdit<Group> {

	private final UserInputTextInput name;
	private final UserInputChoiceBox<Subject> subject;
	private final UserInputCheckBox useSubgroups;
	private final UserInputChoiceBox<GradeSystem> gradeSystem;
	private final UserInputChoiceBox<TestGroupSystem> testGroupSystem;
	private final UserInputColorPicker color;

	public DialogEditGroup(Group group) {
		super(TranslationController.translate("dialog_edit_group_title"), group);

		this.name = new UserInputTextInput(new TextFieldPromptText(TranslationController.translate("group_name")),
				group.getName());
		super.addInput(this.name, TranslationController.translate("group_name"));

		ChoiceBox<Subject> subjectChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(Subject.getSubjects()));
		subjectChoiceBox.setConverter(new SubjectConverter());
		this.subject = new UserInputChoiceBox<>(subjectChoiceBox, group.getSubject());
		super.addInput(this.subject, TranslationController.translate("group_subject"));

		this.useSubgroups = new UserInputCheckBox(new CheckBox(), group.getUseSubgroups());
		super.addInput(this.useSubgroups, TranslationController.translate("group_useSubgroups"));

		ChoiceBox<GradeSystem> gradeSystemChoiceBox = new ChoiceBox<>(
				FXCollections.observableArrayList(GradeSystem.values()));
		gradeSystemChoiceBox.setConverter(new GradeSystemConverter());
		this.gradeSystem = new UserInputChoiceBox<>(gradeSystemChoiceBox, group.getGradeSystem());
		super.addInput(this.gradeSystem, TranslationController.translate("group_gradeSystem"));

		ChoiceBox<TestGroupSystem> testGroupSystemChoiceBox = new ChoiceBox<>(TestGroupSystem.getTestGroupSystems());
		testGroupSystemChoiceBox.setConverter(new TestGroupSystemConverter());
		this.testGroupSystem = new UserInputChoiceBox<>(testGroupSystemChoiceBox, group.getTestGroupSystem());
		super.addInput(this.testGroupSystem, TranslationController.translate("group_testGroupSystem"));

		this.color = new UserInputColorPicker(new ColorPicker(), group.getColor());
		super.addInput(this.color, TranslationController.translate("group_color"));
	}

	@Override
	public boolean edit(Group group) {
		String name = this.name.getValue().trim();
		Subject subject = this.subject.getValue();
		boolean useSubgroups = this.useSubgroups.getValue();
		GradeSystem gradeSystem = this.gradeSystem.getValue();
		TestGroupSystem testGroupSystem = this.testGroupSystem.getValue();
		Color color = this.color.getValue();
		if (name != null && name.length() > 0 && !name.equals(group.getName())) {
			group.setName(name);
		}
		if (subject != group.getSubject()) {
			group.setSubject(subject);
		}
		if (useSubgroups != group.getUseSubgroups()) {
			group.setUseSubgroups(useSubgroups);
		}
		if (gradeSystem != group.getGradeSystem()) {
			group.setGradeSystem(gradeSystem);
		}
		if (testGroupSystem != group.getTestGroupSystem()) {
			new AlertTestSystemChange(group).showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					group.setTestGroupSystem(testGroupSystem);
				} else {
					// abort edit of testGroupSystem, do nothing
				}
			});
		}
		if (color != group.getColor()) {
			group.setColor(color);
		}
		return true;
	}

}
