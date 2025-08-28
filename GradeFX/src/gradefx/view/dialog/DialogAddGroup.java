package gradefx.view.dialog;

import gradefx.model.GradeSystem;
import gradefx.model.Group;
import gradefx.model.TestGroup.TestGroupSystem;
import gradefx.view.converter.GradeSystemConverter;
import gradefx.view.converter.TestGroupSystemConverter;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import kafx.controller.Controller;
import kafx.lang.Translator;
import kafx.view.control.TextFieldPromptText;
import kafx.view.dialog.DialogAdd;
import kafx.view.dialog.userinput.UserInputCheckBox;
import kafx.view.dialog.userinput.UserInputChoiceBox;
import kafx.view.dialog.userinput.UserInputColorPicker;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogAddGroup extends DialogAdd<Group> {

	private final UserInputTextInput name;
	private final UserInputCheckBox useSubgroups;
	private final UserInputChoiceBox<GradeSystem> gradeSystem;
	private final UserInputChoiceBox<TestGroupSystem> testGroupSystem;
	private final UserInputColorPicker color;

	public DialogAddGroup() {
		super(Translator.get("dialog_add_group_title"));

		this.name = new UserInputTextInput(new TextFieldPromptText(Translator.get("group_name")));
		super.addInput(this.name, Translator.get("group_name"));

		this.useSubgroups = new UserInputCheckBox(new CheckBox());
		super.addInput(this.useSubgroups, Translator.get("group_useSubgroups"));

		ChoiceBox<GradeSystem> gradeSystemChoiceBox = new ChoiceBox<>(
				FXCollections.observableArrayList(GradeSystem.values()));
		gradeSystemChoiceBox.setConverter(new GradeSystemConverter());
		this.gradeSystem = new UserInputChoiceBox<>(gradeSystemChoiceBox, GradeSystem.getDefault());
		super.addInput(this.gradeSystem, Translator.get("group_gradeSystem"));

		ChoiceBox<TestGroupSystem> testGroupSystemChoiceBox = new ChoiceBox<>(TestGroupSystem.getTestGroupSystems());
		testGroupSystemChoiceBox.setConverter(new TestGroupSystemConverter());
		this.testGroupSystem = new UserInputChoiceBox<>(testGroupSystemChoiceBox,
				TestGroupSystem.getTestGroupSystems().get(0));
		super.addInput(this.testGroupSystem, Translator.get("group_testGroupSystem"));

		this.color = new UserInputColorPicker(new ColorPicker(),
				Color.web(Controller.getConfigOption("DEFAULT_GROUP_COLOR")), false);
		super.addInput(this.color, Translator.get("group_color"));
	}

	@Override
	public Group create() {
		return new Group(this.name.getValue().trim(), this.useSubgroups.getValue(), this.gradeSystem.getValue(),
				this.testGroupSystem.getValue(), this.color.getValue());
	}

}
