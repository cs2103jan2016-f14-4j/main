package taskey.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class UiClockService extends ScheduledService<Void> {

	private static final DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	private static final int UPDATE_INTERVAL = 1000; // in milliseconds
	private static final String PM_SUFFIX = "PM";
	private static final String AM_SUFFIX = "PM";
	
	private Label timeLabelRef;
	private Label dateLabelRef;

	public UiClockService( Label timeLabel, Label dateLabel ) {
		timeLabelRef = timeLabel;
		dateLabelRef = dateLabel;
		this.setDelay(new Duration(0));
		this.setPeriod(new Duration(UPDATE_INTERVAL));
	}
	
	@Override
	protected Task<Void> createTask() {
    	Task<Void> myTask = new Task<Void>() {
			@Override public Void call () {
				Calendar cal = Calendar.getInstance(); // need to get a new updated instance every time
				Platform.runLater(new Runnable() { // let main thread handle the update
					@Override
					public void run() {
						timeLabelRef.setText(formatTime(cal));
						dateLabelRef.setText(dateFormat.format(cal.getTime()));
					}
				});
				return null;
			}
		};
		return myTask;
	}
	
	public String formatTime(Calendar cal) {
		String myTime = "";
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		String minutePrefix = minute < 10 ? "0" : "";
		String timeOfDay = cal.get(Calendar.AM) == 1 ? AM_SUFFIX : PM_SUFFIX; // AM or PM
		myTime += hour + ":" + minutePrefix + minute + " " + timeOfDay;
		return myTime;
	}
	
}
