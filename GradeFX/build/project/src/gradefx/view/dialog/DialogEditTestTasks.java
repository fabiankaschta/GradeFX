package gradefx.view.dialog;

import java.math.BigDecimal;
import java.util.Optional;

import kafx.lang.Translator;
import gradefx.model.Test;
import gradefx.model.Test.TestTask;
import gradefx.view.converter.TestTaskConverter;
import javafx.scene.control.ButtonBar.ButtonData;
import kafx.view.dialog.DialogUserInput;
import kafx.view.dialog.userinput.UserInputTreeView;
import kafx.view.treeview.DragAndDropCellFactory;
import kafx.view.treeview.TreeCellCustomAddRemove;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class DialogEditTestTasks extends DialogUserInput<Boolean> {

	private final Test test;

	public DialogEditTestTasks(Test test) {
		super(Translator.get("dialog_edit_testTasks_title"));
		this.test = test;

		TreeView<TestTask> treeViewTestTask = new TreeView<>(test.getTasksRoot());
		treeViewTestTask.setEditable(true);
		treeViewTestTask.setCellFactory(new DragAndDropCellFactory<>(new TestTaskConverter(),
				_ -> new TreeCellCustomAddRemove<TestTask>(new TestTaskConverter(), item -> addTo(item),
						item -> edit(item), item -> remove(item))));
		UserInputTreeView<TestTask> testTaskTree = new UserInputTreeView<>(treeViewTestTask);
		super.addInput(testTaskTree, Translator.get("test_testTaskTree"));

		ButtonType doneButtonType = new ButtonType(Translator.get("dialog_button_done"), ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(doneButtonType);

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

	private Optional<TestTask> addTo(TreeItem<TestTask> item) {
		return new DialogAddTestTask(test, (TestTask) item).showAndWait();
	}

	private Optional<Boolean> edit(TreeItem<TestTask> item) {
		return new DialogEditTestTask((TestTask) item).showAndWait();
	}

}
