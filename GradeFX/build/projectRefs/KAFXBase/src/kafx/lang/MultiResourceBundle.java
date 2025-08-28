package kafx.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MultiResourceBundle extends ResourceBundle {

	private final List<ResourceBundle> delegates;

	public MultiResourceBundle(List<ResourceBundle> resourceBundles) {
		this.delegates = resourceBundles == null ? new ArrayList<>() : resourceBundles;
	}

	@Override
	protected Object handleGetObject(String key) {
		return this.delegates.stream().filter(delegate -> delegate != null && delegate.containsKey(key))
				.map(delegate -> delegate.getObject(key)).findFirst().orElse(null);
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(this.delegates.stream().filter(delegate -> delegate != null)
				.flatMap(delegate -> Collections.list(delegate.getKeys()).stream()).collect(Collectors.toList()));
	}
}