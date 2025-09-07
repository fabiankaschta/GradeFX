package org.openjfx.gradefx.model;

import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.io.DataObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Subject {

	private final static ObservableList<Subject> subjects = FXCollections.observableArrayList();

	public static ObservableList<Subject> getSubjects() {
		return subjects;
	}

	public static void removeSubject(Subject subject) {
		subjects.remove(subject);
	}

	public static Subject addSubject(String name, String shortName) {
		Subject subject = get(name);
		if (subject == null) {
			subject = new Subject(name, shortName);
			subjects.add(subject);
		}
		return subject;
	}

	public static Subject get(String name) {
		for (Subject subject : subjects) {
			if (subject.getName().equals(name)) {
				return subject;
			}
		}
		return null;
	}

	public static void setDefault() {
		subjects.clear();
		addSubject(Controller.translate("subject_biology"), Controller.translate("subject_biology_short"));
		addSubject(Controller.translate("subject_chemistry"), Controller.translate("subject_chemistry_short"));
		addSubject(Controller.translate("subject_german"), Controller.translate("subject_german_short"));
		addSubject(Controller.translate("subject_english"), Controller.translate("subject_english_short"));
		addSubject(Controller.translate("subject_ethics"), Controller.translate("subject_ethics_short"));
		addSubject(Controller.translate("subject_evangelical"), Controller.translate("subject_evangelical_short"));
		addSubject(Controller.translate("subject_french"), Controller.translate("subject_french_short"));
		addSubject(Controller.translate("subject_geography"), Controller.translate("subject_geography_short"));
		addSubject(Controller.translate("subject_history"), Controller.translate("subject_history_short"));
		addSubject(Controller.translate("subject_greek"), Controller.translate("subject_greek_short"));
		addSubject(Controller.translate("subject_it"), Controller.translate("subject_it_short"));
		addSubject(Controller.translate("subject_instrumental"), Controller.translate("subject_instrumental_short"));
		addSubject(Controller.translate("subject_italian"), Controller.translate("subject_italian_short"));
		addSubject(Controller.translate("subject_catholic"), Controller.translate("subject_catholic_short"));
		addSubject(Controller.translate("subject_arts"), Controller.translate("subject_arts_short"));
		addSubject(Controller.translate("subject_latin"), Controller.translate("subject_latin_short"));
		addSubject(Controller.translate("subject_maths"), Controller.translate("subject_maths_short"));
		addSubject(Controller.translate("subject_music"), Controller.translate("subject_music_short"));
		addSubject(Controller.translate("subject_nut"), Controller.translate("subject_nut_short"));
		addSubject(Controller.translate("subject_physics"), Controller.translate("subject_physics_short"));
		addSubject(Controller.translate("subject_politics"), Controller.translate("subject_politics_short"));
		addSubject(Controller.translate("subject_psychology"), Controller.translate("subject_psychology_short"));
		addSubject(Controller.translate("subject_spanish"), Controller.translate("subject_spanish_short"));
		addSubject(Controller.translate("subject_pe"), Controller.translate("subject_pe_short"));
		addSubject(Controller.translate("subject_vocal"), Controller.translate("subject_vocal_short"));
		addSubject(Controller.translate("subject_economy"), Controller.translate("subject_economy_short"));
		addSubject(Controller.translate("subject_economyIt"), Controller.translate("subject_economyIt_short"));
		addSubject(Controller.translate("subject_seminarS"), Controller.translate("subject_seminarS_short"));
		addSubject(Controller.translate("subject_seminarP"), Controller.translate("subject_seminarP_short"));
	}

	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty shortName = new SimpleStringProperty(this, "shortName");

	private Subject(String name, String shortName) {
		this.setName(name);
		this.setShortName(shortName);
		this.nameProperty().addListener(Controller.LISTENER_UNSAVED_CHANGES);
		this.shortNameProperty().addListener(Controller.LISTENER_UNSAVED_CHANGES);
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getShortName() {
		return shortName.get();
	}

	public StringProperty shortNameProperty() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName.set(shortName);
	}

	@Override
	public String toString() {
		return this.getName() + " (" + this.getShortName() + ")";
	}

	private static class SubjectS implements DataObject<Subject> {

		private static final long serialVersionUID = -3454435932108811437L;

		private final String name;
		private final String shortName;

		private transient Subject subject;

		private SubjectS(Subject s) {
			DataObject.putSerialized(s, this);
			name = s.name.get();
			shortName = s.shortName.get();
			subject = s;
		}

		public Subject deserialize(Object... params) {
			if (subject == null) {
				subject = addSubject(name, shortName);
			}
			return subject;
		}
	}

	@SuppressWarnings("unchecked")
	public DataObject<Subject> serialize() {
		DataObject<?> subject = DataObject.getSerialized(this);
		if (subject == null) {
			return new SubjectS(this);
		} else {
			return (DataObject<Subject>) subject;
		}
	}

}
