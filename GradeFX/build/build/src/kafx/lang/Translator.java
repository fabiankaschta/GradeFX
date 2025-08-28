package kafx.lang;

import java.util.Locale;
import java.util.ResourceBundle;

public class Translator {

	private final static MultiResourceBundleControl control;
	private static ResourceBundle bundle;

	static {
		control = new MultiResourceBundleControl("lang");
		control.addBundleName("kafx.lang.kafx");
	}

	public static void addBundleName(String bundleName) {
		control.addBundleName(bundleName);
	}

	public static String get(String key) {
		if (bundle == null) {
			try {
				bundle = ResourceBundle.getBundle(control.getBaseName(), Locale.getDefault(), control);
			} catch (Exception e) {
				// FIXME alert
				e.printStackTrace();
			}
		}
		try {
			if (bundle.containsKey(key)) {
				return bundle.getString(key);
			} else {
				System.err.println("missing lang " + key + " " + bundle.getLocale());
				return '[' + key + ']';
			}
		} catch (Exception e) {
			System.err.println("error lang " + key + " " + bundle.getLocale());
			return '[' + key + ']';
		}
	}

	private Translator() {
	}

}
