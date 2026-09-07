package org.openjfx.gradefx.view.pane.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.openjfx.gradefx.model.BoundType;
import org.openjfx.gradefx.model.GradeSystem.GradeSystemBaseType;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.view.tableview.TableViewPointsSystem;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.converter.BigDecimalPercentConverter;
import org.openjfx.kafx.view.imageview.EmojiImageView;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class StatisticsGrid extends GridPane {

	// unicode identifiers for emojis
	private final static String emojiWarning = "26A0";
	private final static String emojiAmazing = "1F600";
	private final static String emojiNice = "1F603";
	private final static String emojiGood = "1F642";
	private final static String emojiOkay = "1F610";
	private final static String emojiBad = "1F641";

	public StatisticsGrid(Group group, TableViewPointsSystem tableViewPointsSystem) {
		super(10, 0);
		this.setStyle("-fx-font-weight: bold;");
		BigDecimalConverter avgConverter = new BigDecimalConverter();
		BigDecimalPercentConverter percentConverter = new BigDecimalPercentConverter(2);
		avgConverter.getDecimalFormat().setMinimumFractionDigits(2);
		avgConverter.getDecimalFormat().setMaximumFractionDigits(2);
		avgConverter.getDecimalFormat().setRoundingMode(RoundingMode.DOWN);

		Label avgLabel = new Label(TranslationController.translate("test_avg") + ": ");
		Label avgValue = new Label();
		avgValue.setContentDisplay(ContentDisplay.RIGHT);
		avgValue.textProperty().bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = tableViewPointsSystem.getGradeAVG();
			if (avg == null) {
				return "-"; // '\u2014'; // long dash
			} else {
				return avgConverter.toString(avg);
			}
		}, tableViewPointsSystem.gradeAVGProperty()));

		Label avgLabelOneToSix = new Label(TranslationController.translate("test_avg_one_to_six") + ": ");
		Label avgValueOneToSix = new Label();
		avgValueOneToSix.setContentDisplay(ContentDisplay.RIGHT);
		avgValueOneToSix.textProperty().bind(Bindings.createStringBinding(() -> {
			BigDecimal avg = tableViewPointsSystem.getGradeAVG();
			if (avg == null) {
				return "-"; // '\u2014'; // long dash
			} else {
				return avgConverter.toString(group.getGradeSystem().mapAvgToOther(avg, GradeSystemBaseType.ONE_TO_SIX));
			}
		}, tableViewPointsSystem.gradeAVGProperty()));
		tableViewPointsSystem.gradeAVGProperty().subscribe(avg -> {
			if (avg != null) {
				switch (group.getGradeSystem().getLevel(avg)) {
				case AMAZING:
					avgValueOneToSix.setGraphic(
							new EmojiImageView(emojiAmazing, FontSizeController.fontSizeProperty().multiply(2)));
					break;
				case NICE:
					avgValueOneToSix.setGraphic(
							new EmojiImageView(emojiNice, FontSizeController.fontSizeProperty().multiply(2)));
					break;
				case GOOD:
					avgValueOneToSix.setGraphic(
							new EmojiImageView(emojiGood, FontSizeController.fontSizeProperty().multiply(2)));
					break;
				case OKAY:
					avgValueOneToSix.setGraphic(
							new EmojiImageView(emojiOkay, FontSizeController.fontSizeProperty().multiply(2)));
					break;
				case BAD:
					avgValueOneToSix.setGraphic(
							new EmojiImageView(emojiBad, FontSizeController.fontSizeProperty().multiply(2)));
					break;
				case TOO_BAD:
				case TOO_GOOD:
					avgValueOneToSix.setGraphic(
							new EmojiImageView(emojiWarning, FontSizeController.fontSizeProperty().multiply(2)));
					break;
				default:
					avgValueOneToSix.setGraphic(null);
					break;
				}
			} else {
				avgValueOneToSix.setGraphic(null);
			}
		});

		Label gradedLabel = new Label(TranslationController.translate("test_graded") + ": ");
		Label gradedValue = new Label();
		gradedValue.textProperty().bind(Bindings.createStringBinding(() -> {
			int graded = tableViewPointsSystem.gradedProperty().get();
			int size = tableViewPointsSystem.getFilteredStudents().size();
			return graded + " " + TranslationController.translate("test_graded_outOf") + " " + size;
		}, tableViewPointsSystem.gradedProperty(), tableViewPointsSystem.getFilteredStudents()));

		Label criticalGradesLabel = new Label(TranslationController.translate("test_critical_grades") + ": ");
		Label criticalGradesValue = new Label();
		criticalGradesValue.setContentDisplay(ContentDisplay.RIGHT);
		criticalGradesValue.textProperty().bind(Bindings.createStringBinding(() -> {
			BigDecimal ratio = tableViewPointsSystem.getCriticalGradesRatio();
			if (ratio == null) {
				return "-"; // '\u2014'; // long dash
			} else {
				return percentConverter.toString(ratio);
			}
		}, tableViewPointsSystem.criticalGradesRatioProperty()));

		tableViewPointsSystem.criticalGradesRatioProperty().subscribe(ratio -> {
			BoundType mode = group.getGradeSystem().getCriticalGradesMode();
			BigDecimal criticalRatio = group.getGradeSystem().getCriticalGradesRatio();
			if (ratio != null) {
				switch (mode) {
				case LESSOREQUAL_THAN:
					if (ratio.compareTo(criticalRatio) <= 0) {
						criticalGradesValue.setGraphic(
								new EmojiImageView(emojiWarning, FontSizeController.fontSizeProperty().multiply(2)));
					} else {
						criticalGradesValue.setGraphic(null);
					}
					break;
				case LESS_THAN:
					if (ratio.compareTo(criticalRatio) < 0) {
						criticalGradesValue.setGraphic(
								new EmojiImageView(emojiWarning, FontSizeController.fontSizeProperty().multiply(2)));
					} else {
						criticalGradesValue.setGraphic(null);
					}
					break;
				case MOREOREQUAL_THAN:
					if (ratio.compareTo(criticalRatio) >= 0) {
						criticalGradesValue.setGraphic(
								new EmojiImageView(emojiWarning, FontSizeController.fontSizeProperty().multiply(2)));
					} else {
						criticalGradesValue.setGraphic(null);
					}
					break;
				case MORE_THAN:
					if (ratio.compareTo(criticalRatio) > 0) {
						criticalGradesValue.setGraphic(
								new EmojiImageView(emojiWarning, FontSizeController.fontSizeProperty().multiply(2)));
					} else {
						criticalGradesValue.setGraphic(null);
					}
					break;
				default:
					criticalGradesValue.setGraphic(null);
					break;
				}
			} else {
				criticalGradesValue.setGraphic(null);
			}
		});

		final RowConstraints rowFixedHeight = new RowConstraints(50);
		rowFixedHeight.setMinHeight(USE_PREF_SIZE);
		rowFixedHeight.setMaxHeight(USE_PREF_SIZE);
		rowFixedHeight.prefHeightProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));

		group.gradeSystemProperty().subscribe(gradeSystem -> {
			if (gradeSystem.getBaseType() == GradeSystemBaseType.ONE_TO_SIX) {
				this.getRowConstraints().clear();
				this.getChildren().clear();
				this.add(avgLabel, 0, 0);
				this.add(avgValueOneToSix, 1, 0);
				this.add(criticalGradesLabel, 0, 1);
				this.add(criticalGradesValue, 1, 1);
				this.add(gradedLabel, 0, 2);
				this.add(gradedValue, 1, 2);
				this.getRowConstraints().addAll(rowFixedHeight, rowFixedHeight, rowFixedHeight);
			} else {
				this.getRowConstraints().clear();
				this.getChildren().clear();
				this.add(avgLabel, 0, 0);
				this.add(avgValue, 1, 0);
				this.add(avgLabelOneToSix, 0, 1);
				this.add(avgValueOneToSix, 1, 1);
				this.add(criticalGradesLabel, 0, 2);
				this.add(criticalGradesValue, 1, 2);
				this.add(gradedLabel, 0, 3);
				this.add(gradedValue, 1, 3);
				this.getRowConstraints().addAll(rowFixedHeight, rowFixedHeight, rowFixedHeight, rowFixedHeight);
			}
		});
	}
}
