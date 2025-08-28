package gradefx.view.dialog;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import kafx.io.CSVParser;
import kafx.lang.Translator;
import gradefx.model.Group;
import gradefx.model.Student;
import gradefx.view.alert.AlertException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.util.StringConverter;
import kafx.view.dialog.DialogUserInput;
import kafx.view.dialog.userinput.UserInputCheckBox;
import kafx.view.dialog.userinput.UserInputChoiceBox;
import kafx.view.tableview.TableCellCustom;
import kafx.view.tableview.TableViewFullSize;

public class StudentParserDialog extends DialogUserInput<Boolean> {

	private final UserInputChoiceBox<Character> separator;
	private final UserInputChoiceBox<Character> quotationMark;
	private final UserInputCheckBox labeled;
	private int previewAmount = 3;

	public StudentParserDialog(Group group, File file) throws IOException {
		super(Translator.get("dialog_import_students_title"));

		ObservableList<Student> studentPreview = FXCollections.observableArrayList();
		// TODO add options for different column order (maybe use preview column
		// sorting)
		CSVParser<Student> csvReader = new CSVParser<>(values -> {
			if (values.length < 2) {
				return null;
			} else if (values.length == 2) {
				return new Student(values[1], values[0]);
			} else {
				return new Student(values[1], values[0], values[2]);
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
					return Translator.get("key_comma");
				case ';':
					return Translator.get("key_semicolon");
				case ' ':
					return Translator.get("key_space");
				case '\t':
					return Translator.get("key_tab");
				case ':':
					return Translator.get("key_colon");
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
			}
		});
		super.addInput(this.separator, Translator.get("dialog_import_students_separator"));

		ChoiceBox<Character> quotationMarkChoiceBox = new ChoiceBox<>();
		quotationMarkChoiceBox.getItems().addAll('"', '\'');
		quotationMarkChoiceBox.setConverter(new StringConverter<Character>() {
			@Override
			public String toString(Character object) {
				switch (object) {
				case '"':
					return Translator.get("csv_quotationMark_double");
				case '\'':
					return Translator.get("csv_quotationMark_single");
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
			}
		});
		super.addInput(this.quotationMark, Translator.get("dialog_import_students_quotationMark"));

		this.labeled = new UserInputCheckBox(new CheckBox());
		csvReader.checkContainsLabels(Translator.get("student_firstName"), Translator.get("student_lastName"),
				Translator.get("student_subgroupName"));
		this.labeled.setDefaultValue(csvReader.containsLabel());
		this.labeled.selectDefault();
		this.labeled.valueProperty().subscribe(v -> {
			csvReader.setContainsLabel(v);
			try {
				studentPreview.setAll(csvReader.preview(previewAmount));
			} catch (IOException e) {
			}
		});
		super.addInput(this.labeled, Translator.get("dialog_import_students_labeled"));

		this.getDialogPane().setExpandableContent(new TableViewFullSize<Student>(24, studentPreview) {
			{
				this.setPadding(new Insets(0));
				TableColumn<Student, String> firstNameCol = new TableColumn<Student, String>(
						Translator.get("student_firstName"));
				firstNameCol.setCellValueFactory(data -> data.getValue().firstNameProperty());
				firstNameCol.setCellFactory(TableCellCustom.forTableColumn());
				firstNameCol.setSortable(false);
				firstNameCol.setReorderable(false);

				TableColumn<Student, String> lastNameCol = new TableColumn<Student, String>(
						Translator.get("student_lastName"));
				lastNameCol.setCellValueFactory(data -> data.getValue().lastNameProperty());
				lastNameCol.setCellFactory(TableCellCustom.forTableColumn());
				lastNameCol.setSortable(false);
				lastNameCol.setReorderable(false);

				TableColumn<Student, String> subgroupNameCol = new TableColumn<Student, String>(
						Translator.get("student_subgroupName"));
				subgroupNameCol.setCellValueFactory(data -> data.getValue().subgroupNameProperty());
				subgroupNameCol.setCellFactory(TableCellCustom.forTableColumn());
				subgroupNameCol.setSortable(false);
				subgroupNameCol.setReorderable(false);
				subgroupNameCol.visibleProperty().bind(group.useSubgroupsProperty());

				this.getColumns().add(lastNameCol);
				this.getColumns().add(firstNameCol);
				this.getColumns().add(subgroupNameCol);

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
		});
		this.getDialogPane().expandedProperty().subscribe(() -> this.setResizable(false));
		this.getDialogPane().setExpanded(true);

		ButtonType doneButtonType = new ButtonType(Translator.get("dialog_button_done"), ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(doneButtonType);
		// this.getDialogPane().detailsButton

		this.setResultConverter(r -> {
			if (r != null) {
				try {
					Stream<Student> stream = csvReader.map();
					stream.forEach(s -> group.addStudent(s));
					stream.close();
					return true;
				} catch (IOException e) {
					new AlertException(e);
					return false;
				}
			} else {
				return false;
			}
		});
	}

}
