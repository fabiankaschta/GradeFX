package org.openjfx.gradefx.view.dialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.gradefx.view.converter.PointsConverter;
import org.openjfx.gradefx.view.converter.TestGroupSystemConverter;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.alert.AlertDelete;
import org.openjfx.kafx.view.control.ComparableField;
import org.openjfx.kafx.view.dialog.DialogUserInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputComboBoxButtons;
import org.openjfx.kafx.view.dialog.userinput.UserInputComparableInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputTreeView;
import org.openjfx.kafx.view.treeview.DragAndDropCellFactory;
import org.openjfx.kafx.view.treeview.TreeCellCustomNodeAddRemove;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class DialogEditTestGroupSystems extends DialogUserInput<Boolean> {

	private final UserInputComboBoxButtons<TestGroupSystem> testGroupSystem;
	private final UserInputTreeView<TestGroupSystem> testGroupTree;
	private final TreeView<TestGroupSystem> treeViewTestGroupSystems;

	private final Map<TestGroupSystem, StringProperty> nameProperties = new HashMap<>();
	private final Map<TestGroupSystem, ObjectProperty<BigDecimal>> weightProperties = new HashMap<>();

	public DialogEditTestGroupSystems() {
		super(TranslationController.translate("dialog_edit_testGroups_title"));

		this.treeViewTestGroupSystems = new TreeView<>();

		ObservableList<TestGroupSystem> baseList = FXCollections
				.observableArrayList(testGroupSystem -> new Observable[] { testGroupSystem.nameProperty() });
		baseList.addAll(TestGroupSystem.getTestGroupSystems());
		baseList.remove(TestGroupSystem.NONE);

		ComboBox<TestGroupSystem> comboBox = new ComboBox<>(baseList);
		comboBox.setConverter(new TestGroupSystemConverter());
		comboBox.getSelectionModel().selectedItemProperty().addListener(_ -> {
			TestGroupSystem item = comboBox.getSelectionModel().getSelectedItem();
			if (item != null) {
				this.treeViewTestGroupSystems.setRoot(item);
			}
		});
		comboBox.disableProperty().bind(comboBox.getSelectionModel().selectedItemProperty().isNull());

		Button add = new Button(TranslationController.translate("dialog_edit_testGroups_add"));
		add.setOnAction(_ -> {
			TestGroupSystem root = TestGroupSystem.createRoot("");
			comboBox.getItems().add(root);
			comboBox.getSelectionModel().select(root);
		});
		Button remove = new Button(TranslationController.translate("dialog_edit_testGroups_remove"));
		remove.setOnAction(_ -> new AlertDelete(TranslationController.translate("testGroups_testGroupSystem") + " "
				+ comboBox.getSelectionModel().getSelectedItem().getName()).showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						int index = comboBox.getSelectionModel().getSelectedIndex();
						TestGroupSystem item = comboBox.getSelectionModel().getSelectedItem();
						comboBox.getItems().remove(item);
						TestGroupSystem.removeTestGroupSystem(item);
						if (comboBox.getItems().size() == 0) {
							comboBox.getSelectionModel().select(null);
						} else if (index > 0) {
							comboBox.getSelectionModel().select(index - 1);
						} else {
							comboBox.getSelectionModel().select(index);
						}
						comboBox.requestFocus();
					} else {
						// abort delete, do nothing
					}
				}));
		// disable if any group uses selected system
		remove.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			if (comboBox.getSelectionModel().getSelectedItem() == null) {
				return true;
			}
			TestGroupSystem selected = comboBox.getSelectionModel().getSelectedItem();
			for (Group group : Group.getGroups()) {
				if (group.getTestGroupSystem() == selected) {
					return true;
				}
			}
			return false;
		}, comboBox.getSelectionModel().selectedItemProperty()));

		this.testGroupSystem = new UserInputComboBoxButtons<>(comboBox);
		this.testGroupSystem.addButton(add);
		this.testGroupSystem.addButton(remove);
		super.addInput(this.testGroupSystem, TranslationController.translate("testGroups_testGroupSystem"));

		Function<TestGroupSystem, Node> converter = testGroupSystem -> {
			if (testGroupSystem.isRoot()) {
				TextField nameField = new TextField();
				nameField.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(20));
				UserInputTextInput name = new UserInputTextInput(nameField, testGroupSystem.getName(), false);
				name.valueProperty().subscribe(n -> testGroupSystem.setName(n));
				nameField.focusedProperty().addListener((_, _, focused) -> {
					if (focused) {
						this.treeViewTestGroupSystems.getSelectionModel().select(testGroupSystem);
					}
				});
				nameField.addEventFilter(KeyEvent.KEY_PRESSED, getKeyHandler(nameField, testGroupSystem));
				this.nameProperties.put(testGroupSystem, nameField.textProperty());

				this.treeViewTestGroupSystems.getSelectionModel().selectedItemProperty().subscribe(item -> {
					if (item == testGroupSystem) {
						Platform.runLater(() -> {
							name.requestFocus();
						});
					}
				});

				return name;
			} else {
				HBox box = new HBox(10);

				TextField nameField = new TextField();
				nameField.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(20));
				UserInputTextInput name = new UserInputTextInput(nameField, testGroupSystem.getName(), false);
				name.valueProperty().subscribe(n -> testGroupSystem.setName(n));
				nameField.focusedProperty().addListener((_, _, focused) -> {
					if (focused) {
						this.treeViewTestGroupSystems.getSelectionModel().select(testGroupSystem);
					}
				});
				nameField.addEventFilter(KeyEvent.KEY_PRESSED, getKeyHandler(nameField, testGroupSystem));
				this.nameProperties.put(testGroupSystem, nameField.textProperty());

				Label weightLabel = new Label(TranslationController.translate("testGroup_weight") + ":");
				weightLabel.prefHeightProperty().bind(box.heightProperty());

				ComparableField<BigDecimal> weightField = new ComparableField<>(BigDecimal.ZERO, null,
						testGroupSystem.getWeight(), new PointsConverter(true), false);
				weightField.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(3));
				UserInputComparableInput<BigDecimal> weight = new UserInputComparableInput<>(weightField,
						testGroupSystem.getWeight());
				weight.valueProperty().subscribe(w -> testGroupSystem.setWeight(w));
				weightField.focusedProperty().addListener((_, _, focused) -> {
					if (focused) {
						this.treeViewTestGroupSystems.getSelectionModel().select(testGroupSystem);
					}
				});
				weightField.addEventFilter(KeyEvent.KEY_PRESSED, getKeyHandler(weightField, testGroupSystem));
				this.weightProperties.put(testGroupSystem, weightField.valueProperty());

				this.treeViewTestGroupSystems.getSelectionModel().selectedItemProperty().subscribe(item -> {
					if (item == testGroupSystem) {
						Platform.runLater(() -> {
							name.requestFocus();
						});
					}
				});

				box.getChildren().addAll(name, weightLabel, weight);
				createDisableBinding();
				return box;
			}
		};
		this.treeViewTestGroupSystems.setEditable(true);
		this.treeViewTestGroupSystems.setCellFactory(
				new DragAndDropCellFactory<>(_ -> new TreeCellCustomNodeAddRemove<TestGroupSystem>(converter,
						item -> addTo(item), item -> remove(item))));
		this.treeViewTestGroupSystems.setPrefHeight(300); // default in TreeViewSkin is 400

		this.testGroupTree = new UserInputTreeView<>(this.treeViewTestGroupSystems);
		this.testGroupTree.prefWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(41));
		super.addInput(this.testGroupTree, TranslationController.translate("testGroups_testGroupTree"));
		this.testGroupTree.visibleProperty().bind(this.testGroupSystem.isSelectedExpression());

		this.getDialogPane().getButtonTypes().add(DONE);

		this.setResultConverter(_ -> true);
	}

	private void remove(TreeItem<TestGroupSystem> item) {
		TreeItem<TestGroupSystem> parent = item.getParent();
		if (parent == null) {
			throw new IllegalArgumentException("can't remove root");
		} else {
			TestGroupSystem parentNode = (TestGroupSystem) parent;
			parentNode.removeSubgroup((TestGroupSystem) item);
			this.nameProperties.remove(item);
			this.weightProperties.remove(item);
			createDisableBinding();
		}
	}

	private TestGroupSystem addTo(TreeItem<TestGroupSystem> item) {
		if (item == null) {
			createDisableBinding();
			return null;
		} else {
			TestGroupSystem subGroup = TestGroupSystem.createSubSystem("", BigDecimal.ONE);
			((TestGroupSystem) item).addSubgroup(subGroup);
			this.treeViewTestGroupSystems.getSelectionModel().select(subGroup);
			return subGroup;
		}
	}

	private void createDisableBinding() {
		Button button = (Button) this.getDialogPane().lookupButton(DONE);
		List<Observable> observables = new ArrayList<>();
		observables.addAll(this.nameProperties.values());
		observables.addAll(this.weightProperties.values());
		button.disableProperty().unbind();
		button.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			for (StringProperty name : this.nameProperties.values()) {
				if (name.get() == null || name.get().length() <= 0) {
					return true;
				}
			}
			for (ObjectProperty<BigDecimal> points : this.weightProperties.values()) {
				if (points.get() == null || points.get().signum() != 1) {
					return true;
				}
			}
			return false;
		}, observables.toArray(n -> new Observable[n])));
	}

	private EventHandler<KeyEvent> getKeyHandler(EventTarget target, TestGroupSystem testGroupSystem) {
		return event -> {
			if (event.getTarget() == target) {
				if (event.getCode() == KeyCode.DOWN) {
					event.consume();
					if (event.isShiftDown() || testGroupSystem.isRoot()) {
						this.treeViewTestGroupSystems.getSelectionModel().selectNext();
					} else {
						TestGroupSystem nextsibling = (TestGroupSystem) testGroupSystem.nextSibling();
						if (nextsibling == null) {
							addTo(testGroupSystem.getParent());
						} else {
							this.treeViewTestGroupSystems.getSelectionModel().selectNext();
						}
					}
				} else if (event.getCode() == KeyCode.UP) {
					event.consume();
					this.treeViewTestGroupSystems.getSelectionModel().selectPrevious();
				} else if (event.getCode() == KeyCode.ENTER) {
					if (event.isShiftDown()) {
						event.consume();
						if (testGroupSystem.getParent() != null) {
							addTo(testGroupSystem.getParent().getParent());
						}
					} else if (event.isControlDown()) {
						((Button) this.getDialogPane().lookupButton(DONE)).fire();
						event.consume();
					} else {
						event.consume();
						addTo(testGroupSystem);
					}
				} else if (event.getCode() == KeyCode.DELETE) {
					event.consume();
					if (!testGroupSystem.isRoot()) {
						remove(testGroupSystem);
					}
				}
			}
		};
	}

}
