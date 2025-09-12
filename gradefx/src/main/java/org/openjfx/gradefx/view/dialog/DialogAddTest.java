package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.gradefx.view.converter.TestGroupConverter;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.control.ChoiceBoxTreeItem;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.dialog.DialogAdd;
import org.openjfx.kafx.view.dialog.userinput.UserInputCheckBox;
import org.openjfx.kafx.view.dialog.userinput.UserInputChoiceBoxTreeItem;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputDatePicker;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

import javafx.beans.binding.Bindings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;

public class DialogAddTest extends DialogAdd<Test> {

	private final Group group;
	private final UserInputTextInput name;
	private final UserInputTextInput shortName;
	private final UserInputDatePicker date;
	private final UserInputComparableInput<BigDecimal> weight;
	private final UserInputCheckBox useTasks;
	private final UserInputCheckBox usePoints;
	private final UserInputComparableInput<BigDecimal> totalPoints;
	private final UserInputChoiceBoxTreeItem<TestGroup> testGroupTree;

	public DialogAddTest(Group group) {
		super(TranslationController.translate("dialog_add_test_title"));
		this.group = group;

		this.name = new UserInputTextInput(new TextFieldPromptText(TranslationController.translate("test_name")));
		super.addInput(this.name, TranslationController.translate("test_name"));

		this.shortName = new UserInputTextInput(new TextFieldPromptText(TranslationController.translate("test_shortName")));
		super.addInput(this.shortName, TranslationController.translate("test_shortName"));

		this.date = new UserInputDatePicker(new DatePicker());
		super.addInput(date, TranslationController.translate("test_date"));

		BigDecimalConverter weightConverter = new BigDecimalConverter();
		weightConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.weight = new UserInputComparableInput<>(new ComparableField<>(BigDecimal.ZERO, null, weightConverter),
				BigDecimal.ONE, false);
		super.addInput(this.weight, TranslationController.translate("test_weight"));

		CheckBox useTasksCheckBox = new CheckBox();
		this.useTasks = new UserInputCheckBox(useTasksCheckBox, true);
		super.addInput(this.useTasks, TranslationController.translate("test_useTasks"));

		CheckBox usePointsCheckBox = new CheckBox();
		this.usePoints = new UserInputCheckBox(usePointsCheckBox, true);
		super.addInput(this.usePoints, TranslationController.translate("test_usePoints"));

		BigDecimalConverter totalPointsConverter = new BigDecimalConverter();
		totalPointsConverter.getDecimalFormat().setMaximumFractionDigits(0);
		this.totalPoints = new UserInputComparableInput<>(
				new ComparableField<>(BigDecimal.ONE, null, totalPointsConverter), BigDecimal.ONE, false);
		super.addInput(this.totalPoints, TranslationController.translate("test_totalPoints"));

		this.totalPoints.visibleProperty()
				.bind(Bindings.createBooleanBinding(
						() -> !useTasksCheckBox.isSelected() && usePointsCheckBox.isSelected(),
						useTasksCheckBox.selectedProperty(), usePointsCheckBox.selectedProperty()));
		useTasksCheckBox.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			if (usePointsCheckBox.isSelected()) {
				return false;
			} else {
				useTasksCheckBox.setSelected(false);
				return true;
			}
		}, usePointsCheckBox.selectedProperty()));
		usePointsCheckBox.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			if (useTasksCheckBox.isSelected()) {
				usePointsCheckBox.setSelected(true);
				return true;
			} else {
				return false;
			}
		}, useTasksCheckBox.selectedProperty()));

		this.testGroupTree = new UserInputChoiceBoxTreeItem<>(
				new ChoiceBoxTreeItem<TestGroup>(group.getTestGroupRoot(), new TestGroupConverter()));
		super.addInput(this.testGroupTree, TranslationController.translate("test_testGroupTree"));
		this.testGroupTree.visibleProperty().bind(group.getTestGroupRoot().leafProperty().not());
	}

	@Override
	public Test create() {
		BigDecimal totalPoints = BigDecimal.ZERO;
		if (this.usePoints.getValue() && !this.useTasks.getValue()) {
			totalPoints = this.totalPoints.getValue();
		}
		Test test = new Test(this.group, this.name.getValue().trim(), this.shortName.getValue().trim(),
				this.date.getValue(), this.weight.getValue(), totalPoints, this.useTasks.getValue(),
				this.usePoints.getValue());
		if (this.group.getTestGroupRoot().isLeaf()) {
			this.group.addTest(test, this.group.getTestGroupRoot());
		} else {
			this.group.addTest(test, (TestGroup) this.testGroupTree.getValue());
		}
		if (this.useTasks.getValue()) {
			new DialogEditTestTasks(test).showAndWait();
		}
		return test;
	}

}
