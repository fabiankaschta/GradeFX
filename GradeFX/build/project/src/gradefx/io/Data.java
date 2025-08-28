package gradefx.io;

import java.io.Serializable;

import gradefx.model.Group;
import gradefx.model.TestGroup.TestGroupSystem;
import kafx.io.DataObject;

class Data implements Serializable {

	private static final long serialVersionUID = 1212549241575978289L;

	// main data - groups
	DataObject<Group>[] groups;
	// main data - testGroupSystems
	// TODO move to config ?
	DataObject<TestGroupSystem>[] testGroupSystems;

	// index of selected group
	int selectedGroup;
	// index of selected tab in groups
	int[] selectedTabInGroup;

}
