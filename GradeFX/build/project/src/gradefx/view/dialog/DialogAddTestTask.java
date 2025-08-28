package gradefx.view.dialog;

import java.math.BigDecimal;

import kafx.lang.Translator;
import gradefx.model.Test;
import gradefx.model.Test.TestTask;
import kafx.view.control.ComparableField;
import kafx.view.control.TextFieldPromptText;
import kafx.view.converter.BigDecimalConverter;
import kafx.view.dialog.DialogAdd;
import kafx.view.dialog.userinput.UserInputComparableInput;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogAddTestTask extends DialogAdd<TestTask> {

	private final TestTask root;
	private final Test test;
	private final UserInputTextInput name;
	private final UserInputComparableInput<BigDecimal> maxPoints;

	public DialogAddTestTask(Test test, TestTask root) {
		super(Translator.get("dialog_add_testTask_title"));
		super.showAddMoreButton(true);
		this.root = root;
		this.test = test;

		this.name = new UserInputTextInput(new TextFieldPromptText(Translator.get("testTask_name")));
		super.addInput(this.name, Translator.get("testTask_name"));

		BigDecimalConverter maxPointsConverter = new BigDecimalConverter();
		maxPointsConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.maxPoints = new UserInputComparableInput<>(
				new ComparableField<>(BigDecimal.ZERO, null, maxPointsConverter), BigDecimal.ZERO, true);
		super.addInput(this.maxPoints, Translator.get("testTask_maxPoints"));
	}

	@Override
	public TestTask create() {
		TestTask task = TestTask.createTask(this.test, this.name.getValue().trim(), this.maxPoints.getValue());
		this.root.addSubtask(task);
		return task;
	}

}
