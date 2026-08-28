package org.openjfx.gradefx.view.tableview.test.columns;

import java.util.function.Consumer;

import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.Test;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.tableview.TableCellEditConverter;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.converter.DefaultStringConverter;

public class TestAnnotationColumn extends TableColumn<Student, String> {

	public TestAnnotationColumn(Test test) {
		this(test, null);
	}

	public TestAnnotationColumn(Test test, Consumer<TableCell<Student, ?>> cellSubscription) {
		super(TranslationController.translate("test_annotation"));
		this.setCellValueFactory(data -> test.annotationProperty(data.getValue()));
		this.setCellFactory(_ -> new TableCellEditConverter<>(new DefaultStringConverter(), true) {
			{
				if (cellSubscription != null) {
					cellSubscription.accept(this);
				}
			}
		});
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(true);
		this.minWidthProperty().bind(FontSizeController.fontSizeProperty().multiply(15));
	}

}
