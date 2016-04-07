package taskey.junit;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;

import taskey.constants.ParserConstants;
import taskey.parser.TimeConverter;

/**
 *  @@author A0107345L
 *  Tests the TimeConverter class
 *  @author: Xue Hui 
 **/
public class TimeConverterTest {
	private TimeConverter tc = new TimeConverter();
	
	@Test
	/**
	 * Test basic methods for TimeConverter 
	 */
	public void testEpoch() {
		tc.setCurrTime();
		
		try {
			//System.out.println(timeConverter.toHumanTime(1455692400));
			//System.out.println("Current Time in Epoch: " + timeConverter.getCurrTime()); 
			assertEquals(1455123600,tc.toEpochTime("11 Feb 2016 01:00:00"));
			
			assertFalse(tc.isSameDay(tc.getCurrTime(),
					tc.toEpochTime("11 Feb 2016 23:00:00")));
			
			assertEquals(1455206399,tc.toEpochTime("11 Feb 2016"));
			assertEquals(1455206399,tc.toEpochTime("11 Feb"));
			assertEquals(1454342399,tc.toEpochTime("1 Feb 2016"));
			assertEquals(1454342399,tc.toEpochTime("1 Feb"));
			assertEquals("01 Feb 2016 23:59",tc.toHumanTime(1454342399));			
			
		} catch (ParseException e) {
			
		}
		assertEquals("11 Feb 2016 01:00",tc.toHumanTime(1455123600)); 
	}
	
	@Test
	/**
	 * Test isSameWeek and isSameDay 
	 */
	public void testWeekDay() {
		try { 
			
		long time1 = tc.toEpochTime("31 Mar");
		long time2 = tc.toEpochTime("1 Apr");
		assertTrue(tc.isSameWeek(time1, time2));
		
		} catch (Exception e) {
			
		}
		
		//compare 10Feb 11pm vs 11Feb 1am 
		assertFalse(tc.isSameDay(1455123600,1455116400));
		//compare 11 feb a few minutes later 
		assertTrue(tc.isSameDay(1455123600,1455123800));
		//compare 10Feb 11pm and 11Feb 1am 
		assertTrue(tc.isSameWeek(1455123600,1455116400));
	}
	
	@Test 
	/**
	 * Test that the week starts on sunday and not monday 
	 */
	public void testSameWeek() {
		try {
			long epochTime1 = tc.toEpochTime("27 Mar");
			long epochTime2 = tc.toEpochTime("28 Mar"); 
			//sun and mon are diff days of the week
			assertFalse(tc.isSameWeek(epochTime1, epochTime2));
			epochTime1 = tc.toEpochTime("29 Mar");
			//mon and tues are same days of the week 
			assertTrue(tc.isSameWeek(epochTime1, epochTime2));
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	@Test 
	/**
	 * Test isToday and isTmr 
	 */
	public void testTodayTmr() {
		assertFalse(tc.isToday(1455123600));
		assertFalse(tc.isTmr(1455123600));
		
		assertTrue(tc.isToday(tc.getCurrTime()));
		assertTrue(tc.isTmr(tc.getCurrTime()+ ParserConstants.ONE_DAY));
	}
	
	@Test 
	/**
	 * Test that the right time/day of the week is given back 
	 */
	public void testTimeConversion() {
		assertEquals("01:00",tc.getTime(1455123600));
		assertEquals("THU",tc.getDayOfTheWeek(1455123600));
	}
	
	/*
	 * Manual Testing: Test human methods
	 */
	
	@Test
	/**
	 * Test that the correct 3 months are given back
	 */
	public void testHumanFunctions() {
		System.out.println(tc.get3MonthsFromNow());
		System.out.println(tc.getDayOfTheWeek());
	}

}
