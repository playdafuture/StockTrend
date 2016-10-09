package OmarCodes;

import java.util.*;

public class DataPointArray {
    private Vector<DataPoint> dpArray;
    
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
        	System.out.println(NFex.getMessage());
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
     * Public method to access number of DataPoints in the DataPointArray
     * @return nElements
     */
    public int getSize() {
        return dpArray.size();
    }
    
}
