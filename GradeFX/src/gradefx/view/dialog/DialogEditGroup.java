package gradefx.view.dialog;

import kafx.lang.Translator;
import gradefx.model.GradeSystem;
import gradefx.model.Group;
import gradefx.model.TestGroup.TestGroupSystem;
import gradefx.view.alert.AlertTestSystemChange;
import gradefx.view.converter.GradeSystemConverter;
import gradefx.view.converter.TestGroupSystemConverter;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import kafx.view.control.TextFieldPromptText;
import kafx.view.dialog.DialogEdit;
import kafx.view.dialog.userinput.UserInputCheckBox;
import kafx.view.dialog.userinput.UserInputChoiceBox;
import kafx.view.dialog.userinput.UserInputColorPicker;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogEditGroup extends DialogEdit<Group> {

	private final UserInputTextInput name;
	private final UserInputCheckBox useSubgroups;
	private final UserInputChoiceBox<GradeSystem> gradeSystem;
	private final UserInputChoiceBox<TestGroupSystem> testGroupSystem;
	private final UserInputColorPicker color;

	public DialogEditGroup(Group group) {
		super(Translator.get("dialog_edit_group_title"), group);

		this.name = new UserInputTextInput(new TextFieldPromptText(Translator.get("group_name")), group.getName());
		super.addInput(this.name, Translator.get("group_name"));

		this.useSubgroups = new UserInputCheckBox(new CheckBox(), group.getUseSubgroups());
		super.addInput(this.useSubgroups, Translator.get("group_useSubgroups"));

		ChoiceBox<GradeSystem> gradeSystemChoiceBox = new ChoiceBox<>(
				FXCollections.observableArrayList(GradeSystem.values()));
		gradeSystemChoiceBox.setConverter(new GradeSystemConverter());
		this.gradeSystem = new UserInputChoiceBox<>(gradeSystemChoiceBox, group.getGradeSystem());
		super.addInput(this.gradeSystem, Translator.get("group_gradeSystem"));

		ChoiceBox<TestGroupSystem> testGroupSystemChoiceBox = new ChoiceBox<>(TestGroupSystem.getTestGroupSystems());
		testGroupSystemChoiceBox.setConverter(new TestGroupSystemConverter());
		this.testGroupSystem = new UserInputChoiceBox<>(testGroupSystemChoiceBox, group.getTestGroupSystem());
		super.addInput(this.testGroupSystem, Translator.get("group_testGroupSystem"));

		this.color = new UserInputColorPicker(new ColorPicker(), group.getColor());
		super.addInput(this.color, Translator.get("group_color"));
	}

	@Override
	public boolean edit(Group group) {
		String name = this.name.getValue().trim();
		boolean useSubgroups = this.useSubgroups.getValue();
		GradeSystem gradeSystem = this.gradeSystem.getValue();
		TestGroupSystem testGroupSystem = this.testGroupSystem.getValue();
		Color color = this.color.getValue();
		if (name != null && name.length() > 0 && !name.equals(group.getName())) {
			group.setName(name);
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
