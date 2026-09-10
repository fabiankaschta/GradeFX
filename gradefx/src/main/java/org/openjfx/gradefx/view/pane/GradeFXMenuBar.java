package org.openjfx.gradefx.view.pane;

import org.openjfx.gradefx.view.menu.GroupMenu;
import org.openjfx.gradefx.view.menu.SettingsMenu;
import org.openjfx.gradefx.view.menu.StudentMenu;
import org.openjfx.gradefx.view.menu.TestMenu;
import org.openjfx.kafx.view.menu.FileMenu;
import org.openjfx.kafx.view.pane.MenuBarMessage;

public class GradeFXMenuBar extends MenuBarMessage {

	public GradeFXMenuBar() {
		super(new FileMenu(), new GroupMenu(), new StudentMenu(), new TestMenu(), new SettingsMenu());
	}

}
