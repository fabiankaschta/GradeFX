package gradefx.view.pane;

import java.math.BigDecimal;

import kafx.lang.Translator;
import gradefx.model.Group;
import gradefx.model.Test;
import gradefx.view.style.Styles;
import javafx.geometry.Insets;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import kafx.view.control.ComparableField;
import kafx.view.control.TextFieldPromptText;
import kafx.view.converter.BigDecimalConverter;
import kafx.view.dialog.userinput.UserInputComparableInput;
import kafx.view.dialog.userinput.UserInputDatePicker;
import kafx.view.dialog.userinput.UserInputTextInput;

public class TestHeaderPane extends HBox {

	private final UserInputTextInput name;
	private final UserInputDatePicker date;
	private final UserInputComparableInput<BigDecimal> weight;
	private final UserInputComparableInput<BigDecimal> totalPoints;
//	private final UserInputChoiceBoxTreeItem<TestGroup> testGroupTree;

	public TestHeaderPane(Group group, Test test) {
		super(10);
		this.setPadding(new Insets(10));

		this.name = new UserInputTextInput(new TextFieldPromptText(Translator.get("test_name")), test.getName());
		test.nameProperty().bind(this.name.valueProperty());
		this.getChildren().add(this.name);

		this.date = new UserInputDatePicker(new DatePicker(), test.getDate());
		test.dateProperty().bind(this.date.valueProperty());
		this.getChildren().add(this.date);

		BigDecimalConverter weightConverter = new BigDecimalConverter();
		weightConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.weight = new UserInputComparableInput<>(new ComparableField<>(BigDecimal.ZERO, null, weightConverter),
				test.getWeight(), false);
		test.weightProperty().bind(this.weight.valueProperty());
		this.getChildren().add(this.weight);

		// FIXME this should only be editable if no tasks are used
		// FIXME if no points are used, this should be invisible
		BigDecimalConverter totalPointsConverter = new BigDecimalConverter();
		totalPointsConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.totalPoints = new UserInputComparableInput<>(
				new ComparableField<>(BigDecimal.ZERO, null, totalPointsConverter), test.getTotalPoints(), false);
//		test.totalPointsProperty().bind(this.totalPoints.valueProperty());
		this.getChildren().add(this.totalPoints);

//		this.testGroupTree = new UserInputChoiceBoxTreeItem<>(
//				new ChoiceBoxTreeItem<TestGroup>(group.getTestGroupRoot(), new TestGroupConverter()),
//				group.getTestGroup(test));
//		super.addInput(this.testGroupTree, Translator.get("test_testGroupTree"));
//		this.testGroupTree.visibleProperty().bind(group.getTestGroupRoot().leafProperty().not());

		Styles.subscribeBackgroundColor(this, group.colorProperty());
	}

}
