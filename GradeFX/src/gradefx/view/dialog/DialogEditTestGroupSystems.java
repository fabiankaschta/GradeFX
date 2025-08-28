package gradefx.view.dialog;

import java.util.Optional;

import kafx.lang.Translator;
import gradefx.model.Group;
import gradefx.model.TestGroup.TestGroupSystem;
import gradefx.view.converter.TestGroupSystemConverter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import kafx.view.alert.AlertDelete;
import kafx.view.dialog.DialogUserInput;
import kafx.view.dialog.userinput.UserInputChoiceBoxButtons;
import kafx.view.dialog.userinput.UserInputTreeView;
import kafx.view.treeview.DragAndDropCellFactory;
import kafx.view.treeview.TreeCellCustomAddRemove;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class DialogEditTestGroupSystems extends DialogUserInput<Boolean> {

	private final UserInputChoiceBoxButtons<TestGroupSystem> testGroupSystem;
	private final UserInputTreeView<TestGroupSystem> testGroupTree;

	public DialogEditTestGroupSystems() {
		super(Translator.get("dialog_edit_testGroups_title"));

		TreeView<TestGroupSystem> treeView = new TreeView<>();
		treeView.setEditable(true);
		treeView.setCellFactory(
				new DragAndDropCellFactory<>(_ -> new TreeCellCustomAddRemove<TestGroupSystem>(item -> addTo(item),
						item -> edit(item), item -> remove(item))));
		treeView.setPrefHeight(300); // default in TreeViewSkin is 400

		ChoiceBox<TestGroupSystem> choiceBox = new ChoiceBox<>();
		choiceBox.getItems().addAll(TestGroupSystem.getTestGroupSystems());
		choiceBox.getItems().remove(TestGroupSystem.get(Translator.get("testGroupSystem_NO_GROUPS")));
		choiceBox.setConverter(new TestGroupSystemConverter());
		choiceBox.getSelectionModel().selectedItemProperty().addListener(_ -> {
			TestGroupSystem item = choiceBox.getSelectionModel().getSelectedItem();
			if (item != null) {
				treeView.setRoot(item);
			}
		});
		choiceBox.disableProperty().bind(choiceBox.getSelectionModel().selectedItemProperty().isNull());

		Button add = new Button(Translator.get("dialog_edit_testGroups_add"));
		add.setOnAction(_ -> new DialogAddTestGroupSystem().showAndWait().ifPresent(response -> {
			if (response != null) {
				choiceBox.getItems().add(response);
				choiceBox.getSelectionModel().select(response);
			} else {
				// abort add, do nothing
			}
		}));
		Button remove = new Button(Translator.get("dialog_edit_testGroups_remove"));
		remove.setOnAction(_ -> new AlertDelete(Translator.get("testGroups_testGroupSystem") + " "
				+ choiceBox.getSelectionModel().getSelectedItem().getName()).showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						int index = choiceBox.getSelectionModel().getSelectedIndex();
						TestGroupSystem item = choiceBox.getSelectionModel().getSelectedItem();
						choiceBox.getItems().remove(item);
						TestGroupSystem.removeTestGroupSystem(item);
						if (choiceBox.getItems().size() == 0) {
							choiceBox.getSelectionModel().select(null);
						} else if (index > 0) {
							choiceBox.getSelectionModel().select(index - 1);
						} else {
							choiceBox.getSelectionModel().select(index);
						}
						choiceBox.requestFocus();
					} else {
						// abort delete, do nothing
					}
				}));
		// disable if any group uses selected system
		remove.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			if (choiceBox.getSelectionModel().getSelectedItem() == null) {
				return true;
			}
			TestGroupSystem selected = choiceBox.getSelectionModel().getSelectedItem();
			for (Group group : Group.getGroups()) {
				if (group.getTestGroupSystem() == selected) {
					return true;
				}
			}
			return false;
		}, choiceBox.getSelectionModel().selectedItemProperty()));

		this.testGroupSystem = new UserInputChoiceBoxButtons<>(choiceBox);
		this.testGroupSystem.addButton(add);
		this.testGroupSystem.addButton(remove);
		super.addInput(this.testGroupSystem, Translator.get("testGroups_testGroupSystem"));

		this.testGroupTree = new UserInputTreeView<>(treeView);
		super.addInput(this.testGroupTree, Translator.get("testGroups_testGroupTree"));
		this.testGroupTree.visibleProperty().bind(this.testGroupSystem.isSelectedExpression());

		ButtonType doneButtonType = new ButtonType(Translator.get("dialog_button_done"), ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(doneButtonType);

		this.setResultConverter(_ -> true);
	}

	private void remove(TreeItem<TestGroupSystem> item) {
		TreeItem<TestGroupSystem> parent = item.getParent();
		if (parent == null) {
			throw new IllegalArgumentException("can't remove root");
		} else {
			TestGroupSystem parentNode = (TestGroupSystem) parent;
			parentNode.removeSubgroup((TestGroupSystem) item);
		}
	}

	private void addTo(TreeItem<TestGroupSystem> item) {
		Platform.runLater(() -> new DialogAddSubTestGroupSystem((TestGroupSystem) item).showAndWait());
	}

	private Optional<Boolean> edit(TreeItem<TestGroupSystem> item) {
		return new DialogEditSubTestGroupSystem((TestGroupSystem) item).showAndWait();
	}

}
