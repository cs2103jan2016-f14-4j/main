package taskey.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
* @author alvaro, junwei
* 
*/

public class UiTrayModule {
	private TrayIcon trayIcon;
	
	public UiTrayModule ( ) {
		Platform.setImplicitExit(false);
	}
	
	/**
	 * This method creates the window dependency link between the alerts Window and the main Window
	 * @param main - main Stage
	 * @param alerts - alerts Stage
	 */
	public void createLinkage(Stage main, Stage alerts) {
		main.setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {	
				hide(main);
				UiAlertsWindow.getInstance().show();
			}
		});
		
		alerts.setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {	
				UiAlertsWindow.getInstance().hide();
				main.show();
			}
		});
	}
	
	public void initTrayVariables(final Stage stage) {
		TaskeyLog.getInstance().log(LogSystems.UI, "Setting up Tray...", Level.ALL);
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();
			
			// create a action listener to listen for default action executed on
			// the tray icon
			ActionListener closeListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.exit(0);
				}
			};

			ActionListener showListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							UiAlertsWindow.getInstance().hide();
							stage.show();
						}
					});
				}
			};
			createTrayIcon(tray,closeListener,showListener);
		}
		TaskeyLog.getInstance().log(LogSystems.UI, "Tray has been set up...", Level.ALL);
	}
	
	private void createTrayIcon(SystemTray tray, ActionListener closeListener, ActionListener showListener) {
		// create a popup menu
		PopupMenu popup = new PopupMenu();
		MenuItem showItem = new MenuItem(UiConstants.TRAY_SHOW_OPTION);
		showItem.addActionListener(showListener);
		popup.add(showItem);

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

		trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), UiConstants.PROGRAM_NAME, popup);

		// set the TrayIcon properties
		trayIcon.addActionListener(showListener);
		
		// add the tray image
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.err.println(e);
		}
	}

	private void showProgramIsMinimizedMsg() {
		trayIcon.displayMessage(UiConstants.MINIMIZE_MESSAGE_HEADER, UiConstants.MINIMIZE_MESSAGE_BODY, TrayIcon.MessageType.INFO);
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
