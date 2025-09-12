package org.openjfx.gradefx.view.dialog;

import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.dialog.DialogAdd;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

public class DialogAddTestGroupSystem extends DialogAdd<TestGroupSystem> {

	private final UserInputTextInput name;

	public DialogAddTestGroupSystem() {
		super(TranslationController.translate("dialog_add_testGroupSystem_title"));

		this.name = new UserInputTextInput(new TextFieldPromptText(TranslationController.translate("testGroups_testGroupSystem")));
		super.addInput(this.name, TranslationController.translate("testGroups_testGroupSystem"));
	}

	@Override
	public TestGroupSystem create() {
		return TestGroupSystem.createRoot(this.name.getValue());
	}

}
