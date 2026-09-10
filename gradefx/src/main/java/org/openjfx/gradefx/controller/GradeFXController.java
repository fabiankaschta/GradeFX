package org.openjfx.gradefx.controller;

import java.util.ResourceBundle;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.kafx.controller.AutoSaveController;
import org.openjfx.kafx.controller.ChangeController;
import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.controller.EncryptionController;
import org.openjfx.kafx.controller.EncryptionControllerDefault;
import org.openjfx.kafx.controller.FileController;
import org.openjfx.kafx.controller.PluginController;
import org.openjfx.kafx.controller.PrintController;
import org.openjfx.kafx.controller.PropertiesController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.controller.UpdateController;
import org.pf4j.JarPluginManager;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GradeFXController extends Controller {

	public static void init() {
		Controller.init(".gradefx.cfg");
//		Controller.init(".gradefx.cfg", org.openjfx.kafx.controller.LogController.DEBUG);
		PropertiesController
				.addProperties(GradeFXController.class.getResourceAsStream("/org/openjfx/gradefx/gradefx.properties"));
		TranslationController.addBundle(ResourceBundle.getBundle("org.openjfx.gradefx.lang.gradefx"));
		EncryptionController.init(new EncryptionControllerDefault("KAFXBase".getBytes()));
		ChangeController.init();
		FileController.init(new GradeFXFileController());
		AutoSaveController.init();
		PrintController.init(new GradeFXPrintController());
		PluginController.init(new JarPluginManager());
		UpdateController.init();
		UpdateController.register("GradeFX", () -> PropertiesController.getProperty("gradefx.version"),
				PropertiesController.getProperty("gradefx.url"));
		ConfigController.putIfNotExists("USE_HALF_POINTS", String.valueOf(true));
		ConfigController.putIfNotExists("TENDENCY_BOUND", String.valueOf(0.0));
		ConfigController.putIfNotExists("DEFAULT_GROUP_COLOR", "#e6e6e6");
	}

	private final static ObjectProperty<Group> selectedGroup = new SimpleObjectProperty<>(null, "selectedGroup", null);
	private final static ObjectProperty<Test> selectedTest = new SimpleObjectProperty<>(null, "selectedTest", null);
	private final static ObjectProperty<Student> selectedStudent = new SimpleObjectProperty<>(null, "selectedStudent", null);

	public static Group getSelectedGroup() {
		return selectedGroup.get();
	}

	public static ObjectProperty<Group> selectedGroupProperty() {
		return selectedGroup;
	}

	public static void setSelectedGroup(Group group) {
		selectedGroup.set(group);
	}

	public static Test getSelectedTest() {
		return selectedTest.get();
	}

	public static ObjectProperty<Test> selectedTestProperty() {
		return selectedTest;
	}

	public static void setSelectedTest(Test test) {
		selectedTest.set(test);
	}

	public static Student getSelectedStudent() {
		return selectedStudent.get();
	}

	public static ObjectProperty<Student> selectedStudentProperty() {
		return selectedStudent;
	}

	public static void setSelectedStudent(Student student) {
		selectedStudent.set(student);
	}
}
