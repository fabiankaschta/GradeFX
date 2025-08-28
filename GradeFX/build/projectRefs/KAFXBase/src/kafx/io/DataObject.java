package kafx.io;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public interface DataObject<T> extends Serializable {

	final static Map<Object, DataObject<?>> serializedObjects = new HashMap<>();

	public static DataObject<?> getSerialized(Object object) {
		return serializedObjects.get(object);
	}

	public static void putSerialized(Object object, DataObject<?> serializedObject) {
		serializedObjects.put(object, serializedObject);
	}

	public static void clear() {
		serializedObjects.clear();
	}

	public abstract T deserialize(Object... params);

}
