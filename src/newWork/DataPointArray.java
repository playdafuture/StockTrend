package newWork;

import java.util.*;

/**
 * The DataPointArray organizes the DataPoints using vector and offers essential manipulation methods.
 */
public class DataPointArray {
    private Vector<DataPoint> dpArray;
    private int indexReturned = 0;
    public String fileName;
    protected String marketName;
    
    public DataPointArray(int size) {
        dpArray = new Vector<DataPoint>(size);
    }
    public DataPointArray() {
        dpArray = new Vector<DataPoint>(1000, 1000);
    }
  
    //replace element at index with dp
    public boolean replace(DataPoint dp, int index) {
    	if (dp == null)
    		return false;
    	dpArray.set(index, dp);
    	return true;
    }
    
    //Adds to tail  
    public boolean insert(DataPoint dp) {
    	if (dp != null)
            return dpArray.add(dp);
    	return false;
    }
    
  //Adds to tail  
    public boolean insert(DataPoint dp, int index) {
    	if (index <0 || index > dpArray.size() || dp == null)
    		return false;
    	dpArray.insertElementAt(dp, index);
    	return true;
    }
    
    //insert at top
    public boolean insertFirst(DataPoint dp) {
    	if (dp != null) {
    		dpArray.insertElementAt(dp, 0);
    		return true;
    	}
    	return false;
    }
    
  //insert at tail
    public boolean insertLast(DataPoint dp) {
    	return this.insert(dp);
    }
    
    //insert at tail data point in form of a string
    public boolean insert(String fileLine) {
    	DataPoint tempPoint = convert(fileLine);
    	if (tempPoint != null)
    		return dpArray.add(tempPoint);
    	return false;
    }
    
    //insert at tail the datapoint in form of a line
    public boolean insert(String fileLine, int index) {
    	if (index <0 || index > dpArray.size())
    		return false;
    	DataPoint tempPoint = convert(fileLine);
    	if(tempPoint != null){
    		dpArray.insertElementAt(tempPoint, index);
    		return true;
    	}
    	return false;
    }
    
    //insert at tail a data point in the form of a string
    public boolean insertLast (String fileLine) {
    	return this.insert(fileLine);
    }
    
  //insert at top the datapoint in form of a line
    public boolean insertFirst(String fileLine) {
    	return this.insert(fileLine, 0);
    }
    
    public DataPoint remove(int index) {
        return dpArray.remove(index);
    }
    
    public DataPoint remove() {
        return dpArray.remove(0);
    }
    
    //remove from the top
    public DataPoint removeFirst() {
    	if (dpArray.size() == 0)
    		return null;
        return dpArray.remove(0);
    }
    
    public DataPoint removeLast() {
    	if (dpArray.size() == 0)
    		return null;
        return dpArray.remove(dpArray.size()-1);
    }
    
    public DataPoint getDP(int index) {
    	if (dpArray.size() == 0 || index < 0 || index >= dpArray.size())
    		return null;
        return dpArray.elementAt(index);
    }
    
    public DataPoint getLast() {
    	if (dpArray.size() == 0 )
            return null;
        return dpArray.lastElement();
    }
    
    public DataPoint getFirst() {
    	if (dpArray.size() == 0 )
    		return null;
        return dpArray.firstElement();
    }
  
    private DataPoint convert(String fileLine) {
        DataPoint thisLine = null;
        StringTokenizer token;
        Date date;
        String dateString;
        double open, high, low, close, adjClose, volume;
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
            if(open <= 0 || close <= 0 || high <= 0 || low == 0) {
            	System.out.println("Problemishere");
            	return null;
            }
            thisLine = new DataPoint(date, open, high, low, close);
        } catch (NumberFormatException NFex) {
            //"Invalid line found"
        	//System.out.println(NFex.getMessage());
        	return null;
        }            
        return thisLine;
    }
    
    void clear() {
    	dpArray.clear();
    }
    
    void print() {
    	for(int i = 0; i < dpArray.size(); i++)
    		this.getDP(i).print();
    }
    
    void reverse() {
    	int i = 0;
    	int j = dpArray.size() -1;
    	while(i<j) {
    		DataPoint temp = this.getDP(i);
    		this.replace(this.getDP(j), i);
    		this.replace(temp, j);
    		i++;
    		j--;
    	}
    }
    
    /**
     * Erases less informative bars starting from a specified index
     * @param i The index to start filter to the end. Enter 0 to start from beginning
     */
    public void filter(int i) {
        int k = i;
        
        while (k < dpArray.size()) {
            if (k < dpArray.size() - 1 && dpArray.get(k).getHigh() >= dpArray.get(k+1).getHigh()) {
                if (dpArray.get(k).getLow() <= dpArray.get(k+1).getLow()) {
                    dpArray.remove(k+1);
                    k--;
                }
            }
            k++;
        }
        
        while (i < dpArray.size()) {
            if (i > 1 && dpArray.get(i-1).getHigh() <= dpArray.get(i).getHigh()) {
                if (dpArray.get(i-1).getLow() >= dpArray.get(i).getLow()) {
                    dpArray.remove(i-1);
                    i -= 2;
                }
            }
            i++;
        }
    }
  
    public void update() {
        if (this.getSize() < 3) {
            return;
        }
        this.getDP(0).setFlag(0);
        this.getDP(1).setFlag(0);
        for (int i = 2; i < this.getSize(); i++) {
            //reset flag
            this.getDP(i).setFlag(0);
            //check for relative low
            if (this.getDP(i - 1).getLow() < this.getDP(i - 2).getLow() 
                    && this.getDP(i - 1).getLow() < this.getDP(i).getLow()) {
                //2nd last bar is a relative low
                this.getDP(i - 1).setFlag(-1);
            }
            //check for relative high
            if (this.getDP(i - 1).getHigh() > this.getDP(i - 2).getHigh() 
                    && this.getDP(i - 1).getHigh() > this.getDP(i).getHigh()) {
                //2nd last bar is a relative high
                this.getDP(i - 1).setFlag(1);
            }
            
            //marking strong flags
            if (this.hasThreeRelatives(i)) {
                if (this.getLastRelative(i).getFlag() < 0) {
                    //check for strong low: if the latest RH is broken, then the second last RL is SL
                    if (this.getLastRL(i).getLow() < this.getSecondRL(i).getLow()) {
                        this.getLastRH(i).setFlag(+2);
                    }
                } else {
                    //check for strong high: if the latest RL is broken, then the second last RH is SH
                    if (this.getLastRH(i).getHigh() > this.getSecondRH(i).getHigh()) {
                        this.getLastRL(i).setFlag(-2);
                    }
                }
            } //end of marking strong flags
            
        } //end of for loop
        if (this.hasXRelatives(2)) { //extra case
            if (this.getLastRelative().getFlag() < 0) {
                if (this.getDP(this.getMax(this.getLastRLIndex(), this.getSize()-1)).getHigh() > this.getLastRH().getHigh()) {
                    this.getLastRL().setFlag(-2);
                }
            } else {
                if (this.getDP(this.getMin(this.getLastRHIndex(), this.getSize()-1)).getLow() < this.getLastRL().getLow()) {
                    this.getLastRH().setFlag(+2);
                }
            }
        } //end of extra case
        if (this.hasXRelatives(2) && this.getLastRelative().getFlag() * this.getXthRelative(1).getFlag() > 0) {
            System.out.println("Problem not fixed.");
            for (int i = 0; i<this.getSize(); i++) {
                if (i>1 && this.getDP(i-1).getLow() >= this.getDP(i).getLow() &&
                        this.getDP(i-1).getHigh() <= this.getDP(i).getHigh()) {
                    System.out.println("An outside bar that shouldn't be here");
                } else if (i<this.getSize()-1 && this.getDP(i).getLow() <= this.getDP(i+1).getLow() &&
                        this.getDP(i).getHigh() >= this.getDP(i+1).getHigh()){
                    System.out.println("An inside bar that shouldn't be here");
                }
            }
        }
    }
    
    /**
     * Checks the latest 3 bars and marks relative and strong points respectively.
     */
    public void checkLastThree() {
        int size = this.getSize();
        if (this.getDP(size - 2).getLow() < this.getDP(size - 3).getLow() &&
                this.getDP(size - 2).getLow() < this.getDP(size - 1).getLow()) {
            //2nd last bar is a relative low
            this.getDP(size - 2).setFlag(-1);
        } else if (this.getDP(size - 2).getHigh() > this.getDP(size - 3).getHigh() &&
                this.getDP(size - 2).getHigh() > this.getDP(size - 1).getHigh()) {
            //2nd last bar is a relative high
            this.getDP(size - 2).setFlag(1);
        }
        if (this.hasTwoRelatives()) { //extra case
            if (this.getLastRelative().getFlag() < 0) {
                if (this.getXthRelative(1).getFlag() <= 0) {
                    this.filter(0);
                    this.update();
                    System.out.println("Something is wrong.");
                }
                if (this.getDP(this.getMax(this.getLastRLIndex(), this.getSize()-1)).getHigh() > this.getLastRH().getHigh()) {
                    this.getLastRL().setFlag(-2);
                }
            } else {
                if (this.getXthRelative(1).getFlag() >= 0) {
                    this.filter(0);
                    this.update();
                    System.out.println("Something is wrong.");
                }
                if (this.getDP(this.getMin(this.getLastRHIndex(), this.getSize()-1)).getLow() < this.getLastRL().getLow()) {
                    this.getLastRH().setFlag(+2);
                }
            }
        } //end of extra case
    }
    
    /**
     * Public method to access number of DataPoints in the DataPointArray
     * @return nElements
     */
    public int getSize() {
        return dpArray.size();
    }
    
    /**
     * Method to obtain DataPoint from the array one by one, or null if none is left.
     * @return the next DataPoint object not yet returned
     */
    public DataPoint getBar () {
        if (indexReturned >= this.getSize()) {
            return null;
        } else {
            indexReturned++;
            DataPoint copyBar = new DataPoint(this.getDP(indexReturned-1).getDate(), 
                    this.getDP(indexReturned-1).getOpen(), 
                    this.getDP(indexReturned-1).getHigh(), 
                    this.getDP(indexReturned-1).getLow(), 
                    this.getDP(indexReturned-1).getClose());
            return copyBar;
        }
    }
    
    public int getLastRLIndex() {
        for (int i = dpArray.size() - 1; i > 0; i--) {
            if (dpArray.get(i).getFlag() < 0) {
                return i;
            }
        }
        return -1;
    }
    
    public int getLastRLIndex(int index) {
        for (int i = index; i > 0; i--) {
            if (dpArray.get(i).getFlag() < 0) {
                return i;
            }
        }
        return -1;
    }
    
    public DataPoint getLastRL() {
        return this.getDP(this.getLastRLIndex());
    }
    
    public DataPoint getLastRL(int index) {
        return this.getDP(this.getLastRLIndex(index));
    }
    
    public int getSecondRLIndex() {
        if (this.getLastRLIndex() == -1) {
            return -1;
        } else {
            for (int i = this.getLastRLIndex() - 1; i > 0; i--) {
                if (dpArray.get(i).getFlag() < 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int getSecondRLIndex(int index) {
        if (this.getLastRLIndex(index) == -1) {
            return -1;
        } else {
            for (int i = this.getLastRLIndex(index) - 1; i > 0; i--) {
                if (dpArray.get(i).getFlag() < 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public DataPoint getSecondRL() {
        return this.getDP(this.getSecondRLIndex());
    }
    
    public DataPoint getSecondRL(int index) {
        return this.getDP(this.getSecondRLIndex(index));
    }
    
    public int getLastRHIndex() {
        for (int i = dpArray.size() - 1; i > 0; i--) {
            if (dpArray.get(i).getFlag() > 0) {
                return i;
            }
        }
        return -1;
    }
    
    public int getLastRHIndex(int index) {
        for (int i = index; i > 0; i--) {
            if (dpArray.get(i).getFlag() > 0) {
                return i;
            }
        }
        return -1;
    }
    
    public DataPoint getLastSL() {
        for (int i = dpArray.size() - 1; i > 0; i--) {
            if (dpArray.get(i).getFlag() == -2) {
                return dpArray.get(i);
            }
        }
        return null;
    }
    
    public DataPoint getLastSH() {
        for (int i = dpArray.size() - 1; i > 0; i--) {
            if (dpArray.get(i).getFlag() == +2) {
                return dpArray.get(i);
            }
        }
        return null;
    }
    
    public DataPoint getLastRH() {
        return this.getDP(this.getLastRHIndex());
    }
    
    public DataPoint getLastRH(int index) {
        return this.getDP(this.getLastRHIndex(index));
    }
    
    public int getSecondRHIndex() {
        if (this.getLastRHIndex() == -1) {
            return -1;
        } else {
            for (int i = this.getLastRHIndex()-1; i > 0; i--) {
                if (dpArray.get(i).getFlag() > 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int getSecondRHIndex(int index) {
        if (this.getLastRHIndex(index) == -1) {
            return -1;
        } else {
            for (int i = this.getLastRHIndex(index) - 1; i > 0; i--) {
                if (dpArray.get(i).getFlag() > 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public DataPoint getSecondRH() {
        return this.getDP(this.getSecondRHIndex());
    }
    
    public DataPoint getSecondRH(int index) {
        return this.getDP(this.getSecondRHIndex(index));
    }
    
    public int getLastRelativeIndex() {
        for (int i = dpArray.size() - 1; i > 0; i--) {
            if (dpArray.get(i).getFlag() != 0) {
                return i;
            }
        }
        return -1;
    }
    
    public int getLastRelativeIndex(int index) {
        for (int i = index; i > 0; i--) {
            if (dpArray.get(i).getFlag() != 0) {
                return i;
            }
        }
        return -1;
    }
    
    public DataPoint getLastRelative() {
        return this.getDP(getLastRelativeIndex());
    }
    
    public DataPoint getLastRelative(int index) {
        return this.getDP(getLastRelativeIndex(index));
    }
    
    /**
     * Checks the entire DataPointArray for Relative points
     * @return true if at least 3 Relative points exist and false otherwise
     */
    public boolean hasThreeRelatives() {
        int count = 0;
        for (int i = dpArray.size() - 1; i > 0; i--) {
            if (dpArray.get(i).getFlag() != 0) {
                //System.out.println(dpArray.get(i).getDate() + " - " + dpArray.get(i).getFlag());
                count++;
            }
            if (count == 3) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasTwoRelatives() {
        int count = 0;
        int first = 0, second = 0;
        for (int i = dpArray.size() - 1; i > 0; i--) {
            if (dpArray.get(i).getFlag() != 0) {
                //System.out.println(dpArray.get(i).getDate() + " - " + dpArray.get(i).getFlag());
                count++;
                if (count == 1) {
                    first = i;
                } else if (count == 2) {
                    second = i;
                }
            }
            if (count == 2) {
                if (this.getDP(first).getFlag() * this.getDP(second).getFlag() > 0) {
                    //System.out.println("Two flag error @size " + dpArray.size() + ", [" + this.fileName + "] flag = " + this.getDP(first).getFlag());
//                    System.out.println(this.hasXRelatives(6));
//                    for (int q = 0; q < 6; q++) {
//                        System.out.print(this.getXthRelative(q).getFlag() + ",");
//                        System.out.println(this.getXthRelative(q).getDate().toString());
//                    }
                    this.filter(0);
                    this.update();
//                    System.out.println("Taken care");
//                    for (int q = 0; q < 6; q++) {
//                        System.out.print(this.getXthRelative(q).getFlag() + ",");
//                        System.out.println(this.getXthRelative(q).getDate().toString());
//                    }
                    i = dpArray.size() - 1;
                    count = 0;
                    continue;
                }
                return true;
            }
        }
        return false;
    }
    

    
    /**
     * Checks the DataPointArray for Relative points only from the specified index and backwards to the beginning
     * @param index The index to be checked and before
     * @return true if at least 3 Relative points exist and false otherwise
     */    
    public boolean hasThreeRelatives(int index) {
        int count = 0;
        for (int i = index; i > 0; i--) {
            if (dpArray.get(i).getFlag() != 0) {
                //System.out.println(dpArray.get(i).getDate() + " - " + dpArray.get(i).getFlag());
                count++;
            }
            if (count == 3) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasTwoRelatives(int index) {
        int count = 0;
        for (int i = index; i > 0; i--) {
            if (dpArray.get(i).getFlag() != 0) {
                //System.out.println(dpArray.get(i).getDate() + " - " + dpArray.get(i).getFlag());
                count++;
            }
            if (count == 2) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasXRelatives(int x) {
        int count = 0;
        for (int i = dpArray.size()-1; i > 0; i--) {
            if (dpArray.get(i).getFlag() != 0) {
                count++;
                if (count == x) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public DataPoint getXthRelative(int x) {
        int count = 0;
        for (int i = dpArray.size()-1; i > 0; i--) {
            if (dpArray.get(i).getFlag() != 0) {
                if (count == x) {
                    return dpArray.get(i);
                }
                count++;
            }
        }
        return null;
    }
    
    
    /**
     * Get the index within the given range for the bar with the highest value
     * @param start beginning index
     * @param end ending index
     * @return index of the bar with the highest high
     */
    public int getMax(int start, int end) {
        int index = start;
        for (int i = start + 1; i <= end; i++) {
            if (dpArray.get(i).getHigh() > dpArray.get(index).getHigh()) {
                index = i;
            }
        }
        return index;
    }
    
    public int getMin(int start, int end) {
        int index = start;
        for (int i = start + 1; i <= end; i++) {
            if (dpArray.get(i).getLow() < dpArray.get(index).getLow()) {
                index = i;
            }
        }
        return index;
    }
    
    public void computeEMA20() {
        double factor = 2.0/21.0;
        if (this.getSize() == 0) {
            return;
        }
        int last = this.getSize()-1;
        if (last == 0) {
            this.getDP(0).EMA20 = this.getDP(0).getClose();
        } else {
            this.getDP(last).EMA20 = this.getDP(last).getClose()*(factor) + (1-factor)*(this.getDP(last-1).EMA20);
        }
    }
    
    public void computeEMA40() {
        double factor = 2.0/51.0;
        if (this.getSize() == 0) {
            return;
        }
        int last = this.getSize()-1;
        if (last == 0) {
            this.getDP(0).EMA40 = this.getDP(0).getClose();
        } else {
            this.getDP(last).EMA40 = this.getDP(last).getClose()*(factor) + (1-factor)*(this.getDP(last-1).EMA40);
        }
    }
    
    public void resetIndexReturned() {
        indexReturned = 0;
    }
}
