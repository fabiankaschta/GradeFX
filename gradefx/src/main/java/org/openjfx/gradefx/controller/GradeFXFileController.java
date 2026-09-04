package org.openjfx.gradefx.controller;

import java.io.File;

import org.openjfx.gradefx.io.GradeFXFileIO;
import org.openjfx.gradefx.model.GradeSystem;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Subject;
import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.kafx.controller.FileController;

public class GradeFXFileController extends FileController {

	public GradeFXFileController() {
		super(new GradeFXFileIO());
	}

	@Override
	protected boolean handleNewFile(File file) {
		Group.clearGroups();
		TestGroupSystem.setDefault();
		Subject.setDefault();
		GradeSystem.setDefault();
		return super.handleNewFile(file);
	}

	@Override
	protected boolean handleOpenFile(File file) {
		Group.clearGroups();
		Subject.clearSubjects();
		GradeSystem.clearGradeSystems();
		TestGroupSystem.clearTestGroupSystems();
		return super.handleOpenFile(file);
	}
}
