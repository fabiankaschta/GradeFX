package gradefx.view.pane;

import java.math.BigDecimal;
import java.math.RoundingMode;

import gradefx.model.GradeSystem.Grade;
import gradefx.model.Group;
import gradefx.model.PointsSystem.BoundType;
import gradefx.model.Test;
import gradefx.view.converter.BoundTypeConverter;
import gradefx.view.style.Styles;
import gradefx.view.tableview.TableViewPointsSystem;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kafx.controller.Controller;
import kafx.lang.Translator;
import kafx.view.control.ComparableField;
import kafx.view.converter.BigDecimalConverter;
import kafx.view.converter.DoublePercentConverter;

public class TestStatisticsSidePane extends ScrollPane {

	private final Group group;
	private final Test test;
	private final TableViewPointsSystem tableViewPointsSystem;

	public TestStatisticsSidePane(Group group, Test test) {
		this.group = group;
		this.test = test;
		this.setPadding(new Insets(0));
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.getStyleClass().add("scroll-pane-no-focus");

		VBox content = new VBox(10) {
			// necessary for hiding / showing slider pane
			@Override
			protected double computeMinWidth(double height) {
				return computePrefWidth(height);
			}

			@Override
			protected double computePrefWidth(double height) {
				double width = 0;
				for (Node child : getChildren()) {
					if (child.isVisible()) {
						width = Math.max(width, child.prefWidth(height));
					}
				}
				return width + snappedLeftInset() + snappedRightInset();
			}

			@Override
			protected double computeMaxWidth(double height) {
				return computePrefWidth(height);
			}
		};
		content.setPadding(new Insets(10));
		this.setContent(content);
		content.minHeightProperty().bind(this.heightProperty());

		Label header = new Label(Translator.get("test_pointsSytem"));
		header.setAlignment(Pos.CENTER);
		header.setStyle("-fx-font-weight: bold;");
		this.tableViewPointsSystem = new TableViewPointsSystem(group, test);
		VBox headerBox = new VBox(10, header, this.tableViewPointsSystem);
		headerBox.setAlignment(Pos.CENTER);
		content.getChildren().add(headerBox);

		content.getChildren().add(new StatisticsGrid());
		content.getChildren().add(new SettingsPane());
		content.getChildren().add(new SliderPane());

		Styles.subscribeBackgroundColor(content, group.colorProperty());
		Styles.subscribeScrollBarColor(this, group.colorProperty());

		this.skinProperty().addListener((_, _, _) -> {
			ScrollBar scrollBarVertical = (ScrollBar) this
					.queryAccessibleAttribute(AccessibleAttribute.VERTICAL_SCROLLBAR);
			scrollBarVertical.setStyle("-fx-background-insets: 0, 0; -fx-padding: 0;");
			content.paddingProperty().bind(Bindings.createObjectBinding(
					() -> scrollBarVertical.isVisible() ? new Insets(10, 10 + scrollBarVertical.getWidth(), 10, 10)
							: new Insets(10),
					scrollBarVertical.visibleProperty(), scrollBarVertical.widthProperty()));
		});
	}

	private class StatisticsGrid extends GridPane {

		private StatisticsGrid() {
			super(10, 0);
			this.setStyle("-fx-font-weight: bold;");
			BigDecimalConverter avgConverter = new BigDecimalConverter();
			avgConverter.getDecimalFormat().setMinimumFractionDigits(2);
			avgConverter.getDecimalFormat().setMaximumFractionDigits(2);
			avgConverter.getDecimalFormat().setRoundingMode(RoundingMode.DOWN);

			Label avgLabel = new Label(Translator.get("test_avg") + ": ");
			Label avgValue = new Label();
			avgValue.textProperty().bind(Bindings.createStringBinding(() -> {
				BigDecimal avg = tableViewPointsSystem.getGradeAVG();
				if (avg == null) {
					return "-"; // '\u2014'; // long dash
				} else {
					return avgConverter.toString(avg);
				}
			}, tableViewPointsSystem.gradeAVGProperty()));
			this.add(avgLabel, 0, 0);
			this.add(avgValue, 1, 0);

			Label gradedLabel = new Label(Translator.get("test_graded") + ": ");
			Label gradedValue = new Label();
			gradedValue.textProperty().bind(Bindings.createStringBinding(() -> {
				int graded = tableViewPointsSystem.gradedProperty().get();
				int size = group.getStudents().size();
				return graded + " " + Translator.get("test_graded_outOf") + " " + size;
			}, tableViewPointsSystem.gradedProperty(), group.getStudents()));
			this.add(gradedLabel, 0, 1);
			this.add(gradedValue, 1, 1);
		}
	}

	private class SettingsPane extends TitledPane {

		private SettingsPane() {
			this.setText(Translator.get("pointsSystem_settings"));

			VBox settingsPaneContent = new VBox(10);

			CheckBox halfPointsCheckBox = new CheckBox();
			halfPointsCheckBox.selectedProperty().bindBidirectional(test.getPointsSystem().useHalfPointsProperty());
			Label halfPointsLabel = new Label(Translator.get("pointsSytem_halfPoints"));
			HBox halfPoints = new HBox(5, halfPointsLabel, halfPointsCheckBox);
			halfPoints.setAlignment(Pos.CENTER_LEFT);
			settingsPaneContent.getChildren().add(halfPoints);

			CheckBox tendenciesCheckBox = new CheckBox();
			Label tendenciesLabel = new Label(Translator.get("pointsSytem_tendencies"));
			ComparableField<BigDecimal> tendencyBoundField = new ComparableField<>(BigDecimal.ZERO, null,
					new BigDecimalConverter());
			tendencyBoundField.setPrefColumnCount(2);
			tendencyBoundField.disableProperty().bind(tendenciesCheckBox.selectedProperty().not());
			Label tendencyBoundLabel = new Label(Translator.get("pointsSytem_tendencyBound"));
			tendenciesCheckBox.setSelected(test.getPointsSystem().getTendencyBound() != null);
			tendenciesCheckBox.selectedProperty()
					.subscribe(v -> test.getPointsSystem().setTendencyBound(v ? tendencyBoundField.getValue() : null));
			tendencyBoundField.setValue(test.getPointsSystem().getTendencyBound() == null ? BigDecimal.ZERO
					: test.getPointsSystem().getTendencyBound());
			tendencyBoundField.valueProperty().subscribe(v -> {
				if (tendenciesCheckBox.isSelected()) {
					test.getPointsSystem().setTendencyBound(v);
				}
			});
			HBox tendencies = new HBox(5, tendenciesLabel, tendenciesCheckBox, tendencyBoundLabel, tendencyBoundField);
			tendencies.setAlignment(Pos.CENTER_LEFT);
			settingsPaneContent.getChildren().add(tendencies);

			this.setContent(settingsPaneContent);
			this.visibleProperty().bind(test.usePointsProperty());
		}

	}

	private class SliderPane extends TitledPane {

		private final ChoiceBox<BoundType> boundType;
		private final ObservableList<RatioSlider> sliderList = FXCollections.observableArrayList();

		private SliderPane() {

			this.setText(Translator.get("pointsSystem_silder"));

			VBox sliderPaneContent = new VBox(10);

			Label silderModeLabel = new Label(Translator.get("pointsSystem_silder_mode") + ":");
			boundType = new ChoiceBox<>(FXCollections.observableArrayList(BoundType.values()));
			boundType.setConverter(new BoundTypeConverter());
			boundType.getSelectionModel().select(test.getPointsSystem().getBoundType());
			boundType.getSelectionModel().selectedItemProperty().subscribe(_ -> {
				if (this.sliderList.size() > 0) {
					updatePointsSystem();
				}
			});
			// switch grades in sliders so that there should be no sudden changes when
			// switching from less to more
			boundType.getSelectionModel().selectedItemProperty().addListener((_, oldValue, newValue) -> {
				Grade[] grades = group.getGradeSystem().getPossibleGradesASC();
				if ((oldValue == BoundType.LESS_THAN || oldValue == BoundType.LESSOREQUAL_THAN)
						&& (newValue == BoundType.MORE_THAN || newValue == BoundType.MOREOREQUAL_THAN)) {
					for (int i = sliderList.size() - 1; i >= 0; i--) {
						if (!this.sliderList.get(i).gradeList.contains(grades[grades.length - 1])) {
							this.sliderList.get(i).gradeList.addFirst(grades[grades.length - 1]);
						}
					}
					for (int i = sliderList.size() - 1; i >= 0; i--) {
						Grade grade = this.sliderList.get(i).getSelectedGrade();
						for (int g = 0; g < grades.length; g++) {
							if (grades[g].getNumericalValue() == grade.getNumericalValue()) {
								this.sliderList.get(i).gradeBox.getSelectionModel().select(grades[g + 1]);
								break;
							}
						}
					}
					for (int i = sliderList.size() - 1; i >= 0; i--) {
						this.sliderList.get(i).gradeList.remove(grades[0]);
					}
				} else if ((oldValue == BoundType.MORE_THAN || oldValue == BoundType.MOREOREQUAL_THAN)
						&& (newValue == BoundType.LESS_THAN || newValue == BoundType.LESSOREQUAL_THAN)) {
					for (int i = 0; i < this.sliderList.size(); i++) {
						if (!this.sliderList.get(i).gradeList.contains(grades[0])) {
							this.sliderList.get(i).gradeList.addLast(grades[0]);
						}
					}
					for (int i = 0; i < this.sliderList.size(); i++) {
						Grade grade = this.sliderList.get(i).getSelectedGrade();
						for (int g = 0; g < grades.length; g++) {
							if (grades[g].getNumericalValue() == grade.getNumericalValue()) {
								this.sliderList.get(i).gradeBox.getSelectionModel().select(grades[g - 1]);
								break;
							}
						}
					}
					for (int i = 0; i < this.sliderList.size(); i++) {
						this.sliderList.get(i).gradeList.remove(grades[grades.length - 1]);
					}
				}
				this.updateBounds();
			});
			Button addSliderButton = new Button("+");
			addSliderButton.setOnAction(_ -> new RatioSlider());
			sliderList.subscribe(() -> addSliderButton
					.setDisable(sliderList.size() == group.getGradeSystem().getPossibleGradesDESC().length));
			HBox sliderMenuMode = new HBox(5, silderModeLabel, boundType, addSliderButton);
			sliderMenuMode.setAlignment(Pos.CENTER_LEFT);
			BorderPane sliderMenu = new BorderPane();
			sliderMenu.setLeft(sliderMenuMode);
			sliderMenu.setRight(addSliderButton);
			sliderPaneContent.getChildren().add(sliderMenu);

			VBox sliderView = new VBox(2);
			Bindings.bindContent(sliderView.getChildren(), this.sliderList);
			ObjectProperty<BigDecimal>[] ratioBounds = test.getPointsSystem().getRatioBounds();
			Grade[] grades = group.getGradeSystem().getPossibleGradesASC();
			for (int i = 0; i < grades.length; i++) {
				if (ratioBounds[i].get() != null) {
					new RatioSlider(grades[i], ratioBounds[i].get());
				}
			}
			updateSliders();
			sliderPaneContent.getChildren().add(sliderView);

			this.setContent(sliderPaneContent);
			this.visibleProperty().bind(test.usePointsProperty());
		}

		private void updatePointsSystem() {
			Grade[] grades = group.getGradeSystem().getPossibleGradesASC();
			BigDecimal[] ratioBounds = new BigDecimal[grades.length];
			for (RatioSlider box : sliderList) {
				Grade grade = box.getSelectedGrade();
				for (int i = 0; i < grades.length; i++) {
					if (grades[i].getNumericalValue() == grade.getNumericalValue()) {
						ratioBounds[i] = BigDecimal.valueOf(box.slider.getValue());
						break;
					}
				}
			}
			test.getPointsSystem().apply(boundType.getValue(), ratioBounds);
		}

		private void updateSliders() {
			// inverted order
			sliderList.sort((s1, s2) -> -s1.getSelectedGrade().compareTo(s2.getSelectedGrade()));
			for (int i = 0; i < sliderList.size(); i++) {
				RatioSlider box = sliderList.get(i);
				box.slider.minProperty().unbind();
				box.slider.maxProperty().unbind();
				if (i != 0) {
					box.slider.maxProperty().bind(sliderList.get(i - 1).slider.valueProperty());
				} else {
					box.slider.maxProperty().set(1);
				}
				if (i != sliderList.size() - 1) {
					box.slider.minProperty().bind(sliderList.get(i + 1).slider.valueProperty());
				} else {
					box.slider.minProperty().set(0);
				}
			}
			updatePointsSystem();
		}

		private class RatioSlider extends HBox {
			private final ObservableList<Grade> gradeList = FXCollections.observableArrayList();
			private final Slider slider;
			private final ChoiceBox<Grade> gradeBox;

			private RatioSlider() {
				this(null, null);
				updateSliders();
			}

			private RatioSlider(Grade grade, BigDecimal value) {
				super(5);
				this.setAlignment(Pos.CENTER_LEFT);
				this.slider = new Slider(0, 1, 0.5);
				// FIXME slider should resize to fill space in sliderBox
				this.slider.prefWidthProperty().bind(Controller.fontSizeProperty().multiply(7));
				ComparableField<Double> field = new ComparableField<>(this.slider.minProperty().asObject(),
						this.slider.maxProperty().asObject(), new DoublePercentConverter(1));
				field.setPrefColumnCount(3);
				field.valueProperty().subscribe(v -> this.slider.setValue(v));
				slider.valueProperty().subscribe(v -> field.setValue(v.doubleValue()));
				this.gradeList.addAll(group.getGradeSystem().getPossibleGradesDESC());
				switch (boundType.getValue()) {
				case LESSOREQUAL_THAN:
				case LESS_THAN:
					this.gradeList.removeFirst();
					break;
				case MOREOREQUAL_THAN:
				case MORE_THAN:
					this.gradeList.removeLast();
					break;
				}
				this.gradeList.removeIf(g -> !sliderList
						.filtered(silder -> g.getNumericalValue().equals(silder.getSelectedGrade().getNumericalValue()))
						.isEmpty());
				this.gradeBox = new ChoiceBox<Grade>(this.gradeList);
				this.gradeBox.getSelectionModel().selectedItemProperty().subscribe((oldSelection, newSelection) -> {
					// TODO update value to current ratio?
					for (RatioSlider slider : sliderList) {
						if (slider != this) {
							if (newSelection != null) {
								slider.gradeList.remove(newSelection);
							}
							if (oldSelection != null) {
								slider.gradeList.add(oldSelection);
							}
							// inverted order
							slider.gradeList.sort((g1, g2) -> -g1.compareTo(g2));
						}
					}
					if (oldSelection != null) {
						updateSliders();
					}
				});
				Button removeButton = new Button("-");
				removeButton.setOnAction(_ -> {
					sliderList.remove(this);
					for (RatioSlider slider : sliderList) {
						if (slider != this) {
							slider.gradeList.add(this.getSelectedGrade());
						}
						// inverted order
						slider.gradeList.sort((g1, g2) -> -g1.compareTo(g2));
					}
					updateSliders();
				});
				this.getChildren().addAll(field, this.slider, this.gradeBox, removeButton);
				if (value == null) {
					switch (boundType.getValue()) {
					case LESSOREQUAL_THAN:
					case LESS_THAN:
						this.slider.setValue(
								test.getPointsSystem().getUpperBoundForGrade(this.gradeList.getLast()).doubleValue()
										/ test.getTotalPoints().doubleValue());
						break;
					case MOREOREQUAL_THAN:
					case MORE_THAN:
						this.slider.setValue(
								test.getPointsSystem().getLowerBoundForGrade(this.gradeList.getLast()).doubleValue()
										/ test.getTotalPoints().doubleValue());
						break;
					}
				} else {
					this.slider.setValue(value.doubleValue());
				}
				if (grade == null) {
					this.gradeBox.getSelectionModel().select(this.gradeList.getLast());
				} else {
					this.gradeBox.getSelectionModel().select(grade);
				}
				this.slider.valueProperty().addListener((_, _, _) -> updatePointsSystem());
				sliderList.add(this);
			}

			private Grade getSelectedGrade() {
				return this.gradeBox.getSelectionModel().getSelectedItem();
			}
		}
	}

}
