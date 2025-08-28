package gradefx.view.style;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import kafx.view.color.ColorHelper;

public class Styles {

	public static void subscribeColor(Node node, String cssProperty, ObjectProperty<Color> colorProperty) {
		colorProperty.subscribe(color -> node.setStyle(cssProperty + ": " + ColorHelper.toHexString(color) + ";"));
	}

	public static void subscribeColor(Tab tab, String cssProperty, ObjectProperty<Color> colorProperty) {
		colorProperty.subscribe(color -> tab.setStyle(cssProperty + ": " + ColorHelper.toHexString(color) + ";"));
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
			tabPane.setStyle(style.toString());
		});
	}

	public static void subscribeBackgroundColor(Node node, ObjectProperty<Color> colorProperty) {
		colorProperty.subscribe(color -> node.setStyle(
				"-fx-background-color: " + ColorHelper.toHexString(color.interpolate(Color.WHITE, 0.9)) + ";"));
	}

	public static void subscribeTableColor(TableView<?> table, ObjectProperty<Color> colorProperty) {
		table.getStyleClass().addAll("table-view-empty", "table-view-custom-scrollbar");
		colorProperty.subscribe(color -> {
			StringBuilder style = new StringBuilder();
			Color background = color.interpolate(Color.WHITE, 0.9);
			Color cell_alt = color.interpolate(Color.WHITESMOKE, 0.8);
			Color selection_nf = color.interpolate(Color.WHITE, 0.5);
			Color scrollBar = color.interpolate(Color.WHITESMOKE, 0.5);
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
			style.append("-fx-custom-scrollbar: " + ColorHelper.toHexString(scrollBar) + ";");
			table.setStyle(style.toString());
		});
	}

	private Styles() {
	}

}
