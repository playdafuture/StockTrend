package legacyModels;

import java.util.Vector;
import newWork.DataPoint;
import newWork.DataPointArray;
import core.*;

public class strongModel2 {
    Vector<Trade> trArray;
    trend currentTrend;
    DataPointArray instanceDPA;
    DataPointArray origDPA;
    String name;
    
    public strongModel2(DataPointArray originalDPA, String stockName) {
        trArray = new Vector<Trade>(10);
        currentTrend = trend.unknown;
        instanceDPA = new DataPointArray();
        origDPA = originalDPA;
        name = stockName;        
    }
    /**
     * Simulate the Trade as if the DataPoints are fed to this class in real time.
     * @param origDPA The DataPointArray to be simulated on
     * @param name The name of the Index/Stock
     * @return A Vector of Trade objects as of result of the simulation
     */
    public Vector<Trade> simulate() {        
        DataPoint tempDP = new DataPoint();
        while ((tempDP = origDPA.getBar()) != null) {
            tempDP.setFlag(0);
            if (!trArray.isEmpty() && trArray.get(trArray.size()-1).getEntryPrice()==0 && trArray.get(trArray.size()-1).isOpen()==false) {
                //check if a new trade needs to be open in the morning
                if (trArray.get(trArray.size()-1).direction == Direction.longTrade) { //long trade pending
                    if (tempDP.getOpen() <= trArray.get(trArray.size()-1).stopLoss) { //open price is lower than stop loss
                        trArray.remove(trArray.size()-1);
                        currentTrend = trend.unknown;
                    } else {
                        trArray.get(trArray.size()-1).Open(tempDP.getDate(), tempDP.getOpen());
                    }
                } else { //short trade pending
                    if (tempDP.getOpen() >= trArray.get(trArray.size()-1).stopLoss) { //open price is lower than stop loss
                        trArray.remove(trArray.size()-1);
                        currentTrend = trend.unknown;
                    } else {
                        trArray.get(trArray.size()-1).Open(tempDP.getDate(), tempDP.getOpen());
                    }
                }
            } //end of morning check
            
            if (instanceDPA.getSize() < 2) {
                //Less then 3 bars added, unable to proceed
                instanceDPA.insert(tempDP);
                instanceDPA.filter(0);
                continue;
            }
            if (tempDP.getLow() >= instanceDPA.getLast().getLow() && tempDP.getHigh() <= instanceDPA.getLast().getHigh()){
                //inside bar
                continue;
            } 
            if (!(tempDP.getLow() <= instanceDPA.getLast().getLow() && tempDP.getHigh() >= instanceDPA.getLast().getHigh())){
                //not an outside bar
                instanceDPA.insert(tempDP);
                instanceDPA.checkLastThree();
                if (currentTrend == trend.unknown && instanceDPA.hasThreeRelatives()) {
                    //Unknown trend and 3 relative points exist
                    trendUnknown();
                } else if (currentTrend == trend.up) {
                    trendUp();
                } else if (currentTrend == trend.down) {
                    trendDown();
                }
            } else {
                //outside bar
                instanceDPA.insert(tempDP);
                if (currentTrend == trend.up) {
                    //check if close condition is met
                    if (tempDP.getLow() <= trArray.get(trArray.size()-1).stopLoss) {
                        //close immediately but open at the end of the day
                        trArray.get(trArray.size()-1).Close(tempDP.getDate(), trArray.get(trArray.size()-1).stopLoss);
                        currentTrend = trend.unknown;
                    }
                    //end of up trend
                } else if (currentTrend == trend.down) {
                    //check if close condition is met
                    if (tempDP.getHigh() >= trArray.get(trArray.size()-1).stopLoss) {
                        //close immediately but open at the end of the day
                        trArray.get(trArray.size()-1).Close(tempDP.getDate(), trArray.get(trArray.size()-1).stopLoss);
                        currentTrend = trend.unknown;
                    }
                    //end of down trend
                }
                instanceDPA.filter(instanceDPA.getSize()-1);
                instanceDPA.update();
                if (currentTrend == trend.unknown) {
                    trendUnknown();
                }
                //end of outside bar
            }
            updateStops();            
        } //end of getting bars
        
        //Check to force close last trade
        if (trArray.get(trArray.size()-1).isOpen()) {
            trArray.get(trArray.size()-1).Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getClose());
        } else {
            trArray.remove(trArray.size()-1);
        }

        return trArray; //for other use
    }
    
    private void updateStops() {
        if (trArray.size() == 0 || !trArray.get(trArray.size()-1).isOpen()) {return;}
        if (trArray.get(trArray.size() - 1).direction == Direction.longTrade) {
            DataPoint dp = instanceDPA.getLastSL();
            if (dp != null && trArray.get(trArray.size() - 1).stopLoss < dp.getLow()-0.01) {
                trArray.get(trArray.size() - 1).stopLoss = dp.getLow() - 0.01;
            }
        } else {
            DataPoint dp = instanceDPA.getLastSH();
            if (dp != null && trArray.get(trArray.size() - 1).stopLoss > dp.getHigh()+0.01) {
                trArray.get(trArray.size() - 1).stopLoss = dp.getHigh() + 0.01;
            }
        }
    }
    
    private void trendUnknown() {
        if (!instanceDPA.hasThreeRelatives()) {return;}
        //Unknown trend and 3 relative points exist
        if (instanceDPA.getLastRelative().getFlag() < 0) {
            //Last Relative is a low, check LHL pattern for possible higher low (up trend)
            if (instanceDPA.getLastRL().getLow() > instanceDPA.getSecondRL().getLow()) {
                currentTrend = trend.up;
                Trade t = new Trade(name);
                t.setUp(instanceDPA.getLast().getDate(), 0, instanceDPA.getSecondRL().getLow()-0.01, instanceDPA.getSecondRL().getLow()-0.01, Direction.longTrade);
                trArray.add(t);
            }
            //Otherwise, a trend can not yet be established
        } else {
            //Last Relative is a high, check HLH pattern for possible lower high (down trend)
            if (instanceDPA.getLastRH().getHigh() < instanceDPA.getSecondRH().getHigh()) {
                currentTrend = trend.down;
                Trade t = new Trade(name);
                t.setUp(instanceDPA.getLast().getDate(), 0, instanceDPA.getSecondRH().getHigh()+0.01, instanceDPA.getSecondRH().getHigh()+0.01, Direction.shortTrade);
                trArray.add(t);
            }
            //Otherwise, a trend can not yet be established
        }
    }
    
    private void trendUp() {
        if (!instanceDPA.hasThreeRelatives()) {return;}
        //price dropped below stoploss: reverse trade
        if (instanceDPA.getLast().getLow() < trArray.get(trArray.size()-1).stopLoss) {
            if (instanceDPA.getLast().getOpen() < trArray.get(trArray.size()-1).stopLoss) {
                //open price is lower than stoploss, close at open
                trArray.get(trArray.size()-1).Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getOpen());
            } else {
                //close at stop loss
                trArray.get(trArray.size()-1).Close(instanceDPA.getLast().getDate(), trArray.get(trArray.size()-1).stopLoss);
            }                        
            currentTrend = trend.down;
            Trade t = new Trade(name);
            double newTarget;
            if (instanceDPA.getLastRH().getFlag() == 2) {
                newTarget = instanceDPA.getLastRH().getHigh();
            } else {
                if (instanceDPA.getSecondRHIndex() != -1) {
                    newTarget = Math.max(instanceDPA.getLastRH().getHigh(), instanceDPA.getSecondRH().getHigh());
                } else {
                    newTarget = instanceDPA.getLastRH().getHigh();
                }
            }
            newTarget += 0.01;
            t.setUp(instanceDPA.getLast().getDate(), 0, newTarget, newTarget, Direction.shortTrade);
            trArray.add(t);
        }
    }
    
    private void trendDown() {
        if (!instanceDPA.hasThreeRelatives()) {return;}
        //price poped above stoploss: reverse trade
        if (instanceDPA.getLast().getHigh() > trArray.get(trArray.size()-1).stopLoss) {
            if (instanceDPA.getLast().getOpen() > trArray.get(trArray.size()-1).stopLoss) {
                //open price is higher than stoploss, close at open
                trArray.get(trArray.size()-1).Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getOpen());
            } else {
                //close at stop loss
                trArray.get(trArray.size()-1).Close(instanceDPA.getLast().getDate(), trArray.get(trArray.size()-1).stopLoss);
            }                        
            currentTrend = trend.up;
            Trade t = new Trade(name);
            double newTarget;
            if (instanceDPA.getLastRL().getFlag() == -2) {
                newTarget = instanceDPA.getLastRL().getLow();
            } else {
                if (instanceDPA.getSecondRLIndex() != -1) {
                    newTarget = Math.min(instanceDPA.getLastRL().getLow(), instanceDPA.getSecondRL().getLow());
                } else {
                    newTarget = instanceDPA.getLastRL().getLow();
                }
            }
            newTarget -= 0.01;
            t.setUp(instanceDPA.getLast().getDate(), 0, newTarget, newTarget, Direction.longTrade);
            trArray.add(t);
        }
        //end of down trend
    }
    
}
