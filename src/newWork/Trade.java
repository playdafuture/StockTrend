package newWork;
import core.*;


public class Trade {
    protected Date	entryDate;
    protected Date 	closingDate;
    protected Date      triggerDate;
    protected double    entryPrice; 
    protected double    closingPrice; 
    protected double    target;
    protected double    stopLoss;
    protected int       numberUnits;
    protected Direction direction;
    protected boolean   on;
    protected String    mTicker;

    public Trade(String tic) {
	entryDate	= new Date();
	closingDate	= new Date();
        triggerDate     = new Date();
	entryPrice	= -1; 
	closingPrice	= -1; 
	target		= -1;
	stopLoss	= -1;
	numberUnits	= 0;
	direction	= Direction.none;
	mTicker = tic;
	this.on = false;
    }

    public String toString() {
	String ret = null;
	ret = mTicker + ", " + triggerDate.toString() + ", " + entryDate.toString() + ", " + closingDate.toString() + ", " + Double.toString(entryPrice) 
		+ ", " + Double.toString(closingPrice) + ", " + Double.toString(target) + ", " + Double.toString(stopLoss) + 
		", " + direction.toString();
            return ret;
	}
	
    public Trade(Trade T) {
	this.triggerDate 	= T.triggerDate;
	this.entryDate 		= T.entryDate;
	this.closingDate	= T.closingDate;
	this.entryPrice		= T.entryPrice; 
	this.closingPrice	= T.closingPrice; 
	this.target		= T.target;
	this.stopLoss		= T.stopLoss;
	this.direction		= T.direction;
	this.on			= T.on;
	this.mTicker		= T.mTicker;
	this.numberUnits	= T.numberUnits;
    }
	
    public void setUp(Date dateSet, double entry, double target, double stop, Direction dir){
	triggerDate 		= new Date(dateSet);
	this.on 		= false;
	this.entryPrice 	= entry;
	this.target 		= target;
	this.stopLoss		= stop;
	this.direction 		= dir;
	this.numberUnits	= 0;
    }
        
    public double getEntryPrice () {return entryPrice;}
    public double getClosingPrice () {return closingPrice;}
    public double getNumberUnits () {return numberUnits;}
    public Date getEntryDate () {return entryDate;}
    public Date getClosingDate () {return closingDate;}
    public String getName() {return mTicker;}
    
    public boolean isSame(Trade T) {
        if (this.triggerDate.isEqual(T.triggerDate)	&& this.direction == T.direction) {
            return true;
        }
            return false;
	}
	
    public double PL(){
	if (direction == Direction.longTrade) //long
            return (closingPrice - entryPrice);
	else 
            return (entryPrice - closingPrice);
    }
	
    double percentPL() {
	if (direction == Direction.longTrade) //long
            return (closingPrice - entryPrice)/entryPrice*100;
	else 
            return (entryPrice - closingPrice)/entryPrice*100;
    }
    
    public void Open() {
        this.entryDate = this.triggerDate;
        on = true;
    }
	
    public void Open (Date d, double price) { 
	this.entryDate = new Date(d);
	this.entryPrice = price;
	on = true;
    }
	
    public boolean Close (Date d, double price) { 
        if (!on) {
            return false;
	}
	this.closingDate = new Date(d);
	this.closingPrice = price;
	on = false;
	return true;
    }
    
    public boolean isOpen() {
        return on;
    }
    
    public void setDirection(int d) {
        if (d > 0) {
            direction = Direction.longTrade;
        } else if (d < 0) {
            direction = Direction.shortTrade;
        } else {
            direction = Direction.none;
        }
    }
    
    /**
     * Gets the trade's direction: long/short/none
     * @return 1 for long, -1 for short and 0 for none
     */
    public int getDirection() {
        if (direction == Direction.longTrade) {
            return 1;
        } else if (direction == Direction.shortTrade) {
            return -1;
        }
        return 0;
    }
	
    public int getHoldingDays() {
        int difference;
        difference = closingDate.difference(entryDate);
        return difference;
    }
    
    public void setTarget(double newTarget) { target = newTarget; }
    public double getTarget() { return target; }
    
}
