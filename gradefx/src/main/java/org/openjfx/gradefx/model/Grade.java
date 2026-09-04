package org.openjfx.gradefx.model;

import java.io.Serializable;

public class Grade implements Serializable {

	private static final long serialVersionUID = 4060606045262948871L;

	public enum Tendency implements Comparable<Tendency> {
		// natural order for compare
		NEGATIVE, NEUTRAL, POSITIVE;

		@Override
		public String toString() {
			switch (this) {
			case NEGATIVE:
				return "-";
			case NEUTRAL:
				return "";
			case POSITIVE:
				return "+";
			}
			return super.toString();
		}

	}

	public static Grade forGradeSystem(GradeSystem gradeSystem, Integer numericalValue, String displayedValue,
			Tendency tendency) {
		Grade grade = gradeSystem.getGrade(numericalValue, displayedValue, tendency);
		if (grade == null) {
			grade = new Grade(numericalValue, displayedValue, tendency);
		}
		return grade;
	}

	private final Integer numericalValue;
	private final String displayedValue;
	private final Tendency tendency;

	private Grade(Integer numericalValue, String displayedValue, Tendency tendency) {
		this.numericalValue = numericalValue;
		this.displayedValue = displayedValue;
		this.tendency = tendency;
	}

	public Integer getNumericalValue() {
		return this.numericalValue;
	}

	public String getDisplayedValue() {
		return this.displayedValue;
	}

	public Tendency getTendency() {
		return this.tendency;
	}

	@Override
	public String toString() {
		return this.displayedValue + this.tendency.toString();
	}

	@Override
	public int hashCode() {
		return this.displayedValue.hashCode() ^ this.tendency.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Grade)) {
			return false;
		} else {
			Grade grade = (Grade) o;
			return this.displayedValue.equals(grade.displayedValue) && this.tendency.equals(grade.tendency);
		}
	}

}
