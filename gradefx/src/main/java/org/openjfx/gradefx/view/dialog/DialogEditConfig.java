package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;

import org.openjfx.kafx.controller.AutoSaveController;
import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.dialog.DialogUserInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputCheckBox;
import org.openjfx.kafx.view.dialog.userinput.UserInputColorPicker;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputSpinner;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;
import org.openjfx.kafx.view.style.Styles;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class DialogEditConfig extends DialogUserInput<Boolean> {

	private final UserInputSpinner<Integer> fontSize;
	private final UserInputCheckBox useHalfPoints;
	private final UserInputTextInput teacherName;
	private final UserInputCheckBox useAutoSave;
	private final UserInputSpinner<Integer> autoSaveInterval;
	private final UserInputComparableInput<BigDecimal> tendencyBound;
	private final UserInputColorPicker groupColor;

	public DialogEditConfig() {
		super(TranslationController.translate("dialog_edit_config_title"));

		this.fontSize = new UserInputSpinner<>(new Spinner<>(1, 100, FontSizeController.getFontSize()),
				FontSizeController.getFontSize());
		this.fontSize.valueProperty().subscribe(fontSize -> FontSizeController.setFontSize(fontSize));
		super.addInput(this.fontSize, TranslationController.translate("configOption_fontSize"));

		this.teacherName = new UserInputTextInput(new TextField(), ConfigController.get("TEACHER_NAME"), true);
		this.teacherName.valueProperty().subscribe(teacherName -> ConfigController.set("TEACHER_NAME", teacherName));
		super.addInput(this.teacherName, TranslationController.translate("configOption_teacherName"));

		this.useAutoSave = new UserInputCheckBox(new CheckBox(), AutoSaveController.isActive());
		this.useAutoSave.valueProperty().subscribe(useAutoSave -> AutoSaveController.setActive(useAutoSave));
		super.addInput(this.useAutoSave, TranslationController.translate("configOption_useAutoSave"));

		this.autoSaveInterval = new UserInputSpinner<>(
				new Spinner<>(1, 30, (int) AutoSaveController.getInterval().toMinutes()),
				(int) AutoSaveController.getInterval().toMinutes());
		this.autoSaveInterval.valueProperty()
				.subscribe(autoSaveInterval -> AutoSaveController.setInterval(Duration.minutes(autoSaveInterval)));
		this.autoSaveInterval.visibleProperty().bind(this.useAutoSave.valueProperty());
		super.addInput(this.autoSaveInterval, TranslationController.translate("configOption_autoSaveInterval"));

		this.useHalfPoints = new UserInputCheckBox(new CheckBox(),
				Boolean.valueOf(ConfigController.get("USE_HALF_POINTS")));
		this.useHalfPoints.valueProperty()
				.subscribe(useHalfPoints -> ConfigController.set("USE_HALF_POINTS", String.valueOf(useHalfPoints)));
		super.addInput(this.useHalfPoints, TranslationController.translate("configOption_useHalfPoints"));

		this.tendencyBound = new UserInputComparableInput<>(
				new ComparableField<BigDecimal>(BigDecimal.ZERO, null, new BigDecimalConverter(), true),
				ConfigController.get("TENDENCY_BOUND").equals("null") ? null
						: BigDecimal.valueOf(Double.valueOf(ConfigController.get("TENDENCY_BOUND"))));
		this.tendencyBound.valueProperty()
				.subscribe(tendencyBound -> ConfigController.set("TENDENCY_BOUND", String.valueOf(tendencyBound)));
		super.addInput(this.tendencyBound, TranslationController.translate("configOption_tendencyBound"));

		this.groupColor = new UserInputColorPicker(new ColorPicker(),
				Color.web(ConfigController.get("DEFAULT_GROUP_COLOR")), false);
		this.groupColor.valueProperty()
				.subscribe(groupColor -> ConfigController.set("DEFAULT_GROUP_COLOR", Styles.toHexString(groupColor)));
		super.addInput(this.groupColor, TranslationController.translate("configOption_groupColor"));

		ButtonType doneButtonType = new ButtonType(TranslationController.translate("dialog_button_done"),
				ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(doneButtonType);

		this.setResultConverter(_ -> true);
	}

}
