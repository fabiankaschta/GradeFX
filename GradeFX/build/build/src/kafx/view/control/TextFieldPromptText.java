package kafx.view.control;

import javafx.scene.control.TextField;

public class TextFieldPromptText extends TextField {

	/**
	 * Creates a {@code TextFieldPromptText} with empty text content and no prompt
	 * text.
	 */
	public TextFieldPromptText() {
		super();
	}

	/**
	 * Creates a {@code TextFieldPromptText} with prompt text and empty text
	 * content.
	 *
	 * @param promptText A string for prompt text.
	 */
	public TextFieldPromptText(String promptText) {
		this(promptText, "");
	}

	/**
	 * Creates a {@code TextFieldPromptText} with prompt text and initial text
	 * content.
	 *
	 * @param promptText A string for prompt text.
	 * @param text       A string for text content.
	 */
	public TextFieldPromptText(String promptText, String text) {
		super(text);
		this.setPromptText(promptText);
	}

}
