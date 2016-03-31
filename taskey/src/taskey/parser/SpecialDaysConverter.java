package taskey.parser;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

import taskey.constants.ParserConstants;

/**
 * @@author A0107345L
 * Purpose of this class is to convert date formats like
 * "tomorrow" or "next Wed" into forms that the Parser can process. 
 * Used by ParseAdd and ParseEdit
 * @author Xue Hui
 *
 */
public class SpecialDaysConverter {
	long currTime = -1;
	private HashMap<String,Long> specialDays = new HashMap<String,Long>();
	private TimeConverter timeConverter = new TimeConverter(); 
	private int toEndOfWeek = -1; //num. of days to the end of the current week.  
	
	public SpecialDaysConverter() { 
		try {
			String dateToday = timeConverter.getDate(timeConverter.getCurrTime());
			currTime = timeConverter.toEpochTime(dateToday); 
			 
			specialDays.put("tomorrow", 
					currTime + ParserConstants.ONE_DAY);
			specialDays.put("tmr", 
					currTime + ParserConstants.ONE_DAY); 
			specialDays.put("2day", currTime);
			specialDays.put("today", currTime);
			
			processDayOfTheWeek(); 
		} catch (ParseException e) {
			//do nothing
		} 
	}
	
	/**
	 * Returns the number of days to the end of the current week
	 * @return
	 */
	public int getToEndOfWeek() {
		return toEndOfWeek; 
	}
	
	/**
	 * Returns the HashMap specialDays
	 * @return
	 */
	public HashMap<String,Long> getSpecialDays() {
		return specialDays; 
	}
	
	/**
	 * Add corresponding day of the week to the specialDays HashMap,
	 * so that Parser can process human defined date formats like next friday
	 */
	public void processDayOfTheWeek() {
		Calendar cal = Calendar.getInstance();
		cal.set(timeConverter.getYear(currTime),
				timeConverter.getMonth(currTime)-1,
				timeConverter.getDay(currTime));
		int day = cal.get(cal.DAY_OF_WEEK); 
		
		
		switch (day) {
			case 1: //sun
				processSun(currTime); 
				break; 
			case 2: //mon
				processMon(currTime); 
				break; 
			case 3: //tue
				processTue(currTime);
				break; 
			case 4: //wed
				processWed(currTime);
				break; 
			case 5: //thu
				processThu(currTime);
				break; 
			case 6: //fri
				processFri(currTime);
				break; 
			case 7: //sat
				processSat(currTime);
				break; 
		}
		
	}
	
	/**
	 * Process days relative to today being friday
	 * @param currTime
	 */
	private void processFri(long currTime) {
		//toEndOfWeek = 2;
		
		specialDays.put("sun", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("mon", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("tue", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("wed", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("thu", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("fri", 
				currTime);
		specialDays.put("sat",
				currTime + ParserConstants.ONE_DAY);
		
		specialDays.put("sunday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("monday", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("tuesday", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("wednesday", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("thursday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("friday", 
				currTime);
		specialDays.put("saturday",
				currTime + ParserConstants.ONE_DAY);
		
		specialDays.put("tues", 
				currTime + ParserConstants.FOUR_DAYS);
		specialDays.put("thurs", 
				currTime + ParserConstants.SIX_DAYS);
		
		specialDays.put("this sun", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this mon", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this tue", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this wed", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this thu", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this fri", 
				currTime);
		specialDays.put("this sat",
				currTime + ParserConstants.ONE_DAY);
		
		specialDays.put("this sunday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this monday", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this tuesday", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this wednesday", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this thursday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this friday", 
				currTime);
		specialDays.put("this saturday",
				currTime + ParserConstants.ONE_DAY);
		
		specialDays.put("this thurs", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this tues", 
				currTime + ParserConstants.FOUR_DAYS); 
		
		specialDays.put("next sun", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next mon", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next tue", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next wed", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next thu", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next fri", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next sat",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY);
		
		specialDays.put("next sunday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next monday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next tuesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next wednesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next thursday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next friday", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next saturday",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY);
		
		specialDays.put("next tues", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next thurs", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
	}
	
	/**
	 * Process days relative to today being thursday
	 * @param currTime
	 */
	private void processThu(long currTime) {
		//toEndOfWeek = 3;
		
		specialDays.put("sun", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("mon", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("tue", 
				currTime +  ParserConstants.FIVE_DAYS); 
		specialDays.put("wed", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("thu", 
				currTime); 
		specialDays.put("fri", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("sat",
				currTime + ParserConstants.TWO_DAYS);
		
		specialDays.put("sunday", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("monday", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("tuesday", 
				currTime +  ParserConstants.FIVE_DAYS); 
		specialDays.put("wednesday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("thursday", 
				currTime); 
		specialDays.put("friday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("saturday",
				currTime + ParserConstants.TWO_DAYS);
		
		specialDays.put("tues", 
				currTime +  ParserConstants.FIVE_DAYS); 
		specialDays.put("thurs", 
				currTime); 
		
		specialDays.put("this sun", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this mon", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this tue", 
				currTime +  ParserConstants.FIVE_DAYS); 
		specialDays.put("this wed", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this thu", 
				currTime); 
		specialDays.put("this fri", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this sat",
				currTime + ParserConstants.TWO_DAYS);
		
		specialDays.put("this sunday", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this monday", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this tuesday", 
				currTime +  ParserConstants.FIVE_DAYS); 
		specialDays.put("this wednesday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this thursday", 
				currTime); 
		specialDays.put("this friday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this saturday",
				currTime + ParserConstants.TWO_DAYS);
		
		specialDays.put("this tues", 
				currTime +  ParserConstants.FIVE_DAYS);  
		specialDays.put("this thurs", 
				currTime); 
		
		
		specialDays.put("next sun", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next mon", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next tue", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next wed", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next thu", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next fri", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next sat",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS);
		
		specialDays.put("next sunday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next monday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next tuesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next wednesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next thursday", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next friday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next saturday",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS);
		
		specialDays.put("next tues", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next thurs", 
				currTime + ParserConstants.ONE_WEEK); 
	}
	
	/**
	 * Process days relative to today being wednesday
	 * @param currTime
	 */
	private void processWed(long currTime) {
		//toEndOfWeek = 4;
		
		specialDays.put("sun", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("mon", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("tue", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("wed", 
				currTime); 
		specialDays.put("thu", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("fri", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("sat",
				currTime + ParserConstants.THREE_DAYS);
		
		specialDays.put("sunday", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("monday", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("tuesday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("wednesday", 
				currTime); 
		specialDays.put("thursday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("friday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("saturday",
				currTime + ParserConstants.THREE_DAYS);
		
		specialDays.put("tues", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("thurs", 
				currTime + ParserConstants.ONE_DAY); 
		
		specialDays.put("this sun", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this mon", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this tue", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this wed", 
				currTime); 
		specialDays.put("this thu", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this fri", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this sat",
				currTime + ParserConstants.THREE_DAYS);
		
		specialDays.put("this sunday", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this monday", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this tuesday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this wednesday", 
				currTime); 
		specialDays.put("this thursday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this friday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this saturday",
				currTime + ParserConstants.THREE_DAYS);
		
		specialDays.put("this tues", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this thurs", 
				currTime + ParserConstants.ONE_DAY); 
		
		specialDays.put("next sun", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next mon", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next tue", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next wed", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next thu", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next fri", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next sat",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS);
		
		specialDays.put("next sunday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next monday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next tueday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next wednesday", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next thursday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next friday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next saturday",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS);
		
		specialDays.put("next tues", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next thurs", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
	}

	/**
	 * Process days relative to today being tuesday
	 * @param currTime
	 */
	private void processTue(long currTime) {
		//toEndOfWeek = 5;
		
		specialDays.put("sun", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("mon", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("tue", 
				currTime); 
		specialDays.put("wed", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("thu", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("fri", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("sat",
				currTime + ParserConstants.FOUR_DAYS);
		
		specialDays.put("sunday", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("monday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("tuesday", 
				currTime); 
		specialDays.put("wednesday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("thursday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("friday", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("saturday",
				currTime + ParserConstants.FOUR_DAYS);
		
		specialDays.put("tues", 
				currTime); 
		specialDays.put("thurs", 
				currTime + ParserConstants.TWO_DAYS); 
		
		specialDays.put("this sun", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this mon", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this tue", 
				currTime); 
		specialDays.put("this wed", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this thu", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this fri", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this sat",
				currTime + ParserConstants.FOUR_DAYS);
		
		specialDays.put("this sunday", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this monday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this tuesday", 
				currTime); 
		specialDays.put("this wednesday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this thursday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this friday", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this saturday",
				currTime + ParserConstants.FOUR_DAYS);
		
		specialDays.put("this tues", 
				currTime); 
		specialDays.put("this thurs", 
				currTime + ParserConstants.TWO_DAYS); 
		
		specialDays.put("next sun", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next mon", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next tue", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next wed", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next thu", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next fri", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next sat",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS);
		
		specialDays.put("next sunday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next monday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next tuesday", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next wednesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next thursday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next friday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next saturday",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS);
		
		specialDays.put("next tues", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next thurs", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
	}

	/**
	 * Process days relative to today being monday
	 * @param currTime
	 */
	private void processMon(long currTime) {
		//toEndOfWeek = 6;
		
		specialDays.put("sun", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("mon", 
				currTime);
		specialDays.put("tue", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("wed", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("thu", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("fri", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("sat",
				currTime + ParserConstants.FIVE_DAYS); 
		
		specialDays.put("sunday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("monday", 
				currTime);
		specialDays.put("tuesday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("wednesday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("thursday", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("friday", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("saturday",
				currTime + ParserConstants.FIVE_DAYS);
		
		specialDays.put("tues", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("thurs", 
				currTime + ParserConstants.THREE_DAYS);
		
		specialDays.put("this sun", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this mon", 
				currTime);
		specialDays.put("this tue", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this wed", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this thu", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this fri", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this sat",
				currTime + ParserConstants.FIVE_DAYS); 
		
		specialDays.put("this sunday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this monday", 
				currTime);
		specialDays.put("this tuesday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this wednesday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this thursday", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this friday", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this saturday",
				currTime + ParserConstants.FIVE_DAYS); 
		
		specialDays.put("this tues", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this thurs", 
				currTime + ParserConstants.THREE_DAYS); 
		
		specialDays.put("next sun", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next mon", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next tue", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next wed", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next thu", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next fri", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next sat",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS);
		
		specialDays.put("next sunday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next monday", 
				currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next tuesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next wednesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next thursday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next friday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next saturday",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS);
		
		specialDays.put("next tues", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next thurs", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
	}
	
	/**
	 * Process days relative to today being sunday
	 * @param currTime
	 */
	private void processSun(long currTime) {
		//toEndOfWeek = 7;
		
		specialDays.put("mon", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("tue", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("wed", 
				currTime  + ParserConstants.THREE_DAYS); 
		specialDays.put("thu", 
				currTime  + ParserConstants.FOUR_DAYS); 
		specialDays.put("fri", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("sat",
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("sun",
				currTime);
		
		specialDays.put("monday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("tuesday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("wednesday", 
				currTime  + ParserConstants.THREE_DAYS); 
		specialDays.put("thursday", 
				currTime  + ParserConstants.FOUR_DAYS); 
		specialDays.put("friday", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("saturday",
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("sunday",
				currTime);
		
		specialDays.put("tues", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("thurs", 
				currTime  + ParserConstants.FOUR_DAYS);
		
		specialDays.put("this mon", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this tue", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this wed", 
				currTime  + ParserConstants.THREE_DAYS); 
		specialDays.put("this thu", 
				currTime  + ParserConstants.FOUR_DAYS); 
		specialDays.put("this fri", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this sat",
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this sun",
				currTime);
		
		specialDays.put("this monday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this tuesday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this wednesday", 
				currTime  + ParserConstants.THREE_DAYS); 
		specialDays.put("this thursday", 
				currTime  + ParserConstants.FOUR_DAYS); 
		specialDays.put("this friday", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this saturday",
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this sunday",
				currTime);
		
		specialDays.put("this tues", 
				currTime + ParserConstants.TWO_DAYS);  
		specialDays.put("this thurs", 
				currTime  + ParserConstants.FOUR_DAYS); 
		
		specialDays.put("next sun", currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next mon", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY ); 
		specialDays.put("next tue", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next wed", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next thu", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next fri", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next sat",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS);
		
		specialDays.put("next sunday", currTime + ParserConstants.ONE_WEEK); 
		specialDays.put("next monday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY ); 
		specialDays.put("next tuesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next wednesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next thursday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next friday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next saturday",
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS);
		
		specialDays.put("next tues", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS);  
		specialDays.put("next thurs", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
	}

	/**
	 * Process days relative to today being saturday
	 * @param currTime
	 */
	private void processSat(long currTime) {
		//toEndOfWeek = 0;
		
		specialDays.put("sun", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("mon", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("tue", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("wed", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("thu", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("fri", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("sat", 
				currTime);
		
		specialDays.put("sunday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("monday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("tuesday", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("wednesday", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("thursday", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("friday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("saturday", 
				currTime);
		
		specialDays.put("tues", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("thurs", 
				currTime + ParserConstants.FIVE_DAYS); 
		
		specialDays.put("this sun", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this mon", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this tue", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this wed", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this thu", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this fri", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this sat", 
				currTime);
		
		specialDays.put("this sunday", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this monday", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this tuesday", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this wednesday", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this thursday", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this friday", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this saturday", 
				currTime);
		
		specialDays.put("this tues", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this thurs", 
				currTime + ParserConstants.FIVE_DAYS); 
		
		specialDays.put("next sun", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next mon", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next tue", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next wed", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next thu", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next fri", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next sat",
				currTime + ParserConstants.ONE_WEEK);
		
		specialDays.put("next sunday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.ONE_DAY); 
		specialDays.put("next monday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.TWO_DAYS); 
		specialDays.put("next tuesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next wednesday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FOUR_DAYS); 
		specialDays.put("next thursday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
		specialDays.put("next friday", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.SIX_DAYS); 
		specialDays.put("next saturday",
				currTime + ParserConstants.ONE_WEEK);
		
		specialDays.put("next tues", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.THREE_DAYS); 
		specialDays.put("next thurs", 
				currTime + ParserConstants.ONE_WEEK + ParserConstants.FIVE_DAYS); 
	}
}
