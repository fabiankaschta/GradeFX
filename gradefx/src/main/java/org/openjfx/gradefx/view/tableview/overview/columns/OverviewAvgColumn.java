package org.openjfx.gradefx.view.tableview.overview.columns;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.Student;
import org.openjfx.gradefx.model.TestGroup;
import org.openjfx.kafx.converter.BigDecimalConverter;
import org.openjfx.kafx.view.tableview.TableCellCustom;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class OverviewAvgColumn extends TableColumn<Student, BigDecimal> {

	public OverviewAvgColumn(Group group, TestGroup testGroup) {
		this(group, testGroup, null);
	}

	public OverviewAvgColumn(Group group, TestGroup testGroup, Consumer<TableCell<Student, ?>> cellSubscription) {
		super("\u2300"); // avg symbol in unicode

		this.setCellValueFactory(data -> testGroup.avgGrade(data.getValue()));
		BigDecimalConverter avgConverter = new BigDecimalConverter();
		avgConverter.getDecimalFormat().setMinimumFractionDigits(2);
		this.setCellFactory(_ -> new TableCellCustom<>(avgConverter, Pos.CENTER) {
			{
				if (cellSubscription != null) {
					cellSubscription.accept(this);
				}
			}
		});
		this.setSortable(true);
		this.setReorderable(false);
		this.setEditable(false);
	}

}