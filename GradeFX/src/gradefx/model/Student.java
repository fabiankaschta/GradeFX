package gradefx.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import kafx.controller.Controller;
import kafx.io.DataObject;

public class Student {

	private StringProperty firstName = new SimpleStringProperty(this, "firstName");
	private StringProperty lastName = new SimpleStringProperty(this, "lastName");
	private StringProperty subgroupName = new SimpleStringProperty(this, "subgroupName");

	public Student(String firstName, String lastName) {
		this(firstName, lastName, null);
	}

	public Student(String firstName, String lastName, String subgroupName) {
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setSubgroupName(subgroupName);
		this.firstNameProperty().addListener(Controller.LISTENER_UNSAVED_CHANGES);
		this.lastNameProperty().addListener(Controller.LISTENER_UNSAVED_CHANGES);
		this.subgroupNameProperty().addListener(Controller.LISTENER_UNSAVED_CHANGES);
	}

	public String getFirstName() {
		return firstName.get();
	}

	public StringProperty firstNameProperty() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}

	public String getLastName() {
		return lastName.get();
	}

	public StringProperty lastNameProperty() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}

	public String getSubgroupName() {
		return subgroupName.get();
	}

	public StringProperty subgroupNameProperty() {
		return subgroupName;
	}

	public void setSubgroupName(String subgroupName) {
		this.subgroupName.set(subgroupName);
	}

	@Override
	public String toString() {
		return this.getLastName() + ", " + this.getFirstName();
	}

	private static class StudentS implements DataObject<Student> {

		private static final long serialVersionUID = 4210035038298738031L;

		private final String firstName, lastName;
		private final String subgroupName;

		private transient Student student;

		private StudentS(Student s) {
			DataObject.putSerialized(s, this);
			firstName = s.firstName.get();
			lastName = s.lastName.get();
			subgroupName = s.subgroupName.get();
			student = s;
		}

		public Student deserialize(Object... params) {
			if (student == null) {
				student = new Student(firstName, lastName, subgroupName);
			}
			return student;
		}
	}

	@SuppressWarnings("unchecked")
	public DataObject<Student> serialize() {
		DataObject<?> student = DataObject.getSerialized(this);
		if (student == null) {
			return new StudentS(this);
		} else {
			return (DataObject<Student>) student;
		}
	}

}
