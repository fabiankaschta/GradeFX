package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;
import java.util.function.Function;

import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.gradefx.view.converter.PointsConverter;
import org.openjfx.gradefx.view.converter.TestTaskConverter;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.pattern.PatternGuesser;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.dialog.DialogPaneCustom;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputTreeView;
import org.openjfx.kafx.view.treeview.DragAndDropCellFactory;
import org.openjfx.kafx.view.treeview.TreeCellCustomNodeAddRemove;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class DialogEditTestTasks extends Dialog<Boolean> {

	private final Test test;
	private final TreeView<TestTask> treeViewTestTask;
	private final ButtonType doneButtonType = new ButtonType(TranslationController.translate("dialog_button_done"),
			ButtonData.OK_DONE);

	public DialogEditTestTasks(Test test) {
		this.test = test;

		this.setTitle(TranslationController.translate("dialog_edit_testTasks_title"));

		DialogPaneCustom dialogPane = new DialogPaneCustom();
		dialogPane.setDetailsButtonMoreText(TranslationController.translate("dialog_edit_testTasks_more"));
		dialogPane.setDetailsButtonLessText(TranslationController.translate("dialog_edit_testTasks_less"));

		Label helper = new Label(TranslationController.translate("dialog_edit_testTasks_help"));
		helper.setWrapText(true);
		dialogPane.setExpandableContent(helper);

		this.treeViewTestTask = new TreeView<>(test.getTasksRoot());
		Function<TestTask, Node> converter = testTask -> {
			if (testTask == test.getTasksRoot()) {
				return new Label(TranslationController.translate("test_testTaskTree"));
			} else {
				HBox box = new HBox(10);

				TextField nameField = new TextField();
				nameField.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(3));
				UserInputTextInput name = new UserInputTextInput(nameField, testTask.getName(), false);
				name.valueProperty().subscribe(n -> testTask.setName(n));
				nameField.focusedProperty().addListener((_, _, focused) -> {
					if (focused) {
						this.treeViewTestTask.getSelectionModel().select(testTask);
					}
				});
				nameField.addEventFilter(KeyEvent.KEY_PRESSED, getKeyHandler(nameField, testTask));

				Label pointsLabel = new Label(TranslationController.translate("test_points_short") + ":");
				pointsLabel.prefHeightProperty().bind(box.heightProperty());

				ComparableField<BigDecimal> pointsField = new ComparableField<>(BigDecimal.ZERO, null,
						testTask.getMaxPoints(), new PointsConverter(true), false);
				pointsField.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(3));
				UserInputComparableInput<BigDecimal> points = new UserInputComparableInput<>(pointsField,
						testTask.getMaxPoints());
				testTask.maxPointsProperty().subscribe(p -> points.setValue(p));
				points.valueProperty().subscribe(p -> {
					if (testTask.isLeaf()) {
						testTask.setMaxPoints(p);
					}
				});
				points.disableProperty().bind(testTask.leafProperty().not());
				pointsField.focusedProperty().addListener((_, _, focused) -> {
					if (focused) {
						this.treeViewTestTask.getSelectionModel().select(testTask);
					}
				});
				pointsField.addEventFilter(KeyEvent.KEY_PRESSED, getKeyHandler(pointsField, testTask));

				this.treeViewTestTask.getSelectionModel().selectedItemProperty().subscribe(item -> {
					if (item == testTask) {
						Platform.runLater(() -> {
							name.requestFocus();
						});
					}
				});

				box.getChildren().addAll(name, pointsLabel, points);
				return box;
			}
		};
		this.treeViewTestTask.setEditable(true);
		this.treeViewTestTask.setCellFactory(new DragAndDropCellFactory<>(new TestTaskConverter(),
				_ -> new TreeCellCustomNodeAddRemove<TestTask>(converter, item -> addTo(item), null,
						item -> remove(item))));
		this.treeViewTestTask.getSelectionModel().selectedIndexProperty()
				.subscribe(index -> this.treeViewTestTask.scrollTo(index.intValue()));

		UserInputTreeView<TestTask> testTaskTree = new UserInputTreeView<>(this.treeViewTestTask);
		testTaskTree.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(30));

		dialogPane.setContent(testTaskTree);
		this.setDialogPane(dialogPane);

		dialogPane.getButtonTypes().add(this.doneButtonType);
		this.setOnShown(_ -> {
			testTaskTree.requestFocus();
			if (this.test.getTasksRoot().isLeaf()) {
				this.addTo(this.test.getTasksRoot());
			}
		});

		this.setResultConverter(_ -> {
			if (test.getTasksRoot().isLeaf()) {
				// TODO alert?
				test.setUseTasks(false);
				if (test.getTotalPoints() == null || test.getTotalPoints().compareTo(BigDecimal.ZERO) <= 0) {
					// TODO user input
					test.setUsePoints(false);
				}
			}
			return true;
		});

		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

	private void remove(TreeItem<TestTask> item) {
		TreeItem<TestTask> parent = item.getParent();
		if (parent == null) {
			throw new IllegalArgumentException("can't remove root");
		} else {
			TestTask parentNode = (TestTask) parent;
			parentNode.removeSubtask((TestTask) item);
		}
	}

	private TestTask addTo(TreeItem<TestTask> item) {
		if (item == null) {
			return null;
		} else {
			String name;
			if (item.isLeaf()) {
				if (((TestTask) item).isRoot()) {
					name = "1";
				} else if (((TestTask) item.getParent()).isRoot()) {
					name = "a";
				} else {
					name = "i";
				}
			} else {
				TestTask previous = (TestTask) item.getChildren().getLast();
				name = PatternGuesser.guessPattern(previous.getName());
			}
			TestTask task = TestTask.createTask(this.test, name, BigDecimal.ZERO);
			((TestTask) item).addSubtask(task);
			this.treeViewTestTask.getSelectionModel().select(task);
			return task;
		}
	}

	private EventHandler<KeyEvent> getKeyHandler(EventTarget target, TestTask testTask) {
		return event -> {
			if (event.getTarget() == target) {
				if (event.getCode() == KeyCode.DOWN) {
					event.consume();
					if (event.isShiftDown()) {
						this.treeViewTestTask.getSelectionModel().selectNext();
					} else if (!testTask.isRoot()) {
						TestTask nextsibling = (TestTask) testTask.nextSibling();
						if (nextsibling == null) {
							addTo(testTask.getParent());
						} else {
							this.treeViewTestTask.getSelectionModel().selectNext();
						}
					}
				} else if (event.getCode() == KeyCode.UP) {
					event.consume();
					this.treeViewTestTask.getSelectionModel().selectPrevious();
				} else if (event.getCode() == KeyCode.ENTER) {
					if (event.isShiftDown()) {
						event.consume();
						if (testTask.getParent() != null) {
							addTo(testTask.getParent().getParent());
						}
					} else if (event.isControlDown()) {
						((Button) this.getDialogPane().lookupButton(this.doneButtonType)).fire();
						event.consume();
					} else {
						event.consume();
						addTo(testTask);
					}
				} else if (event.getCode() == KeyCode.DELETE) {
					event.consume();
					if (!testTask.isRoot()) {
						remove(testTask);
					}
				}
			}
		};
	}

}
