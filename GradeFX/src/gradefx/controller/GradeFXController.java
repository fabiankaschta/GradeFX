package gradefx.controller;

import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import gradefx.io.GradeFXFileIO;
import gradefx.model.Group;
import gradefx.model.TestGroup.TestGroupSystem;
import gradefx.view.pane.GroupsPane;
import gradefx.view.pane.MainMenuBar;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.Node;
import kafx.controller.Controller;
import kafx.io.Config;
import kafx.lang.Translator;
import kafx.secure.EncryptionHelper;

public class GradeFXController extends Controller {

	public GradeFXController() throws NoSuchAlgorithmException, NoSuchPaddingException {
		// TODO disable debug
		super(new GradeFXFileIO(), new Config(null, "gradefx.cfg"), new EncryptionHelper("KAFXBase".getBytes()));
		config.putIfNotExists("USE_HALF_POINTS", String.valueOf(true));
		config.putIfNotExists("TENDENCY_BOUND", String.valueOf(0.0));
		config.putIfNotExists("DEFAULT_GROUP_COLOR", "#e6e6e6");
	}

	@Override
	protected void handleUnsavedChanges() {
//		MainMenuBar.setStatus(Translator.get("status_all_saved"));
		MainMenuBar.setStatus(
				getChangeCounter() == 0 ? "" : getChangeCounter() + " " + Translator.get("status_unsaved_changes"));
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
