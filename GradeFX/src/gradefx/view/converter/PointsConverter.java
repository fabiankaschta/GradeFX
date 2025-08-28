package gradefx.view.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import javafx.util.StringConverter;

public class PointsConverter extends StringConverter<BigDecimal> {

	private final DecimalFormat decimalFormat;
	private final boolean useHalfPoints;

	public PointsConverter(boolean useHalfPoints) {
		this.decimalFormat = new DecimalFormat("#0.###");
		this.useHalfPoints = useHalfPoints;
		this.decimalFormat.setParseBigDecimal(true);
		this.decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
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
			ParsePosition pp = new ParsePosition(0);
			BigDecimal result = (BigDecimal) decimalFormat.parseObject(string, pp);
			if (pp.getIndex() == string.length()) {
				if (result.scale() <= this.decimalFormat.getMaximumFractionDigits()) {
					if (result.scale() <= this.decimalFormat.getMinimumFractionDigits()) {
						result.setScale(this.decimalFormat.getMinimumFractionDigits());
					}
					return result;
				} else {
					throw new ParseException("parsing did not pass scale requirement", pp.getIndex());
				}
			} else {
				throw new ParseException("parsing did not use full string", pp.getIndex());
			}
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
