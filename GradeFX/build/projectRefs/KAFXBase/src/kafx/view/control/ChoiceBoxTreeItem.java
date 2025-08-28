package kafx.view.control;

import java.util.List;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TreeItem;
import javafx.util.StringConverter;
import kafx.view.converter.DefaultForwardConverter;

public class ChoiceBoxTreeItem<T> extends ChoiceBox<TreeItem<T>> {

	private class ChoiceBoxTreeItemConverter extends StringConverter<TreeItem<T>> {

		private StringConverter<TreeItem<T>> singleConverter;

		private ChoiceBoxTreeItemConverter(StringConverter<TreeItem<T>> singleConverter) {
			this.singleConverter = singleConverter;
		}

		@Override
		public String toString(TreeItem<T> object) {
			if (object == null) {
				return "";
			}
			String prefix = "";
			TreeItem<T> current = object.getParent();
			while (current != null) {
				current = current.getParent();
				prefix += '\t';
			}
			return prefix + singleConverter.toString(object);
		}

		@Override
		public TreeItem<T> fromString(String string) {
			throw new UnsupportedOperationException("choiceBoxTreeItem converter does not allow to convert back");
		}

	}

	public ChoiceBoxTreeItem(TreeItem<T> root) {
		this(root, true);
	}

	public ChoiceBoxTreeItem(TreeItem<T> root, StringConverter<TreeItem<T>> converter) {
		this(root, converter, true);
	}

	public ChoiceBoxTreeItem(TreeItem<T> root, boolean showRoot) {
		this(root, new DefaultForwardConverter<>(), showRoot);
	}

	public ChoiceBoxTreeItem(TreeItem<T> root, StringConverter<TreeItem<T>> converter, boolean showRoot) {
		this.setConverter(new ChoiceBoxTreeItemConverter(converter));
		addTreeItemHelper(root, getItems(), showRoot);
	}

	private void addTreeItemHelper(TreeItem<T> root, List<TreeItem<T>> items, boolean addRoot) {
		if (addRoot) {
			items.add(root);
		}
		for (TreeItem<T> t : root.getChildren()) {
			items.add(t);
			addTreeItemHelper(t, items, false);
		}
	}

}
