package org.openjfx.gradefx.view.dialog;

import org.openjfx.gradefx.model.GradeSystem;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.gradefx.view.converter.GradeSystemConverter;
import org.openjfx.gradefx.view.converter.TestGroupSystemConverter;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.dialog.DialogAdd;
import org.openjfx.kafx.view.dialog.userinput.UserInputCheckBox;
import org.openjfx.kafx.view.dialog.userinput.UserInputChoiceBox;
import org.openjfx.kafx.view.dialog.userinput.UserInputColorPicker;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class DialogAddGroup extends DialogAdd<Group> {

	private final UserInputTextInput name;
	private final UserInputCheckBox useSubgroups;
	private final UserInputChoiceBox<GradeSystem> gradeSystem;
	private final UserInputChoiceBox<TestGroupSystem> testGroupSystem;
	private final UserInputColorPicker color;

	public DialogAddGroup() {
		super(Controller.translate("dialog_add_group_title"));

		this.name = new UserInputTextInput(new TextFieldPromptText(Controller.translate("group_name")));
		super.addInput(this.name, Controller.translate("group_name"));

		this.useSubgroups = new UserInputCheckBox(new CheckBox());
		super.addInput(this.useSubgroups, Controller.translate("group_useSubgroups"));

		ChoiceBox<GradeSystem> gradeSystemChoiceBox = new ChoiceBox<>(
				FXCollections.observableArrayList(GradeSystem.values()));
		gradeSystemChoiceBox.setConverter(new GradeSystemConverter());
		this.gradeSystem = new UserInputChoiceBox<>(gradeSystemChoiceBox, GradeSystem.getDefault());
		super.addInput(this.gradeSystem, Controller.translate("group_gradeSystem"));

		ChoiceBox<TestGroupSystem> testGroupSystemChoiceBox = new ChoiceBox<>(TestGroupSystem.getTestGroupSystems());
		testGroupSystemChoiceBox.setConverter(new TestGroupSystemConverter());
		this.testGroupSystem = new UserInputChoiceBox<>(testGroupSystemChoiceBox,
				TestGroupSystem.getTestGroupSystems().get(0));
		super.addInput(this.testGroupSystem, Controller.translate("group_testGroupSystem"));

		this.color = new UserInputColorPicker(new ColorPicker(),
				Color.web(Controller.getConfigOption("DEFAULT_GROUP_COLOR")), false);
		super.addInput(this.color, Controller.translate("group_color"));
	}

	@Override
	public Group create() {
		return new Group(this.name.getValue().trim(), this.useSubgroups.getValue(), this.gradeSystem.getValue(),
				this.testGroupSystem.getValue(), this.color.getValue());
	}

}
