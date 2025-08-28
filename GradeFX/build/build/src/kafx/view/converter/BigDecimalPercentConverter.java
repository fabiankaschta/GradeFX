package kafx.view.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import javafx.util.StringConverter;

public class BigDecimalPercentConverter extends StringConverter<BigDecimal> {

	private static final BigDecimal ONE_HUNDRET = BigDecimal.valueOf(100);

	private final DecimalFormat decimalFormat;

	public BigDecimalPercentConverter(int fractionDigits) {
		this.decimalFormat = new DecimalFormat();
		this.decimalFormat.setMinimumFractionDigits(fractionDigits);
		this.decimalFormat.setMaximumFractionDigits(fractionDigits);
		this.decimalFormat.setPositiveSuffix("%");
		this.decimalFormat.setNegativeSuffix("%");
		this.decimalFormat.setParseBigDecimal(true);
	}

	public DecimalFormat getDecimalFormat() {
		return this.decimalFormat;
	}

	@Override
	public String toString(BigDecimal object) {
		return decimalFormat.format(object.multiply(ONE_HUNDRET));
	}

	@Override
	public BigDecimal fromString(String string) {
		try {
			return ((BigDecimal) decimalFormat.parseObject(string)).divide(ONE_HUNDRET);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
