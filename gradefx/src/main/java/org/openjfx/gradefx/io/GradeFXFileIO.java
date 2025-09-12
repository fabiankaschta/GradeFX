package org.openjfx.gradefx.io;

import java.util.ArrayList;
import java.util.List;

import org.openjfx.gradefx.model.GradeSystem;
import org.openjfx.gradefx.model.GradeSystem.Grade;
import org.openjfx.gradefx.model.GradeSystem.Tendency;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.PointsSystem;
import org.openjfx.gradefx.model.PointsSystem.BoundType;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Subject;
import org.openjfx.gradefx.model.Test;
import org.openjfx.gradefx.model.Test.TestTask;
import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.controller.ExceptionController;
import org.openjfx.kafx.io.Data;
import org.openjfx.kafx.io.DataObject;
import org.openjfx.kafx.io.EncryptedFileIO;

public class GradeFXFileIO extends EncryptedFileIO {

	@Override
	@SuppressWarnings("unchecked")
	public boolean handleData(Data data) {
		try {
			Group.clearGroups();

			ArrayList<DataObject<Group>> groups = (ArrayList<DataObject<Group>>) data.get("groups");
			ArrayList<Integer> selectedTabInGroup = (ArrayList<Integer>) data.get("selectedTabInGroup");
			for (int i = 0; i < groups.size(); i++) {
				Group group = groups.get(i).deserialize();
				GroupsPane.selectTab(group, selectedTabInGroup.get(i));
			}

			// TODO maybe move to config?
			ArrayList<DataObject<TestGroupSystem>> testGroupSystems = (ArrayList<DataObject<TestGroupSystem>>) data
					.get("testGroupSystems");
			testGroupSystems.forEach(tgs -> tgs.deserialize());
			ArrayList<DataObject<Subject>> subjects = (ArrayList<DataObject<Subject>>) data.get("subjects");
			subjects.forEach(s -> s.deserialize());

			int selectedGroup = (int) data.get("selectedGroup");
			GroupsPane.select(selectedGroup);

			return true;
		} catch (Exception e) {
			ExceptionController.exception(e);
			return false;
		}
	}

	@Override
	public List<Class<?>> getPermittedSerializableClasses() {
		List<Class<?>> list = super.getPermittedSerializableClasses();
		list.add(GradeSystem.class);
		list.add(BoundType.class);
		list.add(Grade.class);
		list.add(Tendency.class);
		return list;
	}

	@Override
	public List<Class<?>> getPermittedDataObjectClasses() {
		List<Class<?>> list = super.getPermittedSerializableClasses();
		list.add(Group.class);
		list.add(PointsSystem.class);
		list.add(Student.class);
		list.add(Subject.class);
		list.add(Test.class);
		list.add(TestTask.class);
		list.add(TestGroup.class);
		list.add(TestGroupSystem.class);
		return list;
	}

	@Override
	public Data collectData() {
		Data data = new Data();

		data.put("groups", new ArrayList<>(Group.getGroups().stream().map(g -> g.serialize()).toList()));
		data.put("selectedTabInGroup", new ArrayList<>(
				Group.getGroups().stream().map(g -> GroupsPane.getSelectedTabInGroupIndex(g)).toList()));

		// TODO maybe move to config?
		data.put("testGroupSystems", new ArrayList<DataObject<TestGroupSystem>>(
				TestGroupSystem.getTestGroupSystems().stream().map(tgs -> tgs.serialize()).toList()));
		data.put("subjects",
				new ArrayList<DataObject<Subject>>(Subject.getSubjects().stream().map(s -> s.serialize()).toList()));

		data.put("selectedGroup", GroupsPane.getSelectedGroupIndex());
		return data;
	}

}
