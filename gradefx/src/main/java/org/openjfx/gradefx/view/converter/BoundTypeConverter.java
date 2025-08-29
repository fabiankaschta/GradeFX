package org.openjfx.gradefx.view.converter;

import org.openjfx.gradefx.model.PointsSystem.BoundType;

import javafx.util.StringConverter;

public class BoundTypeConverter extends StringConverter<BoundType> {

	@Override
	public String toString(BoundType object) {
		switch (object) {
		case LESSOREQUAL_THAN:
			return "\u2264";
		case LESS_THAN:
			return "<";
		case MOREOREQUAL_THAN:
			return "\u2265";
		case MORE_THAN:
			return ">";
		}
		return "";
	}

	@Override
	public BoundType fromString(String string) {
		throw new UnsupportedOperationException("not allowed to convert back");
	}

}
