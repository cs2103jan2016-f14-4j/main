package taskey.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import taskey.constants.UiConstants;
import taskey.logger.TaskeyLog;
import taskey.logger.TaskeyLog.LogSystems;

/**
* @@author A0125419H
* This class implements a tray module for the program, adapted from 
* http://stackoverflow.com/questions/14626550/to-hide-javafx-fxml-or-javafx-swing-application-to-system-tray
* It also acts as the main interface between uiController and alertsController
* and performs all window close/open call backs
* @author alvaro, junwei
* 
*/

public class UiTrayModule {
	private SystemTray tray = null;
	private TrayIcon trayIcon;
	private UiController mainController;
	private UiAlertsController alertsController;
	
	/**
	 * This method creates the dependency link between the alertsController and the uiController
	 * @param myController - main Stage
	 * @param alertController - alerts Stage
	 */
	public void createLinkage(UiController _mainController, UiAlertsController _alertsController) {
		assert(_mainController != null);
		assert(_alertsController != null);
		
		Platform.setImplicitExit(false); // such that stage hide does not exit program
		
		mainController = _mainController;
		alertsController = _alertsController;
		
		Stage mainStage =  mainController.getStage();
		Stage alertsStage = alertsController.getStage();
		
		mainController.setUpUpdateService(alertsController); // starts update service with alertController
		registerMainControllerHandlers(mainStage,alertsStage);
		registerAlertsControllerHandlers(mainStage,alertsStage);
	}
	
	/**
	 * Register handlers that wait for stage open / close
	 * and perform the necessary functions
	 * @param mainStage
	 * @param alertStage
	 */
	private void registerMainControllerHandlers(Stage mainStage, Stage alertStage) {
		mainStage.setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {	
				if ( tray != null ) {		
					try {
						tray.add(trayIcon); // do note that the same trayIcon cannot be added twice
					} catch (AWTException e) {
						e.printStackTrace();
					}
				}
				hide(mainStage);
				alertsController.show();
			}
		});
		
		mainStage.setOnShown(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				if ( tray != null ) {
					tray.remove(trayIcon); 
				}
			}
		});
	}
	
	private void registerAlertsControllerHandlers(Stage mainStage, Stage alertsStage) {
		alertsStage.setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {	
				alertsController.hide();
				mainStage.show();
			}
		});
		
		alertsStage.setOnShown(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				mainController.updateAlerts();
			}
		});
	}
	
	public void initTrayVariables(final Stage stage) {
		TaskeyLog.getInstance().log(LogSystems.UI, "Setting up Tray...", Level.ALL);
		
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			tray = SystemTray.getSystemTray();
			
			// create a action listeners to listen for default action executed on
			// the tray icon
			ActionListener showListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							alertsController.hide();
							stage.show();
						}
					});
				}
			};
			
			ActionListener closeNoSaveListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.exit(0);
				}
			};
			
			ActionListener closeListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mainController.doSaveOnExit();
				}
			};
			
			createTrayIcon(tray,showListener,closeNoSaveListener,closeListener);
		}
		
		TaskeyLog.getInstance().log(LogSystems.UI, "Tray has been set up...", Level.ALL);
	}
	
	private void createTrayIcon(SystemTray tray, 
								ActionListener showListener,
								ActionListener closeNoSaveListener,
								ActionListener closeListener) {
		// create a popup menu
		PopupMenu popup = new PopupMenu();
		MenuItem showItem = new MenuItem(UiConstants.TRAY_SHOW_OPTION);
		showItem.addActionListener(showListener);
		popup.add(showItem);

		MenuItem closeNoSaveItem = new MenuItem(UiConstants.TRAY_CLOSE_NO_SAVE_OPTION);
		closeNoSaveItem.addActionListener(closeNoSaveListener);
		popup.add(closeNoSaveItem);
		
		MenuItem closeItem = new MenuItem(UiConstants.TRAY_CLOSE_OPTION);
		closeItem.addActionListener(closeListener);
		popup.add(closeItem);
		
		// construct a TrayIcon
		BufferedImage trayIconImage = null;
		try {
			trayIconImage = ImageIO.read(getClass().getResource(UiConstants.TRAY_IMAGE_PATH));
		} catch (IOException e1) {
			System.out.println(UiConstants.TRAY_IMAGE_LOAD_FAIL);
		}
		int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;

		trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), 
																UiConstants.PROGRAM_NAME, popup);
		// set the TrayIcon click properties
		trayIcon.addActionListener(showListener);
	}

	private void showProgramIsMinimizedMsg() {
		trayIcon.displayMessage(UiConstants.MINIMIZE_MESSAGE_HEADER, UiConstants.MINIMIZE_MESSAGE_BODY, 
								TrayIcon.MessageType.INFO);
	}

	private void hide(final Stage stage) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (SystemTray.isSupported()) {
					stage.hide();
					showProgramIsMinimizedMsg();
				} else {
					System.exit(0);
				}
			}
		});
	}
}
