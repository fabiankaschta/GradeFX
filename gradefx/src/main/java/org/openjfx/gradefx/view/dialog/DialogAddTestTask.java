package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;

import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.dialog.DialogAdd;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

public class DialogAddTestTask extends DialogAdd<TestTask> {

	private final TestTask root;
	private final Test test;
	private final UserInputTextInput name;
	private final UserInputComparableInput<BigDecimal> maxPoints;

	public DialogAddTestTask(Test test, TestTask root) {
		super(Controller.translate("dialog_add_testTask_title"));
		super.showAddMoreButton(true);
		this.root = root;
		this.test = test;

		this.name = new UserInputTextInput(new TextFieldPromptText(Controller.translate("testTask_name")));
		super.addInput(this.name, Controller.translate("testTask_name"));

		BigDecimalConverter maxPointsConverter = new BigDecimalConverter();
		maxPointsConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.maxPoints = new UserInputComparableInput<>(
				new ComparableField<>(BigDecimal.ZERO, null, maxPointsConverter), BigDecimal.ZERO, true);
		super.addInput(this.maxPoints, Controller.translate("testTask_maxPoints"));
	}

	@Override
	public TestTask create() {
		TestTask task = TestTask.createTask(this.test, this.name.getValue().trim(), this.maxPoints.getValue());
		this.root.addSubtask(task);
		return task;
	}

}
