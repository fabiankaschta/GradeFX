package org.openjfx.gradefx.view.tableview.test.columns;

import java.time.LocalDate;
import java.util.function.Consumer;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellEditDatePicker;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class TestDateColumn extends TableColumn<Student, LocalDate> {

	public TestDateColumn(Test test) {
		this(test, null);
	}

	public TestDateColumn(Test test, Consumer<TableCell<Student, ?>> cellSubscription) {
		super(TranslationController.translate("test_date"));
		this.setCellValueFactory(data -> test.dateProperty(data.getValue()));
		this.setCellFactory(_ -> new TableCellEditDatePicker<>(true) {
			{
				if (cellSubscription != null) {
					cellSubscription.accept(this);
				}
			}
		});
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(true);
		this.minWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(7));
	}

}