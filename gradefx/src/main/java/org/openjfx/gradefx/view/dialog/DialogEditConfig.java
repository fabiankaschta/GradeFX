package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;

import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.color.ColorHelper;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.dialog.DialogUserInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputCheckBox;
import org.openjfx.kafx.view.dialog.userinput.UserInputColorPicker;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputSpinner;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;

public class DialogEditConfig extends DialogUserInput<Boolean> {

	private final UserInputSpinner<Integer> fontSize;
	private final UserInputCheckBox useHalfPoints;
	private final UserInputComparableInput<BigDecimal> tendencyBound;
	private final UserInputColorPicker groupColor;

	public DialogEditConfig() {
		super(TranslationController.translate("dialog_edit_config_title"));

		this.fontSize = new UserInputSpinner<>(new Spinner<>(1, 100, FontSizeController.getFontSize()),
				FontSizeController.getFontSize());
		this.fontSize.valueProperty().subscribe(fontSize -> FontSizeController.setFontSize(fontSize));
		super.addInput(this.fontSize, TranslationController.translate("configOption_fontSize"));

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
		this.groupColor.valueProperty().subscribe(
				groupColor -> ConfigController.set("DEFAULT_GROUP_COLOR", ColorHelper.toHexString(groupColor)));
		super.addInput(this.groupColor, TranslationController.translate("configOption_groupColor"));

		ButtonType doneButtonType = new ButtonType(TranslationController.translate("dialog_button_done"),
				ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(doneButtonType);

		this.setResultConverter(_ -> true);
	}

}
