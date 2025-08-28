package kafx.view.dialog.userinput;

import kafx.view.listview.MultiSelectListView;

public class UserInputMultiSelectListView<S> extends UserInputListView<S> {

	@SafeVarargs
	public UserInputMultiSelectListView(MultiSelectListView<S> listView, S... defaultValues) {
		super(listView, true, defaultValues);
	}

	@SafeVarargs
	public UserInputMultiSelectListView(MultiSelectListView<S> listView, boolean allowEmpty, S... defaultValues) {
		super(listView, allowEmpty, defaultValues);
	}

}
