package org.openjfx.gradefx.io;

import java.util.ArrayList;
import java.util.List;

import org.openjfx.gradefx.controller.GradeFXController;
import org.openjfx.gradefx.model.BoundType;
import org.openjfx.gradefx.model.GradeSystem;
import org.openjfx.gradefx.model.GradeSystem.GradeSystemBaseType;
import org.openjfx.gradefx.model.GradeSystem.Level;
import org.openjfx.gradefx.model.Grade;
import org.openjfx.gradefx.model.Grade.Tendency;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.PointsSystem;
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
		Group.clearGroups();
		Subject.clearSubjects();
		GradeSystem.clearGradeSystems();
		TestGroupSystem.clearTestGroupSystems();
		try {
			((ArrayList<DataObject<TestGroupSystem>>) data.get("testGroupSystems")).forEach(tgs -> tgs.deserialize());
			((ArrayList<DataObject<Subject>>) data.get("subjects")).forEach(s -> s.deserialize());
			((ArrayList<DataObject<GradeSystem>>) data.get("gradeSystems")).forEach(gs -> gs.deserialize());
			((ArrayList<DataObject<Group>>) data.get("groups")).forEach(g -> g.deserialize());

			GroupsPane.select((int) data.get("selectedGroup"));

			return true;
		} catch (Exception e) {
			ExceptionController.exception(e);
			return false;
		}
	}

	@Override
	public List<Class<?>> getPermittedSerializableClasses() {
		List<Class<?>> list = super.getPermittedSerializableClasses();
		list.add(BoundType.class);
		list.add(Grade.class);
		list.add(Tendency.class);
		list.add(GradeSystemBaseType.class);
		list.add(Level.class);
		return list;
	}

	@Override
	public List<Class<?>> getPermittedDataObjectClasses() {
		List<Class<?>> list = super.getPermittedSerializableClasses();
		list.add(GradeSystem.class);
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

		data.put("testGroupSystems", new ArrayList<DataObject<TestGroupSystem>>(TestGroupSystem.getTestGroupSystems()
				.stream().filter(tgs -> tgs != TestGroupSystem.NONE).map(tgs -> tgs.serialize()).toList()));
		data.put("subjects",
				new ArrayList<DataObject<Subject>>(Subject.getSubjects().stream().map(s -> s.serialize()).toList()));
		data.put("gradeSystems", new ArrayList<DataObject<GradeSystem>>(
				GradeSystem.getGradeSystems().stream().map(gs -> gs.serialize()).toList()));
		data.put("groups", new ArrayList<>(Group.getGroups().stream().map(g -> g.serialize()).toList()));

		data.put("selectedGroup", Group.getGroups().indexOf(GradeFXController.getSelectedGroup()));

		return data;
	}

}
