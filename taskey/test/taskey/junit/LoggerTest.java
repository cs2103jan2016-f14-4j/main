package taskey.junit;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import taskey.logger.TaskeyLog;
import taskey.logger.TaskeyLog.LogSystems;

public class LoggerTest {

	@Test
	public void testHandlers() {
		Logger UiLog = TaskeyLog.getInstance().getLogger(LogSystems.UI);

		/* This is a boundary case for negative file count partition */
		try {
			TaskeyLog.getInstance().addHandler(LogSystems.UI, "test.out", -10);
		} catch (Exception e) {
			assertTrue(true);
		}
		assertTrue(UiLog.getHandlers().length == 0);
		TaskeyLog.getInstance().addHandler(LogSystems.UI, "test.out", 1);
		assertTrue(UiLog.getHandlers().length == 1);
		TaskeyLog.getInstance().addHandler(LogSystems.UI, "test2.out", 1);
		assertTrue(UiLog.getHandlers().length == 2);
		
		String longString = "";
		for ( int i = 0; i < 10000; i ++ ) { 
			longString += "a";	
		} 
		/* This is a boundary case for character counter > MAX_BYTES partition */
		TaskeyLog.getInstance().log(LogSystems.UI, longString,  Level.ALL);
		File fileOne = new File("logs/test.out");
		assertTrue(fileOne.exists());	
		assertTrue( fileOne.length() <= 100000); // overwrites repeatedly
		File fileTwo = new File("logs/test2.out");
		assertTrue(fileTwo.exists());	
		assertTrue( fileTwo.length() <= 100000);
	}
	
}
