package org.openjfx.gradefx.view.converter;

import org.openjfx.gradefx.model.GradeSystem;

import javafx.util.StringConverter;

public class GradeSystemConverter extends StringConverter<GradeSystem> {

	@Override
	public String toString(GradeSystem gs) {
		if (gs == null) {
			return "null";
		}
		return gs.getName();
	}

	@Override
	public GradeSystem fromString(String string) {
		throw new UnsupportedOperationException();
	}

}
