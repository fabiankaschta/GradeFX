package org.openjfx.gradefx.controller;

import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.controller.PrintController;

import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.Node;

public class GradeFXPrintController extends PrintController {

	@Override
	protected PageLayout getDefaultPageLayout(Printer printer) {
		return printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
	}

	@Override
	protected Node getPrintableNode() {
		return GroupsPane.getSelectedTabInGroup().getContent();
	}
}
