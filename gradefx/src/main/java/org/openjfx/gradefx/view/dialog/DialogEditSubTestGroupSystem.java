package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;

import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.dialog.DialogEdit;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

public class DialogEditSubTestGroupSystem extends DialogEdit<TestGroupSystem> {

	private final UserInputTextInput name;
	private final UserInputComparableInput<BigDecimal> weight;

	public DialogEditSubTestGroupSystem(TestGroupSystem testGroupSystem) {
		super(Controller.translate("dialog_edit_testGroupSystem_title"), testGroupSystem);

		this.name = new UserInputTextInput(new TextFieldPromptText(Controller.translate("testGroups_testGroupSystem")),
				testGroupSystem.getName());
		super.addInput(this.name, Controller.translate("testGroups_testGroupSystem"));

		BigDecimalConverter weightConverter = new BigDecimalConverter();
		weightConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.weight = new UserInputComparableInput<>(new ComparableField<>(BigDecimal.ZERO, null, weightConverter),
				testGroupSystem.getWeight(), false);
		super.addInput(this.weight, Controller.translate("testGroup_weight"));
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
