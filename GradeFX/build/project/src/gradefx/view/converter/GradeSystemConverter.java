package gradefx.view.converter;

import kafx.lang.Translator;
import gradefx.model.GradeSystem;
import javafx.util.StringConverter;

public class GradeSystemConverter extends StringConverter<GradeSystem> {

	@Override
	public String toString(GradeSystem gs) {
		if (gs == null) {
			return "null";
		}
		return Translator.get("gradeSystem_" + gs.name());
	}

	@Override
	public GradeSystem fromString(String string) {
		throw new UnsupportedOperationException();
	}

}
