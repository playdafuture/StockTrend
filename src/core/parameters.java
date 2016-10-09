/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

/**
 *
 * @author Future
 */
public class parameters {
    public double targetFactor;
    public double minLossFactor;
    public double maxLossFactor;
    public boolean emaReverseFactor;
    public boolean updateStops;
    public double fixStopLoss;
    
    public parameters() {
        targetFactor = 0;
        minLossFactor = 0;
        maxLossFactor = 0;
        emaReverseFactor = false;
        updateStops = false;
        fixStopLoss = 0;
    }
    
    @Override
    public String toString() {
        String result = "";
        String temp;
        
        targetFactor += 0.00000000001;
        temp = Double.toString(targetFactor);
        temp = temp.substring(0, 3);
        result = result + temp + ",";
        
        minLossFactor += 0.00000000001;
        temp = Double.toString(minLossFactor);
        temp = temp.substring(0, 4);
        result = result + temp + ",";
        
        maxLossFactor += 0.00000000001;
        temp = Double.toString(maxLossFactor);
        temp = temp.substring(0, 4);
        result = result + temp + ",";
        
        if (fixStopLoss == 0) {
            temp = "0.00";
        } else {
            fixStopLoss += 0.00000000001;
            temp = Double.toString(fixStopLoss);
            temp = temp.substring(0, 4);
        }        
        result = result + temp + ",";
        
        if (emaReverseFactor == true) {
            temp = "T";
        } else {
            temp = "F";
        }
        result = result + temp + ",";
        
        if (updateStops == true) {
            temp = "T";
        } else {
            temp = "F";
        }
        result = result + temp;
        
        return result;
    }    
    
}
