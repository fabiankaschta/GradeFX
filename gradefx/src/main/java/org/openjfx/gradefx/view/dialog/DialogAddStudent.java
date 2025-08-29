package org.openjfx.gradefx.view.dialog;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.view.control.TextFieldPromptText;
import org.openjfx.kafx.view.dialog.DialogAdd;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

public class DialogAddStudent extends DialogAdd<Student> {

	private final UserInputTextInput firstName, lastName;
	private final UserInputTextInput subgroupName;
	private final Group group;

	public DialogAddStudent(Group group) {
		super(Controller.translate("dialog_add_student_title"));
		super.showAddMoreButton(true);
		this.group = group;
		this.firstName = new UserInputTextInput(new TextFieldPromptText(Controller.translate("student_firstName")));
		super.addInput(this.firstName, Controller.translate("student_firstName"));
		this.lastName = new UserInputTextInput(new TextFieldPromptText(Controller.translate("student_lastName")));
		super.addInput(this.lastName, Controller.translate("student_lastName"));
		this.subgroupName = new UserInputTextInput(new TextFieldPromptText(Controller.translate("student_subgroupName")));
		super.addInput(this.subgroupName, Controller.translate("student_subgroupName"));
		this.subgroupName.visibleProperty().bind(group.useSubgroupsProperty());
	}

	@Override
	public Student create() {
		Student s = new Student(this.firstName.getValue().trim(), this.lastName.getValue().trim());
		if (this.group.getUseSubgroups()) {
			s.setSubgroupName(this.subgroupName.getValue().trim());
		}
		this.group.addStudent(s);
		return s;
	}

}
