package gradefx.controller;

import java.util.function.Supplier;

import kafx.lang.Translator;
import gradefx.view.pane.MainMenuBar;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;

public class Controller {

	private static long changeCounter = 0;

	// TODO remove debug listeners

//	public final static ChangeListener<Object> LISTENER_UNSAVED_CHANGES = (_, _, _) -> setUnsavedChanges();
//	public final static ListChangeListener<Object> LISTLISTENER_UNSAVED_CHANGES = _ -> setUnsavedChanges();
//	public final static MapChangeListener<Object, Object> MAPLISTENER_UNSAVED_CHANGES = _ -> setUnsavedChanges();

	public final static ChangeListener<Object> LISTENER_UNSAVED_CHANGES = (o, a, b) -> {
		System.out.println(o + " " + a + " -> " + b);
		setUnsavedChanges();
	};
	public final static ListChangeListener<Object> LISTLISTENER_UNSAVED_CHANGES = c -> {
		System.out.println(c.getList() + " " + c);
		setUnsavedChanges();
	};
	public final static MapChangeListener<Object, Object> MAPLISTENER_UNSAVED_CHANGES = m -> {
		System.out.println(m.getMap() + " " + m);
		setUnsavedChanges();
	};

	public static ChangeListener<Object> getConditionalListenerUnsavedChanges(Supplier<Boolean> condition) {
		ChangeListener<Object> listener = (o, a, b) -> {
			if (condition.get()) {
				System.out.println(o + " " + a + " -> " + b);
				setUnsavedChanges();
			}
		};
		return listener;
	}

	private static void setUnsavedChanges() {
		changeCounter++;
		MainMenuBar.setStatus(changeCounter + " " + Translator.get("status_unsaved_changes"));
	}

	public static void resetUnsavedChanges() {
		changeCounter = 0;
//		MainMenuBar.setStatus(Translator.get("status_all_saved"));
		MainMenuBar.setStatus("");
	}

	public static boolean hasUnsavedChanges() {
		return changeCounter > 0;
	}

	private Controller() {
	}

}
