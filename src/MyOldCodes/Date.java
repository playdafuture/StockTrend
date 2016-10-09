/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyOldCodes;

import java.util.StringTokenizer;

/**
 *
 * @author Future
 */
public class Date {
    private int month, day, year;
    
    public Date() {
        month = 0;
        day = 0;
        year = 0;
    }
    
    public Date(int initMonth, int initDay, int initYear) {
        month = initMonth;
        day = initDay;
        year = initYear;
    }
    
    /**
     * Advanced constructor, converts a string which includes date information into a Date class.
     * @param dateString The string which contains the date information
     * @param sequence Specify how the date information is stored in the String.
     * For example "YMD" means the year comes first, then month, then day.
     * (to be implemented: other arrangements)
     */
    public Date(String dateString, String sequence) {
        month = 0;
        day = 0;
        year = 0;
        
        if (sequence.equalsIgnoreCase("YMD")) {
            String token = "";
            for (int i = 0; i < dateString.length(); i++) {
                if (!isNumeric(dateString.substring(i, i+1))) {
                    token = dateString.substring(i, i+1);
                    break;
                }
            }
            StringTokenizer dateSeperater;
            dateSeperater = new StringTokenizer(dateString, token);
            year = Integer.parseInt(dateSeperater.nextToken());
            month = Integer.parseInt(dateSeperater.nextToken());
            day = Integer.parseInt(dateSeperater.nextToken());            
        }
    }
    
    public void setDateMonth (int setMonth) { month = setMonth; }
    public void setDateDay (int setDay) { day = setDay; }
    public void setDateYear (int setYear) { year = setYear; }
    
    public int getDateMonth () { return month; }
    public int getDateDay () { return day; }
    public int getDateYear () { return year; }
    
    public void printDate() {
        System.out.print(String.valueOf(month) + "-" 
                + String.valueOf(day) + "-"
                + String.valueOf(year));
    }
    
    public String DateToString() {
        return (String.valueOf(month) + "-" 
                + String.valueOf(day) + "-"
                + String.valueOf(year));
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


