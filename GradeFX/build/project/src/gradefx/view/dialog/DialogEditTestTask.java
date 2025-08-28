package gradefx.view.dialog;

import java.math.BigDecimal;

import kafx.lang.Translator;
import gradefx.model.Test.TestTask;
import kafx.view.control.ComparableField;
import kafx.view.control.TextFieldPromptText;
import kafx.view.converter.BigDecimalConverter;
import kafx.view.dialog.DialogEdit;
import kafx.view.dialog.userinput.UserInputComparableInput;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogEditTestTask extends DialogEdit<TestTask> {

	private final UserInputTextInput name;
	private final UserInputComparableInput<BigDecimal> maxPoints;

	public DialogEditTestTask(TestTask testTask) {
		super(Translator.get("dialog_edit_testTask_title"), testTask);

		this.name = new UserInputTextInput(new TextFieldPromptText(Translator.get("testTask_name")),
				testTask.getName());
		super.addInput(this.name, Translator.get("testTask_name"));

		BigDecimalConverter maxPointsConverter = new BigDecimalConverter();
		maxPointsConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.maxPoints = new UserInputComparableInput<>(
				new ComparableField<>(BigDecimal.ZERO, null, maxPointsConverter), testTask.getMaxPoints(), true);
		super.addInput(this.maxPoints, Translator.get("testTask_maxPoints"));
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
