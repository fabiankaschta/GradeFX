package org.openjfx.gradefx.view.converter;

import java.text.DecimalFormat;

import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.TreeItem;
import javafx.util.StringConverter;

public class TestGroupConverter extends StringConverter<TreeItem<TestGroup>> {

	private final DecimalFormat decimalFormat = new DecimalFormat();

	public TestGroupConverter() {
		this.decimalFormat.setParseBigDecimal(true);
	}

	@Override
	public String toString(TreeItem<TestGroup> object) {
		TestGroup testGroup = (TestGroup) object;
		return testGroup.isRoot() ? '(' + TranslationController.translate("testGroup_none") + ')'
				: testGroup.getName() + " (" + TranslationController.translate("testGroup_weight") + ": "
						+ this.decimalFormat.format(testGroup.getWeight()) + ')';
	}

	@Override
	public TreeItem<TestGroup> fromString(String string) {
		throw new UnsupportedOperationException("not allowed to convert back");
	}

}
