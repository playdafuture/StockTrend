package legacyModels;

import java.util.Vector;
import newWork.DataPointArray;
import newWork.Trade;
import core.*;

public class baseModel {
    public static Vector<Trade> simulate(DataPointArray origDPA, String name) {
        Vector<Trade> trArray;
        trArray = new Vector<Trade>(10);
        int tradeCounter = 0;
        Vector<Integer> localHighs;
        localHighs = new Vector<Integer>(10);
        Vector<Integer> localLows;
        localLows = new Vector<Integer>(10);
        int lastHigh = 0;
        int lastLow = 0;
        trend currentTrend = trend.unknown;
        DataPointArray instanceDPA = new DataPointArray();
        instanceDPA.insert(origDPA.getDP(0));
        for(int i = 1; i < origDPA.getSize(); i++) {
            if (origDPA.getDP(i).getHigh() < instanceDPA.getLast().getHigh()) {
                if (origDPA.getDP(i).getLow() > instanceDPA.getLast().getLow()) {
                    //new bar = inside bar, ignore this bar
                } else {
                    //down trend
                    if (currentTrend == trend.unknown) {
                        currentTrend = trend.down;
                    } else if (currentTrend == trend.up) {
                        //change of trend, new local max
                        lastHigh = instanceDPA.getSize();
                        localHighs.add(lastHigh);
                        currentTrend = trend.down;
                    }
                    instanceDPA.insert(origDPA.getDP(i));
                }
                //end of inside bar/down trend bar
            } else { //new high > last high
                if (origDPA.getDP(i).getLow() > instanceDPA.getLast().getLow()) {
                    //up trend
                    if (currentTrend == trend.unknown) {
                        currentTrend = trend.up;
                    } else if (currentTrend == trend.down) {
                        //change of trend, new local min
                        lastLow = instanceDPA.getSize();
                        localLows.add(lastLow);
                        currentTrend = trend.up;
                    }
                } else {
                    //outside bar
                    
                }
                instanceDPA.insert(origDPA.getDP(i));
            }
            //check price and decide trade action
            if (tradeCounter == 0 && lastHigh > 0 && lastLow > 0) {
                //open trade
                if (instanceDPA.getLast().getHigh() > instanceDPA.getDP(lastHigh).getHigh()) {
                    //Local high is broken, start Long
                    Trade t = new Trade(name);
                    t.setDirection(1);
                    t.Open(instanceDPA.getLast().getDate(), instanceDPA.getDP(lastHigh).getHigh() + 0.01);
                    tradeCounter++;
                    trArray.add(t);
                } else if (instanceDPA.getLast().getLow() < instanceDPA.getDP(lastHigh).getLow()) {
                    //Local low is broken, start Short
                    Trade t = new Trade(name);
                    t.setDirection(-1);
                    t.Open(instanceDPA.getLast().getDate(), instanceDPA.getDP(lastLow).getLow() - 0.01);
                    tradeCounter++;
                    trArray.add(t);
                }
            } else if (tradeCounter > 0) {
                //reverse trade
                if (trArray.get(tradeCounter - 1).getDirection() > 0) {
                    //currently in Long trade: look for local low violation
                    if (instanceDPA.getLast().getLow() < instanceDPA.getDP(lastLow).getLow()) {
                        //close trade
                        trArray.get(tradeCounter - 1).Close(instanceDPA.getLast().getDate(), instanceDPA.getDP(lastLow).getLow() - 0.01);
                        //open new trade for reverse - start Short immediately
                        Trade t = new Trade(name);
                        t.Open(instanceDPA.getLast().getDate(), instanceDPA.getDP(lastLow).getLow() + 0.01);
                        t.setDirection(-1);
                        tradeCounter++;
                        trArray.add(t);
                    }
                } else if (trArray.get(tradeCounter - 1).getDirection() < 0) {
                    //currently in Short trade: look for local high violation
                    if (instanceDPA.getLast().getHigh() > instanceDPA.getDP(lastHigh).getHigh()) {
                        //close trade
                        trArray.get(tradeCounter - 1).Close(instanceDPA.getLast().getDate(), instanceDPA.getDP(lastHigh).getHigh() + 0.01);
                        //open new trade for reverse - start Long immediately
                        Trade t = new Trade(name);
                        t.Open(instanceDPA.getLast().getDate(), instanceDPA.getDP(lastHigh).getHigh() - 0.01);
                        t.setDirection(1);
                        tradeCounter++;
                        trArray.add(t);
                    }
                }
            }
        }
        //force close last trade
        trArray.get(tradeCounter - 1).Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getClose());
        
        return trArray; //for other use
    }
}
