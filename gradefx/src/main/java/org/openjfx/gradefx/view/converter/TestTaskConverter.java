package org.openjfx.gradefx.view.converter;

import java.text.DecimalFormat;

import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.kafx.controller.TranslationController;

import javafx.util.StringConverter;

public class TestTaskConverter extends StringConverter<TestTask> {

	private final DecimalFormat decimalFormat = new DecimalFormat();

	public TestTaskConverter() {
		this.decimalFormat.setParseBigDecimal(true);
	}

	@Override
	public String toString(TestTask object) {
		if (object.isRoot()) {
			return TranslationController.translate("test_tasks");
		} else {
			if (object.isLeaf()) {
				return object.getName() + " (" + TranslationController.translate("testTask_maxPoints") + ": "
						+ this.decimalFormat.format(object.getMaxPoints()) + ')';
			} else {

				return object.getName();
			}
		}
	}

	@Override
	public TestTask fromString(String string) {
		throw new UnsupportedOperationException("not allowed to convert back");
	}

}
