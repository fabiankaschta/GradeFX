package org.openjfx.gradefx.controller;

import org.openjfx.gradefx.io.GradeFXFileIO;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Subject;
import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.kafx.controller.FileController;

public class GradeFXFileController extends FileController {

	public GradeFXFileController() {
		super(new GradeFXFileIO());
	}

	@Override
	protected void handleNewFile() {
		TestGroupSystem.setDefault();
		Subject.setDefault();
		Group.clearGroups();
		if (!handleSaveAs()) {
			readFromFile();
//			return readFromFile();
		} else {
//			return false;
		}
	}
}
