package newWork;

import java.util.StringTokenizer;

/**
 * The date class is an abstract data type that contain date and time.
 * This class contains regular constructors, set/get methods, and conversion from String or Double
 * There is also comparison method to determine which "date" is earlier
 */
public class Date {
    private int month, day, year, hour, minute, second;
    
    /**
     * Default constructor that initializes everything to 0.
     */
    public Date() {
        month 	= 0;
        day 	= 0;
        year 	= 0;
        hour 	= 0;
        minute	= 0;
        second	= 0;
    }
    
    /**
     * Constructor that takes in Month, Day and Year, but time is 0.
     * @param initMonth Initial month assignment.
     * @param initDay Initial day assignment.
     * @param initYear Initial year assignment.
     */
    public Date(int initMonth, int initDay, int initYear) {
        month 	= initMonth;
        day 	= initDay;
        year 	= initYear;
        hour	= 0;
        minute	= 0;
        second 	= 0;
    }
    
    /**
     * Constructor that takes in Date and Time.
     * @param initMonth Initial month assignment.
     * @param initDay Initial day assignment.
     * @param initYear Initial year assignment.
     * @param h Hour assignment.
     * @param m Minute assignment.
     * @param s Second assignment.
     */
    public Date(int initMonth, int initDay, int initYear, int h, int m, int s) {
        month 	= initMonth;
        day 	= initDay;
        year 	= initYear;
        hour	= h;
        minute	= m;
        second 	= s;
    }
    
    /**
     * Copies all the information from another Date object.
     * @param in The date object to be copied from.
     */
    public Date(Date in) {
        month 	= in.month;
        day 	= in.day;
        year 	= in.year;
        hour	= in.hour;
        minute	= in.minute;
        second 	= in.second;
    }
    
    /**
     * Construct a Date object from a Double, but sets the time to 0.
     * @param DDate A double in the format of YYYYMMDD
     */
    public Date(double DDate) {
        hour 	= 0;
        minute 	= 0;
        second	= 0;
        
        day = (int) (DDate%100);
        DDate /= 100;
        month = (int) (DDate%100);
        DDate /= 100;
        year = (int) DDate;
    }
    
    /**
     * Construct a Date object from a String, which can handle a few different formats.
     * @param dateString A String that contains the Date information.
     */
    public Date(String dateString) {
        hour 	= 0;
        minute 	= 0;
        second	= 0;
        
        String token = "";
    	char foundSlash = ' ', foundDash = ' ', foundQuote = ' ';
    	for (int i = 0; i < dateString.length(); i++) {
    		if (dateString.charAt(i) == '/' && foundSlash == ' ') 
    			 foundSlash = '/';
    		if(dateString.charAt(i) == '-' && foundDash == ' ')
    			foundDash	= '-';
    		if(dateString.charAt(i) == ':' && foundQuote == ' ')
    			foundQuote	= ':';	
    	} 
    	token = Character.toString(foundSlash) + Character.toString(foundDash) + Character.toString(foundQuote);
        StringTokenizer dateSeperater;
        dateSeperater = new StringTokenizer(dateString, token);
        int temp = Integer.parseInt(dateSeperater.nextToken());
        if(temp < 13){
            month 	= temp;
            day 	= Integer.parseInt(dateSeperater.nextToken());
            year 	= Integer.parseInt(dateSeperater.nextToken());  
        } else {
            year 	= temp;
            month 	= Integer.parseInt(dateSeperater.nextToken());
            day 	= Integer.parseInt(dateSeperater.nextToken());  	
        }
        if(foundQuote != ' ') {
            hour 	= Integer.parseInt(dateSeperater.nextToken());
            minute 	= Integer.parseInt(dateSeperater.nextToken());
            second	= Integer.parseInt(dateSeperater.nextToken());	
        }
    
    }
    
    public void setDateMonth (int setMonth) { month = setMonth; }
    public void setDateDay (int setDay) { day = setDay; }
    public void setDateYear (int setYear) { year = setYear; }
    public void setDateHour (int h) { hour = h; }
    public void setDateMinute (int m) { minute = m; }
    public void setDateSecond (int s) { second = s; }
    
    public int getMonth () { return month; }
    public int getDay () { return day; }
    public int getYear () { return year; }
    public int getHour() {return hour;}
    public int getMinute() {return minute;}
    public int getSecond() {return second;}
    
    /**
     * Prints Date to output MM/DD/YYYY, include time if time is not 0.
     */
    public void printDate() {
    	if (hour == 0 && minute == 0 && second == 0) {
    		System.out.print(String.valueOf(month) + "/" 
                + String.valueOf(day) + "/"
                + String.valueOf(year));
    	} else {
    		System.out.print(String.valueOf(month) + "/" 
                    + String.valueOf(day) + "/"
                    + String.valueOf(year) + " " + String.valueOf(hour) + ":" 
                    + String.valueOf(minute) + ":" + String.valueOf(second));
    	}
    }
    
    /**
     * Creates a String that contains the Date information.
     * @return  MM/DD/YYYY + time (if time != 0)
     */
    public String toString() {
    	if (hour == 0 && minute == 0 && second == 0) {
    		return (String.valueOf(month) + "/" 
                + String.valueOf(day) + "/"
                + String.valueOf(year));
    	} else {
    		return (String.valueOf(month) + "/" 
                    + String.valueOf(day) + "/"
                    + String.valueOf(year) + " " + String.valueOf(hour) + ":" 
                    + String.valueOf(minute) + ":" + String.valueOf(second));
    	}
    }
    
    /**
     * Assigns this Date based on information from another Date object completely.
     * @param d A date object to have information to be copied from.
     */
    public void set(Date d) {
    	day = d.day;
    	month = d.month;
    	year = d.year;
    	hour = d.hour;
    	minute = d.minute;
    	second = d.second;
    }
    
    /**
     * Changes the date to the next day in the calendar.
     */
    public void increment() {
    	int [] daysInMonth = {0, 31, 28, 31, 30, 31, 30,31, 31, 30, 31, 30, 31};
    	if (day < daysInMonth[month]) {
    		day++;
    		return;
    	}
    	if (day >= daysInMonth[month]) {
    		if (month == 2 && day == daysInMonth[month] && year%4 == 0) {
    			day++;
    			return;
    		}
    		day = 1;
    		if (month == 12) { 
    			month = 1;
    			year++;
    			return;
    		}
    		month++;
    		return;
    	}
    }
    
    /**
     * Determines if this date is equivalent to another.
     * @param d The other Date to be compared with.
     * @return True if date and time are both equal and false otherwise.
     */
    public boolean isEqual(Date d) {
    	if (month == d.getMonth() && day == d.getDay() && year == d.getYear()
    			&& hour == d.getHour() && minute == d.getMinute() && second == d.getSecond()) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Compares this Date and time to another, and determines which is later.
     * @param d The Date to be compared with.
     * @return True if this Date is later and false otherwise.
     */
    public boolean isLarger(Date d) {
    	if (year > d.getYear())
    		return true; 
    	if (year == d.getYear()) {
    		if (month > d.getMonth()) 
    			return true;
    		if (month == d.getMonth()) {
    			if (day > d.getDay())
    				return true;
    			if (day == d.getDay()) {
    				if (hour > d.getHour())
    					return true;
    				if(hour == d.getHour()) {
    					if(minute > d.getMinute())
    						return true;
    					if(minute == d.getMinute()) {
    						if(second > d.getSecond())
    							return true;
    					}
    						
    				}
    			}
    		}
    	}
    	return false;
    }
    
    public boolean isSmaller(Date d) {
    	return (!isLarger(d));
    }
    
    /**
     * Converts the Date to a double, but time information will be lost!
     * @return A double in the format of YYYYMMDD
     */
    public double DateToDouble() {
        return year*10000 + month*100 + day;
    }
    
    /**
     * Determines if a String is completely numeric.
     * @param tryThis The String to be tested on
     * @return True if the String is numeric only and false otherwise.
     */
    public static boolean isNumeric (String tryThis) {
        boolean result = true;
        try {
            Double.parseDouble(tryThis);
        }catch (NumberFormatException e) {
            result = false;
        }
        return result;
    }
    
    public int difference (Date R) {
        int counter = 0;
        if (this.isSmaller(R)) {
            Date temp = new Date(this);
             while (!temp.isEqual(R)) {
                temp.increment();
                counter++;
            }
            return -counter;
        } else {
            Date temp = new Date(R);
            while (!this.isEqual(temp)) {
                temp.increment();
                counter++;
            }
            return counter;
        }
    }
}


