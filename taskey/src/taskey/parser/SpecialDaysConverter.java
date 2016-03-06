package taskey.parser;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

import taskey.constants.ParserConstants;

/**
 * Purpose of this class is to convert date formats like
 * "tomorrow" or "next Wed" into forms that the Parser can process. 
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
		toEndOfWeek = 2;
		
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
		specialDays.put("this sat",
				currTime + ParserConstants.ONE_DAY);
		
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
	}
	
	/**
	 * Process days relative to today being thursday
	 * @param currTime
	 */
	private void processThu(long currTime) {
		toEndOfWeek = 3;
		
		specialDays.put("this sun", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this mon", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this tue", 
				currTime +  ParserConstants.FIVE_DAYS); 
		specialDays.put("this wed", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this fri", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this sat",
				currTime + ParserConstants.TWO_DAYS);
		
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
	}
	
	/**
	 * Process days relative to today being wednesday
	 * @param currTime
	 */
	private void processWed(long currTime) {
		toEndOfWeek = 4;
		
		specialDays.put("this sun", 
				currTime + ParserConstants.FOUR_DAYS); 
		specialDays.put("this mon", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this tue", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this thu", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this fri", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this sat",
				currTime + ParserConstants.THREE_DAYS);
		
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
	}

	/**
	 * Process days relative to today being tuesday
	 * @param currTime
	 */
	private void processTue(long currTime) {
		toEndOfWeek = 5;
		
		specialDays.put("this sun", 
				currTime + ParserConstants.FIVE_DAYS); 
		specialDays.put("this mon", 
				currTime + ParserConstants.SIX_DAYS); 
		specialDays.put("this wed", 
				currTime + ParserConstants.ONE_DAY); 
		specialDays.put("this thu", 
				currTime + ParserConstants.TWO_DAYS); 
		specialDays.put("this fri", 
				currTime + ParserConstants.THREE_DAYS); 
		specialDays.put("this sat",
				currTime + ParserConstants.FOUR_DAYS);
		
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
	}

	/**
	 * Process days relative to today being monday
	 * @param currTime
	 */
	private void processMon(long currTime) {
		toEndOfWeek = 6;
		
		specialDays.put("this sun", 
				currTime + ParserConstants.SIX_DAYS); 
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
	}
	
	/**
	 * Process days relative to today being sunday
	 * @param currTime
	 */
	private void processSun(long currTime) {
		toEndOfWeek = 7;
		
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
	}

	/**
	 * Process days relative to today being saturday
	 * @param currTime
	 */
	private void processSat(long currTime) {
		toEndOfWeek = 0;
		
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
	}
}
