package gradefx.view.dialog;

import java.math.BigDecimal;

import kafx.lang.Translator;
import gradefx.model.TestGroup.TestGroupSystem;
import kafx.view.control.ComparableField;
import kafx.view.control.TextFieldPromptText;
import kafx.view.converter.BigDecimalConverter;
import kafx.view.dialog.DialogEdit;
import kafx.view.dialog.userinput.UserInputComparableInput;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogEditSubTestGroupSystem extends DialogEdit<TestGroupSystem> {

	private final UserInputTextInput name;
	private final UserInputComparableInput<BigDecimal> weight;

	public DialogEditSubTestGroupSystem(TestGroupSystem testGroupSystem) {
		super(Translator.get("dialog_edit_testGroupSystem_title"), testGroupSystem);

		this.name = new UserInputTextInput(new TextFieldPromptText(Translator.get("testGroups_testGroupSystem")),
				testGroupSystem.getName());
		super.addInput(this.name, Translator.get("testGroups_testGroupSystem"));

		BigDecimalConverter weightConverter = new BigDecimalConverter();
		weightConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.weight = new UserInputComparableInput<>(new ComparableField<>(BigDecimal.ZERO, null, weightConverter),
				testGroupSystem.getWeight(), false);
		super.addInput(this.weight, Translator.get("testGroup_weight"));
	}

	@Override
	public boolean edit(TestGroupSystem testGroupSystem) {
		String name = this.name.getValue().trim();
		BigDecimal weight = this.weight.getValue();
		if (name != null && name.length() > 0 && !name.equals(testGroupSystem.getName())) {
			testGroupSystem.setName(name);
		}
		if (!weight.equals(testGroupSystem.getWeight())) {
			testGroupSystem.setWeight(weight);
		}
		return true;
	}

}
