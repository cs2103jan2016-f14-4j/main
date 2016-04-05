package taskey.ui;

import java.util.ArrayList;
import java.util.Calendar;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.util.Duration;
import taskey.constants.UiConstants;
import taskey.logic.Logic;
import taskey.logic.LogicMemory;

/**
 * @@author A0125419H
 * This class performs a background service, which updates the UI date
 * and performs polling of logic to update the Alerts window
 *
 * @author JunWei
 */

public class UiUpdateService extends ScheduledService<Void> {

	private UiAlertsController alertController;
	private Label dateLabelRef;
	private Logic logicRef; // logic reference

	public UiUpdateService(Label dateLabel, Logic logic, UiAlertsController _alertController) {
		assert(dateLabel != null);
		assert(logic != null);
		dateLabelRef = dateLabel;
		logicRef = logic;
		alertController = _alertController;
		this.setDelay(new Duration(0));
		this.setPeriod(new Duration(UiConstants.UPDATE_SERVICE_INTERVAL));
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
						dateLabelRef.setText(UiConstants.CLOCK_DATE_FORMAT.format(cal.getTime()));
						pollFromLogic();
					}
				});
				return null;
			}
		};
		return myTask;
	}
	
	/**
	 * This method polls task lists from logic and updates the Alert Window
	 */
	public void pollFromLogic() {
		ArrayList<ArrayList<taskey.messenger.Task>> allLists = logicRef.getAllTaskLists();
		ArrayList<UiAlert> alertList = new ArrayList<UiAlert>();
		
		ArrayList<taskey.messenger.Task> expiredList = allLists.get(LogicMemory.INDEX_EXPIRED);
		for ( int i = 0; i < expiredList.size(); i++ ) {
			alertList.add(createAlert(expiredList.get(i), "This has expired!"));
		}
		
		ArrayList<taskey.messenger.Task> pendingList = allLists.get(LogicMemory.INDEX_PENDING);		
		for ( int i = 0; i < pendingList.size(); i++ ) {
			taskey.messenger.Task currentTask = pendingList.get(i);
			checkTaskBounds(currentTask,alertList);
		}
		
		alertController.setAllAlerts(alertList);
	}

	/**
	 * This methods adds periodic reminders for events and deadlines
	 * Note that intervals that are closer have higher precedence over further ones
	 * such that if there is 3 hours left, > 3 hours are not added as alerts
	 * 
	 * @param currentTask
	 * @param alertList
	 */
	private void checkTaskBounds(taskey.messenger.Task currentTask, ArrayList<UiAlert> alertList) {
		int secondsInOneHour = 3600;
		long currTimeInSeconds = System.currentTimeMillis()/1000;
		ArrayList<Integer> checkIntervals = new ArrayList<Integer>();
		for (int index = 0; index < UiConstants.HOUR_MARKS.length; index++) {
			checkIntervals.add(UiConstants.HOUR_MARKS[index]);
		}
		
		for ( int i = 0; i < checkIntervals.size(); i ++ ) {
			int hoursBefore = checkIntervals.get(i);
			int secondsBefore = secondsInOneHour * hoursBefore;
			if ( currentTask.getTaskType().equals("DEADLINE")) {
				if ( currentTask.getDeadlineEpoch() - currTimeInSeconds < secondsBefore) {
					alertList.add(createAlert(currentTask, "Within " + getHourString(hoursBefore) + " Left!"));
					break;
				}
			} else if ( currentTask.getTaskType().equals("EVENT")) {
				// event has started
				if (currTimeInSeconds >= currentTask.getStartDateEpoch() && 
					currentTask.getEndDateEpoch() > currTimeInSeconds && 
					currentTask.getEndDateEpoch() - currTimeInSeconds < secondsBefore) {
					alertList.add(createAlert(currentTask,
							"Event Ending within " + getHourString(hoursBefore) + "!"));
					break;
				// event has not started
				} else if ( currentTask.getStartDateEpoch() > currTimeInSeconds && 
							currentTask.getStartDateEpoch() - currTimeInSeconds  < secondsBefore) {
					alertList.add(createAlert(currentTask,
							"Event Starting within " + getHourString(hoursBefore) + "!"));
					break;
				} 
			}
		}
	}

	private String getHourString( int hours ) {
		// with or without the 's' at the end
		if ( hours <= 1 ) {
			return hours + " Hour";
		} else {
			return hours + " Hours";
		}
	}
	
	private UiAlert createAlert(taskey.messenger.Task fromTask, String msg) {
		UiAlert myAlert = new UiAlert(fromTask);
		myAlert.setMessage(msg);
		return myAlert;
	}
}
