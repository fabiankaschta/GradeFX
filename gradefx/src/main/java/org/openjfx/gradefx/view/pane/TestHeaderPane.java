package org.openjfx.gradefx.view.pane;

import java.math.BigDecimal;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.view.style.Styles;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputDatePicker;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

import javafx.geometry.Insets;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class TestHeaderPane extends HBox {

	private final UserInputTextInput name;
	private final UserInputDatePicker date;
	private final UserInputComparableInput<BigDecimal> weight;
	private final UserInputComparableInput<BigDecimal> totalPoints;
//	private final UserInputChoiceBoxTreeItem<TestGroup> testGroupTree;

	public TestHeaderPane(Group group, Test test) {
		super(10);
		this.setPadding(new Insets(10));

		GridPane left = new GridPane(10, 0);

		this.name = new UserInputTextInput(new TextFieldPromptText(TranslationController.translate("test_name")), test.getName());
		test.nameProperty().bindBidirectional(this.name.valueProperty());
		this.name.setMinWidth(USE_PREF_SIZE);
		this.name.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(12));
		this.name.focusedProperty().subscribe(focused -> {
			if (!focused) {
				this.requestLayout();
			}
		});
		Label nameLabel = new Label(TranslationController.translate("test_name"));
		nameLabel.setMinWidth(USE_PREF_SIZE);
		left.add(nameLabel, 0, 0);
		left.add(this.name, 1, 0);

		this.date = new UserInputDatePicker(new DatePicker(), test.getDate());
		this.date.setMinWidth(USE_PREF_SIZE);
		this.date.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(12));
		test.dateProperty().bindBidirectional(this.date.valueProperty());
		Label dateLabel = new Label(TranslationController.translate("test_date"));
		dateLabel.setMinWidth(USE_PREF_SIZE);
		left.add(dateLabel, 0, 1);
		left.add(this.date, 1, 1);
		this.getChildren().add(left);

		GridPane right = new GridPane(10, 0);

		BigDecimalConverter totalPointsConverter = new BigDecimalConverter();
		totalPointsConverter.getDecimalFormat().setMaximumFractionDigits(2);
		ComparableField<BigDecimal> totalPointsField = new ComparableField<>(BigDecimal.ZERO, null,
				totalPointsConverter);
		this.totalPoints = new UserInputComparableInput<>(totalPointsField, test.getTotalPoints(), false);
		this.totalPoints.setMinWidth(USE_PREF_SIZE);
		this.totalPoints.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(3));
		totalPointsField.disableProperty().bind(test.useTasksProperty());
		this.totalPoints.valueProperty().subscribe(v -> {
			if (test.getUsePoints() && !test.getUseTasks()) {
				test.setTotalPoints(v);
			}
		});
		this.totalPoints.visibleProperty().bind(test.usePointsProperty());
		test.totalPointsProperty().subscribe(v -> this.totalPoints.setValue(v));
		Label totalPointsLabel = new Label(TranslationController.translate("test_totalPoints"));
		totalPointsLabel.setMinWidth(USE_PREF_SIZE);
		right.add(totalPointsLabel, 0, 0);
		right.add(this.totalPoints, 1, 0);

		BigDecimalConverter weightConverter = new BigDecimalConverter();
		weightConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.weight = new UserInputComparableInput<>(new ComparableField<>(BigDecimal.ZERO, null, weightConverter),
				test.getWeight(), false);
		this.weight.setMinWidth(USE_PREF_SIZE);
		this.weight.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(3));
		test.weightProperty().bindBidirectional(this.weight.valueProperty());
		Label weightPointsLabel = new Label(TranslationController.translate("test_weight"));
		weightPointsLabel.setMinWidth(USE_PREF_SIZE);
		right.add(weightPointsLabel, 0, 1);
		right.add(this.weight, 1, 1);
		this.getChildren().add(right);

		Styles.subscribeBackgroundColor(this, group.colorProperty());
	}

}
