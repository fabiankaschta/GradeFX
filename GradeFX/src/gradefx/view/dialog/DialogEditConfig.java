package gradefx.view.dialog;

import java.math.BigDecimal;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.paint.Color;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import kafx.controller.Controller;
import kafx.lang.Translator;
import kafx.view.color.ColorHelper;
import kafx.view.control.ComparableField;
import kafx.view.converter.BigDecimalConverter;
import kafx.view.dialog.DialogUserInput;
import kafx.view.dialog.userinput.UserInputCheckBox;
import kafx.view.dialog.userinput.UserInputColorPicker;
import kafx.view.dialog.userinput.UserInputComparableInput;
import kafx.view.dialog.userinput.UserInputSpinner;

public class DialogEditConfig extends DialogUserInput<Boolean> {

	private final UserInputSpinner<Integer> fontSize;
	private final UserInputCheckBox useHalfPoints;
	private final UserInputComparableInput<BigDecimal> tendencyBound;
	private final UserInputColorPicker groupColor;

	public DialogEditConfig() {
		super(Translator.get("dialog_edit_config_title"));

		this.fontSize = new UserInputSpinner<>(new Spinner<>(1, 100, Controller.getFontSize()),
				Controller.getFontSize());
		this.fontSize.valueProperty().subscribe(fontSize -> Controller.setFontSize(fontSize));
		super.addInput(this.fontSize, Translator.get("configOption_fontSize"));

		this.useHalfPoints = new UserInputCheckBox(new CheckBox(),
				Boolean.valueOf(Controller.getConfigOption("USE_HALF_POINTS")));
		this.useHalfPoints.valueProperty().subscribe(
				useHalfPoints -> Controller.setConfigOption("USE_HALF_POINTS", String.valueOf(useHalfPoints)));
		super.addInput(this.useHalfPoints, Translator.get("configOption_useHalfPoints"));

		this.tendencyBound = new UserInputComparableInput<>(
				new ComparableField<BigDecimal>(BigDecimal.ZERO, null, new BigDecimalConverter(), true),
				Controller.getConfigOption("TENDENCY_BOUND").equals("null") ? null
						: BigDecimal.valueOf(Double.valueOf(Controller.getConfigOption("TENDENCY_BOUND"))));
		this.tendencyBound.valueProperty().subscribe(
				tendencyBound -> Controller.setConfigOption("TENDENCY_BOUND", String.valueOf(tendencyBound)));
		super.addInput(this.tendencyBound, Translator.get("configOption_tendencyBound"));

		this.groupColor = new UserInputColorPicker(new ColorPicker(),
				Color.web(Controller.getConfigOption("DEFAULT_GROUP_COLOR")), false);
		this.groupColor.valueProperty().subscribe(
				groupColor -> Controller.setConfigOption("DEFAULT_GROUP_COLOR", ColorHelper.toHexString(groupColor)));
		super.addInput(this.groupColor, Translator.get("configOption_groupColor"));

		ButtonType doneButtonType = new ButtonType(Translator.get("dialog_button_done"), ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(doneButtonType);

		this.setResultConverter(_ -> true);
	}

}
