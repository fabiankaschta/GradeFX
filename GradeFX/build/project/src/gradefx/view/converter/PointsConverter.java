package gradefx.view.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

import javafx.util.StringConverter;

public class PointsConverter extends StringConverter<BigDecimal> {

	private final DecimalFormat decimalFormat;
	private final boolean useHalfPoints;

	public PointsConverter(boolean useHalfPoints) {
		this.decimalFormat = new DecimalFormat();
		this.useHalfPoints = useHalfPoints;
		this.decimalFormat.setParseBigDecimal(true);
		this.decimalFormat.setMaximumFractionDigits(useHalfPoints ? 1 : 0);
	}

	@Override
	public String toString(BigDecimal object) {
		if (this.useHalfPoints) {
			object = object.multiply(BigDecimal.TWO);
			object = object.setScale(0, RoundingMode.HALF_UP);
			object = object.setScale(1);
			object = object.divide(BigDecimal.TWO);
		}
		return decimalFormat.format(object);
	}

	@Override
	public BigDecimal fromString(String string) {
		try {
			return (BigDecimal) decimalFormat.parseObject(string);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
