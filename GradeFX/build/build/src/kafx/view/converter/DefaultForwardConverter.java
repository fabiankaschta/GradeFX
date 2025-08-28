package kafx.view.converter;

import javafx.util.StringConverter;

public class DefaultForwardConverter<T> extends StringConverter<T> {

	@Override
	public String toString(T object) {
		return object.toString();
	}

	@Override
	public T fromString(String string) {
		throw new UnsupportedOperationException("DefaultForwardConverter does not allow to convert back");
	}

}
