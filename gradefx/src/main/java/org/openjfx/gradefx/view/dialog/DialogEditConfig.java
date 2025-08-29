package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;

import org.openjfx.kafx.controller.Controller;
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
		super(Controller.translate("dialog_edit_config_title"));

		this.fontSize = new UserInputSpinner<>(new Spinner<>(1, 100, Controller.getFontSize()),
				Controller.getFontSize());
		this.fontSize.valueProperty().subscribe(fontSize -> Controller.setFontSize(fontSize));
		super.addInput(this.fontSize, Controller.translate("configOption_fontSize"));

		this.useHalfPoints = new UserInputCheckBox(new CheckBox(),
				Boolean.valueOf(Controller.getConfigOption("USE_HALF_POINTS")));
		this.useHalfPoints.valueProperty().subscribe(
				useHalfPoints -> Controller.setConfigOption("USE_HALF_POINTS", String.valueOf(useHalfPoints)));
		super.addInput(this.useHalfPoints, Controller.translate("configOption_useHalfPoints"));

		this.tendencyBound = new UserInputComparableInput<>(
				new ComparableField<BigDecimal>(BigDecimal.ZERO, null, new BigDecimalConverter(), true),
				Controller.getConfigOption("TENDENCY_BOUND").equals("null") ? null
						: BigDecimal.valueOf(Double.valueOf(Controller.getConfigOption("TENDENCY_BOUND"))));
		this.tendencyBound.valueProperty().subscribe(
				tendencyBound -> Controller.setConfigOption("TENDENCY_BOUND", String.valueOf(tendencyBound)));
		super.addInput(this.tendencyBound, Controller.translate("configOption_tendencyBound"));

		this.groupColor = new UserInputColorPicker(new ColorPicker(),
				Color.web(Controller.getConfigOption("DEFAULT_GROUP_COLOR")), false);
		this.groupColor.valueProperty().subscribe(
				groupColor -> Controller.setConfigOption("DEFAULT_GROUP_COLOR", ColorHelper.toHexString(groupColor)));
		super.addInput(this.groupColor, Controller.translate("configOption_groupColor"));

		ButtonType doneButtonType = new ButtonType(Controller.translate("dialog_button_done"), ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(doneButtonType);

		this.setResultConverter(_ -> true);
	}

}
