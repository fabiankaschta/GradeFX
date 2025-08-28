package gradefx.view.converter;

import java.text.DecimalFormat;

import kafx.lang.Translator;
import gradefx.model.Test.TestTask;
import javafx.util.StringConverter;

public class TestTaskConverter extends StringConverter<TestTask> {

	private final DecimalFormat decimalFormat = new DecimalFormat();

	public TestTaskConverter() {
		this.decimalFormat.setParseBigDecimal(true);
	}

	@Override
	public String toString(TestTask object) {
		if (object.isRoot()) {
			return Translator.get("test_tasks");
		} else {
			if (object.isLeaf()) {
				return object.getName() + " (" + Translator.get("testTask_maxPoints") + ": "
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
