package org.openjfx.gradefx.controller;

import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import javax.crypto.NoSuchPaddingException;

import org.openjfx.gradefx.io.GradeFXFileIO;
import org.openjfx.gradefx.model.Group;
import org.openjfx.gradefx.model.TestGroup.TestGroupSystem;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.gradefx.view.pane.MainMenuBar;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.io.Config;
import org.openjfx.kafx.lang.Translator;
import org.openjfx.kafx.secure.EncryptionHelper;

import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.Node;

public class GradeFXController extends Controller {

	public GradeFXController() throws NoSuchAlgorithmException, NoSuchPaddingException {
		// TODO disable debug
		super(new GradeFXFileIO(), new Config(null, "gradefx.cfg"),
				new Translator(ResourceBundle.getBundle("org.openjfx.gradefx.lang.gradefx")),
				new EncryptionHelper("KAFXBase".getBytes()));
		System.out.println(GradeFXController.class.getModule());
		config.putIfNotExists("USE_HALF_POINTS", String.valueOf(true));
		config.putIfNotExists("TENDENCY_BOUND", String.valueOf(0.0));
		config.putIfNotExists("DEFAULT_GROUP_COLOR", "#e6e6e6");
	}

	@Override
	protected void handleUnsavedChanges() {
//		MainMenuBar.setStatus(Controller.translate("status_all_saved"));
		MainMenuBar.setStatus(getChangeCounter() == 0 ? ""
				: getChangeCounter() + " " + Controller.translate("status_unsaved_changes"));
	}

	@Override
	protected void handleNewFile() {
		TestGroupSystem.setDefault();
		Group.clearGroups();
		if (!handleSaveAs()) {
			readFromFile();
		}
	}

	@Override
	protected PageLayout getDefaultPageLayout(Printer printer) {
		return printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
	}

	@Override
	protected Node getPrintableNode() {
		return GroupsPane.getSelectedTabInGroup().getContent();
	}
}
