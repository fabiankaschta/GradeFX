package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;

import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.dialog.DialogEdit;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

public class DialogEditTestTask extends DialogEdit<TestTask> {

	private final UserInputTextInput name;
	private final UserInputComparableInput<BigDecimal> maxPoints;

	public DialogEditTestTask(TestTask testTask) {
		super(Controller.translate("dialog_edit_testTask_title"), testTask);

		this.name = new UserInputTextInput(new TextFieldPromptText(Controller.translate("testTask_name")),
				testTask.getName());
		super.addInput(this.name, Controller.translate("testTask_name"));

		BigDecimalConverter maxPointsConverter = new BigDecimalConverter();
		maxPointsConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.maxPoints = new UserInputComparableInput<>(
				new ComparableField<>(BigDecimal.ZERO, null, maxPointsConverter), testTask.getMaxPoints(), true);
		super.addInput(this.maxPoints, Controller.translate("testTask_maxPoints"));
	}

	@Override
	public boolean edit(TestTask testTask) {
		String name = this.name.getValue().trim();
		BigDecimal maxPoints = this.maxPoints.getValue();
		if (name != null && name.length() > 0 && !name.equals(testTask.getName())) {
			testTask.setName(name);
		}
		if (!maxPoints.equals(testTask.getMaxPoints())) {
			testTask.setMaxPoints(maxPoints);
		}
		return true;
	}

}
