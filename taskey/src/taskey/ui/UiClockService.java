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

	private Label timeLabelRef;
	private Label dateLabelRef;
	DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	
	public UiClockService( Label timeLabel, Label dateLabel ) {
		timeLabelRef = timeLabel;
		dateLabelRef = dateLabel;
		this.setDelay(new Duration(0));
		this.setPeriod(new Duration(1000));
	}
	
	@Override
	protected Task<Void> createTask() {

    	Task<Void> myTask = new Task<Void>() {
			@Override public Void call () {
				Calendar cal = Calendar.getInstance(); // need to get a new updated instance every time
				Platform.runLater(new Runnable() { // let main thread handle the update
					@Override
					public void run() {
						timeLabelRef.setText(timeFormat.format(cal.getTime()));
						dateLabelRef.setText(dateFormat.format(cal.getTime()));
						//timeLabelRef.setAlignment(arg0);
					}
				});
				return null;
			}
		};
    	
		return myTask;
	}
}
