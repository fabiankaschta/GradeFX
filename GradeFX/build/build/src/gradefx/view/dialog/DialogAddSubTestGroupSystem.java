package gradefx.view.dialog;

import java.math.BigDecimal;

import kafx.lang.Translator;
import gradefx.model.TestGroup.TestGroupSystem;
import kafx.view.control.ComparableField;
import kafx.view.control.TextFieldPromptText;
import kafx.view.converter.BigDecimalConverter;
import kafx.view.dialog.DialogAdd;
import kafx.view.dialog.userinput.UserInputComparableInput;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogAddSubTestGroupSystem extends DialogAdd<TestGroupSystem> {

	private final UserInputTextInput name;
	private final UserInputComparableInput<BigDecimal> weight;
	private final TestGroupSystem parent;

	public DialogAddSubTestGroupSystem(TestGroupSystem parent) {
		super(Translator.get("dialog_add_subTestGroupSystem_title"));
		this.parent = parent;

		this.name = new UserInputTextInput(new TextFieldPromptText(Translator.get("testGroups_testGroupSystem")));
		super.addInput(this.name, Translator.get("testGroups_testGroupSystem"));

		BigDecimalConverter weightConverter = new BigDecimalConverter();
		weightConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.weight = new UserInputComparableInput<>(new ComparableField<>(BigDecimal.ZERO, null, weightConverter),
				BigDecimal.ONE, false);
		super.addInput(this.weight, Translator.get("testGroup_weight"));
	}

	@Override
	public TestGroupSystem create() {
		TestGroupSystem testGroupSystem = TestGroupSystem.createSubSystem(this.name.getValue(), this.weight.getValue());
		this.parent.addSubgroup(testGroupSystem);
		return testGroupSystem;
	}

}
