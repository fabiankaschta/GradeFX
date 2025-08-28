package gradefx.view.converter;

import gradefx.model.TestGroup.TestGroupSystem;
import javafx.util.StringConverter;

public class TestGroupSystemConverter extends StringConverter<TestGroupSystem> {

	@Override
	public String toString(TestGroupSystem gs) {
		if (gs == null) {
			return "";
		}
		return gs.getName();
	}

	@Override
	public TestGroupSystem fromString(String string) {
		throw new UnsupportedOperationException();
	}

}
