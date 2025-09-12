package org.openjfx.gradefx.model;

import org.openjfx.kafx.controller.ChangeController;
import org.openjfx.kafx.controller.TranslationController;
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
		addSubject(TranslationController.translate("subject_biology"),
				TranslationController.translate("subject_biology_short"));
		addSubject(TranslationController.translate("subject_chemistry"),
				TranslationController.translate("subject_chemistry_short"));
		addSubject(TranslationController.translate("subject_german"),
				TranslationController.translate("subject_german_short"));
		addSubject(TranslationController.translate("subject_english"),
				TranslationController.translate("subject_english_short"));
		addSubject(TranslationController.translate("subject_ethics"),
				TranslationController.translate("subject_ethics_short"));
		addSubject(TranslationController.translate("subject_evangelical"),
				TranslationController.translate("subject_evangelical_short"));
		addSubject(TranslationController.translate("subject_french"),
				TranslationController.translate("subject_french_short"));
		addSubject(TranslationController.translate("subject_geography"),
				TranslationController.translate("subject_geography_short"));
		addSubject(TranslationController.translate("subject_history"),
				TranslationController.translate("subject_history_short"));
		addSubject(TranslationController.translate("subject_greek"),
				TranslationController.translate("subject_greek_short"));
		addSubject(TranslationController.translate("subject_it"), TranslationController.translate("subject_it_short"));
		addSubject(TranslationController.translate("subject_instrumental"),
				TranslationController.translate("subject_instrumental_short"));
		addSubject(TranslationController.translate("subject_italian"),
				TranslationController.translate("subject_italian_short"));
		addSubject(TranslationController.translate("subject_catholic"),
				TranslationController.translate("subject_catholic_short"));
		addSubject(TranslationController.translate("subject_arts"),
				TranslationController.translate("subject_arts_short"));
		addSubject(TranslationController.translate("subject_latin"),
				TranslationController.translate("subject_latin_short"));
		addSubject(TranslationController.translate("subject_maths"),
				TranslationController.translate("subject_maths_short"));
		addSubject(TranslationController.translate("subject_music"),
				TranslationController.translate("subject_music_short"));
		addSubject(TranslationController.translate("subject_nut"),
				TranslationController.translate("subject_nut_short"));
		addSubject(TranslationController.translate("subject_physics"),
				TranslationController.translate("subject_physics_short"));
		addSubject(TranslationController.translate("subject_politics"),
				TranslationController.translate("subject_politics_short"));
		addSubject(TranslationController.translate("subject_psychology"),
				TranslationController.translate("subject_psychology_short"));
		addSubject(TranslationController.translate("subject_spanish"),
				TranslationController.translate("subject_spanish_short"));
		addSubject(TranslationController.translate("subject_pe"), TranslationController.translate("subject_pe_short"));
		addSubject(TranslationController.translate("subject_vocal"),
				TranslationController.translate("subject_vocal_short"));
		addSubject(TranslationController.translate("subject_economy"),
				TranslationController.translate("subject_economy_short"));
		addSubject(TranslationController.translate("subject_economyIt"),
				TranslationController.translate("subject_economyIt_short"));
		addSubject(TranslationController.translate("subject_seminarS"),
				TranslationController.translate("subject_seminarS_short"));
		addSubject(TranslationController.translate("subject_seminarP"),
				TranslationController.translate("subject_seminarP_short"));
	}

	private final StringProperty name = new SimpleStringProperty(this, "name");
	private final StringProperty shortName = new SimpleStringProperty(this, "shortName");

	private Subject(String name, String shortName) {
		this.setName(name);
		this.setShortName(shortName);
		this.nameProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
		this.shortNameProperty().addListener(ChangeController.LISTENER_UNSAVED_CHANGES);
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
