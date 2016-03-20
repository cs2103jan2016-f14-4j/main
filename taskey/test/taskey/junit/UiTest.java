package taskey.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import taskey.logger.TaskeyLog;
import taskey.logger.TaskeyLog.LogSystems;



public class UiTest {

	@Test
	public void test() {
		Logger UiLog = TaskeyLog.getInstance().getLogger(LogSystems.UI);
		assertTrue(UiLog.getHandlers().length == 0);
		TaskeyLog.getInstance().addHandler(LogSystems.UI, "test.out", 1);
		assertTrue(UiLog.getHandlers().length == 1);
		TaskeyLog.getInstance().addHandler(LogSystems.UI, "test2.out", 1);
		assertTrue(UiLog.getHandlers().length == 2);
		
		for ( int i = 0; i < 10000; i ++ ) { 
			TaskeyLog.getInstance().log(LogSystems.UI, "a\n",  Level.ALL);
		} 
		File fileOne = new File("logs/test.out");
		assertTrue(fileOne.exists());	
		assertTrue( fileOne.length() < 1000);
		File fileTwo = new File("logs/test2.out");
		assertTrue(fileTwo.exists());	
		assertTrue( fileTwo.length() < 1000);
	}
}
