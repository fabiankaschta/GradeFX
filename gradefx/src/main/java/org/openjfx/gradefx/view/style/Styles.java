package org.openjfx.gradefx.view.style;

import org.openjfx.kafx.view.color.ColorHelper;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

public class Styles {

	public static void subscribeColor(Node node, String cssProperty, ObjectProperty<Color> colorProperty) {
		colorProperty.subscribe(color -> node.setStyle((node.getStyle() == null ? "" : node.getStyle()) + cssProperty
				+ ": " + ColorHelper.toHexString(color) + ";"));
	}

	public static void subscribeColor(Tab tab, String cssProperty, ObjectProperty<Color> colorProperty) {
		colorProperty.subscribe(color -> tab.setStyle((tab.getStyle() == null ? "" : tab.getStyle()) + cssProperty
				+ ": " + ColorHelper.toHexString(color) + ";"));
	}

	public static void subscribeTabPaneColor(TabPane tabPane, ObjectProperty<Color> colorProperty) {
		tabPane.getStyleClass().addAll("tab-pane-custom-background");
		colorProperty.subscribe(color -> {
			StringBuilder style = new StringBuilder();
			Color background = color.interpolate(Color.WHITE, 0.5);
			// base
			style.append("-fx-base: " + ColorHelper.toHexString(color) + ";");
			// background
			style.append("-fx-custom-background: " + ColorHelper.toHexString(background) + ";");
			tabPane.setStyle((tabPane.getStyle() == null ? "" : tabPane.getStyle()) + style.toString());
		});
	}

	public static void subscribeBackgroundColor(Node node, ObjectProperty<Color> colorProperty) {
		colorProperty.subscribe(color -> node.setStyle((node.getStyle() == null ? "" : node.getStyle())
				+ "-fx-background-color: " + ColorHelper.toHexString(color.interpolate(Color.WHITE, 0.9)) + ";"));
	}

	public static void subscribeScrollBarColor(Node node, ObjectProperty<Color> colorProperty) {
		node.getStyleClass().add("custom-scrollbar");
		colorProperty.subscribe(color -> node.setStyle((node.getStyle() == null ? "" : node.getStyle())
				+ "-fx-custom-scrollbar: " + ColorHelper.toHexString(color.interpolate(Color.WHITESMOKE, 0.5)) + ";"));
	}

	public static void subscribeTableColor(TableView<?> table, ObjectProperty<Color> colorProperty) {
		table.getStyleClass().add("table-view-empty");
		colorProperty.subscribe(color -> {
			StringBuilder style = new StringBuilder();
			Color background = color.interpolate(Color.WHITE, 0.9);
			Color cell_alt = color.interpolate(Color.WHITESMOKE, 0.8);
			Color selection_nf = color.interpolate(Color.WHITE, 0.5);
			// background
			style.append("-fx-background-color: " + ColorHelper.toHexString(background) + ";");
			// even cells
			style.append("-fx-control-inner-background: white;");
			// odd cells
			style.append("-fx-control-inner-background-alt: " + ColorHelper.toHexString(cell_alt) + ";");
			// text color depends on background
			style.append("-fx-text-fill: -fx-text-base-color;");
			// selected cell(s)
			style.append("-fx-selection-bar: " + ColorHelper.toHexString(color) + ";");
			// selected, not focused cell(s)
			style.append("-fx-selection-bar-non-focused: " + ColorHelper.toHexString(selection_nf) + ";");
			table.setStyle((table.getStyle() == null ? "" : table.getStyle()) + style.toString());
		});
		subscribeScrollBarColor(table, colorProperty);
	}

	private Styles() {
	}

}
