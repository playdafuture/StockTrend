package MyOldCodes;

/**
 *
 * @author Future
 */
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
    public void printRecord () {
        System.out.println(dpDate.DateToString() + " " + open + " " + high + 
                " " + low + " " + close);
    }
    
}
