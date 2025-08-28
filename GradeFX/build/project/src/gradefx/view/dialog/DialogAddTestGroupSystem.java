package gradefx.view.dialog;

import kafx.lang.Translator;
import gradefx.model.TestGroup.TestGroupSystem;
import kafx.view.control.TextFieldPromptText;
import kafx.view.dialog.DialogAdd;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogAddTestGroupSystem extends DialogAdd<TestGroupSystem> {

	private final UserInputTextInput name;

	public DialogAddTestGroupSystem() {
		super(Translator.get("dialog_add_testGroupSystem_title"));

		this.name = new UserInputTextInput(new TextFieldPromptText(Translator.get("testGroups_testGroupSystem")));
		super.addInput(this.name, Translator.get("testGroups_testGroupSystem"));
	}

	@Override
	public TestGroupSystem create() {
		return TestGroupSystem.createRoot(this.name.getValue());
	}

}
