package gradefx.view.dialog;

import kafx.lang.Translator;
import gradefx.model.Group;
import gradefx.model.Student;
import kafx.view.control.TextFieldPromptText;
import kafx.view.dialog.DialogAdd;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogAddStudent extends DialogAdd<Student> {

	private final UserInputTextInput firstName, lastName;
	private final UserInputTextInput subgroupName;
	private final Group group;

	public DialogAddStudent(Group group) {
		super(Translator.get("dialog_add_student_title"));
		super.showAddMoreButton(true);
		this.group = group;
		this.firstName = new UserInputTextInput(new TextFieldPromptText(Translator.get("student_firstName")));
		super.addInput(this.firstName, Translator.get("student_firstName"));
		this.lastName = new UserInputTextInput(new TextFieldPromptText(Translator.get("student_lastName")));
		super.addInput(this.lastName, Translator.get("student_lastName"));
		this.subgroupName = new UserInputTextInput(new TextFieldPromptText(Translator.get("student_subgroupName")));
		super.addInput(this.subgroupName, Translator.get("student_subgroupName"));
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
