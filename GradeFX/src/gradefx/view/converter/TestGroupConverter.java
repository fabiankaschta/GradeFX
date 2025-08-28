package gradefx.view.converter;

import java.text.DecimalFormat;

import kafx.lang.Translator;
import gradefx.model.TestGroup;
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
		return testGroup.isRoot() ? '(' + Translator.get("testGroup_none") + ')'
				: testGroup.getName() + " (" + Translator.get("testGroup_weight") + ": "
						+ this.decimalFormat.format(testGroup.getWeight()) + ')';
	}

	@Override
	public TreeItem<TestGroup> fromString(String string) {
		throw new UnsupportedOperationException("not allowed to convert back");
	}

}
