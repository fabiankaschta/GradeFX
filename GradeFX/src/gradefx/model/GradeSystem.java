package gradefx.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import gradefx.model.PointsSystem.BoundType;
import javafx.beans.value.ObservableValue;
import javafx.util.StringConverter;
import kafx.controller.Controller;

public enum GradeSystem {

	ONE_TO_SIX, FIFTEEN_POINTS;

	public Grade calculateGrade(BigDecimal avg) {
		if (avg == null) {
			return null;
		}
		switch (this) {
		case ONE_TO_SIX:
			return getGrade(avg.setScale(0, RoundingMode.HALF_DOWN).intValue());
		case FIFTEEN_POINTS:
			if (avg.compareTo(BigDecimal.ONE) < 0) {
				return getGrade(0);
			} else {
				return getGrade(avg.setScale(0, RoundingMode.HALF_UP).intValue());
			}
		}
		throw new IllegalArgumentException("Error using grade system.");
	}

	public BigDecimal calculateAverage(BigDecimal... values) {
		if (values.length == 0) {
			return null;
		}
		BigDecimal sum = BigDecimal.ZERO;
		for (BigDecimal v : values) {
			sum = sum.add(v);
		}
		BigDecimal avg = sum.divide(new BigDecimal(values.length), MathContext.DECIMAL64);
		return avg.setScale(2, RoundingMode.FLOOR);
	}

	public BigDecimal calculateAverage(BigDecimal[] values, BigDecimal[] weights) {
		if (values.length == 0) {
			return null;
		}
		if (values == null || weights == null || values.length != weights.length) {
			throw new IllegalArgumentException("Error calculationg average.");
		}
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal divisor = BigDecimal.ZERO;
		for (int i = 0; i < values.length; i++) {
			sum = sum.add(values[i].multiply(weights[i]));
			divisor = divisor.add(weights[i]);
		}
		BigDecimal avg = sum.divide(divisor, MathContext.DECIMAL64);
		return avg.setScale(2, RoundingMode.FLOOR);
	}

	public static GradeSystem getDefault() {
		return ONE_TO_SIX;
	}

	/**
	 * in ascending order, without tendencies
	 * 
	 * @return
	 */
	public Grade[] getPossibleGradesASC() {
		switch (this) {
		case ONE_TO_SIX:
			return Arrays.asList(6, 5, 4, 3, 2, 1).stream().map(x -> getGrade(x)).toArray(n -> new Grade[n]);
		case FIFTEEN_POINTS:
			return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).stream().map(x -> getGrade(x))
					.toArray(n -> new Grade[n]);
		}
		throw new IllegalArgumentException("Error using grade system.");
	}

	public Grade[] getPossibleGradesASCTendencies() {
		switch (this) {
		case ONE_TO_SIX:
			return Arrays.asList(6, 5, 4, 3, 2, 1).stream()
					.mapMulti((BiConsumer<Integer, Consumer<Grade>>) (x, consumer) -> {
						for (Tendency tendency : Tendency.values()) {
							consumer.accept(getGrade(x, tendency));
						}
					}).toArray(n -> new Grade[n]);
		case FIFTEEN_POINTS:
			return getPossibleGradesASC();
		}
		throw new IllegalArgumentException("Error using grade system.");
	}

	/**
	 * in descending order, without tendencies
	 * 
	 * @return
	 */
	public Grade[] getPossibleGradesDESC() {
		switch (this) {
		case ONE_TO_SIX:
			return Arrays.asList(1, 2, 3, 4, 5, 6).stream().map(x -> getGrade(x)).toArray(n -> new Grade[n]);
		case FIFTEEN_POINTS:
			return Arrays.asList(15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0).stream().map(x -> getGrade(x))
					.toArray(n -> new Grade[n]);
		}
		throw new IllegalArgumentException("Error using grade system.");
	}

	public Grade[] getPossibleGradesDESCTendencies() {
		switch (this) {
		case ONE_TO_SIX:
			return Arrays.asList(1, 2, 3, 4, 5, 6).stream()
					.mapMulti((BiConsumer<Integer, Consumer<Grade>>) (x, consumer) -> {
						for (Tendency tendency : Tendency.valuesReversed()) {
							consumer.accept(getGrade(x, tendency));
						}
					}).toArray(n -> new Grade[n]);
		case FIFTEEN_POINTS:
			return getPossibleGradesDESC();
		}
		throw new IllegalArgumentException("Error using grade system.");
	}

	public BigDecimal[] getDefaultRatioBounds() {
		switch (this) {
		case ONE_TO_SIX:
			return new BigDecimal[] { null, null, BigDecimal.valueOf(0.4), null, null, null };
		case FIFTEEN_POINTS:
			return new BigDecimal[] { null, BigDecimal.valueOf(0.2), null, null, BigDecimal.valueOf(0.4), null, null,
					null, null, null, null, null, null, null, null, null };
		}
		throw new IllegalArgumentException("Error using grade system.");
	}

	public PointsSystem getDefaultPointsSystem(ObservableValue<BigDecimal> totalPoints) {
		return new PointsSystem(totalPoints,
				Controller.getConfigOption("TENDENCY_BOUND").equals("null") ? null
						: BigDecimal.valueOf(Double.valueOf(Controller.getConfigOption("TENDENCY_BOUND"))),
				BoundType.MOREOREQUAL_THAN, Boolean.valueOf(Controller.getConfigOption("USE_HALF_POINTS")), this);
	}

	public boolean useTendencies() {
		switch (this) {
		case ONE_TO_SIX:
			return true;
		case FIFTEEN_POINTS:
			return false;
		}
		throw new IllegalArgumentException("Error using grade system.");
	}

	private boolean moreIsLess() {
		switch (this) {
		case ONE_TO_SIX:
			return true;
		case FIFTEEN_POINTS:
			return false;
		}
		throw new IllegalArgumentException("Error using grade system.");
	}

	public Grade getWorst() {
		return getPossibleGradesASCTendencies()[0];
	}

	public Grade getBest() {
		return getPossibleGradesDESCTendencies()[0];
	}

	private final Set<Grade> grades = new HashSet<>();

	private Grade getOrAdd(Grade tmp) {
		for (Grade grade : grades) {
			if (grade.equals(tmp)) {
				return grade;
			}
		}
		if (tmp.getNumericalValue() == null) {
			throw new IllegalArgumentException("Can't add grade without numerical value");
		} else {
			grades.add(tmp);
			return tmp;
		}
	}

	public Grade getGrade(Integer numericalValue) {
		return getOrAdd(new Grade(numericalValue, "" + numericalValue));
	}

	public Grade getGrade(Integer numericalValue, Tendency tendency) {
		return getOrAdd(new Grade(numericalValue, "" + numericalValue, tendency));
	}

	public Grade getGrade(Integer numericalValue, String displayedValue) {
		return getOrAdd(new Grade(numericalValue, displayedValue));
	}

	public Grade getGrade(Integer numericalValue, String displayedValue, Tendency tendency) {
		return getOrAdd(new Grade(numericalValue, displayedValue, tendency));
	}

	private Grade getGrade(String displayedValue, Tendency tendency) {
		return getOrAdd(new Grade(null, displayedValue, tendency));
	}

	public enum Tendency implements Comparable<Tendency> {
		// natural order for compare
		NEGATIVE, NONE, POSITIVE;

		@Override
		public String toString() {
			switch (this) {
			case NEGATIVE:
				return "-";
			case NONE:
				return "";
			case POSITIVE:
				return "+";
			}
			return super.toString();
		}

		private static Tendency[] valuesReversed() {
			return new Tendency[] { POSITIVE, NONE, NEGATIVE };
		}

	}

	private final StringConverter<Grade> converter = new StringConverter<>() {

		@Override
		public String toString(Grade object) {
			if (useTendencies()) {
				return object.displayedValue + object.tendency.toString();
			} else {
				return object.displayedValue;
			}
		}

		@Override
		public Grade fromString(String string) {
			if (useTendencies()) {
				for (Tendency tendency : Tendency.values()) {
					int index = string.lastIndexOf(tendency.toString());
					if (index != -1 && tendency.toString().length() > 0) {
						if (index + tendency.toString().length() == string.length()) {
							return getGrade(string.substring(0, index), tendency);
						} else {
							throw new IllegalArgumentException(
									"Can't parse grades with characaters after tendency indicator");
						}
					}
				}
				return getGrade(string, Tendency.NONE);
			} else {
				return getGrade(string, Tendency.NONE);
			}
		}

	};

	public StringConverter<Grade> getGradeConverter() {
		return this.converter;
	}

	public class Grade implements Comparable<Grade>, Serializable {

		private static final long serialVersionUID = 4060606045262948871L;

		private Grade(Integer numericalValue, String displayedValue) {
			this(numericalValue, displayedValue, Tendency.NONE);
		}

		private Grade(Integer numericalValue, String displayedValue, Tendency tendency) {
			this.numericalValue = numericalValue;
			this.displayedValue = displayedValue;
			this.tendency = tendency;
		}

		private final Integer numericalValue;
		private final String displayedValue;
		private final Tendency tendency;

		public Integer getNumericalValue() {
			return this.numericalValue;
		}

		public Tendency getTendency() {
			return this.tendency;
		}

		public Grade setTendency(Tendency tendency) {
			return getGrade(this.numericalValue, this.displayedValue, tendency);
		}

		@Override
		public String toString() {
			return converter.toString(this);
		}

		@Override
		public int hashCode() {
			return displayedValue.hashCode() ^ tendency.hashCode();
		}

		@Override
		public int compareTo(Grade o) {
			int compare = this.numericalValue.compareTo(o.numericalValue);
			if (compare == 0) {
				return this.tendency.compareTo(o.tendency);
			} else if (moreIsLess()) {
				return compare * (-1);
			} else {
				return compare;
			}
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

}
