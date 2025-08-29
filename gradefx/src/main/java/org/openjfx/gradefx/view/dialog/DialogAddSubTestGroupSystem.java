package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;

import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.dialog.DialogAdd;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

public class DialogAddSubTestGroupSystem extends DialogAdd<TestGroupSystem> {

	private final UserInputTextInput name;
	private final UserInputComparableInput<BigDecimal> weight;
	private final TestGroupSystem parent;

	public DialogAddSubTestGroupSystem(TestGroupSystem parent) {
		super(Controller.translate("dialog_add_subTestGroupSystem_title"));
		this.parent = parent;

		this.name = new UserInputTextInput(new TextFieldPromptText(Controller.translate("testGroups_testGroupSystem")));
		super.addInput(this.name, Controller.translate("testGroups_testGroupSystem"));

		BigDecimalConverter weightConverter = new BigDecimalConverter();
		weightConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.weight = new UserInputComparableInput<>(new ComparableField<>(BigDecimal.ZERO, null, weightConverter),
				BigDecimal.ONE, false);
		super.addInput(this.weight, Controller.translate("testGroup_weight"));
	}

	@Override
	public TestGroupSystem create() {
		TestGroupSystem testGroupSystem = TestGroupSystem.createSubSystem(this.name.getValue(), this.weight.getValue());
		this.parent.addSubgroup(testGroupSystem);
		return testGroupSystem;
	}

}
