package taskey.logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import taskey.logic.Logic;
import taskey.parser.Parser;
import taskey.storage.Storage;
import taskey.ui.UiMain;

public class TaskeyLog {
	public enum LogSystems {
		UI(0), LOGIC(1), PARSER(2), STORAGE(3);
		private int index;
		private LogSystems(int index) {
			this.index = index;
		}	
		public int getIndex() {
			return index;
		}
	};
	
	private static final String defaultLocation = "logs/";
	private static TaskeyLog instance = null;	
	private ArrayList<Logger> myLoggers;
	
	private TaskeyLog() {
		myLoggers = new ArrayList<Logger>();
		// add loggers
		createLogger(UiMain.class.getName());
		createLogger(Logic.class.getName());
		createLogger(Parser.class.getName());
		createLogger(Storage.class.getName());
	}
	public static TaskeyLog getInstance() {
		if ( instance == null) {
			instance = new TaskeyLog();
		}
		return instance;
	}
	public void createLogger(String className) {
		Logger newLogger = Logger.getLogger(className);
		newLogger.setLevel(Level.ALL);
		myLoggers.add(newLogger);
	}
	
	/**
	 * This method adds a handler to a logger variable, which makes any log() call
	 * pass on the msg to each handler.
	 * Count is the number of files to use for logging for this handler, note that logs are not appended but overwritten
	 * Note: Log files are found in main directory under logs folder
	 * @param system : LogSystems enum
	 * @param fileName
	 * @param count
	 */
	public void addHandler( LogSystems system , String fileName, int count ) {
		Logger theLogger = myLoggers.get(system.getIndex());
		int byteCount = 1000000;
		Handler handler = null; 
		try {
			handler = new FileHandler(defaultLocation + fileName, byteCount,count); 
			SimpleFormatter formatter = new SimpleFormatter();
			handler.setFormatter(formatter);
			theLogger.addHandler(handler); // if handle
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		theLogger.setLevel(Level.ALL); // default to show all levels
	}
	
	// Log through all levels provided
	public void log( LogSystems system, String msg, Level ...levelOfLogging) {
		Logger theLogger = myLoggers.get(system.getIndex());
		for ( Level l : levelOfLogging) {
			theLogger.log(l,msg);	
		}
	}
}
