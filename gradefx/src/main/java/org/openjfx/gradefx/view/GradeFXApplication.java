package org.openjfx.gradefx.view;

import org.openjfx.gradefx.view.pane.GradeFXMenuBar;
import org.openjfx.gradefx.view.pane.GroupsPane;
import org.openjfx.kafx.view.KAFXApplication;

import javafx.scene.image.Image;

public class GradeFXApplication extends KAFXApplication {

	public GradeFXApplication() {
		super(GroupsPane.get(), new GradeFXMenuBar(),
				new Image(GradeFXApplication.class.getResourceAsStream("/org/openjfx/gradefx/img/icon.png")));
	}

}
