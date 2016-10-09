package newWork;

import java.util.StringTokenizer;
enum Status {relativeLow, relativeHigh, strongLow, strongHigh, none};
/**
 * The DataPoint object contains a Date, and the four key values of one day.
 * Open, high, low, close.
 */
public class DataPoint {
    private Date dpDate;
    private double open, high, low, close;
    private Status flag;
    public double EMA20, EMA40;
    
    /**
     * Default constructor that sets everything to 0.
     */
    public DataPoint () {
    	dpDate = new Date(0,0,0);
    	open = 0;
    	high = 0;
    	low = 0;
    	close = 0;
        flag = Status.none;
        EMA20 = 0;
        EMA40 = 0;
    }
    
    /**
     * Constructor that takes in 1 Date object and 4 Doubles.
     * @param initDate Initial Date assignment.
     * @param initOpen Initial Open Value assignment.
     * @param initHigh Initial High Value assignment.
     * @param initLow Initial Low Value assignment.
     * @param initClose Initial Close Value assignment.
     */
    public DataPoint (Date initDate, double initOpen, double initHigh, double initLow, double initClose) {
    	dpDate = initDate;
    	open = initOpen;
    	high = initHigh;
    	low = initLow;
    	close = initClose;
        flag = Status.none;
        EMA20 = 0;
        EMA40 = 0;
    }
	    
    public Date getDate() { return dpDate;}
    public double getOpen() { return open;}
    public double getHigh() { return high;}
    public double getLow() { return low;}
    public double getClose() { return close;} 
	
    public void setDate (Date newDate) { dpDate = newDate;}	
    public void setOpen (double newOpen) { open = newOpen;}
    public void setHigh (double newHigh) { high = newHigh;}
    public void setLow (double newLow) { low = newLow;}
    public void setClose (double newClose) { close = newClose;}
	    
    /**
     * Print method to print the information of this DataPoint in one line.
     */
    public void print () {
        System.out.println(dpDate.toString() + " " + open + " " + high + 
                " " + low + " " + close);
    }

    /**
     * Converts a String to a DataPoint.
     * @param fileLine "YYYY-MM-DD,Open,High,Low,Close, ..."
     * @return The DataPoint information contained in the line
     */
    public DataPoint convert(String fileLine) {
        DataPoint thisLine = null;
        StringTokenizer token;
        Date date;
        String dateString;
        double open, high, low, close, volume, adjClose;
        token = new StringTokenizer(fileLine, ",");
        try { //Parse line into DataPoint and add. If fail, skip line and show error.
            dateString = token.nextToken();
            date = new Date(dateString);
            open = Double.parseDouble(token.nextToken());
            high = Double.parseDouble(token.nextToken());
            low = Double.parseDouble(token.nextToken());
            close = Double.parseDouble(token.nextToken());
            volume = Double.parseDouble(token.nextToken());
            adjClose = Double.parseDouble(token.nextToken());
            double ratio = close/adjClose;
            open /= ratio;
            high/= ratio;
            low /= ratio;
            close /= ratio;
            
            thisLine = new DataPoint(date, open, high, low, close);
        } catch (NumberFormatException NFex) {
            //"Invalid line found"
        }            
        return thisLine;
    }
    
    public void setFlag (int f) {
        if (f == 2) {
            flag = Status.strongHigh;
        } else if (f == 1) {
            flag = Status.relativeHigh;
        } else if (f == -1) {
            flag = Status.relativeLow;
        } else if (f == -2) {
            flag = Status.strongLow;
        } else {
            flag = Status.none;
        }
    }
    
    public int getFlag () {
        if (flag == Status.strongHigh) {
            return 2;
        } else if (flag == Status.relativeHigh) {
            return 1;
        } else if (flag == Status.relativeLow) {
            return -1;
        } else if (flag == Status.strongLow) {
            return -2;
        } else {
            return 0;
        }
    }
}
