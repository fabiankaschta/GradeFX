package org.openjfx.gradefx.view.tableview.columns;

import java.util.function.Consumer;

import org.openjfx.gradefx.model.Student;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellCustom;
import org.openjfx.kafx.view.tableview.TableCellEditConverter;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.converter.DefaultStringConverter;

public class StudentFirstNameColumn extends TableColumn<Student, String> {

	public StudentFirstNameColumn(boolean editable) {
		this(editable, null);
	}

	public StudentFirstNameColumn(boolean editable, Consumer<TableCell<Student, ?>> cellSubscription) {
		super(TranslationController.translate("student_firstName"));
		this.setCellValueFactory(data -> data.getValue().firstNameProperty());
		if (cellSubscription == null) {
			this.setCellFactory(editable ? TableCellEditConverter.forTableColumn() : TableCellCustom.forTableColumn());
		} else {
			this.setCellFactory(editable ? _ -> new TableCellEditConverter<>(new DefaultStringConverter()) {
				{
					cellSubscription.accept(this);
				}
			} : _ -> new TableCellCustom<>() {
				{
					cellSubscription.accept(this);
				}
			});
		}
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(editable);
	}
}
