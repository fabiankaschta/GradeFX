package kafx.view.dialog.userinput;

public class UserInputSliderGroup {

	private double maxValue;
	private double minValue;
	private double targetValue;

	public UserInputSliderGroup(double minValue, double maxValue, double targetValue, UserInputSlider... sliders) {
		this.maxValue = maxValue;
		this.minValue = minValue;
		this.targetValue = targetValue;
		int i = 0;
		for (UserInputSlider s : sliders) {
			final int index = i;
			s.valueProperty().addListener((_, _, _) -> {
				double valueSum = 0;
				for (UserInputSlider sli : sliders) {
					if (sli.isVisible()) {
						valueSum += sli.getValue();
					}
				}
				double diff = this.targetValue - valueSum;
				if (diff == 0) {
					return;
				}
				int nextIndex = index;
				do {
					nextIndex = (nextIndex + 1) % sliders.length;
					if (nextIndex == index) {
						return;
					}
				} while (!sliders[nextIndex].isVisible());
				UserInputSlider next = sliders[nextIndex];
				if (diff > 0) {
					if (next.getValue() + diff <= this.maxValue) {
						next.setValue(next.getValue() + diff);
					} else {
						next.setValue(this.maxValue);
					}
				} else if (diff < 0) {
					if (next.getValue() + diff >= this.minValue) {
						next.setValue(next.getValue() + diff);
					} else {
						next.setValue(this.minValue);
					}
				}
			});
			i++;
		}
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getTargetValue() {
		return targetValue;
	}

	public void setTargetValue(double targetValue) {
		this.targetValue = targetValue;
	}

}
