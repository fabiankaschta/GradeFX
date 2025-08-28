package gradefx.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import gradefx.controller.Controller;
import gradefx.io.Config.ConfigOption;
import gradefx.model.Group;
import gradefx.model.TestGroup.TestGroupSystem;
import gradefx.view.alert.AlertException;
import gradefx.view.pane.GroupsPane;
import kafx.io.DataObject;

public class FileOutput {

	public static void writeToFile(String pathname) {
		try {
			writeToFile(new File(pathname));
		} catch (Exception e) {
			new AlertException(e);
			return;
		}
	}

	@SuppressWarnings("unchecked")
	public static void writeToFile(File file) {
		if (file == null) {
			new AlertException(new NullPointerException());
			return;
		}
		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(new FileOutputStream(file));
			Data data = new Data();
			List<Group> gs = Group.getGroups();
			data.groups = new DataObject[gs.size()];
			data.selectedTabInGroup = new int[gs.size()];
			for (int i = 0; i < gs.size(); i++) {
				Group group = gs.get(i);
				data.groups[i] = group.serialize();
				data.selectedTabInGroup[i] = GroupsPane.getSelectedTabInGroupIndex(group);
			}
			List<TestGroupSystem> tgss = TestGroupSystem.getTestGroupSystems();
			data.testGroupSystems = new DataObject[tgss.size()];
			for (int i = 0; i < tgss.size(); i++) {
				data.testGroupSystems[i] = tgss.get(i).serialize();
			}

			data.selectedGroup = GroupsPane.getSelectedGroupIndex();
			stream.writeObject(data);
			stream.close();
			Config.set(ConfigOption.LAST_FILE, file.getPath());
			DataObject.clear();
			Controller.resetUnsavedChanges();
		} catch (Exception e) {
			new AlertException(e);
			DataObject.clear();
			return;
		}
	}

	private FileOutput() {
	}

}
