package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.gradefx.view.converter.TestGroupConverter;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.control.ChoiceBoxTreeItem;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.dialog.DialogEdit;
import org.openjfx.kafx.view.dialog.userinput.UserInputCheckBox;
import org.openjfx.kafx.view.dialog.userinput.UserInputChoiceBoxTreeItem;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputDatePicker;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

import javafx.beans.binding.Bindings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;

public class DialogEditTest extends DialogEdit<Test> {

	private final Group group;
	private final UserInputTextInput name;
	private final UserInputTextInput shortName;
	private final UserInputDatePicker date;
	private final UserInputComparableInput<BigDecimal> weight;
	private final UserInputCheckBox useTasks;
	private final UserInputCheckBox usePoints;
	private final UserInputComparableInput<BigDecimal> totalPoints;
	private final UserInputChoiceBoxTreeItem<TestGroup> testGroupTree;

	public DialogEditTest(Group group, Test test) {
		super(TranslationController.translate("dialog_edit_test_title"), test);
		this.group = group;

		this.name = new UserInputTextInput(new TextFieldPromptText(TranslationController.translate("test_name")), test.getName());
		super.addInput(this.name, TranslationController.translate("test_name"));

		this.shortName = new UserInputTextInput(new TextFieldPromptText(TranslationController.translate("test_shortName")),
				test.getShortName());
		super.addInput(this.shortName, TranslationController.translate("test_shortName"));

		this.date = new UserInputDatePicker(new DatePicker(), test.getDate());
		super.addInput(this.date, TranslationController.translate("test_date"));

		BigDecimalConverter weightConverter = new BigDecimalConverter();
		weightConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.weight = new UserInputComparableInput<>(new ComparableField<>(BigDecimal.ZERO, null, weightConverter),
				test.getWeight(), false);
		super.addInput(this.weight, TranslationController.translate("test_weight"));

		CheckBox useTasksCheckBox = new CheckBox();
		this.useTasks = new UserInputCheckBox(useTasksCheckBox, test.getUseTasks());
		super.addInput(this.useTasks, TranslationController.translate("test_useTasks"));

		CheckBox usePointsCheckBox = new CheckBox();
		this.usePoints = new UserInputCheckBox(usePointsCheckBox, test.getUsePoints());
		super.addInput(this.usePoints, TranslationController.translate("test_usePoints"));

		BigDecimalConverter totalPointsConverter = new BigDecimalConverter();
		totalPointsConverter.getDecimalFormat().setMaximumFractionDigits(0);
		this.totalPoints = new UserInputComparableInput<>(
				new ComparableField<>(BigDecimal.ONE, null, totalPointsConverter),
				test.getUsePoints() ? test.getTotalPoints() : BigDecimal.ONE, false);
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
				new ChoiceBoxTreeItem<TestGroup>(group.getTestGroupRoot(), new TestGroupConverter()),
				group.getTestGroup(test));
		super.addInput(this.testGroupTree, TranslationController.translate("test_testGroupTree"));
		this.testGroupTree.visibleProperty().bind(group.getTestGroupRoot().leafProperty().not());
	}

	@Override
	public boolean edit(Test test) {
		String name = this.name.getValue().trim();
		String shortName = this.shortName.getValue().trim();
		LocalDate date = this.date.getValue();
		BigDecimal weight = this.weight.getValue();
		boolean useTasks = this.useTasks.getValue();
		boolean usePoints = this.usePoints.getValue();
		BigDecimal totalPoints = this.totalPoints.getValue();
		if (name != null && name.length() > 0 && !name.equals(test.getName())) {
			test.setName(name);
		}
		if (shortName != null && shortName.length() > 0 && !shortName.equals(test.getShortName())) {
			test.setShortName(shortName);
		}
		if (date == null && test.getDate() != null || !date.equals(test.getDate())) {
			test.setDate(date);
		}
		if (!weight.equals(test.getWeight())) {
			test.setWeight(weight);
		}
		if (useTasks != test.getUseTasks()) {
			test.setUseTasks(useTasks);
		}
		if (usePoints != test.getUsePoints()) {
			test.setUsePoints(usePoints);
		}
		if (usePoints && !useTasks) {
			test.setTotalPoints(totalPoints);
		}
		if (!this.group.getTestGroupRoot().isLeaf()) {
			TestGroup testGroup = (TestGroup) this.testGroupTree.getValue();
			if (testGroup != this.group.getTestGroup(test)) {
				this.group.setTestGroup(test, testGroup);
			}
		}
		return true;
	}

}
