package org.openjfx.gradefx.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.openjfx.gradefx.model.GradeSystem.Grade;
import org.openjfx.gradefx.model.GradeSystem.Tendency;
import org.openjfx.kafx.controller.ChangeController;
import org.openjfx.kafx.io.DataObject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class PointsSystem {

	// used for divisions
	private final static Integer SCALE = 7;
	private final static RoundingMode ROUNDINGMODE = RoundingMode.HALF_UP;

	public enum BoundType {
		LESS_THAN, LESSOREQUAL_THAN, MOREOREQUAL_THAN, MORE_THAN;
	}

	private final ObjectProperty<BigDecimal> totalPoints = new SimpleObjectProperty<>(this, "totalPoints");
	private final ObjectProperty<BoundType> boundType = new SimpleObjectProperty<>(this, "boundType");
	private final BooleanProperty useHalfPoints = new SimpleBooleanProperty(this, "useHalfPoints", true);
	private final ObjectProperty<BigDecimal> tendencyBound = new SimpleObjectProperty<>(this, "tendencyBound");
	private final ObjectProperty<BigDecimal>[] ratioBounds;
	private final Grade[] grades;
	private final ObjectProperty<BigDecimal>[] lowerBounds;
	private final ObjectProperty<BigDecimal>[] upperBounds;
	private final ChangeListener<BigDecimal> updateListener = (_, _, _) -> this.callListener();
	private Runnable updateSubscription;

	@SuppressWarnings("unchecked")
	public PointsSystem(ObservableValue<BigDecimal> totalPoints, BigDecimal tendencyBound, BoundType boundType, boolean useHalfPoints,
			GradeSystem gradeSystem) {
		this.totalPoints.bind(totalPoints);
		this.setBoundType(boundType);
		this.setUseHalfPoints(useHalfPoints);
		this.grades = gradeSystem.getPossibleGradesASC();
		this.lowerBounds = new ObjectProperty[grades.length];
		this.upperBounds = new ObjectProperty[grades.length];
		this.ratioBounds = new ObjectProperty[grades.length];
		BigDecimal[] ratioBounds = gradeSystem.getDefaultRatioBounds();
		for (int i = 0; i < grades.length; i++) {
			this.ratioBounds[i] = new SimpleObjectProperty<>(this, "ratio bound for grade " + grades[i],
					ratioBounds[i]);
			this.lowerBounds[i] = new SimpleObjectProperty<>(this, "lower bound for grade " + grades[i]);
			this.upperBounds[i] = new SimpleObjectProperty<>(this, "upper bound for grade " + grades[i]);
			if (i > 0) {
				final int index = i;
				this.lowerBounds[i].addListener((_, _, newValue) -> this.upperBounds[index - 1]
						.set(this.calculateUpperBoundFromLowerBound(newValue)));
			}
			if (i < this.grades.length - 1) {
				final int index = i;
				this.upperBounds[i].addListener((_, _, newValue) -> this.lowerBounds[index + 1]
						.set(this.calculateLowerBoundFromUpperBound(newValue)));
			}
		}
		this.updateBounds();

		this.useHalfPoints.addListener((_, _, _) -> this.updateBounds());
		this.useHalfPoints.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);

		this.tendencyBound.addListener((_, _, _) -> this.updateBounds());
		this.tendencyBound.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);

		this.totalPoints.addListener((_, _, _) -> this.updateBounds());

		this.boundType.addListener((_, oldValue, newValue) -> {
			if ((oldValue == BoundType.LESS_THAN || oldValue == BoundType.LESSOREQUAL_THAN)
					&& (newValue == BoundType.MORE_THAN || newValue == BoundType.MOREOREQUAL_THAN)) {
				for (int i = grades.length - 1; i > 0; i--) {
					this.ratioBounds[i].set(this.ratioBounds[i - 1].get());
				}
				this.ratioBounds[0].set(null);

			} else if ((oldValue == BoundType.MORE_THAN || oldValue == BoundType.MOREOREQUAL_THAN)
					&& (newValue == BoundType.LESS_THAN || newValue == BoundType.LESSOREQUAL_THAN)) {
				for (int i = 0; i < grades.length - 1; i++) {
					this.ratioBounds[i].set(this.ratioBounds[i + 1].get());
				}
				this.ratioBounds[grades.length - 1].set(null);
			}
			this.updateBounds();
		});
		this.boundType.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
	}

	@SuppressWarnings("unchecked")
	private PointsSystem(BigDecimal totalPoints, BigDecimal tendencyBound, BoundType boundType, boolean useHalfPoints,
			Grade[] grades, BigDecimal[] lowerBounds, BigDecimal[] upperBounds, BigDecimal[] ratioBounds) {
		this.setTotalPoints(totalPoints);
		this.setTendencyBound(tendencyBound);
		this.setBoundType(boundType);
		this.setUseHalfPoints(useHalfPoints);
		this.grades = grades;
		this.lowerBounds = new ObjectProperty[grades.length];
		this.upperBounds = new ObjectProperty[grades.length];
		this.ratioBounds = new ObjectProperty[grades.length];
		for (int i = 0; i < grades.length; i++) {
			this.ratioBounds[i] = new SimpleObjectProperty<>(this, "ratio bound for grade " + grades[i],
					ratioBounds[i]);
			this.lowerBounds[i] = new SimpleObjectProperty<>(this, "lower bound for grade " + grades[i],
					lowerBounds[i]);
			this.upperBounds[i] = new SimpleObjectProperty<>(this, "upper bound for grade " + grades[i],
					upperBounds[i]);
			if (i > 0) {
				final int index = i;
				this.lowerBounds[i].addListener((_, _, newValue) -> this.upperBounds[index - 1]
						.set(this.calculateUpperBoundFromLowerBound(newValue)));
			}
			if (i < this.grades.length - 1) {
				final int index = i;
				this.upperBounds[i].addListener((_, _, newValue) -> this.lowerBounds[index + 1]
						.set(this.calculateLowerBoundFromUpperBound(newValue)));
			}
			this.lowerBounds[i].addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			this.lowerBounds[i].addListener(this.updateListener);
			this.upperBounds[i].addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			this.upperBounds[i].addListener(this.updateListener);
		}

		this.useHalfPoints.addListener((_, _, _) -> this.updateBounds());
		this.useHalfPoints.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);

		this.tendencyBound.addListener((_, _, _) -> this.updateBounds());
		this.tendencyBound.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);

		this.totalPoints.addListener((_, _, _) -> this.updateBounds());

		this.boundType.addListener((_, oldValue, newValue) -> {
			if ((oldValue == BoundType.LESS_THAN || oldValue == BoundType.LESSOREQUAL_THAN)
					&& (newValue == BoundType.MORE_THAN || newValue == BoundType.MOREOREQUAL_THAN)) {
				for (int i = grades.length - 1; i > 0; i--) {
					this.ratioBounds[i].set(this.ratioBounds[i - 1].get());
				}
				this.ratioBounds[0].set(null);

			} else if ((oldValue == BoundType.MORE_THAN || oldValue == BoundType.MOREOREQUAL_THAN)
					&& (newValue == BoundType.LESS_THAN || newValue == BoundType.LESSOREQUAL_THAN)) {
				for (int i = 0; i < grades.length - 1; i++) {
					this.ratioBounds[i].set(this.ratioBounds[i + 1].get());
				}
				this.ratioBounds[grades.length - 1].set(null);
			}
			this.updateBounds();
		});
		this.boundType.addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
	}

	public void subscribeBoundUpdates(Runnable listener) {
		this.updateSubscription = listener;
	}

	private void callListener() {
		if (this.updateSubscription != null) {
			this.updateSubscription.run();
		}
	}

	public BigDecimal getTotalPoints() {
		return this.totalPoints.get();
	}

	public ObjectProperty<BigDecimal> totalPointsProperty() {
		return this.totalPoints;
	}

	public void setTotalPoints(BigDecimal totalPoints) {
		this.totalPoints.set(totalPoints);
	}

	public BoundType getBoundType() {
		return this.boundType.get();
	}

	public ObjectProperty<BoundType> boundTypeProperty() {
		return this.boundType;
	}

	public void setBoundType(BoundType boundType) {
		this.boundType.set(boundType);
	}

	public BooleanProperty useHalfPointsProperty() {
		return this.useHalfPoints;
	}

	public void setUseHalfPoints(boolean useHalfPoints) {
		this.useHalfPoints.set(useHalfPoints);
	}

	public boolean isUseHalfPoints() {
		return this.useHalfPoints.get();
	}

	private int indexOf(Grade grade) {
		for (int i = 0; i < this.grades.length; i++) {
			if (this.grades[i].equals(grade)) {
				return i;
			}
		}
		return -1;
	}

	private void clearRatioBounds() {
		for (int i = 0; i < this.ratioBounds.length; i++) {
			this.ratioBounds[i].set(null);
		}
	}

	public BigDecimal getLowerBoundForGrade(Grade grade) {
		return this.lowerBounds[indexOf(grade)].get();
	}

	public ObjectProperty<BigDecimal> lowerBoundForGrade(Grade grade) {
		return this.lowerBounds[indexOf(grade)];
	}

	public BigDecimal getUpperBoundForGrade(Grade grade) {
		return this.upperBounds[indexOf(grade)].get();
	}

	public ObjectProperty<BigDecimal> upperBoundForGrade(Grade grade) {
		return this.upperBounds[indexOf(grade)];
	}

	public ObjectProperty<BigDecimal>[] getRatioBounds() {
		return this.ratioBounds;
	}

	private int getNextRatioIndex(int from) {
		for (int i = from + 1; i < this.ratioBounds.length; i++) {
			if (this.ratioBounds[i].get() != null) {
				return i;
			}
		}
		return this.ratioBounds.length;
	}

	private int getPrevRatioIndex(int from) {
		for (int i = from - 1; i >= 0; i--) {
			if (this.ratioBounds[i].get() != null) {
				return i;
			}
		}
		return -1;
	}

	private void updateBounds() {
		String oldString = this.toString();
		BigDecimal[] calculatedBounds = calculateBounds();
		for (int i = 0; i < this.grades.length; i++) {
			this.lowerBounds[i].removeListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			this.lowerBounds[i].removeListener(this.updateListener);
			this.upperBounds[i].removeListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			this.upperBounds[i].removeListener(this.updateListener);
		}
		this.lowerBounds[0].set(BigDecimal.ZERO);
		this.upperBounds[this.grades.length - 1].set(this.getTotalPoints());
		for (int i = 0; i < this.grades.length; i++) {
			BigDecimal bound = calculatedBounds[i];
			switch (this.getBoundType()) {
			case LESS_THAN:
				if (isUseHalfPoints()) {
					bound = bound.subtract(BigDecimal.valueOf(.5));
				} else {
					bound = bound.subtract(BigDecimal.ONE);
				}
				// fall through
			case LESSOREQUAL_THAN:
				if (isUseHalfPoints()) {
					bound = bound.multiply(BigDecimal.TWO);
					bound = bound.setScale(0, RoundingMode.DOWN);
					bound = bound.divide(BigDecimal.TWO, 1, RoundingMode.HALF_UP);
				} else {
					bound = bound.setScale(0, RoundingMode.DOWN);
				}
				this.upperBounds[i].set(bound);
				break;
			case MORE_THAN:
				if (isUseHalfPoints()) {
					bound = bound.add(BigDecimal.valueOf(.5));
				} else {
					bound = bound.add(BigDecimal.ONE);
				}
				// fall through
			case MOREOREQUAL_THAN:
				if (isUseHalfPoints()) {
					bound = bound.multiply(BigDecimal.TWO);
					bound = bound.setScale(0, RoundingMode.UP);
					bound = bound.divide(BigDecimal.TWO, 1, RoundingMode.HALF_UP);
				} else {
					bound = bound.setScale(0, RoundingMode.UP);
				}
				this.lowerBounds[i].set(bound);
				break;
			}
		}
		for (int i = 0; i < this.grades.length; i++) {
			this.lowerBounds[i].addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			this.lowerBounds[i].addListener(this.updateListener);
			this.upperBounds[i].addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
			this.upperBounds[i].addListener(this.updateListener);
		}
		String newString = this.toString();
		// TODO not the best solution to check for changes...
		if (oldString != null && !oldString.equals(newString)) {
			ChangeController.LISTENER_UNSAVED_CHANGES.changed(null, oldString, newString);
		}
		this.callListener();
	}

	private BigDecimal calculateLowerBoundFromUpperBound(BigDecimal prevUpperBound) {
		BigDecimal value;
		if (isUseHalfPoints()) {
			value = prevUpperBound.add(BigDecimal.valueOf(.5));
		} else {
			value = prevUpperBound.add(BigDecimal.ONE);
		}
		return value.compareTo(totalPoints.get()) > 0 ? totalPoints.get() : value;
	}

	private BigDecimal calculateUpperBoundFromLowerBound(BigDecimal nextLowerBound) {
		BigDecimal value;
		if (isUseHalfPoints()) {
			value = nextLowerBound.subtract(BigDecimal.valueOf(.5));
		} else {
			value = nextLowerBound.subtract(BigDecimal.ONE);
		}
		return value.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : value;
	}

	// if boundTyp MORE[OREQUAL]_THAN, this calculates the lower bounds
	// if boundTyp LESS[OREQUAL]_THAN, this calculates the upper bounds
	private BigDecimal[] calculateBounds() {
		BigDecimal[] bounds = new BigDecimal[this.grades.length];
		int indexFrom;
		switch (this.getBoundType()) {
		case LESSOREQUAL_THAN:
		case LESS_THAN:
			bounds[this.ratioBounds.length - 1] = this.totalPoints.get();
			indexFrom = this.ratioBounds.length - 1;
			while (indexFrom > 0) {
				int indexTo = getPrevRatioIndex(indexFrom);
				BigDecimal steps = BigDecimal.valueOf(indexFrom - indexTo);
				BigDecimal ratioFrom = indexFrom == this.ratioBounds.length - 1 ? BigDecimal.ONE
						: this.ratioBounds[indexFrom].get();
				BigDecimal ratioTo = indexTo == -1 ? BigDecimal.ZERO : this.ratioBounds[indexTo].get();
				BigDecimal ratioDiff = ratioFrom.subtract(ratioTo);
				BigDecimal pointStep = this.totalPoints.get().multiply(ratioDiff).divide(steps, SCALE, ROUNDINGMODE);
				for (int i = indexFrom - 1; i >= indexTo; i--) {
					if (i >= 0) {
						bounds[i] = bounds[i + 1].subtract(pointStep);
					}
				}
				indexFrom = indexTo;
			}
			break;
		case MOREOREQUAL_THAN:
		case MORE_THAN:
			bounds[0] = BigDecimal.ZERO;
			indexFrom = 0;
			while (indexFrom < this.ratioBounds.length - 1) {
				int indexTo = getNextRatioIndex(indexFrom);
				BigDecimal steps = BigDecimal.valueOf(indexTo - indexFrom);
				BigDecimal ratioFrom = indexFrom == 0 ? BigDecimal.ZERO : this.ratioBounds[indexFrom].get();
				BigDecimal ratioTo = indexTo == this.ratioBounds.length ? BigDecimal.ONE
						: this.ratioBounds[indexTo].get();
				BigDecimal ratioDiff = ratioTo.subtract(ratioFrom);
				BigDecimal pointStep = this.totalPoints.get().multiply(ratioDiff).divide(steps, SCALE, ROUNDINGMODE);
				for (int i = indexFrom + 1; i <= indexTo; i++) {
					if (i < this.ratioBounds.length) {
						bounds[i] = bounds[i - 1].add(pointStep);
					}
				}
				indexFrom = indexTo;
			}
			break;
		}
		return bounds;
	}

	public void apply(BoundType boundType, BigDecimal[] ratioBounds) {
		this.setBoundType(boundType);
		for (int i = 0; i < this.ratioBounds.length; i++) {
			this.ratioBounds[i].set(ratioBounds[i]);
		}
		this.updateBounds();
	}

	public void setLinear() {
		this.clearRatioBounds();
	}

	public BigDecimal getTendencyBound() {
		return this.tendencyBound.get();
	}

	public void setTendencyBound(BigDecimal tendencyBound) {
		this.tendencyBound.set(tendencyBound);
	}

	public ObjectProperty<BigDecimal> tendencyBoundProperty() {
		return this.tendencyBound;
	}

	public Grade calculateGrade(BigDecimal points) {
		for (int i = 0; i < this.grades.length; i++) {
			if (i == 0 && points.compareTo(this.upperBounds[i].get()) <= 0
					|| i == this.grades.length - 1 && points.compareTo(this.lowerBounds[i].get()) >= 0
					|| points.compareTo(this.lowerBounds[i].get()) >= 0
							&& points.compareTo(this.upperBounds[i].get()) <= 0) {
				Grade grade = this.grades[i];
				if (this.getTendencyBound() != null) {
					if (i == 0 && points.subtract(getTendencyBound()).compareTo(BigDecimal.ZERO) <= 0
							|| points.subtract(getTendencyBound()).compareTo(this.lowerBounds[i].get()) <= 0) {
						grade = grade.setTendency(Tendency.NEGATIVE);
					}
					if (i == this.grades.length - 1 && points.add(getTendencyBound()).compareTo(getTotalPoints()) >= 0
							|| points.add(getTendencyBound()).compareTo(this.upperBounds[i].get()) >= 0) {
						grade = grade.setTendency(Tendency.POSITIVE);
					}
				}
				return grade;
			}
		}
		throw new IllegalArgumentException("error calculating grade");
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("{");
		string.append(this.boundType.get());
		string.append(": ");
		switch (this.getBoundType()) {
		case LESSOREQUAL_THAN:
		case LESS_THAN:
			for (int i = 0; i < this.grades.length - 1; i++) {
				// null if not configured yet
				if (this.lowerBounds[i].get() == null) {
					return null;
				}
				string.append(this.lowerBounds[i].get());
				string.append("-");
				string.append(this.upperBounds[i].get());
				string.append(" -> ");
				string.append(this.grades[i]);
				string.append(", ");
			}
			string.append(this.lowerBounds[this.grades.length - 1].get());
			string.append("-");
			string.append(this.upperBounds[this.grades.length - 1].get());
			string.append(" -> ");
			string.append(this.grades[this.grades.length - 1]);
			break;
		case MOREOREQUAL_THAN:
		case MORE_THAN:
			for (int i = this.grades.length - 1; i > 0; i--) {
				// null if not configured yet
				if (this.lowerBounds[i].get() == null) {
					return null;
				}
				string.append(this.lowerBounds[i].get());
				string.append("-");
				string.append(this.upperBounds[i].get());
				string.append(" -> ");
				string.append(this.grades[i]);
				string.append(", ");
			}
			string.append(this.lowerBounds[0].get());
			string.append("-");
			string.append(this.upperBounds[0].get());
			string.append(" -> ");
			string.append(this.grades[0]);
			break;
		}
		string.append("}");
		return string.toString();
	}

	private static class PointsSystemS implements DataObject<PointsSystem> {

		private static final long serialVersionUID = 4210035038298738031L;

		private final BigDecimal totalPoints;
		private final BigDecimal tendencyBound;
		private final BoundType boundType;
		private final boolean useHalfPoints;
		private final BigDecimal[] ratioBounds;
		private final Grade[] grades;
		private final BigDecimal[] lowerBounds;
		private final BigDecimal[] upperBounds;

		private transient PointsSystem pointsSystem;

		private PointsSystemS(PointsSystem ps) {
			DataObject.putSerialized(ps, this);
			this.totalPoints = ps.getTotalPoints();
			this.tendencyBound = ps.getTendencyBound();
			this.boundType = ps.getBoundType();
			this.useHalfPoints = ps.isUseHalfPoints();
			this.grades = ps.grades;
			this.lowerBounds = new BigDecimal[ps.grades.length];
			this.upperBounds = new BigDecimal[ps.grades.length];
			this.ratioBounds = new BigDecimal[ps.grades.length];
			for (int i = 0; i < this.grades.length; i++) {
				this.lowerBounds[i] = ps.lowerBounds[i].get();
				this.upperBounds[i] = ps.upperBounds[i].get();
				this.ratioBounds[i] = ps.ratioBounds[i].get();
			}
			pointsSystem = ps;
		}

		public PointsSystem deserialize(Object... params) {
			if (pointsSystem == null) {
				pointsSystem = new PointsSystem(totalPoints, tendencyBound, boundType, useHalfPoints, grades,
						lowerBounds, upperBounds, ratioBounds);
			}
			return pointsSystem;
		}
	}

	@SuppressWarnings("unchecked")
	public DataObject<PointsSystem> serialize() {
		DataObject<?> pointsSystem = DataObject.getSerialized(this);
		if (pointsSystem == null) {
			return new PointsSystemS(this);
		} else {
			return (DataObject<PointsSystem>) pointsSystem;
		}
	}

}
