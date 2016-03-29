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
		ArrayList<taskey.messenger.Task> expiredList = allLists.get(LogicMemory.INDEX_EXPIRED);

		alertController.setAll(expiredList);
	}
}
