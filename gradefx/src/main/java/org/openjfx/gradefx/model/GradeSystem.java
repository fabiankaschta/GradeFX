package org.openjfx.gradefx.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openjfx.gradefx.model.Grade.Tendency;
import org.openjfx.gradefx.view.converter.GradeConverter;
import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.io.DataObject;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

public class GradeSystem {

	// used for divisions
	private final static Integer SCALE = 7;
	private final static RoundingMode ROUNDINGMODE = RoundingMode.HALF_UP;

	public static enum GradeSystemBaseType {
		ONE_TO_SIX, FIFTEEN_POINTS
	}

	public static enum Level {
		TOO_GOOD, AMAZING, NICE, GOOD, OKAY, BAD, TOO_BAD
	}

	private final static ObservableList<GradeSystem> gradeSystems = FXCollections.observableArrayList();

	public static ObservableList<GradeSystem> getGradeSystems() {
		return gradeSystems;
	}

	public static void removeGradeSystem(GradeSystem gradeSystem) {
		gradeSystems.remove(gradeSystem);
	}

	public static GradeSystem get(String name) {
		for (GradeSystem gradeSystem : gradeSystems) {
			if (gradeSystem.getName().equals(name)) {
				return gradeSystem;
			}
		}
		return null;
	}

	public static void setDefault() {
		gradeSystems.clear();

		GradeSystem ONE_TO_SIX = new GradeSystem(GradeSystemBaseType.ONE_TO_SIX,
				TranslationController.translate("gradeSystem_ONE_TO_SIX"), true, true, BoundType.MOREOREQUAL_THAN,
				BigDecimal.valueOf(1.8), BoundType.LESS_THAN, BigDecimal.valueOf(4.2), BoundType.MORE_THAN,
				BigDecimal.valueOf(0.4), BoundType.MORE_THAN);
		for (int i = 6; i >= 1; i--) {
			ONE_TO_SIX.addGrade(i, Tendency.NEGATIVE, i > 4);
			ONE_TO_SIX.addGrade(i, Tendency.NEUTRAL, i > 4);
			ONE_TO_SIX.addGrade(i, Tendency.POSITIVE, i > 4);
		}
		ONE_TO_SIX.setDefaultRatioBound(ONE_TO_SIX.getGrade(4), BigDecimal.valueOf(0.4));

		GradeSystem FIFTEEN_POINTS = new GradeSystem(GradeSystemBaseType.FIFTEEN_POINTS,
				TranslationController.translate("gradeSystem_FIFTEEN_POINTS"), false, false, BoundType.MOREOREQUAL_THAN,
				BigDecimal.valueOf(11.6), BoundType.MORE_THAN, BigDecimal.valueOf(4.4), BoundType.LESS_THAN,
				BigDecimal.valueOf(0.4), BoundType.MORE_THAN);
		for (int i = 0; i <= 15; i++) {
			FIFTEEN_POINTS.addGrade(i, i < 4);
		}
		FIFTEEN_POINTS.setRoundingMode(FIFTEEN_POINTS.getGrade(0), RoundingMode.DOWN);
		FIFTEEN_POINTS.setDefaultRatioBound(FIFTEEN_POINTS.getGrade(1), BigDecimal.valueOf(0.2));
		FIFTEEN_POINTS.setDefaultRatioBound(FIFTEEN_POINTS.getGrade(4), BigDecimal.valueOf(0.4));
	}

	public static void clearGradeSystems() {
		gradeSystems.clear();
	}

	public static GradeSystem getDefault() {
		return gradeSystems.getFirst();
	}

	private final String name;
	private final boolean useTendencies;
	private final boolean moreIsWorse;
	private final GradeSystemBaseType baseType;
	private final BoundType defaultBoundType;
	private final BigDecimal criticalGoodAvg;
	private final BoundType criticalGoodAvgMode;
	private final BigDecimal criticalBadAvg;
	private final BoundType criticalBadAvgMode;
	private final BigDecimal criticalGradesRatio;
	private final BoundType criticalGradesMode;
	private final Set<Grade> grades = new HashSet<>();
	private final Map<Grade, BigDecimal> defaultRatioBounds = new HashMap<>();
	private final Map<Grade, RoundingMode> roundingModes = new HashMap<>();

	private GradeSystem(GradeSystemBaseType baseType, String name, boolean useTendencies, boolean moreIsWorse,
			BoundType defaultBoundType, BigDecimal criticalGoodAvg, BoundType criticalGoodAvgMode,
			BigDecimal criticalBadAvg, BoundType criticalBadAvgMode, BigDecimal criticalGradesRatio,
			BoundType criticalGradesMode) {
		this.baseType = baseType;
		this.name = name;
		this.useTendencies = useTendencies;
		this.moreIsWorse = moreIsWorse;
		this.defaultBoundType = defaultBoundType;
		this.criticalGoodAvg = criticalGoodAvg;
		this.criticalGoodAvgMode = criticalGoodAvgMode;
		this.criticalBadAvg = criticalBadAvg;
		this.criticalBadAvgMode = criticalBadAvgMode;
		this.criticalGradesRatio = criticalGradesRatio;
		this.criticalGradesMode = criticalGradesMode;
		gradeSystems.add(this);
	}

	public GradeSystemBaseType getBaseType() {
		return this.baseType;
	}

	public Level getLevel(BigDecimal avg) {
		if (this.moreIsWorse()) {
			if (this.criticalGoodAvgMode == BoundType.LESS_THAN) {
				if (avg.compareTo(this.criticalGoodAvg) < 0) {
					return Level.TOO_GOOD;
				}
			} else if (this.criticalGoodAvgMode == BoundType.LESSOREQUAL_THAN) {
				if (avg.compareTo(this.criticalGoodAvg) <= 0) {
					return Level.TOO_GOOD;
				}
			} else {
				throw new UnsupportedOperationException("Illegal bound type for GradeSystem");
			}
			if (this.criticalBadAvgMode == BoundType.MORE_THAN) {
				if (avg.compareTo(this.criticalBadAvg) > 0) {
					return Level.TOO_BAD;
				}
			} else if (this.criticalBadAvgMode == BoundType.MOREOREQUAL_THAN) {
				if (avg.compareTo(this.criticalBadAvg) >= 0) {
					return Level.TOO_BAD;
				}
			} else {
				throw new UnsupportedOperationException("Illegal bound type for GradeSystem");
			}
			BigDecimal range = this.criticalBadAvg.subtract(this.criticalGoodAvg);
			int steps = Level.values().length - 2;
			BigDecimal step = range.divide(BigDecimal.valueOf(steps), SCALE, ROUNDINGMODE);
			BigDecimal currentBound = this.criticalGoodAvg.add(step);
			for (int i = 0; i < steps; i++) {
				// not critical, this just uses less than
				if (avg.compareTo(currentBound) < 0) {
					return Level.values()[i + 1];
				}
				currentBound = currentBound.add(step);
			}
			return Level.values()[steps + 1];
		} else {
			if (this.criticalGoodAvgMode == BoundType.MORE_THAN) {
				if (avg.compareTo(this.criticalGoodAvg) > 0) {
					return Level.TOO_GOOD;
				}
			} else if (this.criticalGoodAvgMode == BoundType.MOREOREQUAL_THAN) {
				if (avg.compareTo(this.criticalGoodAvg) >= 0) {
					return Level.TOO_GOOD;
				}
			} else {
				throw new UnsupportedOperationException("Illegal bound type for GradeSystem");
			}
			if (this.criticalBadAvgMode == BoundType.LESS_THAN) {
				if (avg.compareTo(this.criticalBadAvg) < 0) {
					return Level.TOO_BAD;
				}
			} else if (this.criticalBadAvgMode == BoundType.LESSOREQUAL_THAN) {
				if (avg.compareTo(this.criticalBadAvg) <= 0) {
					return Level.TOO_BAD;
				}
			} else {
				throw new UnsupportedOperationException("Illegal bound type for GradeSystem");
			}
			BigDecimal range = this.criticalGoodAvg.subtract(this.criticalBadAvg);
			int steps = Level.values().length - 2;
			BigDecimal step = range.divide(BigDecimal.valueOf(steps), SCALE, ROUNDINGMODE);
			BigDecimal currentBound = this.criticalGoodAvg.subtract(step);
			for (int i = 0; i < steps; i++) {
				// not critical, this just uses greater than
				if (avg.compareTo(currentBound) > 0) {
					return Level.values()[i + 1];
				}
				currentBound = currentBound.add(step);
			}
			return Level.values()[steps + 1];
		}
	}

	public BigDecimal mapAvgToOther(BigDecimal avg, GradeSystemBaseType mapTo) {
		if (this.baseType == mapTo) {
			return avg;
		} else if (this.baseType == GradeSystemBaseType.ONE_TO_SIX && mapTo == GradeSystemBaseType.FIFTEEN_POINTS) {
			return BigDecimal.valueOf(17).subtract(avg.multiply(BigDecimal.valueOf(3)));
		} else if (this.baseType == GradeSystemBaseType.FIFTEEN_POINTS && mapTo == GradeSystemBaseType.ONE_TO_SIX) {
			return BigDecimal.valueOf(17).subtract(avg).divide(BigDecimal.valueOf(3), SCALE, ROUNDINGMODE);
		} else {
			throw new UnsupportedOperationException("Can't map from " + this.baseType + " to " + mapTo);
		}
	}

	public Grade calculateGrade(BigDecimal avg) {
		if (avg == null) {
			return null;
		} else {
			for (Grade grade : this.getPossibleGradesASC()) {
				RoundingMode roundingMode = this.getRoundingMode(grade);
				if (roundingMode == null) {
					if (this.moreIsWorse()) {
						roundingMode = RoundingMode.HALF_DOWN;
					} else {
						roundingMode = RoundingMode.HALF_UP;
					}
				}
				if (avg.setScale(0, roundingMode).intValue() == grade.getNumericalValue().intValue()) {
					return grade;
				}
			}
			throw new IllegalArgumentException("no grade possible for given average " + avg);
		}
	}

	public BigDecimal calculateAverage(BigDecimal... values) {
		if (values.length == 0) {
			return null;
		}
		BigDecimal sum = BigDecimal.ZERO;
		for (BigDecimal v : values) {
			sum = sum.add(v);
		}
		BigDecimal avg = sum.divide(new BigDecimal(values.length), SCALE, ROUNDINGMODE);
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
		BigDecimal avg = sum.divide(divisor, SCALE, ROUNDINGMODE);
		return avg.setScale(2, RoundingMode.FLOOR);
	}

	public boolean useTendencies() {
		return this.useTendencies;
	}

	/**
	 * in ascending order, without tendencies
	 * 
	 * @return
	 */
	public Grade[] getPossibleGradesASC() {
		return this.grades.stream().filter(g -> g.getTendency() == Tendency.NEUTRAL).sorted(this.gradeComparator)
				.toArray(n -> new Grade[n]);
	}

	public Grade[] getPossibleGradesASCTendencies() {
		if (this.useTendencies()) {
			return this.grades.stream().sorted(this.gradeComparator).toArray(n -> new Grade[n]);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * in descending order, without tendencies
	 * 
	 * @return
	 */
	public Grade[] getPossibleGradesDESC() {
		return this.grades.stream().filter(g -> g.getTendency() == Tendency.NEUTRAL)
				.sorted(this.gradeComparator.reversed()).toArray(n -> new Grade[n]);
	}

	public Grade[] getPossibleGradesDESCTendencies() {
		if (this.useTendencies()) {
			return this.grades.stream().sorted(this.gradeComparator.reversed()).toArray(n -> new Grade[n]);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public PointsSystem getDefaultPointsSystem(ObservableValue<BigDecimal> totalPoints) {
		return new PointsSystem(this, totalPoints,
				ConfigController.get("TENDENCY_BOUND").equals("null") ? null
						: BigDecimal.valueOf(Double.valueOf(ConfigController.get("TENDENCY_BOUND"))),
				Boolean.valueOf(ConfigController.get("USE_HALF_POINTS")));
	}

	protected boolean moreIsWorse() {
		return this.moreIsWorse;
	}

	public Grade getWorst() {
		return this.grades.stream().sorted(this.gradeComparator).toList().getFirst();
	}

	public Grade getBest() {
		return this.grades.stream().sorted(this.gradeComparator).toList().getLast();
	}

	private void setDefaultRatioBound(Grade grade, BigDecimal defaultRatioBound) {
		this.defaultRatioBounds.put(grade, defaultRatioBound);
	}

	public BigDecimal getDefaultRatioBound(Grade grade) {
		return this.defaultRatioBounds.get(grade);
	}

	/**
	 * If not set, default rounding modes are {@link RoundingMode.HALF_UP} if
	 * {@link #moreIsWorse()} and {@link RoundingMode.HALF_DOWN} if not. <br>
	 * Rounding modes are applied worst to best, so a rounding mode for a worse
	 * grade is applied before one for a better grade (i.e. if the avg is 0.8, the
	 * rounding mode for the worse grade with numerical value 0 is
	 * {@link RoundingMode.DOWN} and for the better grade with numerical value 1 is
	 * {@link RoundingMode.HALF_UP}, then the resulting grade is the one with
	 * numerical value 0 since it is checked first).
	 * 
	 * @param grade
	 * @param roundingMode
	 */
	private void setRoundingMode(Grade grade, RoundingMode roundingMode) {
		this.roundingModes.put(grade, roundingMode);
	}

	public RoundingMode getRoundingMode(Grade grade) {
		return this.roundingModes.get(grade);
	}

	public BoundType getDefaultBoundType() {
		return this.defaultBoundType;
	}

	public StringConverter<Grade> getGradeConverter() {
		return new GradeConverter(this);
	}

	public List<Grade> getCriticalGrades() {
		return this.grades.stream().filter(grade -> grade.isCritical()).toList();
	}

	public BigDecimal getCriticalGradesRatio() {
		return this.criticalGradesRatio;
	}

	public BoundType getCriticalGradesMode() {
		return this.criticalGradesMode;
	}

	public Grade getGrade(Integer numericalValue) {
		return getGrade(numericalValue, String.valueOf(numericalValue), Tendency.NEUTRAL);
	}

	public Grade getGrade(Integer numericalValue, String displayedValue) {
		return getGrade(numericalValue, displayedValue, Tendency.NEUTRAL);
	}

	public Grade getGrade(Integer numericalValue, Tendency tendency) {
		return getGrade(numericalValue, String.valueOf(numericalValue), tendency);
	}

	public Grade getGrade(Integer numericalValue, String displayedValue, Tendency tendency) {
		for (Grade grade : this.grades) {
			if (grade.getNumericalValue().equals(numericalValue) && grade.getDisplayedValue().equals(displayedValue)
					&& grade.getTendency().equals(tendency)) {
				return grade;
			}
		}
		return null;
	}

	public Grade getGrade(String displayedValue) {
		return getGrade(displayedValue, Tendency.NEUTRAL);
	}

	public Grade getGrade(String displayedValue, Tendency tendency) {
		for (Grade grade : this.grades) {
			if (grade.getDisplayedValue().equals(displayedValue) && grade.getTendency().equals(tendency)) {
				return grade;
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void addGrade(Integer numericalValue) {
		this.addGrade(numericalValue, String.valueOf(numericalValue), Tendency.NEUTRAL, false);
	}

	private void addGrade(Integer numericalValue, boolean isCritical) {
		this.addGrade(numericalValue, String.valueOf(numericalValue), Tendency.NEUTRAL, isCritical);
	}

	@SuppressWarnings("unused")
	private void addGrade(Integer numericalValue, String displayedValue) {
		this.addGrade(numericalValue, displayedValue, Tendency.NEUTRAL, false);
	}

	@SuppressWarnings("unused")
	private void addGrade(Integer numericalValue, String displayedValue, boolean isCritical) {
		this.addGrade(numericalValue, displayedValue, Tendency.NEUTRAL, isCritical);
	}

	@SuppressWarnings("unused")
	private void addGrade(Integer numericalValue, Tendency tendency) {
		this.addGrade(numericalValue, String.valueOf(numericalValue), tendency, false);
	}

	private void addGrade(Integer numericalValue, Tendency tendency, boolean isCritical) {
		this.addGrade(numericalValue, String.valueOf(numericalValue), tendency, isCritical);
	}

	@SuppressWarnings("unused")
	private void addGrade(Integer numericalValue, String displayedValue, Tendency tendency) {
		this.grades.add(Grade.forGradeSystem(this, numericalValue, displayedValue, tendency, false));
	}

	private void addGrade(Integer numericalValue, String displayedValue, Tendency tendency, boolean isCritical) {
		this.grades.add(Grade.forGradeSystem(this, numericalValue, displayedValue, tendency, isCritical));
	}

	private final Comparator<Grade> gradeComparator = new Comparator<Grade>() {

		@Override
		public int compare(Grade g1, Grade g2) {
			int compare = g1.getNumericalValue().compareTo(g2.getNumericalValue());
			if (compare == 0) {
				return g1.getTendency().compareTo(g2.getTendency());
			} else if (GradeSystem.this.moreIsWorse()) {
				return compare * (-1);
			} else {
				return compare;
			}
		}
	};

	public Comparator<Grade> getGradeComparator() {
		return this.gradeComparator;
	}

	public String getName() {
		return this.name;
	}

	private static class GradeSystemS implements DataObject<GradeSystem> {

		private static final long serialVersionUID = -3790532903681625300L;

		private transient GradeSystem gradeSystem;

		private final String name;
		private final boolean useTendencies;
		private final boolean moreIsWorse;
		private final GradeSystemBaseType baseType;
		private final BoundType defaultBoundType;
		private final BigDecimal criticalGoodAvg;
		private final BoundType criticalGoodAvgMode;
		private final BigDecimal criticalBadAvg;
		private final BoundType criticalBadAvgMode;
		private final BigDecimal criticalGradesRatio;
		private final BoundType criticalGradesMode;
		private final Set<Grade> grades = new HashSet<>();
		private final Map<Grade, BigDecimal> defaultRatioBounds = new HashMap<>();
		private final Map<Grade, RoundingMode> roundingModes = new HashMap<>();

		private GradeSystemS(GradeSystem g) {
			DataObject.putSerialized(g, this);
			this.name = g.getName();
			this.useTendencies = g.useTendencies();
			this.moreIsWorse = g.moreIsWorse();
			this.baseType = g.getBaseType();
			this.defaultBoundType = g.getDefaultBoundType();
			this.criticalGoodAvg = g.criticalGoodAvg;
			this.criticalGoodAvgMode = g.criticalGoodAvgMode;
			this.criticalBadAvg = g.criticalBadAvg;
			this.criticalBadAvgMode = g.criticalBadAvgMode;
			this.criticalGradesRatio = g.getCriticalGradesRatio();
			this.criticalGradesMode = g.getCriticalGradesMode();
			for (Grade grade : g.grades) {
				this.grades.add(grade);
			}
			for (Entry<Grade, BigDecimal> gd : g.defaultRatioBounds.entrySet()) {
				this.defaultRatioBounds.put(gd.getKey(), gd.getValue());
			}
			for (Entry<Grade, RoundingMode> gr : g.roundingModes.entrySet()) {
				this.roundingModes.put(gr.getKey(), gr.getValue());
			}
			this.gradeSystem = g;
		}

		@Override
		public GradeSystem deserialize(Object... params) {
			if (gradeSystem == null) {
				gradeSystem = new GradeSystem(baseType, name, useTendencies, moreIsWorse, defaultBoundType,
						criticalGoodAvg, criticalGoodAvgMode, criticalBadAvg, criticalBadAvgMode, criticalGradesRatio,
						criticalGradesMode);
				for (Grade grade : grades) {
					gradeSystem.grades.add(grade);
				}
				for (Entry<Grade, BigDecimal> gd : defaultRatioBounds.entrySet()) {
					gradeSystem.defaultRatioBounds.put(gd.getKey(), gd.getValue());
				}
				for (Entry<Grade, RoundingMode> gr : roundingModes.entrySet()) {
					gradeSystem.roundingModes.put(gr.getKey(), gr.getValue());
				}
			}
			return gradeSystem;
		}

	}

	@SuppressWarnings("unchecked")
	public DataObject<GradeSystem> serialize() {
		DataObject<?> gradeSystem = DataObject.getSerialized(this);
		if (gradeSystem == null) {
			return new GradeSystemS(this);
		} else {
			return (DataObject<GradeSystem>) gradeSystem;
		}
	}

}
