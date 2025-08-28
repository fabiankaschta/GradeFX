package kafx.lang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.stream.Collectors;

public class MultiResourceBundleControl extends Control {

	private final String baseName;
	private final List<String> dependencies = new ArrayList<>();

	public MultiResourceBundleControl(String baseName) {
		this.baseName = baseName;
	}

	public String getBaseName() {
		return this.baseName;
	}

	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		return new MultiResourceBundle(
				this.dependencies.stream().filter(name -> name != null && !"".equals(name.trim()))
						.map(name -> ResourceBundle.getBundle(name, locale)).collect(Collectors.toList()));
	}

	public void addBundleName(String bundleName) {
		this.dependencies.addFirst(bundleName);
	}
}
