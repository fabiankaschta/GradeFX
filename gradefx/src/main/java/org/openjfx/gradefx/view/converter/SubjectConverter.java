package org.openjfx.gradefx.view.converter;

import org.openjfx.gradefx.model.Subject;

import javafx.util.StringConverter;

public class SubjectConverter extends StringConverter<Subject> {

	@Override
	public String toString(Subject s) {
		if (s == null) {
			return "";
		}
		return s.toString();
	}

	@Override
	public Subject fromString(String string) {
		throw new UnsupportedOperationException();
	}

}
