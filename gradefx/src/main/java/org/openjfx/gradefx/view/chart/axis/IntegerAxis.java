package org.openjfx.gradefx.view.chart.axis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.chart.ValueAxis;

public class IntegerAxis extends ValueAxis<Integer> {

	// from NumberAxis

	/**
	 * The value between each major tick mark in data units. This is automatically
	 * set if we are auto-ranging.
	 */
	private IntegerProperty tickUnit = new StyleableIntegerProperty(1) {
		@Override
		protected void invalidated() {
			if (!isAutoRanging()) {
				invalidateRange();
				requestAxisLayout();
			}
		}

		@Override
		public CssMetaData<IntegerAxis, Number> getCssMetaData() {
			return StyleableProperties.TICK_UNIT;
		}

		@Override
		public Object getBean() {
			return IntegerAxis.this;
		}

		@Override
		public String getName() {
			return "tickUnit";
		}
	};

	public final int getTickUnit() {
		return tickUnit.get();
	}

	public final void setTickUnit(int value) {
		tickUnit.set(value);
	}

	public final IntegerProperty tickUnitProperty() {
		return tickUnit;
	}

	/**
	 * Called to set the upper and lower bound and anything else that needs to be
	 * auto-ranged.
	 *
	 * @param minValue  The min data value that needs to be plotted on this axis
	 * @param maxValue  The max data value that needs to be plotted on this axis
	 * @param length    The length of the axis in display coordinates
	 * @param labelSize The approximate average size a label takes along the axis
	 * @return The calculated range
	 */
	@Override
	protected Object autoRange(double minValue, double maxValue, double length, double labelSize) {
		return new Object[] { 0, (int) Math.ceil(maxValue), getTickUnit(), calculateNewScale(length, 0, maxValue) };
	}

	@Override
	protected List<Integer> calculateMinorTickMarks() {
		return Collections.emptyList();
	}

	@Override
	protected void setRange(Object range, boolean animate) {
		final Object[] rangeProps = (Object[]) range;
		final int lowerBound = (Integer) rangeProps[0];
		final int upperBound = (Integer) rangeProps[1];
		final int tickUnit = (Integer) rangeProps[2];
		final double scale = (Double) rangeProps[3];
		setLowerBound(lowerBound);
		setUpperBound(upperBound);
		setTickUnit(tickUnit);
		currentLowerBound.set(lowerBound);
		setScale(scale);
	}

	@Override
	protected Object getRange() {
		return new Object[] { getLowerBound(), getUpperBound(), getTickUnit(), getScale() };
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	protected List<Integer> calculateTickValues(double length, Object range) {
		final Object[] rangeProps = (Object[]) range;
		final int lowerBound = (Integer) rangeProps[0];
		final int upperBound = (Integer) rangeProps[1];
		final int tickUnit = (Integer) rangeProps[2];
		List<Integer> tickValues = new ArrayList<>();
		if (lowerBound == upperBound) {
			tickValues.add(lowerBound);
		} else if (tickUnit <= 0) {
			tickValues.add(lowerBound);
			tickValues.add(upperBound);
		} else if (tickUnit > 0) {
			tickValues.add(lowerBound);
			if (((upperBound - lowerBound) / tickUnit) > 2000) {
				// This is a ridiculous amount of major tick marks, something has probably gone
				// wrong
				System.err.println("Warning we tried to create more than 2000 major tick marks on a NumberAxis. "
						+ "Lower Bound=" + lowerBound + ", Upper Bound=" + upperBound + ", Tick Unit=" + tickUnit);
			} else {
				if (lowerBound + tickUnit < upperBound) {
					// If tickUnit is integer, start with the nearest integer
					double major = Math.rint(tickUnit) == tickUnit ? Math.ceil(lowerBound) : lowerBound + tickUnit;
					int count = (int) Math.ceil((upperBound - major) / tickUnit);
					for (int i = 0; major < upperBound && i < count; major += tickUnit, i++) {
						if (!tickValues.contains(major)) {
							tickValues.add((int) major);
						}
					}
				}
			}
			tickValues.add(upperBound);
		}
		return tickValues;
	}

	@Override
	protected String getTickMarkLabel(Integer value) {
		return String.valueOf(value);
	}

	// from NumberAxis

	// -------------- STYLESHEET HANDLING
	// ------------------------------------------------------------------------------

	private static class StyleableProperties {
		private static final CssMetaData<IntegerAxis, Number> TICK_UNIT = new CssMetaData<>("-fx-tick-unit",
				SizeConverter.getInstance(), 5.0) {

			@Override
			public boolean isSettable(IntegerAxis n) {
				return n.tickUnit == null || !n.tickUnit.isBound();
			}

			@SuppressWarnings("unchecked")
			@Override
			public StyleableProperty<Number> getStyleableProperty(IntegerAxis n) {
				return (StyleableProperty<Number>) n.tickUnitProperty();
			}
		};

		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
					ValueAxis.getClassCssMetaData());
			styleables.add(TICK_UNIT);
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	/**
	 * Gets the {@code CssMetaData} associated with this class, which may include
	 * the {@code CssMetaData} of its superclasses.
	 * 
	 * @return the {@code CssMetaData}
	 * @since JavaFX 8.0
	 */
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.STYLEABLES;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since JavaFX 8.0
	 */
	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}

}
