package gradefx.io;

import javafx.animation.Animation.Status;
import gradefx.io.Config.ConfigOption;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class AutoSave {

	private static final AutoSave instance;

	static {
		instance = new AutoSave(Duration.minutes(5));
	}

	public static void start() {
		instance.timer.playFromStart();
	}

	public static void stop() {
		instance.timer.stop();
	}

	public static void setIntervall(Duration duration) {
		if (instance.timer.getStatus() != Status.STOPPED) {
			stop();
			instance.timer.setDuration(duration);
			start();
		} else {
			instance.timer.setDuration(duration);
		}
	}

	private final PauseTransition timer = new PauseTransition();

	private AutoSave(Duration duration) {
		this.timer.setDuration(duration);
		this.timer.setCycleCount(PauseTransition.INDEFINITE);
		this.timer.setOnFinished(_ -> {
			if (Config.exists(ConfigOption.LAST_FILE)) {
				FileOutput.writeToFile(Config.get(ConfigOption.LAST_FILE));
			}
		});
	}

}
