package kafx.view.converter;

import java.text.DecimalFormat;
import java.text.ParseException;

import javafx.util.StringConverter;

public class DoublePercentConverter extends StringConverter<Double> {

	private final DecimalFormat decimalFormat;

	public DoublePercentConverter(int fractionDigits) {
		this.decimalFormat = new DecimalFormat();
		this.decimalFormat.setMinimumFractionDigits(fractionDigits);
		this.decimalFormat.setMaximumFractionDigits(fractionDigits);
		this.decimalFormat.setPositiveSuffix("%");
		this.decimalFormat.setNegativeSuffix("%");
	}

	public DecimalFormat getDecimalFormat() {
		return this.decimalFormat;
	}

	@Override
	public String toString(Double object) {
		return decimalFormat.format(object * 100.0);
	}

	@Override
	public Double fromString(String string) {
		try {
			return decimalFormat.parse(string).doubleValue() / 100.0;
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
