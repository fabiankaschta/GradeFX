package gradefx.view.dialog;

import kafx.lang.Translator;
import gradefx.model.Group;
import gradefx.model.Student;
import kafx.view.control.TextFieldPromptText;
import kafx.view.dialog.DialogEdit;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogEditStudent extends DialogEdit<Student> {

	private final UserInputTextInput firstName, lastName;
	private final UserInputTextInput subgroupName;
	private final Group group;

	public DialogEditStudent(Group group, Student student) {
		super(Translator.get("dialog_edit_student_title"), student);
		this.group = group;
		this.firstName = new UserInputTextInput(new TextFieldPromptText(Translator.get("student_firstName")),
				student.getFirstName());
		super.addInput(this.firstName, Translator.get("student_firstName"));
		this.lastName = new UserInputTextInput(new TextFieldPromptText(Translator.get("student_lastName")),
				student.getLastName());
		super.addInput(this.lastName, Translator.get("student_lastName"));
		this.subgroupName = new UserInputTextInput(new TextFieldPromptText(Translator.get("student_subgroupName")),
				student.getSubgroupName());
		super.addInput(this.subgroupName, Translator.get("student_subgroupName"));
		this.subgroupName.visibleProperty().bind(group.useSubgroupsProperty());
	}

	@Override
	public boolean edit(Student student) {
		String firstName = this.firstName.getValue().trim();
		String lastName = this.lastName.getValue().trim();
		String subgroupName = this.subgroupName.getValue().trim();
		if (firstName != null && firstName.length() > 0 && !firstName.equals(student.getFirstName())) {
			student.setFirstName(firstName);
		}
		if (lastName != null && lastName.length() > 0 && !lastName.equals(student.getLastName())) {
			student.setLastName(lastName);
		}
		if (group.getUseSubgroups() && subgroupName != null && subgroupName.length() > 0
				&& !subgroupName.equals(student.getSubgroupName())) {
			student.setSubgroupName(subgroupName);
		}
		return true;
	}

}
