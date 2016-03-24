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
import javax.imageio.ImageIO;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
	public void doLinkage(Stage main, Stage alerts) {
		main.setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {	
				hide(main);
				UiAlertController.getInstance().show();
			}
		});
		
		alerts.setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {	
				UiAlertController.getInstance().hide();
				main.show();
			}
		});
	}
	public void createTrayIcon(final Stage stage) {
		
		
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();
			
			// create a action listener to listen for default action executed on
			// the tray icon
			final ActionListener closeListener = new ActionListener() {
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
							UiAlertController.getInstance().hide();
							stage.show();
						}
					});
				}
			};
			// create a popup menu
			PopupMenu popup = new PopupMenu();

			MenuItem showItem = new MenuItem("Show Taskey");
			showItem.addActionListener(showListener);
			popup.add(showItem);

			MenuItem closeItem = new MenuItem("Close Program");
			closeItem.addActionListener(closeListener);
			popup.add(closeItem);

			// construct a TrayIcon
			BufferedImage trayIconImage = null;
			try {
				trayIconImage = ImageIO.read(getClass().getResource("utility/images/windowIcon.png"));
			} catch (IOException e1) {
				System.out.println("Failed to load tray icon");
			}
			int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;

			trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "Taskey", popup);
	
			// set the TrayIcon properties
			trayIcon.addActionListener(showListener);
			
			// add the tray image
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}
		}
	}

	public void showProgramIsMinimizedMsg() {
		trayIcon.displayMessage("Taskey has been minimized.", "Taskey will continue running in the background, click to resume planning your tasks.", TrayIcon.MessageType.INFO);
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
