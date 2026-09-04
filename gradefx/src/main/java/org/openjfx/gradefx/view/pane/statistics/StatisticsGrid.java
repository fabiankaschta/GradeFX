package org.openjfx.gradefx.view.pane.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.openjfx.gradefx.model.GradeSystem;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.view.tableview.TableViewPointsSystem;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.converter.BigDecimalConverter;
import org.openjfx.kafx.view.imageview.EmojiImageView;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class StatisticsGrid extends GridPane {

	private final static EmojiImageView emojiAmazing = new EmojiImageView("1F600",
			FontSizeController.fontSizeProperty().multiply(2));
	private final static EmojiImageView emojiNice = new EmojiImageView("1F603",
			FontSizeController.fontSizeProperty().multiply(2));
	private final static EmojiImageView emojiGood = new EmojiImageView("1F642",
			FontSizeController.fontSizeProperty().multiply(2));
	private final static EmojiImageView emojiOkay = new EmojiImageView("1F610",
			FontSizeController.fontSizeProperty().multiply(2));
	private final static EmojiImageView emojiBad = new EmojiImageView("1F641",
			FontSizeController.fontSizeProperty().multiply(2));
	private final static EmojiImageView emojiDeavastating = new EmojiImageView("1F635",
			FontSizeController.fontSizeProperty().multiply(2));

	public StatisticsGrid(Group group, TableViewPointsSystem tableViewPointsSystem) {
		super(10, 0);
		this.setStyle("-fx-font-weight: bold;");
		BigDecimalConverter avgConverter = new BigDecimalConverter();
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
		tableViewPointsSystem.gradeAVGProperty().subscribe(avg -> {
			if (avg != null) {
				// FIXME apply for all grade systems
				if (group.getGradeSystem() == GradeSystem.getDefault()) {
					if (avg.doubleValue() < 2.0) {
						avgValue.setGraphic(emojiAmazing);
					} else if (avg.doubleValue() < 2.5) {
						avgValue.setGraphic(emojiNice);
					} else if (avg.doubleValue() < 3.0) {
						avgValue.setGraphic(emojiGood);
					} else if (avg.doubleValue() < 3.5) {
						avgValue.setGraphic(emojiOkay);
					} else if (avg.doubleValue() < 4.0) {
						avgValue.setGraphic(emojiBad);
					} else {
						avgValue.setGraphic(emojiDeavastating);
					}
				}
			}
		});
		this.add(avgLabel, 0, 0);
		this.add(avgValue, 1, 0);

		Label gradedLabel = new Label(TranslationController.translate("test_graded") + ": ");
		Label gradedValue = new Label();
		gradedValue.textProperty().bind(Bindings.createStringBinding(() -> {
			int graded = tableViewPointsSystem.gradedProperty().get();
			int size = group.getStudents().size();
			return graded + " " + TranslationController.translate("test_graded_outOf") + " " + size;
		}, tableViewPointsSystem.gradedProperty(), group.getStudents()));
		this.add(gradedLabel, 0, 1);
		this.add(gradedValue, 1, 1);

		// TODO add warnings if avg grade is better than / worse than ... (values from
		// grade system/config?)
		// TODO add critial grades (i.e. 5 & 6) to show individual percentage at once
	}
}
