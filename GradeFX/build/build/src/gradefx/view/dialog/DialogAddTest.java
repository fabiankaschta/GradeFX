package gradefx.view.dialog;

import java.math.BigDecimal;

import kafx.lang.Translator;
import gradefx.model.Group;
import gradefx.model.Test;
import gradefx.model.TestGroup;
import gradefx.view.converter.TestGroupConverter;
import javafx.beans.binding.Bindings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import kafx.view.control.ChoiceBoxTreeItem;
import kafx.view.control.ComparableField;
import kafx.view.control.TextFieldPromptText;
import kafx.view.converter.BigDecimalConverter;
import kafx.view.dialog.DialogAdd;
import kafx.view.dialog.userinput.UserInputCheckBox;
import kafx.view.dialog.userinput.UserInputChoiceBoxTreeItem;
import kafx.view.dialog.userinput.UserInputComparableInput;
import kafx.view.dialog.userinput.UserInputDatePicker;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogAddTest extends DialogAdd<Test> {

	private final Group group;
	private final UserInputTextInput name;
	private final UserInputDatePicker date;
	private final UserInputComparableInput<BigDecimal> weight;
	private final UserInputCheckBox useTasks;
	private final UserInputCheckBox usePoints;
	private final UserInputComparableInput<BigDecimal> totalPoints;
	private final UserInputChoiceBoxTreeItem<TestGroup> testGroupTree;

	public DialogAddTest(Group group) {
		super(Translator.get("dialog_add_test_title"));
		this.group = group;

		this.name = new UserInputTextInput(new TextFieldPromptText(Translator.get("test_name")));
		super.addInput(this.name, Translator.get("test_name"));

		this.date = new UserInputDatePicker(new DatePicker());
		super.addInput(date, Translator.get("test_date"));

		BigDecimalConverter weightConverter = new BigDecimalConverter();
		weightConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.weight = new UserInputComparableInput<>(new ComparableField<>(BigDecimal.ZERO, null, weightConverter),
				BigDecimal.ONE, false);
		super.addInput(this.weight, Translator.get("test_weight"));

		CheckBox useTasksCheckBox = new CheckBox();
		this.useTasks = new UserInputCheckBox(useTasksCheckBox, true);
		super.addInput(this.useTasks, Translator.get("test_useTasks"));

		CheckBox usePointsCheckBox = new CheckBox();
		this.usePoints = new UserInputCheckBox(usePointsCheckBox, true);
		super.addInput(this.usePoints, Translator.get("test_usePoints"));

		BigDecimalConverter totalPointsConverter = new BigDecimalConverter();
		totalPointsConverter.getDecimalFormat().setMaximumFractionDigits(0); // FIXME 0.9 -> 9 ???
		this.totalPoints = new UserInputComparableInput<>(
				new ComparableField<>(BigDecimal.ZERO, null, totalPointsConverter), BigDecimal.ZERO, false);
		super.addInput(this.totalPoints, Translator.get("test_totalPoints"));
		super.setCustomButtonDisableExpression(this.totalPoints, this.totalPoints.visibleProperty().and(this.totalPoints
				.isSelectedExpression().not().or(this.totalPoints.valueProperty().isEqualTo(BigDecimal.ZERO))));

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
		super.addInput(this.testGroupTree, Translator.get("test_testGroupTree"));
		this.testGroupTree.visibleProperty().bind(group.getTestGroupRoot().leafProperty().not());
	}

	@Override
	public Test create() {
		BigDecimal totalPoints = BigDecimal.ZERO;
		if (this.usePoints.getValue() && !this.useTasks.getValue()) {
			totalPoints = this.totalPoints.getValue();
		}
		Test test = new Test(this.group, this.name.getValue().trim(), this.date.getValue(), this.weight.getValue(),
				totalPoints, this.useTasks.getValue(), this.usePoints.getValue());
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
