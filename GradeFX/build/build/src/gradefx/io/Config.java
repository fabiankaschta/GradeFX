package gradefx.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import gradefx.view.alert.AlertException;

public class Config {

	public enum ConfigOption {
		LAST_FILE, LANGUAGE, MAXIMIZED, WIDTH, HEIGHT
	}

	private final static String filename = "gradefx.cfg";
	private final static String path = System.getProperty("user.home");
	private final static String separator = System.getProperty("file.separator");
	private final static String configFile = /* path + separator + */filename;
	private final static Properties properties = new Properties();

	static {
		load();
	}

	public static void load() {
		try {
			new File(configFile).createNewFile();
			properties.load(new FileInputStream(configFile));
		} catch (IOException e) {
			new AlertException(e);
		}
	}

	public static void store() {
		try {
			properties.store(new FileOutputStream(configFile), "");
		} catch (IOException e) {
			new AlertException(e);
		}
	}

	public static boolean exists(ConfigOption option) {
		return get(option) != null;
	}

	public static void remove(ConfigOption option) {
		properties.remove(option.toString());
	}

	public static String get(ConfigOption option) {
		return properties.getProperty(option.toString());
	}

	public static void set(ConfigOption option, String value) {
		properties.setProperty(option.toString(), value);
	}

}
