package org.openjfx.gradefx.view.converter;

import org.openjfx.gradefx.model.Grade;
import org.openjfx.gradefx.model.Grade.Tendency;
import org.openjfx.gradefx.model.GradeSystem;

import javafx.util.StringConverter;

public class GradeConverter extends StringConverter<Grade> {
	
	private final GradeSystem gradeSystem;

	public GradeConverter(GradeSystem gradeSystem) {
		this.gradeSystem = gradeSystem;
	}

	@Override
	public String toString(Grade object) {
		if (this.gradeSystem.useTendencies()) {
			return object.getDisplayedValue() + object.getTendency().toString();
		} else {
			return object.getDisplayedValue();
		}
	}

	@Override
	public Grade fromString(String string) {
		if (this.gradeSystem.useTendencies()) {
			for (Tendency tendency : Tendency.values()) {
				int index = string.lastIndexOf(tendency.toString());
				if (index != -1 && tendency.toString().length() > 0) {
					if (index + tendency.toString().length() == string.length()) {
						return this.gradeSystem.getGrade(string.substring(0, index), tendency);
					} else {
						throw new IllegalArgumentException(
								"Can't parse grades with characaters after tendency indicator");
					}
				}
			}
			return this.gradeSystem.getGrade(string, Tendency.NEUTRAL);
		} else {
			return this.gradeSystem.getGrade(string, Tendency.NEUTRAL);
		}
	}

}
