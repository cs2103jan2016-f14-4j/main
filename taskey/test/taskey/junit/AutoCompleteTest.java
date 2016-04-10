package taskey.junit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskey.constants.UiConstants.ContentBox;
import taskey.logic.Logic;
import taskey.messenger.TagCategory;
import taskey.parser.AutoComplete;
import taskey.parser.Parser;
import taskey.parser.TimeConverter;

/**
 * @@author: A0107345L 
 * Tests the AutoComplete class
 * Type of test: Component Test 
 * @author Xue Hui
 *
 */
public class AutoCompleteTest {
	AutoComplete ac = new AutoComplete(); 
	ArrayList<TagCategory> tagDB = new ArrayList<TagCategory>(); 
	
	@Before
	public void setUp() {
		tagDB.add(new TagCategory("lala"));
		tagDB.add(new TagCategory("yolo"));
		tagDB.add(new TagCategory("hahaha"));
	}
	
	@Test
	/**
	 * Check that correcting wrong spellings for months
	 * gives the correct suggestions 
	 */
	public void testSpellChecker() {
		String[] months = {"feb","mar","apr"}; 
		ArrayList<String> correction = ac.correctDateError("fbr"); 
		
		assertEquals(months.length, correction.size()); 
		
		for(int i = 0; i < months.length; i++) {
			assertEquals(months[i], correction.get(i)); 
		}
	}
	
	@Test
	public void testCommandSuggestions() {
		assertEquals("Command: DISPLAY_COMMAND\nOptions: view, \n",
				ac.getSuggestions("biw",tagDB).toString());
		assertEquals("Command: DISPLAY_COMMAND\nOptions: add, \n",
				ac.getSuggestions("a", tagDB).toString()); 
	}
	
	@Test
	/**
	 * Test Suggestions for View
	 */
	public void testViewSuggestions() {
		assertEquals("Command: DISPLAY_COMMAND\nOptions: #lala, #yolo, #hahaha, \n",
				ac.getSuggestions("view #", tagDB).toString());
		assertEquals("Command: DISPLAY_COMMAND\nOptions: general, \n",
				ac.getSuggestions("view g", tagDB).toString());
		
		assertEquals("Command: NO_SUCH_COMMAND\n\n",
				ac.getSuggestions("view aadadsdaer", tagDB).toString());
	}
	
	@Test
	/**
	 * Test suggestions for Add
	 */
	public void testAddSuggestions() {
		assertEquals("Command: DISPLAY_COMMAND\nOptions: tmr, 8pm, next mon, \n",
				ac.getSuggestions("add sth by", tagDB).toString());
		assertEquals("Command: FINISHED_COMMAND\n\n",
				ac.getSuggestions("add my task", tagDB).toString());
		assertEquals("Command: DISPLAY_COMMAND\nOptions: 18 apr, 18 may, 18 jun, \n",
				ac.getSuggestions("add sth by 18", tagDB).toString());
		assertEquals("Command: FINISHED_COMMAND\n\n",
				ac.getSuggestions("add sth by the park", tagDB).toString());
		assertEquals("Command: DISPLAY_COMMAND\nOptions: 1800h, \n",
				ac.getSuggestions("add sth by 1800", tagDB).toString());
		assertEquals("Command: DISPLAY_COMMAND\nOptions: #lala, #yolo, #hahaha, \n",
				ac.getSuggestions("add sth #", tagDB).toString());
		assertEquals("Command: DISPLAY_COMMAND\nOptions: #yolo, \n",
				ac.getSuggestions("add sth #y", tagDB).toString());
		assertEquals("Command: FINISHED_COMMAND\n\n",
				ac.getSuggestions("add sth #babablack", tagDB).toString());
	}
	
	@Test
	/**
	 * Test the Add spelling corrector for months
	 */
	public void testAddSpellingCorrector() {
		//test spelling corrector
		assertEquals("Command: DISPLAY_COMMAND\nOptions: feb, mar, apr, \n",
				ac.getSuggestions("add task by 17 fbr", tagDB).toString());
	}
	
	@Test
	/**
	 * Test suggestions for Set
	 */
	public void testEditSuggestions() {
		assertEquals("Command: DISPLAY_COMMAND\nOptions: \"New Task Name\", [New Date], !!, \n",
				ac.getSuggestions("set 1 ", tagDB).toString());
		assertEquals("Command: DISPLAY_COMMAND\nOptions: tomorrow, \n",
				ac.getSuggestions("set 1 [", tagDB).toString());
		
		assertEquals("Command: DISPLAY_COMMAND\nOptions: 1800h, \n",
				ac.getSuggestions("set 1 [1800", tagDB).toString());
		
		assertEquals("Command: DISPLAY_COMMAND\nOptions: tomorrow, \n",
				ac.getSuggestions("set 1 [9pm ", tagDB).toString()); 
		
		
		
		assertEquals("Command: DISPLAY_COMMAND\nOptions: 19 apr, 19 may, 19 jun, \n",
				ac.getSuggestions("set 1 [18 may, 19", tagDB).toString());
		
		assertEquals("Command: FINISHED_COMMAND\n\n",
				ac.getSuggestions("set 1 [18 may", tagDB).toString());
		
		assertEquals("Command: FINISHED_COMMAND\n\n",
				ac.getSuggestions("set 1 \"sth\"", tagDB).toString());
	}
	
	@Test
	/**
	 * Test changing of task priority suggestions
	 */
	public void testEditPrioritySuggestions() {
		assertEquals("Command: DISPLAY_COMMAND\nOptions: !, !!, !!!, \n",
				ac.getSuggestions("set 1 !", tagDB).toString());
		assertEquals("Command: DISPLAY_COMMAND\nOptions: !!, !!!, \n",
				ac.getSuggestions("set 1 !!", tagDB).toString());
		assertEquals("Command: FINISHED_COMMAND\n\n",
				ac.getSuggestions("set 1 !!!", tagDB).toString());
		assertEquals("Command: NO_SUCH_COMMAND\n\n",
				ac.getSuggestions("set 1 !!!!!", tagDB).toString());
	}
	
	@Test
	/**
	 * Test suggestions for other commands
	 */
	public void testOtherCommandSuggestions() {
		assertEquals("Command: FINISHED_COMMAND\n\n",
				ac.getSuggestions("clear", tagDB).toString());
	}
	
	@Test
	/**
	 * Test suggestions for unavailable commands 
	 */
	public void testNoSuchCommand() {
		assertEquals("Command: NO_SUCH_COMMAND\n\n",
				ac.getSuggestions("finderrrrrr ", tagDB).toString());
		assertEquals("Command: NO_SUCH_COMMAND\n\n",
				ac.getSuggestions("finderrrr 1", tagDB).toString());
	}
	
	/*
	 * Manual Testing: Human Times
	 */
	
	@Test
	/**
	 * Test human time suggestions for set 
	 */
	public void testEditSuggestionsHuman() {
		System.out.println(ac.getSuggestions("set 1 [9pm 18", tagDB).toString()); 
		System.out.println(ac.getSuggestions("set 1 [18", tagDB).toString());
	}
}
