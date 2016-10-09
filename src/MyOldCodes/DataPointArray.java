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
public class DataPointArray {
    private DataPoint[] dpArray;
    private int nElements;
    
    public DataPointArray(int size) {
        dpArray = new DataPoint[size];
        nElements = 0;
    }
    
    public void insert(DataPoint dp) {
        for (int i = nElements; i > 0; i--) {
            dpArray[i] = dpArray[i-1];
        }
        dpArray[0] = dp;
        nElements++;
    }
    
    public void insert(String fileLine) {
        DataPoint thisLine = convert(fileLine);
        insert(thisLine);
    }
    
    public DataPoint remove() {
        DataPoint topDP = dpArray[0];
        for (int i = 0; i < nElements; i++) {
            dpArray[i] = dpArray[i+1];
        }
        nElements--;
        return topDP;
    }
    
    public DataPoint show(int index) {//for testing only
        return dpArray[index];
    }
    
    private DataPoint convert(String fileLine) {
        DataPoint thisLine = null;
        StringTokenizer token;
        Date date;
        String dateString;
        double open, high, low, close;
        token = new StringTokenizer(fileLine, ",");
        try { //Parse line into DataPoint and add. If fail, skip line and show error.
            dateString = token.nextToken();
            date = new Date(dateString, "YMD");
            open = Double.parseDouble(token.nextToken());
            high = Double.parseDouble(token.nextToken());
            low = Double.parseDouble(token.nextToken());
            close = Double.parseDouble(token.nextToken());                
            thisLine = new DataPoint(date, open, high, low, close);
        } catch (NumberFormatException NFex) {
            System.out.println("Invalid line found");
        }            
        return thisLine;
    }
    
    /**
     * Public method to access number of DataPoints in the DataPointArray
     * @return nElements
     */
    public int getElements() {
        return nElements;
    }
    
}
