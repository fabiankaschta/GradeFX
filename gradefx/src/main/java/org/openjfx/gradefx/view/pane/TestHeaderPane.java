package org.openjfx.gradefx.view.pane;

import java.math.BigDecimal;
import java.util.Map.Entry;

import org.controlsfx.control.ToggleSwitch;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputDatePicker;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputToggleSwitch;
import org.openjfx.kafx.view.imageview.EmojiImageView;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

public class TestHeaderPane extends GridPane {

	private final UserInputTextInput name;
	private final UserInputDatePicker date;
	private final UserInputComparableInput<BigDecimal> weight;
	private final UserInputComparableInput<BigDecimal> totalPoints;
	private final UserInputToggleSwitch showReturns;
	private final IntegerProperty returnsMissingAmount = new SimpleIntegerProperty(this, "returnsAmount", 0);
//	private final UserInputChoiceBoxTreeItem<TestGroup> testGroupTree;

	public TestHeaderPane(Group group, Test test) {
		super(10, 0);
		this.setPadding(new Insets(10));

		this.name = new UserInputTextInput(new TextFieldPromptText(TranslationController.translate("test_name")),
				test.getName());
		test.nameProperty().bindBidirectional(this.name.valueProperty());
		this.name.setMinWidth(USE_PREF_SIZE);
		this.name.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(12));
		Label nameLabel = new Label(TranslationController.translate("test_name"));
		nameLabel.setMinWidth(USE_PREF_SIZE);
		this.add(nameLabel, 0, 0);
		this.add(this.name, 1, 0);

		this.date = new UserInputDatePicker(new DatePicker(), test.getDate());
		this.date.setMinWidth(USE_PREF_SIZE);
		this.date.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(12));
		test.dateProperty().bindBidirectional(this.date.valueProperty());
		Label dateLabel = new Label(TranslationController.translate("test_date"));
		dateLabel.setMinWidth(USE_PREF_SIZE);
		this.add(dateLabel, 0, 1);
		this.add(this.date, 1, 1);

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
		this.add(totalPointsLabel, 2, 0);
		this.add(this.totalPoints, 3, 0);

		BigDecimalConverter weightConverter = new BigDecimalConverter();
		weightConverter.getDecimalFormat().setMaximumFractionDigits(2);
		this.weight = new UserInputComparableInput<>(new ComparableField<>(BigDecimal.ZERO, null, weightConverter),
				test.getWeight(), false);
		this.weight.setMinWidth(USE_PREF_SIZE);
		this.weight.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(3));
		test.weightProperty().bindBidirectional(this.weight.valueProperty());
		Label weightPointsLabel = new Label(TranslationController.translate("test_weight"));
		weightPointsLabel.setMinWidth(USE_PREF_SIZE);
		this.add(weightPointsLabel, 2, 1);
		this.add(this.weight, 3, 1);

		this.showReturns = new UserInputToggleSwitch(new ToggleSwitch(), test.getShowReturns());
		test.showReturnsProperty().bindBidirectional(this.showReturns.valueProperty());
		this.showReturns.setMinWidth(USE_PREF_SIZE);
		this.showReturns.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(3));
		Label showReturnsLabel = new Label(TranslationController.translate("test_showReturns"));
		showReturnsLabel.setMinWidth(USE_PREF_SIZE);
		this.add(showReturnsLabel, 5, 0);
		this.add(this.showReturns, 6, 0);

		ColumnConstraints fillerColumn = new ColumnConstraints();
		fillerColumn.setHgrow(Priority.ALWAYS);

		this.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(),
				new ColumnConstraints(), fillerColumn, new ColumnConstraints(), new ColumnConstraints());

		Label returnsMissingLabel = new Label();
		returnsMissingLabel.setMinWidth(USE_PREF_SIZE);
		returnsMissingLabel.visibleProperty().bind(test.showReturnsProperty());
		returnsMissingLabel.textProperty().bind(Bindings.createStringBinding(() -> {
			if (this.returnsMissingAmount.getValue() == 0) {
				return TranslationController.translate("test_returnsMissingNone");
			} else {
				return TranslationController.translate("test_returnsMissing");
			}
		}, this.returnsMissingAmount));
		Label returnsMissing = new Label();
		returnsMissing.setMinWidth(USE_PREF_SIZE);
		returnsMissing.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(3));
		returnsMissing.setAlignment(Pos.CENTER);
		returnsMissing.setTextAlignment(TextAlignment.CENTER);
		EmojiImageView emoji = new EmojiImageView("1F60E");
		emoji.fitWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(2));
		emoji.fitHeightProperty().bind(FontSizeController.fontSizeProperty().multiply(2));
		this.returnsMissingAmount.subscribe(value -> {
			if (value.intValue() == 0) {
				returnsMissing.setGraphic(emoji);
				returnsMissing.setText("");
			} else {
				returnsMissing.setGraphic(null);
				returnsMissing.setText(value.toString());
			}
		});
		returnsMissing.visibleProperty().bind(test.showReturnsProperty());
		this.returnsMissingAmount.setValue(test.getReturns().size());

		final ChangeListener<Boolean> updateReturnsListener = (_, _, newValue) -> {
			if (newValue) {
				this.returnsMissingAmount.setValue(this.returnsMissingAmount.getValue() - 1);
			} else {
				this.returnsMissingAmount.setValue(this.returnsMissingAmount.getValue() + 1);
			}
		};
		// add listeners to all existing returns
		for (Entry<Student, ReadOnlyBooleanWrapper> e : test.getReturns().entrySet()) {
			e.getValue().addListener(updateReturnsListener);
		}
		// update listener if new student / return is entered
		test.getReturns().addListener((MapChangeListener<Student, ReadOnlyBooleanWrapper>) c -> {
			// new mapping
			if (c.getValueRemoved() == null) {
				c.getValueAdded().addListener(updateReturnsListener);
				if (!c.getValueAdded().get()) {
					this.returnsMissingAmount.setValue(this.returnsMissingAmount.getValue() + 1);
				}
			}
			// mapping was removed
			else if (c.getValueAdded() == null) {
				c.getValueRemoved().removeListener(updateReturnsListener);
				if (!c.getValueRemoved().get()) {
					this.returnsMissingAmount.setValue(this.returnsMissingAmount.getValue() - 1);
				}
			}
			// mapping was replaced
			else {
				c.getValueRemoved().removeListener(updateReturnsListener);
				c.getValueAdded().addListener(updateReturnsListener);
			}
		});

		this.add(returnsMissingLabel, 5, 1);
		this.add(returnsMissing, 6, 1);
	}

}
