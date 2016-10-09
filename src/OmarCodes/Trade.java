package OmarCodes;

enum Direction {longTrade, shortTrade, none};
public class Trade {
	protected Date	setupDate;
	protected Date	entryDate;
	protected Date 	closingDate;
	protected double entryPrice; 
	protected double closingPrice; 
	protected double target;
	protected double stopLoss;
	protected int numberUnits;
	protected Direction direction;
	protected boolean on;
	protected String mTicker;

    public Trade(String tic) {
	setupDate 	= new Date();
	entryDate	= new Date();
	closingDate	= new Date();
	entryPrice	= -1; 
	closingPrice	= -1; 
	target		= -1;
	stopLoss	= -1;
	numberUnits	= 0;
	direction	= Direction.none;
	mTicker = tic;
	this.on = false;
    }
	
	/*public Trade (Date dateSet, double entry, double target, double stop, Direction dir){
		setUp(dateSet, entry, target, stop, dir);
	}*/
	
    public String toString() {
	String ret = null;
	ret = mTicker + ", " + setupDate.toString() + ", " + entryDate.toString() + ", " + closingDate.toString() + ", " + Double.toString(entryPrice) 
            + ", " + Double.toString(closingPrice) + ", " + Double.toString(target) + ", " + Double.toString(stopLoss) + 
            ", " + direction.toString();
	return ret;
    }
	
	public Trade(Trade T) {
		this.setupDate 		= T.setupDate;
		this.entryDate 		= T.entryDate;
		this.closingDate	= T.closingDate;
		this.entryPrice		= T.entryPrice; 
		this.closingPrice	= T.closingPrice; 
		this.target			= T.target;
		this.stopLoss		= T.stopLoss;
		this.direction		= T.direction;
		this.on				= T.on;
		this.mTicker		= T.mTicker;
		this.numberUnits	= T.numberUnits;
	}
	
	public void setUp(Date dateSet, double entry, double target, double stop, Direction dir){
		setupDate 			= new Date(dateSet);
		this.on 			= false;
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
	public boolean isSame(Trade T) {
		if (this.setupDate.isEqual(T.setupDate)	&& this.direction == T.direction) 
			return true;
		return false;
	}
	
	public double PL (){
		if (direction == Direction.longTrade) //long
			return (closingPrice - entryPrice);
		else 
			return (entryPrice - closingPrice);
	}
	
	double percentPL (){
		if (direction == Direction.longTrade) //long
			return (closingPrice - entryPrice)/entryPrice*100;
		else 
			return (entryPrice - closingPrice)/entryPrice*100;
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
	
}