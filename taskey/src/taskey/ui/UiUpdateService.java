package taskey.ui;

import java.util.Calendar;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.util.Duration;
import taskey.constants.UiConstants;

/**
 * @@author A0125419H
 * This class performs a background service, which updates the UI date
 * and performs polling of logic
 *
 * @author JunWei
 */

public class UiUpdateService extends ScheduledService<Void> {

	private Label dateLabelRef;

	public UiUpdateService(Label dateLabel) {
		assert(dateLabel != null);
		dateLabelRef = dateLabel;
		this.setDelay(new Duration(0));
		this.setPeriod(new Duration(UiConstants.ClOCK_UPDATE_INTERVAL));
	}

	@Override
	protected Task<Void> createTask() {
		Task<Void> myTask = new Task<Void>() {
			@Override
			public Void call() {
				Calendar cal = Calendar.getInstance(); // need to get a new updated instance every time
				Platform.runLater(new Runnable() { // let main thread handle the update
					@Override
					public void run() {		
						taskey.logic.Task t = new taskey.logic.Task(); // to do, prompt for expiring tasks
						UiAlertsWindow.getInstance().addEntry(t);
						dateLabelRef.setText(UiConstants.CLOCK_DATE_FORMAT.format(cal.getTime()));
					}
				});
				return null;
			}
		};
		return myTask;
	}
}
