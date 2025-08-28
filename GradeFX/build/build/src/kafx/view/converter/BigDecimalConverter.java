package kafx.view.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import javafx.util.StringConverter;

public class BigDecimalConverter extends StringConverter<BigDecimal> {

	private final DecimalFormat decimalFormat;

	public BigDecimalConverter() {
		this.decimalFormat = new DecimalFormat();
		this.decimalFormat.setParseBigDecimal(true);
	}

	public DecimalFormat getDecimalFormat() {
		return this.decimalFormat;
	}

	@Override
	public String toString(BigDecimal object) {
		return decimalFormat.format(object);
	}

	@Override
	public BigDecimal fromString(String string) {
		try {
			return (BigDecimal) decimalFormat.parseObject(string);
		} catch (ParseException e) {
			return null;
		}
	}

}
