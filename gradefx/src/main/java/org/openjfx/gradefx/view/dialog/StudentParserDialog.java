package org.openjfx.gradefx.view.dialog;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.kafx.controller.ExceptionController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.io.CSVParser;
import org.openjfx.kafx.view.dialog.DialogUserInput;
import org.openjfx.kafx.view.dialog.userinput.UserInputCheckBox;
import org.openjfx.kafx.view.dialog.userinput.UserInputChoiceBox;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableViewFullSize;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.util.StringConverter;

// TODO move most to KAFXBase
public class StudentParserDialog extends DialogUserInput<Boolean> {

	private final UserInputChoiceBox<Character> separator;
	private final UserInputChoiceBox<Character> quotationMark;
	private final UserInputCheckBox labeled;
	private final TableViewFullSize<Student> preview;
	private final TableColumn<Student, String> firstNameCol, lastNameCol, subgroupNameCol;
	private int previewAmount = 3;

	public StudentParserDialog(Group group, File file) throws IOException {
		super(TranslationController.translate("dialog_import_students_title"));

		ObservableList<Student> studentPreview = FXCollections.observableArrayList();

		this.firstNameCol = new TableColumn<Student, String>(TranslationController.translate("student_firstName"));
		this.firstNameCol.setCellValueFactory(data -> data.getValue().firstNameProperty());
		this.firstNameCol.setCellFactory(TableCellCustom.forTableColumn());
		this.firstNameCol.setSortable(false);
		this.firstNameCol.setReorderable(true);

		this.lastNameCol = new TableColumn<Student, String>(TranslationController.translate("student_lastName"));
		this.lastNameCol.setCellValueFactory(data -> data.getValue().lastNameProperty());
		this.lastNameCol.setCellFactory(TableCellCustom.forTableColumn());
		this.lastNameCol.setSortable(false);
		this.lastNameCol.setReorderable(true);

		this.subgroupNameCol = new TableColumn<Student, String>(
				TranslationController.translate("student_subgroupName"));
		this.subgroupNameCol.setCellValueFactory(data -> data.getValue().subgroupNameProperty());
		this.subgroupNameCol.setCellFactory(TableCellCustom.forTableColumn());
		this.subgroupNameCol.setSortable(false);
		this.subgroupNameCol.setReorderable(true);
		this.subgroupNameCol.visibleProperty().bind(group.useSubgroupsProperty());

		this.preview = new TableViewFullSize<Student>(24, studentPreview) {
			{
				this.setPadding(new Insets(0));

				this.getColumns().add(StudentParserDialog.this.lastNameCol);
				this.getColumns().add(StudentParserDialog.this.firstNameCol);
				this.getColumns().add(StudentParserDialog.this.subgroupNameCol);

				this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
				this.setSelectionModel(null);
			}

			@Override
			protected double computePrefHeight(double width) {
				double height = 0;
				TableHeaderRow header = (TableHeaderRow) this.queryAccessibleAttribute(AccessibleAttribute.HEADER);
				if (header != null) {
					height = header.getHeight();
				}
				height += this.getFixedCellSize() * StudentParserDialog.this.previewAmount;
				return height + this.snappedTopInset() + this.snappedBottomInset();
			}
		};

		CSVParser<Student> csvReader = new CSVParser<>(values -> {
			if (values.length < 2) {
				return null;
			} else if (values.length == 2) {
				return new Student(values[preview.getColumns().indexOf(firstNameCol)],
						values[preview.getColumns().indexOf(lastNameCol)]);
			} else {
				return new Student(values[preview.getColumns().indexOf(firstNameCol)],
						values[preview.getColumns().indexOf(lastNameCol)],
						values[preview.getColumns().indexOf(subgroupNameCol)]);
			}
		}, file, ';');

		ChoiceBox<Character> separatorChoiceBox = new ChoiceBox<>();
		for (char s : CSVParser.defaultSeparators()) {
			separatorChoiceBox.getItems().add(s);
		}
		separatorChoiceBox.setConverter(new StringConverter<Character>() {
			@Override
			public String toString(Character object) {
				switch (object) {
				case ',':
					return TranslationController.translate("key_comma");
				case ';':
					return TranslationController.translate("key_semicolon");
				case ' ':
					return TranslationController.translate("key_space");
				case '\t':
					return TranslationController.translate("key_tab");
				case ':':
					return TranslationController.translate("key_colon");
				default:
					return String.valueOf(object);
				}
			}

			@Override
			public Character fromString(String string) {
				throw new UnsupportedOperationException();
			}
		});
		this.separator = new UserInputChoiceBox<Character>(separatorChoiceBox);
		csvReader.guessSeparator();
		this.separator.setDefaultValue(csvReader.getSeparator());
		this.separator.selectDefault();
		this.separator.valueProperty().subscribe(v -> {
			csvReader.setSeparator(v);
			try {
				studentPreview.setAll(csvReader.preview(previewAmount));
			} catch (IOException e) {
				ExceptionController.exception(e);
			}
		});
		super.addInput(this.separator, TranslationController.translate("dialog_import_students_separator"));

		ChoiceBox<Character> quotationMarkChoiceBox = new ChoiceBox<>();
		quotationMarkChoiceBox.getItems().addAll('"', '\'');
		quotationMarkChoiceBox.setConverter(new StringConverter<Character>() {
			@Override
			public String toString(Character object) {
				switch (object) {
				case '"':
					return TranslationController.translate("csv_quotationMark_double");
				case '\'':
					return TranslationController.translate("csv_quotationMark_single");
				default:
					return String.valueOf(object);
				}
			}

			@Override
			public Character fromString(String string) {
				throw new UnsupportedOperationException();
			}
		});
		this.quotationMark = new UserInputChoiceBox<Character>(quotationMarkChoiceBox, '"');
		this.quotationMark.valueProperty().subscribe(v -> {
			csvReader.setQuotationMark(v);
			try {
				studentPreview.setAll(csvReader.preview(previewAmount));
			} catch (IOException e) {
				ExceptionController.exception(e);
			}
		});
		super.addInput(this.quotationMark, TranslationController.translate("dialog_import_students_quotationMark"));

		this.labeled = new UserInputCheckBox(new CheckBox());
		csvReader.checkContainsLabels(TranslationController.translate("student_firstName"),
				TranslationController.translate("student_lastName"),
				TranslationController.translate("student_subgroupName"));
		this.labeled.setDefaultValue(csvReader.containsLabel());
		this.labeled.selectDefault();
		this.labeled.valueProperty().subscribe(v -> {
			csvReader.setContainsLabel(v);
			try {
				studentPreview.setAll(csvReader.preview(previewAmount));
			} catch (IOException e) {
				ExceptionController.exception(e);
			}
		});
		super.addInput(this.labeled, TranslationController.translate("dialog_import_students_labeled"));

		this.getDialogPane().setExpandableContent(this.preview);
		this.preview.getColumns().addListener((ListChangeListener<TableColumn<Student, ?>>) c -> {
			boolean permutate = false;
			while (c.next()) {
				if (c.wasPermutated()) {
					permutate = true;
				}
			}
			if (permutate) {
				try {
					studentPreview.setAll(csvReader.preview(previewAmount));
				} catch (IOException e) {
					ExceptionController.exception(e);
				}
			}
		});
		this.getDialogPane().expandedProperty().subscribe(() -> this.setResizable(false));
		this.getDialogPane().setExpanded(true);

		ButtonType doneButtonType = new ButtonType(TranslationController.translate("dialog_button_done"),
				ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(doneButtonType);
		// TODO this.getDialogPane().detailsButton

		this.setResultConverter(r -> {
			if (r != null) {
				try {
					Stream<Student> stream = csvReader.map();
					stream.forEach(s -> group.addStudent(s));
					stream.close();
					return true;
				} catch (IOException e) {
					ExceptionController.exception(e);
					return false;
				}
			} else {
				return false;
			}
		});
	}

}
