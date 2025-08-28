package gradefx.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputFilter.Status;
import java.io.ObjectInputStream;

import gradefx.controller.Controller;
import gradefx.io.Config.ConfigOption;
import gradefx.model.GradeSystem;
import gradefx.model.GradeSystem.Grade;
import gradefx.model.GradeSystem.Tendency;
import gradefx.model.Group;
import gradefx.model.PointsSystem.BoundType;
import gradefx.model.TestGroup.TestGroupSystem;
import gradefx.view.alert.AlertException;
import gradefx.view.pane.GroupsPane;
import kafx.io.DataObject;

public class FileInput {

	public static void readFromFile(String pathname) {
		readFromFile(new File(pathname));
	}

	public static void readFromFile(File file) {
		if (file == null) {
			new AlertException(new NullPointerException());
			return;
		}
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new FileInputStream(file));
			stream.setObjectInputFilter(ObjectInputFilter.allowFilter(cl -> {
				if (cl == Data.class) {
					return true;
				}
				if (cl == DataObject.class) {
					return true;
				}
				if (cl == GradeSystem.class) {
					return true;
				}
				if (cl == TestGroupSystem.class) {
					return true;
				}
				if (cl == BoundType.class) {
					return true;
				}
				if (cl == Grade.class) {
					return true;
				}
				if (cl == Tendency.class) {
					return true;
				}
				if (cl.getPackageName().startsWith("java.math")) {
					return true;
				}
				if (cl.getPackageName().startsWith("java.lang")) {
					return true;
				}
				if (cl.getPackageName().startsWith("java.time")) {
					return true;
				}
				if (cl.getPackageName().startsWith("java.util")) {
					return true;
				}
				if (cl.isArray() && cl.getComponentType() == DataObject.class) {
					return true;
				}
				if (cl.isArray() && cl.getComponentType() == Grade.class) {
					return true;
				}
				for (Class<?> c : cl.getInterfaces()) {
					if (c == DataObject.class) {
						return true;
					}
				}
				return false;
			}, Status.REJECTED));
			Data data = (Data) stream.readObject();
			stream.close();
			Group.clearGroups();
			Config.remove(ConfigOption.LAST_FILE);
			if (data.groups != null) {
				for (int i = 0; i < data.groups.length; i++) {
					Group group = data.groups[i].deserialize();
					GroupsPane.selectTab(group, data.selectedTabInGroup[i]);
				}
			}
			if (data.testGroupSystems != null) {
				for (DataObject<TestGroupSystem> d : data.testGroupSystems) {
					d.deserialize();
				}
			}
			GroupsPane.select(data.selectedGroup);
			Config.set(ConfigOption.LAST_FILE, file.getPath());
			Controller.resetUnsavedChanges();
		} catch (Exception e) {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
				}
			}
			Config.remove(ConfigOption.LAST_FILE);
			new AlertException(e);
		}
	}

	private FileInput() {
	}

}
