package OmarCodes;


import java.util.StringTokenizer;
/**
 *
 * @author Future
 */
public class Date {
    private int month, day, year, hour, minute, second;
    
    public Date() {
        month 	= 0;
        day 	= 0;
        year 	= 0;
        hour 	= 0;
        minute	= 0;
        second	= 0;
    }
    
    public Date(int initMonth, int initDay, int initYear) {
        month 	= initMonth;
        day 	= initDay;
        year 	= initYear;
        hour	= 0;
        minute	= 0;
        second 	= 0;
    }
    
    public Date(int initMonth, int initDay, int initYear, int h, int m, int s) {
        month 	= initMonth;
        day 	= initDay;
        year 	= initYear;
        hour	= h;
        minute	= m;
        second 	= s;
    }
    public Date(Date in) {
        month 	= in.month;
        day 	= in.day;
        year 	= in.year;
        hour	= in.hour;
        minute	= in.minute;
        second 	= in.second;
    }
    public Date(double DDate) {
        day = (int) (DDate%100);
        DDate /= 100;
        month = (int) (DDate%100);
        DDate /= 100;
        year = (int) DDate;
    }
    
    /**
     * Advanced constructor, converts a string which includes date information into a Date class.
     * @param dateString The string which contains the date information
     * @param sequence Specify how the date information is stored in the String.
     * For example "YMD" means the year comes first, then month, then day.
     * (to be implemented: other arrangements)
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
        }else {
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
    public void set(Date d) {
    	day = d.day;
    	month = d.month;
    	year = d.year;
    	hour = d.hour;
    	minute = d.minute;
    	second = d.second;
    }
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
    public boolean isEqual(Date d) {
    	if (month == d.getMonth() && day == d.getDay() && year == d.getYear()
    			&& hour == d.getHour() && minute == d.getMinute() && second == d.getSecond()) {
    		return true;
    	}
    	return false;
    }
    
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
    
    public double DateToDouble() {
        return year*10000 + month*100 + day;
    }
    
    public static boolean isNumeric (String tryThis) {
    boolean result = true;
    try {
        Double.parseDouble(tryThis);
    }catch (NumberFormatException e) {
        result = false;
    }
    return result;
}
}


