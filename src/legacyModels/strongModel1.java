package legacyModels;

import java.util.Vector;
import newWork.*;
import core.*;

public class strongModel1 {
    /**
     * Simulate the Trade as if the DataPoints are fed to this class in real time.
     * @param origDPA The DataPointArray to be simulated on
     * @param name The name of the Index/Stock
     * @return A Vector of Trade objects as of result of the simulation
     */
    public static Vector<Trade> simulate(DataPointArray origDPA, String name) {
        Vector<Trade> trArray;
        trArray = new Vector<Trade>(10);
        trend currentTrend = trend.unknown;
        DataPointArray instanceDPA = new DataPointArray();
        DataPoint tempDP = new DataPoint();
        tempDP = origDPA.getBar();
        while (tempDP != null) {
            if (instanceDPA.getSize() < 3) {
                //Less then 3 bars added, unable to proceed
                instanceDPA.insert(tempDP);
                instanceDPA.filter(0);
            } else if (tempDP.getLow()>instanceDPA.getLast().getLow() && tempDP.getHigh()<instanceDPA.getLast().getHigh()){
                //inside bar, ignore
            } else if (!(tempDP.getLow()<instanceDPA.getLast().getLow() && tempDP.getHigh()>instanceDPA.getLast().getHigh())){
                //not an outside bar
                instanceDPA.insert(tempDP);
                checkLastThree(instanceDPA);
                
                if (currentTrend == trend.unknown && instanceDPA.hasThreeRelatives()) {
                    //Unknown trend and 3 relative points exist
                    if (instanceDPA.getLastRelative().getFlag() < 0) {
                        //Last Relative is a low, check LHL pattern for possible higher low (up trend)
                        if (instanceDPA.getLastRL().getLow() > instanceDPA.getSecondRL().getLow()) {
                            currentTrend = trend.up;
                            Trade t = new Trade(name);
                            t.setUp(tempDP.getDate(), tempDP.getClose(), tempDP.getClose(), tempDP.getClose(), Direction.longTrade);
                            t.Open();
                            trArray.add(t);
                        }
                        //Otherwise, a trend can not yet be established
                    } else {
                        //Last Relative is a high, check HLH pattern for possible lower high (down trend)
                        //System.out.println(instanceDPA.getLastRH().getDate());
                        //System.out.println(instanceDPA.getSecondRH().getDate());
                        if (instanceDPA.getLastRH().getHigh() < instanceDPA.getSecondRH().getHigh()) {
                            currentTrend = trend.down;
                            Trade t = new Trade(name);
                            t.setUp(tempDP.getDate(), tempDP.getClose(), tempDP.getClose(), tempDP.getClose(), Direction.shortTrade);
                            t.Open();
                            trArray.add(t);
                        }
                        //Otherwise, a trend can not yet be established
                    }
                    //end of unknown trend
                } else if (currentTrend == trend.up) {
                    //price dropped below target: reverse trade
                    if (tempDP.getLow() < trArray.get(trArray.size()-1).getTarget()) {
                        double triggerPrice = trArray.get(trArray.size()-1).getTarget();
                        trArray.get(trArray.size()-1).Close(tempDP.getDate(), triggerPrice - 0.01);
                        currentTrend = trend.down;
                        Trade t = new Trade(name);
                        t.setUp(tempDP.getDate(), triggerPrice, triggerPrice, triggerPrice, Direction.shortTrade);
                        t.Open();
                        trArray.add(t);
                    }
                    //update target price according to new Strong low
                    if (instanceDPA.getLastRHIndex() != -1 && tempDP.getHigh() > instanceDPA.getLastRH().getHigh()) {
                        instanceDPA.getLastRL().setFlag(-2);
                        trArray.get(trArray.size()-1).setTarget(instanceDPA.getLastRL().getLow());
                    }
                    //end of up trend
                } else if (currentTrend == trend.down) {
                    //price poped above target: reverse trade
                    if (tempDP.getHigh() > trArray.get(trArray.size()-1).getTarget()) {
                        double triggerPrice = trArray.get(trArray.size()-1).getTarget();
                        trArray.get(trArray.size()-1).Close(tempDP.getDate(), triggerPrice + 0.01);
                        currentTrend = trend.up;
                        Trade t = new Trade(name);
                        t.setUp(tempDP.getDate(), triggerPrice, triggerPrice, triggerPrice, Direction.longTrade);
                        t.Open();
                        trArray.add(t);
                    }
                    //update target price according to new Strong high
                    if (instanceDPA.getLastRLIndex() != -1 && tempDP.getLow() < instanceDPA.getLastRL().getLow()) {
                        instanceDPA.getLastRH().setFlag(+2);
                        trArray.get(trArray.size()-1).setTarget(instanceDPA.getLastRH().getHigh());
                    }
                    //end of down trend
                }
            } else {
                //outside bar
                instanceDPA.insert(tempDP);
                if (currentTrend == trend.up) {
                    //check if close condition is met
                    if (tempDP.getLow() < trArray.get(trArray.size()-1).getTarget()) {
                        double triggerPrice = trArray.get(trArray.size()-1).getTarget();
                        trArray.get(trArray.size()-1).Close(tempDP.getDate(), triggerPrice - 0.01);
                        //enter reverse trade only if outside bar does not violate last relative high
                        if (tempDP.getHigh() < instanceDPA.getLastRH().getHigh()) {
                            currentTrend = trend.down;
                            Trade t = new Trade(name);
                            t.setUp(tempDP.getDate(), triggerPrice, triggerPrice, triggerPrice, Direction.shortTrade);
                            t.Open();
                            trArray.add(t);
                        } else {
                            //reset
                            currentTrend = trend.unknown;
                        }
                    } else {
                        //no need to close, continue;
                    }
                    //end of up trend
                } else if (currentTrend == trend.down) {
                    //check if close condition is met
                    if (tempDP.getHigh() > trArray.get(trArray.size()-1).getTarget()) {
                        double triggerPrice = trArray.get(trArray.size()-1).getTarget();
                        trArray.get(trArray.size()-1).Close(tempDP.getDate(), triggerPrice + 0.01);
                        //enter reverse trade only if outside bar does not violate last relative low
                        if (tempDP.getLow() > instanceDPA.getLastRL().getLow()) {
                            currentTrend = trend.up;
                            Trade t = new Trade(name);
                            t.setUp(tempDP.getDate(), triggerPrice, triggerPrice, triggerPrice, Direction.longTrade);
                            t.Open();
                            trArray.add(t);
                        } else {
                            //reset
                            currentTrend = trend.unknown;
                        }
                    } else {
                        //no need to close, continue;
                    }
                    //end of down trend
                } else {
                    //no trend, safe to filter directly
                }
                instanceDPA.filter(instanceDPA.getSize()-1);
                //end of outside bar
            }
            tempDP = origDPA.getBar();
        } //end of getting bars
        //Check to force close last trade
        if (trArray.get(trArray.size()-1).isOpen()) {
            trArray.get(trArray.size()-1).Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getClose());
        }

        return trArray; //for other use
    }

    /**
     * Checks the last three bars in a DataPointArray, and determine if a new relative High/Low can be marked.
     * @param instanceDPA the DataPointArray to check
     */
    private static void checkLastThree(DataPointArray instanceDPA) {
        int size = instanceDPA.getSize();
        if (instanceDPA.getDP(size - 2).getLow() < instanceDPA.getDP(size - 3).getLow()) {
            //AND
            if (instanceDPA.getDP(size - 2).getLow() < instanceDPA.getDP(size - 1).getLow()) {
                //2nd last bar is a relative low
                instanceDPA.getDP(size - 2).setFlag(-1);
            }
        }
        if (instanceDPA.getDP(size - 2).getHigh() > instanceDPA.getDP(size - 3).getHigh()) {
            //AND
            if (instanceDPA.getDP(size - 2).getHigh() > instanceDPA.getDP(size - 1).getHigh()) {
                //2nd last bar is a relative high
                instanceDPA.getDP(size - 2).setFlag(1);
            }
        }
    }
}
