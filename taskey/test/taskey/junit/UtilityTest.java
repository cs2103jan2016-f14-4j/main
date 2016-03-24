package taskey.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import taskey.constants.UiConstants.IMAGE_ID;
import taskey.logger.TaskeyLog;
import taskey.logger.TaskeyLog.LogSystems;
import taskey.ui.utility.UiImageManager;

public class UtilityTest {

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
		assertTrue( fileOne.length() < 1000); // overwrites repeatedly
		File fileTwo = new File("logs/test2.out");
		assertTrue(fileTwo.exists());	
		assertTrue( fileTwo.length() < 1000);
	}
	
	@Test
	public void testImageManager() {
		assertEquals(null,UiImageManager.getInstance().getImage(IMAGE_ID.ADD_DEADLINE));
		UiImageManager.getInstance().loadImages();
		assertFalse(UiImageManager.getInstance().getImage(IMAGE_ID.ADD_DEADLINE).isError());
	}
}
