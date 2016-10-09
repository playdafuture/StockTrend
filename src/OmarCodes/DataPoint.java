package OmarCodes;

import java.util.StringTokenizer;

public class DataPoint {
    private Date dpDate;
    private double open, high, low, close;
    
    public DataPoint () {
    	dpDate = new Date(0,0,0);
    	open = 0;
    	high = 0;
    	low = 0;
    	close = 0;
    }
	    
    public DataPoint (Date initDate, double initOpen, double initHigh, double initLow, double initClose) {
    	dpDate = initDate;
    	open = initOpen;
    	high = initHigh;
    	low = initLow;
    	close = initClose;
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
	    
	public DataPoint convert(String fileLine) {
        DataPoint thisLine = null;
        StringTokenizer token;
        Date date;
        String dateString;
        double open, high, low, close;
        token = new StringTokenizer(fileLine, ",");
        try { //Parse line into DataPoint and add. If fail, skip line and show error.
            dateString = token.nextToken();
            date = new Date(dateString);
            open = Double.parseDouble(token.nextToken());
            high = Double.parseDouble(token.nextToken());
            low = Double.parseDouble(token.nextToken());
            close = Double.parseDouble(token.nextToken());                
            thisLine = new DataPoint(date, open, high, low, close);
        } catch (NumberFormatException NFex) {
            //"Invalid line found"
        }            
        return thisLine;
    }
}
