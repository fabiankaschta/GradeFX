package org.openjfx.gradefx.converter;

import org.openjfx.gradefx.model.Group;

import javafx.util.StringConverter;

public class GroupConverter extends StringConverter<Group> {

	@Override
	public String toString(Group g) {
		if (g == null) {
			return "";
		}
		return g.getName() + ' ' + '(' + g.getSubject().getShortName() + ')';
	}

	@Override
	public Group fromString(String string) {
		throw new UnsupportedOperationException();
	}

}
